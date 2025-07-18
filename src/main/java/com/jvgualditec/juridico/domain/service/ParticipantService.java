package com.jvgualditec.juridico.domain.service;

import com.jvgualditec.juridico.api.dto.ParticipantCreationDTO;
import com.jvgualditec.juridico.domain.entity.Participants;

import java.util.UUID;

public interface ParticipantService {
    Participants getById(UUID participantId);
    Participants getByCpfCnpj(String cpfCnpj);
    Participants create(ParticipantCreationDTO dto);
}
