package com.jvgualditec.juridico.api.dto;

import com.jvgualditec.juridico.domain.entity.ProcessStatus;

public record PatchProcessDTO(
        String description,
        ProcessStatus status
) {}
