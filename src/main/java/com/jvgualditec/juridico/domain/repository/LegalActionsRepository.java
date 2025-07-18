package com.jvgualditec.juridico.domain.repository;

import com.jvgualditec.juridico.domain.entity.LegalActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LegalActionsRepository extends JpaRepository<LegalActions, UUID> {
    Page<LegalActions> findByProcessId(Long processId, Pageable pageable);
}
