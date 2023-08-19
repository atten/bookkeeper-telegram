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
    OTHER("Другое");

    private final String name;

    Expenditure(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
