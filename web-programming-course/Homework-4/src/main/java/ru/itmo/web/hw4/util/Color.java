package ru.itmo.web.hw4.util;

public enum Color {
    RED("color: red;"),
    GREEN("color: green;"),
    BLUE("color: blue;");


    private final String format;

    Color(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return format;
    }
}
