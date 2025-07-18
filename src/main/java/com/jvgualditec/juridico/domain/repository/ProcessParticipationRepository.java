package com.jvgualditec.juridico.domain.repository;

import com.jvgualditec.juridico.domain.entity.ParticipationKey;
import com.jvgualditec.juridico.domain.entity.ProcessParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessParticipationRepository extends JpaRepository<ProcessParticipation, ParticipationKey> {
}
