package com.jvgualditec.juridico.domain.service;

import com.jvgualditec.juridico.api.dto.LegalActionDTO;
import com.jvgualditec.juridico.domain.entity.*;
import com.jvgualditec.juridico.domain.repository.LegalActionsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.*;

import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActionServiceImplTest {

    @Mock
    private LegalActionsRepository actionsRepository;

    @InjectMocks
    private ActionServiceImpl actionService;

    private LegalProcess dummyProcess;
    private LegalActionDTO dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dummyProcess = new LegalProcess();
        dummyProcess.setId(100L);
        dto = new LegalActionDTO(
                ActionType.AUDIENCE,
                LocalDateTime.now(),
                "Teste audiência"
        );
    }

    @Test
    @DisplayName("mapToEntity popula todos os campos corretamente")
    void mapToEntity_shouldMapAllFields() {
        LegalActions action = actionService.mapToEntity(dummyProcess, dto);

        assertEquals(dummyProcess, action.getProcess());
        assertEquals(ActionType.AUDIENCE, action.getType());
        assertEquals("Teste audiência", action.getDescription());
        assertEquals(dto.registrationDate(), action.getRegistrationDate());
    }

    @Test
    @DisplayName("save delega ao repository e retorna a entidade salva")
    void save_shouldCallRepositoryAndReturnSaved() {
        LegalActions toSave = new LegalActions();
        LegalActions saved  = new LegalActions();
        saved.setId(UUID.randomUUID());

        when(actionsRepository.save(toSave)).thenReturn(saved);

        LegalActions result = actionService.save(toSave);

        verify(actionsRepository, times(1)).save(toSave);
        assertEquals(saved, result);
    }

    @Test
    @DisplayName("listByProcess retorna página vinda do repository")
    void listByProcess_shouldReturnPageFromRepo() {
        Pageable pageable = PageRequest.of(0, 5);
        LegalActions a1 = new LegalActions();
        LegalActions a2 = new LegalActions();
        Page<LegalActions> fakePage = new PageImpl<>(List.of(a1, a2));

        when(actionsRepository.findByProcessId(100L, pageable)).thenReturn(fakePage);

        Page<LegalActions> result = actionService.listByProcess(100L, pageable);

        verify(actionsRepository, times(1)).findByProcessId(100L, pageable);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().containsAll(List.of(a1, a2)));
    }

    @Test
    @DisplayName("getById retorna a entidade quando encontrada")
    void getById_whenFound_shouldReturnEntity() {
        UUID id = UUID.randomUUID();
        LegalActions found = new LegalActions();
        found.setId(id);

        when(actionsRepository.findById(id)).thenReturn(Optional.of(found));

        LegalActions result = actionService.getById(id);

        verify(actionsRepository, times(1)).findById(id);
        assertEquals(found, result);
    }

    @Test
    @DisplayName("getById lança EntityNotFoundException quando não encontra")
    void getById_whenNotFound_shouldThrowException() {
        UUID id = UUID.randomUUID();
        when(actionsRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> actionService.getById(id)
        );
        assertTrue(ex.getMessage().contains(id.toString()));
    }
}
