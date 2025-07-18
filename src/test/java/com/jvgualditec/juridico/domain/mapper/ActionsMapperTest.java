package com.jvgualditec.juridico.domain.mapper;

import com.jvgualditec.juridico.api.dto.ActionResponseDTO;
import com.jvgualditec.juridico.domain.entity.ActionType;
import com.jvgualditec.juridico.domain.entity.LegalActions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ActionsMapperTest {

    private ActionsMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ActionsMapper.class);
    }

    @Test
    @DisplayName("toResponse: mapeia todos os campos de LegalActions para ActionResponseDTO")
    void toResponse_shouldMapAllFields() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);

        LegalActions entity = new LegalActions();
        entity.setId(id);
        entity.setDescription("descrição X");
        entity.setType(ActionType.AUDIENCE);
        entity.setRegistrationDate(now);

        ActionResponseDTO dto = mapper.toResponse(entity);

        assertEquals(id, dto.id());
        assertEquals("descrição X", dto.description());
        assertEquals(ActionType.AUDIENCE, dto.type());
        assertEquals(now, dto.registrationDate());
    }
}
