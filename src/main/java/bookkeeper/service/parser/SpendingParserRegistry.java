package bookkeeper.service.parser;

import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An aggregate of multiple parsers.
 * Only one parser is suitable for given message.
 */
public class SpendingParserRegistry {
    private final ArrayList<SpendingParser<? extends Spending>> spendingParsers = new ArrayList<>();
    private static final Reflections reflections = new Reflections("bookkeeper");

    public static SpendingParserRegistry ofAllParsers() {
        var registry = new SpendingParserRegistry();
        reflections.getTypesAnnotatedWith(MarkSpendingParser.class).forEach(registry::add);
        return registry;
    }

    public static SpendingParserRegistry ofProvider(String provider) {
        var registry = new SpendingParserRegistry();
        var annotationClass = MarkSpendingParser.class;

        reflections.getTypesAnnotatedWith(annotationClass).forEach(klass -> {
            var annotation = klass.getAnnotation(annotationClass);
            if (!Objects.equals(annotation.provider(), provider))
                return;
            registry.add(klass);
        });


        return registry;
    }

    @SuppressWarnings("rawtypes")
    public Spending parse(String rawMessage) throws ParseException {
        ArrayList<Spending> candidates = new ArrayList<>();
        ArrayList<SpendingParser> parsers = new ArrayList<>();

        for (SpendingParser<? extends Spending> spendingParser : spendingParsers) {
            try {
                candidates.add(spendingParser.parse(rawMessage));
                parsers.add(spendingParser);
            } catch (ParseException e) {
                // will try another parser
            }
        }

        if (candidates.size() == 1)
            // single result as expected
            return candidates.getFirst();

        if (candidates.isEmpty())
            throw new ParseException("No suitable SmsParser found.", 0);

        // Multiple SmsParsers found suitable.
        // Do the best guess and suggest the most detailed candidate.
        // Otherwise, parser with a higher rank wins.
        var uniqueFieldsCount = candidates
            .stream()
            .map(spending -> spending.getClass().getFields().length)
            .collect(Collectors.toSet());

        var uniqueRanks = parsers
            .stream()
            .map(SpendingParser::weight)
            .collect(Collectors.toSet());

        if (uniqueFieldsCount.size() != 1) {
            // most detailed candidate = class with the largest number of fields
            return candidates
                .stream()
                .sorted(Comparator.comparingInt(spending -> spending.getClass().getFields().length).reversed())
                .toList()
                .getFirst();
        }

        if (uniqueRanks.size() != 1) {
            return parsers
                .stream()
                .sorted(Comparator.comparingInt(SpendingParser::weight))
                .toList()
                .getLast()
                .parse(rawMessage);
        }


        throw new ParseException("Multiple SmsParser found suitable: %s".formatted(candidates), 0);
    }

    private void add(Class<?> spendingParserClass) {
        try {
            var instance = spendingParserClass.getConstructor().newInstance();
            add((SpendingParser<? extends Spending>) instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void add(SpendingParser<? extends Spending> spendingParser) {
        spendingParsers.add(spendingParser);
    }
}
