package com.jvgualditec.juridico.domain.service;

import com.jvgualditec.juridico.api.dto.*;
import com.jvgualditec.juridico.domain.entity.LegalProcess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ProcessService {
    LegalProcess create(ProcessCreationDTO dto);
    LegalProcess update(Long id, UpdateProcessDTO dto);
    LegalProcess patch(Long id, PatchProcessDTO dto);
    LegalProcess getById(Long id);
    Page<LegalProcess> getAll(Pageable pageable);
    LegalProcess archive(Long id);
    LegalProcess addParticipant(Long processId, ProcessParticipationDTO participantDTO);
    Page<LegalProcess> findByParticipantCpfCnpj(String cpfCnpj, Pageable pageable);
    Page<LegalProcess> findByCreationDate(LocalDateTime creationDate, Pageable pageable);
    Page<LegalProcess> findByStatus(String label, Pageable pageable);
}
