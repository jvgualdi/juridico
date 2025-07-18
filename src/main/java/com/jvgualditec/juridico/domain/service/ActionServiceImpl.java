package com.jvgualditec.juridico.domain.service;

import com.jvgualditec.juridico.api.dto.LegalActionDTO;
import com.jvgualditec.juridico.domain.entity.ActionType;
import com.jvgualditec.juridico.domain.entity.LegalActions;
import com.jvgualditec.juridico.domain.entity.LegalProcess;
import com.jvgualditec.juridico.domain.repository.LegalActionsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ActionServiceImpl implements ActionService {

    private final LegalActionsRepository actionsRepository;

    public ActionServiceImpl(LegalActionsRepository actionsRepo) {
        this.actionsRepository = actionsRepo;
    }

    @Override
    public LegalActions mapToEntity(LegalProcess process, LegalActionDTO dto) {
        LegalActions action = new LegalActions();
        action.setProcess(process);
        action.setType(dto.type());
        action.setDescription(dto.description());
        action.setRegistrationDate(dto.registrationDate());
        return action;
    }

    @Override
    @Transactional
    public LegalActions save(LegalActions action) {
        return actionsRepository.save(action);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LegalActions> listByProcess(Long processId, Pageable pageable) {
        return actionsRepository.findByProcessId(processId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public LegalActions getById(UUID id) {
        return actionsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Action not found with id: " + id));
    }


}