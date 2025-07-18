package com.jvgualditec.juridico.domain.service;

import com.jvgualditec.juridico.domain.entity.LegalProcess;
import com.jvgualditec.juridico.domain.entity.Participants;
import com.jvgualditec.juridico.domain.entity.ParticipantsType;
import com.jvgualditec.juridico.domain.entity.ProcessParticipation;

import java.util.UUID;


public interface ParticipationService {
    ProcessParticipation create(LegalProcess process, Participants participant, ParticipantsType role);

}
