package app.model;

public enum Status {
    WATCHING("Смотрю"),
    COMPLETED("Просмотрено"),
    ON_HOLD("Отложено"),
    DROPPED("Брошено"),
    PLAN_TO_WATCH("Запланировано");

    private String text;

    Status(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static Status getEnum(String name) {
        for (Status status : Status.values()) {
            if (status.text.equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }
}
