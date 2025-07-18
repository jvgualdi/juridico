package com.jvgualditec.juridico.api.dto;

import com.jvgualditec.juridico.domain.entity.ProcessStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateProcessDTO(@NotBlank String description,
                               @NotNull ProcessStatus status,
                               @NotNull List<ProcessParticipationDTO> participants,
                               List<LegalActionDTO> actions) {
}
