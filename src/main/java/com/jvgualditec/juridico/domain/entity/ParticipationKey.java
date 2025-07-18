package com.jvgualditec.juridico.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationKey implements Serializable {

    @Column(name = "process_id", nullable = false)
    private Long processId;

    @Column(name = "participant_id", nullable = false)
    private UUID participantId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipationKey)) return false;
        ParticipationKey that = (ParticipationKey) o;
        return Objects.equals(processId, that.processId) &&
                Objects.equals(participantId, that.participantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processId, participantId);
    }
}
