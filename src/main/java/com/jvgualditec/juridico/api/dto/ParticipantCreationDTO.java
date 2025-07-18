package com.jvgualditec.juridico.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ParticipantCreationDTO(@NotBlank @JsonAlias("full_name") String fullName,
                                     @NotBlank @JsonAlias("cpf_cnpj") String cpfCnpj,
                                     @NotNull ContactInformationDTO contact) {
}
