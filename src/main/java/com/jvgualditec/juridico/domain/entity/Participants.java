package com.jvgualditec.juridico.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "participants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participants {

    @Id
    @GeneratedValue
    @Column(name = "participant_id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, name = "full_name")
    private String fullName;

    @Column(nullable = false, name = "cpf_cnpj", length = 20)
    private String cpfCnpj;

    @OneToMany(
            mappedBy = "participant",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProcessParticipation> participations = new ArrayList<>();

    @Embedded
    private ContactInformation contactInformation;
}
