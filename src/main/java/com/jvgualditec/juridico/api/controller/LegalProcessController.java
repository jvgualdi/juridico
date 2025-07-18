package com.jvgualditec.juridico.api.controller;

import com.jvgualditec.juridico.api.dto.*;
import com.jvgualditec.juridico.domain.entity.LegalProcess;
import com.jvgualditec.juridico.domain.mapper.ManualProcessMapper;
import com.jvgualditec.juridico.domain.service.ProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/processes")
@Tag(name = "Processos", description = "Operações CRUD sobre Processos Judiciais")
public class LegalProcessController {

    private final ProcessService processService;
    private final ManualProcessMapper manualProcessMapper;

    public LegalProcessController(ProcessService processService, ManualProcessMapper mapper) {
        this.processService = processService;
        this.manualProcessMapper = mapper;
    }

    @PostMapping
    @Operation(
            summary = "Cria um novo Processo",
            description = "Recebe um DTO de criação e retorna o Processo persistido com ID gerado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Processo criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProcessDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos no payload"),
            @ApiResponse(responseCode = "404", description = "Participante não encontrado")
    })
    public ResponseEntity<ProcessDTO> create(
            @Valid @RequestBody ProcessCreationDTO dto
    ) {
        LegalProcess created = processService.create(dto);
        ProcessDTO response = manualProcessMapper.toResponse(created);
        return ResponseEntity
                .created(URI.create("/api/processes/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "Busca todos os Processos",
            description = "Recebe um Pageable (opcional) e retorna uma lista paginada de Processos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de Processos retornada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ProcessDTO>> getAll(Pageable pageable) {
        Page<LegalProcess> all = processService.getAll(pageable);
        Page<ProcessDTO> dtos = all.map(manualProcessMapper::toResponse);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta Processo por ID",
            description = "Recupera informações detalhadas de um Processo existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Processo encontrado",
                    content = @Content(schema = @Schema(implementation = ProcessDTO.class))),
            @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    })
    public ResponseEntity<ProcessDTO> getById(
            @Parameter(description = "ID do Processo", required = true, example = "1") @PathVariable Long id
    ) {
        LegalProcess p = processService.getById(id);
        ProcessDTO dto = manualProcessMapper.toResponse(p);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Atualiza Processo",
            description = "Substitui todos os campos do Processo identificado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Processo atualizado",
                    content = @Content(schema = @Schema(implementation = ProcessDTO.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProcessDTO> update(
            @Parameter(description = "ID do Processo", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateProcessDTO dto
    ) {
        LegalProcess updated = processService.update(id, dto);
        return ResponseEntity.ok(manualProcessMapper.toResponse(updated));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualiza parcialmente Processo",
            description = "Altera campos específicos do Processo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Processo parcialmente atualizado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProcessDTO.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    })
    public ResponseEntity<ProcessDTO> partiallyUpdateProcess(
            @Parameter(description = "ID do Processo", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody PatchProcessDTO dto
    ) {
        LegalProcess patched = processService.patch(id, dto);
        return ResponseEntity.ok(manualProcessMapper.toResponse(patched));
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Arquiva Processo",
            description = "Altera status do Processo para 'Arquivado'.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Processo arquivado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProcessDTO.class))),
            @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    })
    public ResponseEntity<ProcessDTO> archive(
            @Parameter(description = "ID do Processo", required = true, example = "1") @PathVariable Long id
    ) {
        LegalProcess archived = processService.archive(id);
        return ResponseEntity.ok(manualProcessMapper.toResponse(archived));
    }

    @PatchMapping("/{id}/participants")
    @Operation(summary = "Adiciona participante ao Processo",
            description = "Inclui um novo participante ao Processo existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Participante adicionado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProcessDTO.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido"),
            @ApiResponse(responseCode = "404", description = "Processo ou Participante não encontrado")
    })
    public ResponseEntity<ProcessDTO> addParticipant(
            @Parameter(description = "ID do Processo", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody ProcessParticipationDTO dto
    ) {
        LegalProcess updated = processService.addParticipant(id, dto);
        return ResponseEntity.ok(manualProcessMapper.toResponse(updated));
    }

    @GetMapping("/search/cpf_cnpj")
    @Operation(summary = "Busca Processos por CPF/CNPJ",
            description = "Retorna página de Processos que contêm o CPF/CNPJ informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página retornada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ProcessDTO>> searchByCpfCnpj(Pageable pageable,
        @Parameter(description = "CPF ou CNPJ do participante", example = "12345678901")
        @RequestParam("cpf_cnpj") String cpfCnpj
    ) {
        Page<ProcessDTO> dtos = processService
                .findByParticipantCpfCnpj(cpfCnpj, pageable)
                .map(manualProcessMapper::toResponse);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search/creation_date")
    @Operation(summary = "Busca Processos por data de criação",
            description = "Retorna página de Processos criados em data específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página retornada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ProcessDTO>> searchByCreationDate(Pageable pageable,
            @Parameter(description = "Data de criação no formato ISO", example = "2025-07-17T14:00:00")
            @RequestParam("creation_date")LocalDateTime creationDate
            ) {
        Page<ProcessDTO> dtos = processService
                .findByCreationDate(creationDate,  pageable)
                .map(manualProcessMapper::toResponse);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search/status")
    @Operation(summary = "Busca Processos por status",
            description = "Retorna página de Processos filtrados pelo status informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página retornada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ProcessDTO>> searchByStatus(Pageable pageable,
        @Parameter(description = "Status do processo", example = "Ativo")
        @RequestParam("status")String status
    ) {
        Page<ProcessDTO> dtos = processService
                .findByStatus(status, pageable)
                .map(manualProcessMapper::toResponse);
        return ResponseEntity.ok(dtos);
    }
}


