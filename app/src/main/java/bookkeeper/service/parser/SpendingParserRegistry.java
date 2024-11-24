package bookkeeper.service.parser;

import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;

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

    public Spending parse(String rawMessage) throws ParseException {
        ArrayList<Spending> candidates = new ArrayList<>();

        for (SpendingParser<? extends Spending> spendingParser : spendingParsers) {
            try {
                candidates.add(spendingParser.parse(rawMessage));
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
        // Do the best guess and suggest the most detailed candidate (if all candidates have same balance).
        // Otherwise, a result is ambiguous and should be handled manually.
        var balances = candidates
            .stream()
            .map(Spending::getBalance)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

        var uniqueBalances = Set.copyOf(balances);

        if (balances.size() == candidates.size() && uniqueBalances.size() == 1) {
            // most detailed candidate = class with the largest number of fields
            return candidates
                .stream()
                .sorted(Comparator.comparingInt(spending -> spending.getClass().getFields().length).reversed())
                .toList()
                .getFirst();
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
