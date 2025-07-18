package com.jvgualditec.juridico.domain.repository;

import com.jvgualditec.juridico.domain.entity.LegalProcess;
import com.jvgualditec.juridico.domain.entity.ProcessStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface LegalProcessRepository extends JpaRepository<LegalProcess, Long> {
    Page<LegalProcess> findByStatus(ProcessStatus status, Pageable pageable);
    @Query("select lp from LegalProcess lp join lp.participations p"
            + " where p.participant.id = :participantId")
    List<LegalProcess> findByParticipantId(@Param("participantId") UUID participantId);
    @Query("""
      select lp
      from LegalProcess lp
      join lp.participations p
      join p.participant pt
      where pt.cpfCnpj = :cpfCnpj
    """)
    Page<LegalProcess> findByParticipantCpfCnpj(@Param("cpfCnpj") String cpfCnpj, Pageable pageable);

    @Query("select lp from LegalProcess lp where lp.creationDate = :creationDate")
    Page<LegalProcess> findByCreationDate(@Param("creationDate") LocalDateTime creationDate, Pageable pageable);

}
