package app.model;

/**
 * Перечисление возможных статусов просмотра аниме.
 * Определяет различные состояния, в которых может находиться аниме в списке пользователя.
 */
public enum Status {
    /** Аниме просматривается в данный момент */
    WATCHING("Смотрю"),

    /** Аниме полностью просмотрено */
    COMPLETED("Просмотрено"),

    /** Просмотр приостановлен */
    ON_HOLD("Отложено"),

    /** Просмотр прекращен */
    DROPPED("Брошено"),

    /** Запланировано к просмотру */
    PLAN_TO_WATCH("Запланировано");

    private String text;

    /**
     * Конструктор для создания элемента перечисления с текстовым представлением.
     *
     * @param text текстовое представление статуса
     */
    Status(String text) {
        this.text = text;
    }

    /**
     * Возвращает текстовое представление статуса.
     *
     * @return строковое представление статуса
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Возвращает элемент перечисления Status по его текстовому представлению.
     *
     * @param name текстовое представление статуса
     * @return соответствующий элемент Status или null, если не найден
     */
    public static Status getEnum(String name) {
        for (Status status : Status.values()) {
            if (status.text.equalsIgnoreCase(name)) {
                return status;
            }
        }
        return null;
    }
}