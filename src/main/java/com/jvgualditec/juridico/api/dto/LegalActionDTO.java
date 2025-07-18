package com.jvgualditec.juridico.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jvgualditec.juridico.domain.entity.ActionType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record LegalActionDTO(@NotNull ActionType type,
                             @NotNull @JsonProperty("registration_date") LocalDateTime registrationDate,
                             String description) {
}
