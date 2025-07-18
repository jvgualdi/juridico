package com.jvgualditec.juridico.domain.repository;

import com.jvgualditec.juridico.domain.entity.Participants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParticipantsRepository extends JpaRepository<Participants, UUID> {
    Optional<Participants> findByCpfCnpj(String cpfCnpj);
}
