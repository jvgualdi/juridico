package com.jvgualditec.juridico.domain.service;

import com.jvgualditec.juridico.api.dto.LegalActionDTO;
import com.jvgualditec.juridico.domain.entity.LegalActions;
import com.jvgualditec.juridico.domain.entity.LegalProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ActionService {
    LegalActions mapToEntity(LegalProcess process, LegalActionDTO dto);
    LegalActions save(LegalActions action);
    Page<LegalActions> listByProcess(Long processId, Pageable pageable);
    LegalActions getById(UUID id);

}
