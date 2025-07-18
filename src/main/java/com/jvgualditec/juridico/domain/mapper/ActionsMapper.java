package com.jvgualditec.juridico.domain.mapper;

import com.jvgualditec.juridico.api.dto.ActionResponseDTO;
import com.jvgualditec.juridico.domain.entity.LegalActions;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActionsMapper {

    ActionResponseDTO toResponse(LegalActions legalAction);
}
