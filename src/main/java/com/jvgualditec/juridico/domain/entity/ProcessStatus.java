package com.jvgualditec.juridico.domain.entity;

import java.util.Arrays;

public enum ProcessStatus {
    ACTIVE("Ativo"),
    SUSPENDED("Suspenso"),
    ARCHIVED("Arquivado");


    private final String label;

    ProcessStatus(String label) {
        this.label = label;
    }

    public static ProcessStatus fromLabel(String label) {
        return Arrays.stream(ProcessStatus.values())
            .filter(status -> status.label.equalsIgnoreCase(label.trim()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Status de processo inválido: "
                    + label + ". Valores válidos: " + Arrays.toString(ProcessStatus.values())));
    }

    @Override
    public String toString() {
        return this.label;
    }
}
