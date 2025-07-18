package com.jvgualditec.juridico.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "process_participation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessParticipation implements Serializable {

    @EmbeddedId
    private ParticipationKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("processId")
    @JoinColumn(name = "process_id", nullable = false)
    private LegalProcess process;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("participantId")
    @JoinColumn(name = "participant_id", nullable = false)
    private Participants participant;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ParticipantsType role;

    @Column(name = "participation_date", nullable = false)
    private LocalDateTime participationDate;
}
