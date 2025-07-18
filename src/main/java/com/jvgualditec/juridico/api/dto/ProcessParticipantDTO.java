package com.jvgualditec.juridico.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jvgualditec.juridico.domain.entity.ParticipantsType;

import java.util.UUID;

public record ProcessParticipantDTO(
        UUID id,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("cpf_cnpj") String cpfCnpj,
        ParticipantsType role,
        ContactInformationDTO contact
) {}
