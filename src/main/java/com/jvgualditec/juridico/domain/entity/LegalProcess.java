package com.jvgualditec.juridico.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "legal_process")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LegalProcess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(nullable = false, name = "last_update_date")
    private LocalDateTime lastUpdateDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProcessStatus status;

    @OneToMany(
            mappedBy = "process",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProcessParticipation> participations = new ArrayList<>();

    @OneToMany(
            mappedBy = "process",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    private List<LegalActions> actions = new ArrayList<>();

}
