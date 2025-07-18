package com.jvgualditec.juridico.domain.repository;

import com.jvgualditec.juridico.domain.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class LegalProcessRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private LegalProcessRepository processRepo;

    private LegalProcess createProcess(ProcessStatus status, LocalDateTime creationDate) {
        LegalProcess p = new LegalProcess();
        p.setStatus(status);
        p.setCreationDate(creationDate);
        em.persistAndFlush(p);
        return p;
    }

    private Participants createParticipant(String cpfCnpj) {
        Participants pt = new Participants();
        pt.setFullName("Nome Teste");
        pt.setCpfCnpj(cpfCnpj);
        pt.setContactInformation(new ContactInformation("x@x.com", "0000-0000"));
        em.persistAndFlush(pt);
        return pt;
    }

    private ProcessParticipation linkParticipant(LegalProcess process, Participants participant, ParticipantsType role) {
        ParticipationKey key = new ParticipationKey(process.getId(), participant.getId());
        ProcessParticipation pp = new ProcessParticipation();
        pp.setId(key);
        pp.setProcess(process);
        pp.setParticipant(participant);
        pp.setRole(role);
        pp.setParticipationDate(LocalDateTime.now());
        em.persistAndFlush(pp);
        return pp;
    }

    @Test
    void findByStatus_withPageable_shouldReturnPage() {
        LegalProcess p1 = createProcess(ProcessStatus.ACTIVE, LocalDateTime.now());
        createProcess(ProcessStatus.ACTIVE, LocalDateTime.now());
        Pageable pg = PageRequest.of(0, 1, Sort.by("id"));

        Page<LegalProcess> page = processRepo.findByStatus(ProcessStatus.ACTIVE, pg);

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).contains(p1);
    }

    @Test
    void findByStatus_withPageable_whenNoneMatch_shouldReturnEmptyPage() {
        Pageable pg = PageRequest.of(0, 5);
        Page<LegalProcess> page = processRepo.findByStatus(ProcessStatus.SUSPENDED, pg);
        assertThat(page).isEmpty();
    }


    @Test
    void findByParticipantCpfCnpj_withPageable_shouldReturnPage() {
        LegalProcess proc1 = createProcess(ProcessStatus.ACTIVE, LocalDateTime.now());
        createProcess(ProcessStatus.ACTIVE, LocalDateTime.now());

        Participants pt = createParticipant("99999999999");
        linkParticipant(proc1, pt, ParticipantsType.AUTHOR);

        Pageable pg = PageRequest.of(0, 10);
        Page<LegalProcess> page = processRepo.findByParticipantCpfCnpj(pt.getCpfCnpj(), pg);

        assertThat(page.getContent()).containsExactly(proc1);
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findByParticipantCpfCnpj_withPageable_whenNoLink_shouldReturnEmpty() {
        createProcess(ProcessStatus.ACTIVE, LocalDateTime.now());
        createParticipant("88888888888");

        Pageable pg = PageRequest.of(0, 2);
        Page<LegalProcess> page = processRepo.findByParticipantCpfCnpj("88888888888", pg);

        assertThat(page).isEmpty();
    }

    @Test
    void findByCreationDate_shouldReturnPage() {
        LocalDateTime dt = LocalDateTime.now();
        createProcess(ProcessStatus.ACTIVE, dt.minusHours(1));
        LegalProcess p1 = createProcess(ProcessStatus.ACTIVE, dt);
        createProcess(ProcessStatus.SUSPENDED, dt.plusHours(1));

        Pageable pg = PageRequest.of(0, 10, Sort.by("id"));
        Page<LegalProcess> page = processRepo.findByCreationDate(dt, pg);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).containsExactly(p1);
    }

    @Test
    void findByCreationDate_whenNoMatch_shouldReturnEmptyPage() {
        createProcess(ProcessStatus.ACTIVE, LocalDateTime.now().minusYears(1));

        Pageable pg = PageRequest.of(0,5);
        Page<LegalProcess> page = processRepo.findByCreationDate(LocalDateTime.now(), pg);

        assertThat(page).isEmpty();
    }
}