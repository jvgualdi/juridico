package com.jvgualditec.juridico.domain.mapper;


import com.jvgualditec.juridico.api.dto.ProcessDTO;
import com.jvgualditec.juridico.api.dto.ProcessParticipantDTO;
import com.jvgualditec.juridico.domain.entity.LegalProcess;
import com.jvgualditec.juridico.domain.entity.Participants;
import com.jvgualditec.juridico.domain.entity.ProcessParticipation;
import org.springframework.stereotype.Component;

@Component
public class ManualProcessMapper {

    private final ContactInformationMapper contactInformationMapper;
    private final ActionsMapper actionsMapper;

    public ManualProcessMapper(ContactInformationMapper contactInformationMapper, ActionsMapper actionsMapper) {
        this.contactInformationMapper = contactInformationMapper;
        this.actionsMapper = actionsMapper;
    }

    public ProcessDTO toResponse(LegalProcess process) {
        return new ProcessDTO(
                process.getId(),
                process.getDescription(),
                process.getCreationDate(),
                process.getStatus(),
                process.getParticipations().stream()
                        .map(this::mapParticipationToDto)
                        .toList(),
                process.getActions().stream()
                        .map(actionsMapper::toResponse)
                        .toList()
        );
    }

    private ProcessParticipantDTO mapParticipationToDto(ProcessParticipation participation) {
        Participants participant = participation.getParticipant();
        return new ProcessParticipantDTO(
                participant.getId(),
                participant.getFullName(),
                participant.getCpfCnpj(),
                participation.getRole(),
                contactInformationMapper.toDto(participant.getContactInformation())
        );
    }
}
