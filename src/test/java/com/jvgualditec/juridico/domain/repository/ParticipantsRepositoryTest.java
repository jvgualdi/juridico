package com.jvgualditec.juridico.domain.repository;

import com.jvgualditec.juridico.domain.entity.ContactInformation;
import com.jvgualditec.juridico.domain.entity.Participants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ParticipantsRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ParticipantsRepository participantsRepo;

    private Participants persistParticipant(String cpfCnpj) {
        Participants p = new Participants();
        p.setFullName("Teste Nome");
        p.setCpfCnpj(cpfCnpj);
        p.setContactInformation(new ContactInformation("teste@ex.com", "9999-9999"));
        return em.persistAndFlush(p);
    }

    @Test
    void findByCpfCnpj_shouldReturnParticipant() {
        Participants persisted = persistParticipant("12345678900");

        Optional<Participants> found = participantsRepo.findByCpfCnpj("12345678900");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(persisted.getId());
        assertThat(found.get().getFullName()).isEqualTo("Teste Nome");
    }

    @Test
    void findByCpfCnpj_whenNotExists_shouldReturnEmptyOptional() {

        Optional<Participants> found = participantsRepo.findByCpfCnpj("00000000000");

        assertThat(found).isEmpty();
    }
}
