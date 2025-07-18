package com.jvgualditec.juridico.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProcessCreationDTO(@NotBlank String description,
                                 @NotNull List<ProcessParticipationDTO> participants,
                                 @NotNull List<LegalActionDTO> actions ) {
}
