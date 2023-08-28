package bookkeeper.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Expenditure {
    FOOD("Еда и хозтовары"),
    GOODS("Вещи"),
    FUN("Развлечения"),
    HEALTH("Здоровье"),
    TRANSPORT("Транспорт"),
    TELECOMMUNICATIONS("Связь"),
    HOUSE("Квартира"),
    TRAVEL("Путешествия"),
    TAXES("Налоги"),
    JOB("Работа"),
    RELATIVES("Семья"),
    BANKING("Кэшбек и проценты"),

    // Values which are used to enlarge upper limit for corresponding integer column constraint.
    // Instead of adding a value, just pick and rename reserved one.
    // Column constraints and existing values won't be affected this way.
    RESERVED_1("Резерв_1"),
    RESERVED_2("Резерв_2"),
    RESERVED_3("Резерв_3"),
    RESERVED_4("Резерв_4"),
    RESERVED_5("Резерв_5"),
    RESERVED_6("Резерв_6"),
    RESERVED_7("Резерв_7"),
    RESERVED_8("Резерв_8"),
    OTHER("Другое");

    private final String verboseName;

    Expenditure(String verboseName) {
        this.verboseName = verboseName;
    }

    public String getVerboseName() {
        return verboseName;
    }

    private boolean isEnabled() { return !name().startsWith("RESERVED"); }

    public static List<Expenditure> enabledValues() {
        return Arrays.stream(values())
            .filter(Expenditure::isEnabled)
            .collect(Collectors.toList());
    }
}
