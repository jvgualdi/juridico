package com.jvgualditec.juridico.domain.service;

import com.jvgualditec.juridico.api.dto.*;
import com.jvgualditec.juridico.domain.entity.*;
import com.jvgualditec.juridico.domain.repository.LegalProcessRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProcessServiceImpl implements ProcessService{

    private final LegalProcessRepository    processRepo;
    private final ParticipantService        participantSvc;
    private final ParticipationService      participationSvc;
    private final ActionService             actionSvc;

    public ProcessServiceImpl(
            LegalProcessRepository processRepo,
            ParticipantService participantSvc,
            ParticipationService participationSvc,
            ActionService actionSvc
    ) {
        this.processRepo      = processRepo;
        this.participantSvc   = participantSvc;
        this.participationSvc = participationSvc;
        this.actionSvc        = actionSvc;
    }

    @Override
    @Transactional
    public LegalProcess create(ProcessCreationDTO dto) {
        LegalProcess process = new LegalProcess();
        process.setDescription(dto.description());
        process.setStatus(ProcessStatus.ACTIVE);

        List<ProcessParticipation> parts = dto.participants().stream()
                .map(ppDto -> {
                    Participants part = participantSvc.getById(ppDto.participantId());
                    return participationSvc.create(process, part, ppDto.role());
                })
                .toList();

        process.setParticipations(parts);

        List<LegalActions> actions = dto.actions().stream()
                .map(actionDto -> actionSvc.mapToEntity(process, actionDto))
                .toList();

        process.setActions(actions);

        return processRepo.save(process);
    }

    @Override
    @Transactional
    public LegalProcess update(Long id, UpdateProcessDTO dto) {
        LegalProcess process = getById(id);
        process.setDescription(dto.description());
        process.setStatus(dto.status());
        process.setLastUpdateDate(LocalDateTime.now());

        List<ProcessParticipation> newParticipation = dto.participants().stream()
                .map(ppDto -> {
                    Participants participants = participantSvc.getById(ppDto.participantId());
                    return participationSvc.create(process, participants, ppDto.role());
                }).toList();

        process.setParticipations(newParticipation);

        List<LegalActions> newActions = dto.actions().stream()
                .map(actionDto -> actionSvc.mapToEntity(process, actionDto))
                .toList();

        process.setActions(newActions);

        return processRepo.save(process);
    }

    @Override
    @Transactional
    public LegalProcess patch(Long id, PatchProcessDTO dto) {
        LegalProcess process = getById(id);
        if (dto.description() != null) process.setDescription(dto.description());
        if (dto.status() != null)      process.setStatus(dto.status());
        return processRepo.save(process);
    }

    @Override
    @Transactional(readOnly = true)
    public LegalProcess getById(Long id) {
        return processRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LegalProcess> getAll(Pageable pageable) {
        return processRepo.findAll(pageable);
    }

    @Override
    @Transactional
    public LegalProcess archive(Long id) {
        LegalProcess process = getById(id);
        process.setStatus(ProcessStatus.ARCHIVED);
        return processRepo.save(process);
    }

    @Override
    @Transactional
    public LegalProcess addParticipant(Long processId, ProcessParticipationDTO participantDTO) {
        LegalProcess process = getById(processId);
        Participants participant = participantSvc.getById(participantDTO.participantId());
        var participation = participationSvc.create(
                process,
                participant,
                participantDTO.role()
        );
        process.getParticipations().add(participation);

        return processRepo.save(process);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LegalProcess> findByParticipantCpfCnpj(String cpfCnpj, Pageable pageable) {
        return processRepo.findByParticipantCpfCnpj(cpfCnpj, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LegalProcess> findByCreationDate(LocalDateTime creationDate, Pageable pageable) {
        return processRepo.findByCreationDate(creationDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LegalProcess> findByStatus(String label, Pageable pageable) {
        var status = ProcessStatus.fromLabel(label);
        return processRepo.findByStatus(status, pageable);
    }

}
