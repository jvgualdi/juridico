package com.jvgualditec.juridico.domain.service;

import com.jvgualditec.juridico.api.dto.*;
import com.jvgualditec.juridico.domain.entity.*;
import com.jvgualditec.juridico.domain.repository.LegalProcessRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ProcessServiceImpl Tests")
class ProcessServiceImplTest {

    @Mock
    private LegalProcessRepository processRepo;
    @Mock
    private ParticipantService participantSvc;
    @Mock
    private ParticipationService participationSvc;
    @Mock
    private ActionService actionSvc;

    @InjectMocks
    private ProcessServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("create: should create process with participants and actions")
    void create_withValidDto_createsProcess() {
        UUID partId = UUID.randomUUID();
        Participants participant = new Participants(); participant.setId(partId);
        ProcessParticipationDTO ppDto = new ProcessParticipationDTO(partId, ParticipantsType.AUTHOR);
        LegalActionDTO actionDto = new LegalActionDTO(ActionType.PETITION, LocalDateTime.now(), "desc");
        ProcessCreationDTO dto = new ProcessCreationDTO("descProc", List.of(ppDto), List.of(actionDto));

        ProcessParticipation participation = new ProcessParticipation();
        LegalActions action = new LegalActions();

        when(participantSvc.getById(partId)).thenReturn(participant);
        when(participationSvc.create(any(), eq(participant), eq(ParticipantsType.AUTHOR))).thenReturn(participation);
        when(actionSvc.mapToEntity(any(), eq(actionDto))).thenReturn(action);
        when(processRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LegalProcess result = service.create(dto);

        assertEquals("descProc", result.getDescription());
        assertEquals(ProcessStatus.ACTIVE, result.getStatus());
        assertTrue(result.getParticipations().contains(participation));
        assertTrue(result.getActions().contains(action));
    }

    @Test
    @DisplayName("create: should throw when participant not found")
    void create_whenParticipantNotFound_throwsException() {
        UUID partId = UUID.randomUUID();
        ProcessParticipationDTO ppDto = new ProcessParticipationDTO(partId, ParticipantsType.AUTHOR);
        ProcessCreationDTO dto = new ProcessCreationDTO("desc", List.of(ppDto), List.of());

        when(participantSvc.getById(partId)).thenThrow(new EntityNotFoundException("not found"));

        assertThrows(EntityNotFoundException.class, () -> service.create(dto));
    }

    @Test
    @DisplayName("update: should update existing process")
    void update_withValidDto_updatesProcess() {
        LegalProcess existing = new LegalProcess(); existing.setId(1L);
        Participants participant = new Participants(); participant.setId(UUID.randomUUID());
        ProcessParticipationDTO ppDto = new ProcessParticipationDTO(participant.getId(), ParticipantsType.DEFENDANT);
        LegalActionDTO actionDto = new LegalActionDTO(ActionType.AUDIENCE, LocalDateTime.now(), "act");
        UpdateProcessDTO dto = new UpdateProcessDTO("newDesc", ProcessStatus.SUSPENDED, List.of(ppDto), List.of(actionDto));

        ProcessParticipation participation = new ProcessParticipation();
        LegalActions action = new LegalActions();

        when(processRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(participantSvc.getById(participant.getId())).thenReturn(participant);
        when(participationSvc.create(any(), eq(participant), eq(ParticipantsType.DEFENDANT))).thenReturn(participation);
        when(actionSvc.mapToEntity(any(), eq(actionDto))).thenReturn(action);
        when(processRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LegalProcess result = service.update(1L, dto);

        assertEquals("newDesc", result.getDescription());
        assertEquals(ProcessStatus.SUSPENDED, result.getStatus());
    }

    @Test
    @DisplayName("update: should throw when process not found")
    void update_whenNotFound_throwsException() {
        UpdateProcessDTO dto = new UpdateProcessDTO("desc", ProcessStatus.ACTIVE, List.of(), List.of());
        when(processRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.update(2L, dto));
    }

    @Test
    @DisplayName("patch: should patch non-null fields")
    void patch_withValidDto_patchesProcess() {
        LegalProcess proc = new LegalProcess(); proc.setId(1L); proc.setDescription("old"); proc.setStatus(ProcessStatus.ACTIVE);
        PatchProcessDTO dto = new PatchProcessDTO("upd", ProcessStatus.ARCHIVED);

        when(processRepo.findById(1L)).thenReturn(Optional.of(proc));
        when(processRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LegalProcess result = service.patch(1L, dto);

        assertEquals("upd", result.getDescription());
        assertEquals(ProcessStatus.ARCHIVED, result.getStatus());
    }

    @Test
    @DisplayName("patch: should throw when process not found")
    void patch_whenNotFound_throwsException() {
        when(processRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.patch(1L, new PatchProcessDTO(null, null)));
    }

    @Test
    @DisplayName("getById: when exists returns process")
    void getById_whenExists_returnsProcess() {
        LegalProcess proc = new LegalProcess(); proc.setId(1L);
        when(processRepo.findById(1L)).thenReturn(Optional.of(proc));
        assertEquals(proc, service.getById(1L));
    }

    @Test
    @DisplayName("getById: when not exists throws exception")
    void getById_whenNotExists_throwsException() {
        when(processRepo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.getById(9L));
    }

    @Test
    @DisplayName("getAll: should return paginated results")
    void getAll_returnsPage() {
        Pageable pg = PageRequest.of(0,5);
        Page<LegalProcess> fake = new PageImpl<>(List.of(new LegalProcess()));
        when(processRepo.findAll(pg)).thenReturn(fake);
        assertEquals(fake, service.getAll(pg));
    }

    @Test
    @DisplayName("archive: should set status to ARCHIVED")
    void archive_withValidId_archivesProcess() {
        LegalProcess proc = new LegalProcess(); proc.setId(1L); proc.setStatus(ProcessStatus.ACTIVE);
        when(processRepo.findById(1L)).thenReturn(Optional.of(proc));
        when(processRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        LegalProcess result = service.archive(1L);
        assertEquals(ProcessStatus.ARCHIVED, result.getStatus());
    }

    @Test
    @DisplayName("archive: should throw when not found")
    void archive_whenNotFound_throwsException() {
        when(processRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.archive(1L));
    }

    @Test
    @DisplayName("addParticipant: should add participation to process")
    void addParticipant_withValidData_addsParticipation() {
        LegalProcess proc = new LegalProcess(); proc.setId(1L);
        proc.setParticipations(new ArrayList<>());
        UUID partId = UUID.randomUUID();
        Participants part = new Participants(); part.setId(partId);
        ProcessParticipationDTO dto = new ProcessParticipationDTO(partId, ParticipantsType.LAWYER);
        ProcessParticipation pp = new ProcessParticipation();

        when(processRepo.findById(1L)).thenReturn(Optional.of(proc));
        when(participantSvc.getById(partId)).thenReturn(part);
        when(participationSvc.create(proc, part, ParticipantsType.LAWYER)).thenReturn(pp);
        when(processRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LegalProcess result = service.addParticipant(1L, dto);
        assertTrue(result.getParticipations().contains(pp));
    }

    @Test
    @DisplayName("addParticipant: should throw when process not found")
    void addParticipant_whenProcessNotFound_throwsException() {
        ProcessParticipationDTO dto = new ProcessParticipationDTO(UUID.randomUUID(), ParticipantsType.AUTHOR);
        when(processRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.addParticipant(1L, dto));
    }

    @Test
    @DisplayName("findByParticipantCpfCnpj: returns matching page")
    void findByParticipantCpfCnpj_returnsPage() {
        Pageable pg = PageRequest.of(0,5);
        Page<LegalProcess> fake = new PageImpl<>(List.of(new LegalProcess()));
        when(processRepo.findByParticipantCpfCnpj("cpf", pg)).thenReturn(fake);
        assertEquals(fake, service.findByParticipantCpfCnpj("cpf", pg));
    }

    @Test
    @DisplayName("findByCreationDate: returns matching page")
    void findByCreationDate_returnsPage() {
        Pageable pg = PageRequest.of(0,5);
        LocalDateTime dt = LocalDateTime.now();
        Page<LegalProcess> fake = new PageImpl<>(List.of(new LegalProcess()));
        when(processRepo.findByCreationDate(dt, pg)).thenReturn(fake);
        assertEquals(fake, service.findByCreationDate(dt, pg));
    }

    @Test
    @DisplayName("findByStatus: returns matching page for valid label")
    void findByStatus_withValidLabel_returnsPage() {
        Pageable pg = PageRequest.of(0,5);
        Page<LegalProcess> fake = new PageImpl<>(List.of(new LegalProcess()));
        when(processRepo.findByStatus(ProcessStatus.ACTIVE, pg)).thenReturn(fake);
        assertEquals(fake, service.findByStatus("Ativo", pg));
    }

    @Test
    @DisplayName("findByStatus: should throw for invalid label")
    void findByStatus_withInvalidLabel_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> service.findByStatus("Invalid", PageRequest.of(0,5)));
    }
}
