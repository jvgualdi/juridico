package com.jvgualditec.juridico.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvgualditec.juridico.api.dto.*;
import com.jvgualditec.juridico.domain.entity.*;
import com.jvgualditec.juridico.domain.mapper.ManualProcessMapper;
import com.jvgualditec.juridico.domain.service.ProcessService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LegalProcessController.class)
class LegalProcessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProcessService processService;

    @MockBean
    private ManualProcessMapper manualProcessMapper;


    @Test
    @DisplayName("POST /api/processes → 201 CREATED e JSON com number, description, status")
    void createProcess_whenValid_returnsCreatedAndBody() throws Exception {
        var dtoReq = new ProcessCreationDTO(
                "Caso de Teste",
                List.of(new ProcessParticipationDTO(UUID.randomUUID(), ParticipantsType.AUTHOR)),
                List.of(new LegalActionDTO(ActionType.PETITION, LocalDateTime.now(), "Petição inicial"))
        );

        var entity = new LegalProcess();
        entity.setId(42L);
        entity.setDescription(dtoReq.description());
        entity.setStatus(ProcessStatus.ACTIVE);

        var dtoRes = new ProcessDTO(
                42L,
                entity.getDescription(),
                entity.getCreationDate(),
                entity.getStatus(),
                List.of(),
                List.of()
        );

        when(processService.create(any())).thenReturn(entity);
        when(manualProcessMapper.toResponse(entity)).thenReturn(dtoRes);

        mockMvc.perform(post("/api/processes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoReq)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/processes/42"))
                .andExpect(jsonPath("$.number").value(42))
                .andExpect(jsonPath("$.description").value("Caso de Teste"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /api/processes → 404 se service lança EntityNotFoundException")
    void createProcess_whenServiceThrowsNotFound_returns404() throws Exception {
        var dtoReq = new ProcessCreationDTO("x", List.of(), List.of());
        when(processService.create(any()))
                .thenThrow(new EntityNotFoundException("Participante não encontrado"));

        mockMvc.perform(post("/api/processes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoReq)))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("GET /api/processes/{id} → 200 OK e JSON correto")
    void getProcessById_whenExists_returns200AndBody() throws Exception {
        var entity = new LegalProcess(); entity.setId(7L);
        var dtoRes = new ProcessDTO(
                7L, "Desc", entity.getCreationDate(), ProcessStatus.ACTIVE, List.of(), List.of()
        );

        when(processService.getById(7L)).thenReturn(entity);
        when(manualProcessMapper.toResponse(entity)).thenReturn(dtoRes);

        mockMvc.perform(get("/api/processes/{id}", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(7))
                .andExpect(jsonPath("$.description").value("Desc"));
    }

    @Test
    @DisplayName("GET /api/processes/{id} → 404 se não existe")
    void getProcessById_whenNotFound_returns404() throws Exception {
        when(processService.getById(5L))
                .thenThrow(new EntityNotFoundException("Não achou"));

        mockMvc.perform(get("/api/processes/{id}", 5L))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("GET /api/processes → 200 OK e página de DTOs")
    void getAllProcesses_whenCalled_returnsPage() throws Exception {
        var entity = new LegalProcess(); entity.setId(8L);
        Page<LegalProcess> pageEntity = new PageImpl<>(List.of(entity));
        ProcessDTO dto = new ProcessDTO(8L, null, LocalDateTime.now(), ProcessStatus.ACTIVE, List.of(), List.of());
        Page<ProcessDTO> pageDto = new PageImpl<>(List.of(dto));

        when(processService.getAll(any(Pageable.class))).thenReturn(pageEntity);
        when(manualProcessMapper.toResponse(entity)).thenReturn(dto);

        mockMvc.perform(get("/api/processes")
                        .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].number").value(8));
    }


    @Test
    @DisplayName("PUT /api/processes/{id} → 200 OK e campos atualizados")
    void updateProcess_whenValid_returns200AndBody() throws Exception {
        var dtoReq = new UpdateProcessDTO("NovaDesc", ProcessStatus.SUSPENDED, List.of(), List.of());
        var entity = new LegalProcess(); entity.setId(9L); entity.setDescription("NovaDesc"); entity.setStatus(ProcessStatus.SUSPENDED);
        var dtoRes = new ProcessDTO(9L, "NovaDesc", entity.getCreationDate(), ProcessStatus.SUSPENDED, List.of(), List.of());

        when(processService.update(eq(9L), any())).thenReturn(entity);
        when(manualProcessMapper.toResponse(entity)).thenReturn(dtoRes);

        mockMvc.perform(put("/api/processes/{id}", 9L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("NovaDesc"))
                .andExpect(jsonPath("$.status").value("SUSPENDED"));
    }

    @Test
    @DisplayName("PUT /api/processes/{id} → 404 se não encontrado")
    void updateProcess_whenNotFound_returns404() throws Exception {
        var dtoReq = new UpdateProcessDTO("x", ProcessStatus.ACTIVE, List.of(), List.of());
        when(processService.update(eq(10L), any()))
                .thenThrow(new EntityNotFoundException("Não existe"));

        mockMvc.perform(put("/api/processes/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoReq)))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("PATCH /api/processes/{id} → 200 OK e altera parcialmente")
    void patchProcess_whenValid_returns200AndBody() throws Exception {
        var dtoReq = new PatchProcessDTO("pDesc", ProcessStatus.ARCHIVED);
        var entity = new LegalProcess(); entity.setId(11L); entity.setDescription("pDesc"); entity.setStatus(ProcessStatus.ARCHIVED);
        var dtoRes = new ProcessDTO(11L, "pDesc", entity.getCreationDate(), ProcessStatus.ARCHIVED, List.of(), List.of());

        when(processService.patch(11L, dtoReq)).thenReturn(entity);
        when(manualProcessMapper.toResponse(entity)).thenReturn(dtoRes);

        mockMvc.perform(patch("/api/processes/{id}", 11L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));
    }


    @Test
    @DisplayName("POST /api/processes/{id}/archive → 200 OK e status ARCHIVED")
    void archiveProcess_whenExists_returns200AndBody() throws Exception {
        var entity = new LegalProcess(); entity.setId(12L); entity.setStatus(ProcessStatus.ARCHIVED);
        var dtoRes = new ProcessDTO(12L, null, entity.getCreationDate(), ProcessStatus.ARCHIVED, List.of(), List.of());

        when(processService.archive(12L)).thenReturn(entity);
        when(manualProcessMapper.toResponse(entity)).thenReturn(dtoRes);

        mockMvc.perform(post("/api/processes/{id}/archive", 12L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));
    }


    @Test
    @DisplayName("PATCH /api/processes/{id}/participants → 200 OK e inclui participante")
    void addParticipant_whenValid_returns200AndBody() throws Exception {
        var dtoReq = new ProcessParticipationDTO(UUID.randomUUID(), ParticipantsType.LAWYER);
        var entity = new LegalProcess(); entity.setId(13L);
        var dtoRes = new ProcessDTO(13L, null, entity.getCreationDate(), ProcessStatus.ACTIVE, List.of(), List.of());

        when(processService.addParticipant(13L, dtoReq)).thenReturn(entity);
        when(manualProcessMapper.toResponse(entity)).thenReturn(dtoRes);

        mockMvc.perform(patch("/api/processes/{id}/participants", 13L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(13));
    }

    @Test
    @DisplayName("GET /api/processes/search/cpf_cnpj → 200 OK e página")
    void searchByCpfCnpj_returnsPage() throws Exception {
        Page<LegalProcess> page = new PageImpl<>(List.of(new LegalProcess() {{ setId(14L); }}));
        var dtoRes = new ProcessDTO(14L, null, LocalDateTime.now(), ProcessStatus.ACTIVE, List.of(), List.of());

        when(processService.findByParticipantCpfCnpj(eq("123"), any(Pageable.class))).thenReturn(page);
        when(manualProcessMapper.toResponse(any())).thenReturn(dtoRes);

        mockMvc.perform(get("/api/processes/search/cpf_cnpj")
                        .param("cpf_cnpj", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].number").value(14));
    }


    @Test
    @DisplayName("GET /api/processes/search/creation_date → 200 OK e página")
    void searchByCreationDate_returnsPage() throws Exception {
        var now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        String dateParam = now.toString();

        Page<LegalProcess> page = new PageImpl<>(
                List.of(new LegalProcess() {{
                    setId(15L);
                    setCreationDate(now);
                }})
        );
        var dtoRes = new ProcessDTO(15L, null, now, ProcessStatus.ACTIVE, List.of(), List.of());

        when(processService.findByCreationDate(eq(now), any(Pageable.class)))
                .thenReturn(page);
        when(manualProcessMapper.toResponse(any(LegalProcess.class)))
                .thenReturn(dtoRes);

        mockMvc.perform(get("/api/processes/search/creation_date")
                        .param("creation_date", dateParam)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].creation_date").value(dateParam));
    }


    @Test
    @DisplayName("GET /api/processes/search/status → 200 OK e página")
    void searchByStatus_returnsPage() throws Exception {
        Page<LegalProcess> page = new PageImpl<>(List.of(new LegalProcess() {{ setId(16L); setStatus(ProcessStatus.SUSPENDED); }}));
        var dtoRes = new ProcessDTO(16L, null, LocalDateTime.now(), ProcessStatus.SUSPENDED, List.of(), List.of());

        when(processService.findByStatus(eq("Suspenso"), any(Pageable.class))).thenReturn(page);
        when(manualProcessMapper.toResponse(any())).thenReturn(dtoRes);

        mockMvc.perform(get("/api/processes/search/status")
                        .param("status", "Suspenso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("SUSPENDED"));
    }
}
