package com.jvgualditec.juridico.api.controller;

import com.jvgualditec.juridico.api.dto.ActionResponseDTO;
import com.jvgualditec.juridico.api.dto.LegalActionDTO;
import com.jvgualditec.juridico.api.dto.ProcessDTO;
import com.jvgualditec.juridico.domain.entity.LegalActions;
import com.jvgualditec.juridico.domain.entity.LegalProcess;
import com.jvgualditec.juridico.domain.service.ActionService;
import com.jvgualditec.juridico.domain.mapper.ActionsMapper;
import com.jvgualditec.juridico.domain.service.ProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;


@RestController
@RequestMapping("/api/processes/{processId}/actions")
public class ActionController {

    private final ActionService actionService;
    private final ProcessService processService;
    private final ActionsMapper actionsMapper;

    public ActionController(ActionService actionService, ProcessService processService,
                            ActionsMapper actionsMapper) {
        this.actionService   = actionService;
        this.processService = processService;
        this.actionsMapper   = actionsMapper;
    }


    @PostMapping
    @Operation(
            summary = "Cria uma nova ação legal",
            description = "Recebe um DTO de criação e retorna a ação persistida com ID gerado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ação criada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProcessDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos no payload"),
            @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    })
    public ResponseEntity<ActionResponseDTO> create(
            @PathVariable("processId") Long processId,
            @Valid @RequestBody LegalActionDTO dto
    ) {
        LegalProcess process = processService.getById(processId);
        LegalActions action = actionService.mapToEntity(process, dto);
        var created = actionService.save(action);

        ActionResponseDTO response = actionsMapper.toResponse(created);

        URI location = URI.create(
                String.format("/api/processes/%d/actions/%s", processId, response.id())
        );
        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping
    @Operation(
            summary = "Busca todas as ações de um processo",
            description = "Recebe um Pageable (opcional) e retorna uma lista paginada de ações."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Página de ações retornada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ActionResponseDTO>> listActionsByProcess(@PageableDefault(size = 20) Pageable pageable,
            @PathVariable Long processId
    ) {
        Page<LegalActions> page = actionService.listByProcess(processId, pageable);
        Page<ActionResponseDTO> dtoPage = page.map(actionsMapper::toResponse);
        return ResponseEntity.ok(dtoPage);
    }


    @GetMapping("/{actionId}")
    @Operation(summary = "Consulta a ação por ID",
            description = "Recupera informações detalhadas de uma ação existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ação encontrada",
                    content = @Content(schema = @Schema(implementation = ProcessDTO.class))),
            @ApiResponse(responseCode = "404", description = "Ação ou Processo não encontrados")
    })
    public ResponseEntity<ActionResponseDTO> getById(
            @PathVariable("actionId") UUID actionId
    ) {
        LegalActions action = actionService.getById(actionId);
        return ResponseEntity.ok(actionsMapper.toResponse(action));
    }

}
