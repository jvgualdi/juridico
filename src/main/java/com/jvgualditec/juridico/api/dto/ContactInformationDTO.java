package com.jvgualditec.juridico.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;

public record ContactInformationDTO(@Email String email, @JsonProperty("phone_number") String phoneNumber) {

}
