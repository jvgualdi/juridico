package com.jvgualditec.juridico.domain.mapper;

import com.jvgualditec.juridico.api.dto.ContactInformationDTO;
import com.jvgualditec.juridico.api.dto.ParticipantCreationDTO;
import com.jvgualditec.juridico.api.dto.ParticipantResponseDTO;
import com.jvgualditec.juridico.domain.entity.ContactInformation;
import com.jvgualditec.juridico.domain.entity.Participants;
import com.jvgualditec.juridico.domain.entity.ParticipantsType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ContactInformationMapper.class})
public interface ParticipantMapper {
    @Mapping(source = "contactInformation", target = "contact")
    ParticipantResponseDTO toResponse(Participants participant);


}
