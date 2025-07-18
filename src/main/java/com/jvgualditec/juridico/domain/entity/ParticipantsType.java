package com.jvgualditec.juridico.domain.entity;

import java.util.Arrays;

public enum ParticipantsType {
    AUTHOR("Autor"),
    DEFENDANT("Réu"),
    LAWYER("Advogado");

    private final String participantType;

    ParticipantsType(String participantType) {
        this.participantType = participantType;
    }

    public static ParticipantsType fromString(String type) {
        return Arrays.stream(ParticipantsType.values())
                .filter(participant -> participant.participantType.equalsIgnoreCase(type.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de participante inválido: "
                        + type + ". Valores válidos: " + Arrays.toString(ParticipantsType.values())));
    }

    @Override
    public String toString() {
        return this.participantType;
    }
}
