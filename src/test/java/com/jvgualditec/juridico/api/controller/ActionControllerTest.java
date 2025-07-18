package com.jvgualditec.juridico.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvgualditec.juridico.api.dto.ActionResponseDTO;
import com.jvgualditec.juridico.api.dto.LegalActionDTO;
import com.jvgualditec.juridico.domain.entity.ActionType;
import com.jvgualditec.juridico.domain.entity.LegalActions;
import com.jvgualditec.juridico.domain.entity.LegalProcess;
import com.jvgualditec.juridico.domain.service.ActionService;
import com.jvgualditec.juridico.domain.service.ProcessService;
import com.jvgualditec.juridico.domain.mapper.ActionsMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActionController.class)
class ActionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActionService actionService;

    @MockBean
    private ProcessService processService;

    @MockBean
    private ActionsMapper actionsMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/processes/{id}/actions - success")
    void createAction_whenValid_returnsCreated() throws Exception {
        long processId = 1L;
        LegalProcess process = new LegalProcess(); process.setId(processId);
        LegalActionDTO dto = new LegalActionDTO(ActionType.PETITION, LocalDateTime.now(), "desc");
        LegalActions action = new LegalActions(); action.setId(UUID.randomUUID()); action.setProcess(process);
        ActionResponseDTO responseDto = new ActionResponseDTO(action.getId(),  dto.description(), dto.type(), dto.registrationDate());

        when(processService.getById(processId)).thenReturn(process);
        when(actionService.mapToEntity(process, dto)).thenReturn(action);
        when(actionService.save(action)).thenReturn(action);
        when(actionsMapper.toResponse(action)).thenReturn(responseDto);

        mockMvc.perform(post("/api/processes/{pid}/actions", processId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/processes/1/actions/" + action.getId()))
                .andExpect(jsonPath("$.id").value(responseDto.id().toString()));
    }

    @Test
    @DisplayName("POST /api/processes/{id}/actions - process not found")
    void createAction_whenProcessNotFound_returnsNotFound() throws Exception {
        long processId = 2L;
        LegalActionDTO dto = new LegalActionDTO(ActionType.PETITION, LocalDateTime.now(), "desc");
        when(processService.getById(processId)).thenThrow(new EntityNotFoundException("not found"));

        mockMvc.perform(post("/api/processes/{pid}/actions", processId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/processes/{id}/actions - success")
    void listActions_whenExist_returnsPage() throws Exception {
        long processId = 1L;
        Pageable pg = PageRequest.of(0, 20);
        LegalActions a = new LegalActions(); a.setId(UUID.randomUUID());
        Page<LegalActions> page = new PageImpl<>(List.of(a));
        ActionResponseDTO dto = new ActionResponseDTO(a.getId(),a.getDescription(), a.getType(), a.getRegistrationDate() );

        when(actionService.listByProcess(eq(processId), any(Pageable.class))).thenReturn(page);
        when(actionsMapper.toResponse(a)).thenReturn(dto);

        mockMvc.perform(get("/api/processes/{pid}/actions", processId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(a.getId().toString()));
    }

    @Test
    @DisplayName("GET /api/processes/{id}/actions/{aid} - success")
    void getById_whenExist_returnsAction() throws Exception {
        long processId = 1L;
        UUID actionId = UUID.randomUUID();
        LegalActions action = new LegalActions(); action.setId(actionId);
        ActionResponseDTO dto = new ActionResponseDTO(actionId,action.getDescription(), action.getType(), action.getRegistrationDate());

        when(actionService.getById(actionId)).thenReturn(action);
        when(actionsMapper.toResponse(action)).thenReturn(dto);

        mockMvc.perform(get("/api/processes/{pid}/actions/{aid}", processId, actionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(actionId.toString()));
    }

    @Test
    @DisplayName("GET /api/processes/{id}/actions/{aid} - not found")
    void getById_whenNotExist_returnsNotFound() throws Exception {
        UUID actionId = UUID.randomUUID();
        when(actionService.getById(actionId)).thenThrow(new EntityNotFoundException("no"));

        mockMvc.perform(get("/api/processes/{pid}/actions/{aid}", 1L, actionId))
                .andExpect(status().isNotFound());
    }
}
