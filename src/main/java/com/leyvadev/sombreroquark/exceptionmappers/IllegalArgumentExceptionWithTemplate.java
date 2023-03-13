package com.leyvadev.sombreroquark.exceptionmappers;

public class IllegalArgumentExceptionWithTemplate extends IllegalArgumentException {
    private final String template;

    public IllegalArgumentExceptionWithTemplate(String template) {
        super();
        this.template = template;
    }

    public IllegalArgumentExceptionWithTemplate(String message, String template) {
        super(message);
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

}