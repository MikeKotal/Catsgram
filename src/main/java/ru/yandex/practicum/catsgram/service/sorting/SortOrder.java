package ru.yandex.practicum.catsgram.service.sorting;

public enum SortOrder {
    ASCENDING, DESCENDING;

    public static SortOrder from(String order) {
        return switch (order.toLowerCase()) {
            case "descending", "desc" -> DESCENDING;
            default -> ASCENDING;
        };
    }
}
