package com.jvgualditec.juridico.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContactInformation implements Serializable {

    private String email;
    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;

}
