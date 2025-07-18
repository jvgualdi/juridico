package com.jvgualditec.juridico.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jvgualditec.juridico.domain.entity.ActionType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActionResponseDTO (UUID id, String description, ActionType type, @JsonProperty("registration_date") LocalDateTime registrationDate) {
}
