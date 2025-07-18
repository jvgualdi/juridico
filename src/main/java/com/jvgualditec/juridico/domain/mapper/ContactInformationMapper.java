package com.jvgualditec.juridico.domain.mapper;

import com.jvgualditec.juridico.api.dto.ContactInformationDTO;
import com.jvgualditec.juridico.domain.entity.ContactInformation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactInformationMapper {

    ContactInformationDTO toDto(ContactInformation contact);
}
