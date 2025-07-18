package com.jvgualditec.juridico.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jvgualditec.juridico.domain.entity.ProcessStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ProcessDTO(@JsonProperty("number") Long id,
                         String description,
                         @JsonProperty("creation_date")LocalDateTime  creationDate,
                         ProcessStatus status,
                         List<ProcessParticipantDTO> participants,
                         List<ActionResponseDTO> actions) {
}
