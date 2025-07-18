package com.jvgualditec.juridico.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jvgualditec.juridico.domain.entity.ParticipantsType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProcessParticipationDTO(@NotNull @JsonProperty("participant_id") UUID participantId,
                                      @NotNull ParticipantsType role) {
}
