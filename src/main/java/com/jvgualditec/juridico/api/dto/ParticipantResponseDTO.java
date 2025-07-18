package com.jvgualditec.juridico.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ParticipantResponseDTO(
        UUID id,
        @JsonProperty("full_name") String fullName,
        @JsonProperty("cpf_cnpj") String cpfCnpj,
        ContactInformationDTO contact) {
}
