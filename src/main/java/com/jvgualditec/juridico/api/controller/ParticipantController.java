package com.jvgualditec.juridico.api.controller;

import com.jvgualditec.juridico.api.dto.ParticipantCreationDTO;
import com.jvgualditec.juridico.api.dto.ParticipantResponseDTO;
import com.jvgualditec.juridico.api.dto.ProcessDTO;
import com.jvgualditec.juridico.domain.entity.Participants;
import com.jvgualditec.juridico.domain.mapper.ParticipantMapper;
import com.jvgualditec.juridico.domain.service.ParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/participants")
public class ParticipantController {

    private final ParticipantService participantService;
    private final ParticipantMapper participantMapper;

    public ParticipantController(ParticipantService participantService, ParticipantMapper participantMapper) {
        this.participantService = participantService;
        this.participantMapper = participantMapper;
    }

    @PostMapping
    @Operation(
            summary = "Cria uma nova pessoa participante",
            description = "Recebe um DTO de criação e retorna o participante persistido com ID gerado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Participante criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProcessDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos no payload")
    })
    public ResponseEntity<ParticipantResponseDTO> create(
            @Valid @RequestBody ParticipantCreationDTO dto
    ) {
        Participants created = participantService.create(dto);
        ParticipantResponseDTO response = participantMapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/participants/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta pessoa por ID",
            description = "Recupera informações detalhadas de um participante existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participante encontrado",
                    content = @Content(schema = @Schema(implementation = ProcessDTO.class))),
            @ApiResponse(responseCode = "404", description = "Participante não encontrado")
    })
    public ResponseEntity<ParticipantResponseDTO> getById(
            @PathVariable UUID id
    ) {
        Participants participant = participantService.getById(id);
        return ResponseEntity.ok(participantMapper.toResponse(participant));
    }

    @GetMapping("/cpf/{cpfCnpj}")
    @Operation(
            summary = "Busca per CPF ou CNPJ",
            description = "Recebe as informações detalhadas do participante com base no CPF ou CNPJ fornecido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participante encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<ParticipantResponseDTO> getByCpf(
            @Parameter(description = "CPF ou CNPJ do participante", required = true, example = "99999999999")
            @PathVariable String cpfCnpj
    ) {
        Participants participant = participantService.getByCpfCnpj(cpfCnpj);
        return ResponseEntity.ok(participantMapper.toResponse(participant));
    }

}