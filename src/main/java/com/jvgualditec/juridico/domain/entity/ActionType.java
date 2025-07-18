package com.jvgualditec.juridico.domain.entity;

import java.util.Arrays;

public enum ActionType {
    PETITION("Petição"),
    AUDIENCE("Audiência"),
    SENTENCE("Sentença");

    private final String description;

    ActionType(String description) {
        this.description = description;
    }

    public static ActionType fromDescription(String description) {
        return Arrays.stream(ActionType.values())
                .filter(actionType -> actionType.description.equalsIgnoreCase(description.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de ação inválido: "
                        + description + ". Valores válidos: " + Arrays.toString(ActionType.values())));
    }

    @Override
    public String toString() {
        return this.description;
    }
}
