package com.jvgualditec.juridico.domain.repository;

import com.jvgualditec.juridico.domain.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class LegalActionsRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private LegalActionsRepository actionsRepo;


    private LegalProcess createProcess() {
        LegalProcess p = new LegalProcess();
        p.setDescription("Processo Teste");
        p.setStatus(ProcessStatus.ACTIVE);
        p.setCreationDate(LocalDateTime.now());
        return em.persistAndFlush(p);
    }

    private LegalActions createAction(LegalProcess process, ActionType type, String desc) {
        LegalActions a = new LegalActions();
        a.setProcess(process);
        a.setType(type);
        a.setDescription(desc);
        a.setRegistrationDate(LocalDateTime.now());
        return em.persistAndFlush(a);
    }


    @Test
    void findByProcessId_shouldReturnPage() {
        LegalProcess p1 = createProcess();
        LegalProcess p2 = createProcess();

        LegalActions a1 = createAction(p1, ActionType.PETITION, "Ação P1-1");
        LegalActions a2 = createAction(p1, ActionType.AUDIENCE, "Ação P1-2");
        createAction(p2, ActionType.SENTENCE, "Ação P2-1");

        Pageable pg = PageRequest.of(0, 2, Sort.by("registrationDate").ascending());
        Page<LegalActions> page = actionsRepo.findByProcessId(p1.getId(), pg);

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).containsExactly(a1, a2);
    }

    @Test
    void findByProcessId_withPagination_shouldSliceCorrectly() {
        LegalProcess p = createProcess();
        LegalActions a1 = createAction(p, ActionType.PETITION, "A1");
        LegalActions a2 = createAction(p, ActionType.AUDIENCE, "A2");
        LegalActions a3 = createAction(p, ActionType.SENTENCE, "A3");

        Pageable firstPage  = PageRequest.of(0, 2, Sort.by("registrationDate"));
        Pageable secondPage = PageRequest.of(1, 2, Sort.by("registrationDate"));

        Page<LegalActions> page1 = actionsRepo.findByProcessId(p.getId(), firstPage);
        Page<LegalActions> page2 = actionsRepo.findByProcessId(p.getId(), secondPage);

        assertThat(page1.getContent()).containsExactly(a1, a2);
        assertThat(page2.getContent()).containsExactly(a3);
        assertThat(page1.getTotalElements()).isEqualTo(3);
    }

    @Test
    void findByProcessId_whenNoActions_shouldReturnEmptyPage() {
        LegalProcess p = createProcess();

        Pageable pg = PageRequest.of(0, 5);
        Page<LegalActions> page = actionsRepo.findByProcessId(p.getId(), pg);

        assertThat(page).isEmpty();
    }
}
