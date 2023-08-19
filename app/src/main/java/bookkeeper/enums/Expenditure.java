package bookkeeper.enums;

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
    OTHER("Другое"),

    RESERVED_1("Резерв_1"),
    RESERVED_2("Резерв_2"),
    RESERVED_3("Резерв_3"),
    RESERVED_4("Резерв_4"),
    RESERVED_5("Резерв_5"),
    RESERVED_6("Резерв_6"),
    RESERVED_7("Резерв_7"),
    RESERVED_8("Резерв_8"),
    RESERVED_9("Резерв_9"),
    RESERVED_10("Резерв_10");

    private final String name;

    Expenditure(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
