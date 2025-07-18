package com.jvgualditec.juridico.domain.service;

import com.jvgualditec.juridico.domain.entity.LegalProcess;
import com.jvgualditec.juridico.domain.entity.Participants;
import com.jvgualditec.juridico.domain.entity.ParticipantsType;
import com.jvgualditec.juridico.domain.entity.ProcessParticipation;
import com.jvgualditec.juridico.domain.repository.ProcessParticipationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class ParticipationServiceImpl implements ParticipationService {


    @Override
    public ProcessParticipation create(LegalProcess process, Participants participant, ParticipantsType role) {
        ProcessParticipation participation = new ProcessParticipation();
        participation.setProcess(process);
        participation.setParticipant(participant);
        participation.setRole(role);
        return participation;
    }



}
