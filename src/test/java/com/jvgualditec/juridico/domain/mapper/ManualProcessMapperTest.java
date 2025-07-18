package com.jvgualditec.juridico.domain.mapper;

import com.jvgualditec.juridico.api.dto.ActionResponseDTO;
import com.jvgualditec.juridico.api.dto.ContactInformationDTO;
import com.jvgualditec.juridico.api.dto.ProcessDTO;
import com.jvgualditec.juridico.api.dto.ProcessParticipantDTO;
import com.jvgualditec.juridico.domain.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManualProcessMapperTest {

    @Mock
    private ContactInformationMapper contactInformationMapper;

    @Mock
    private ActionsMapper actionsMapper;

    @InjectMocks
    private ManualProcessMapper manualProcessMapper;

    @Test
    void testToResponse_successfullyMapsLegalProcessToDto() {
        UUID participantId = UUID.randomUUID();
        UUID actionId = UUID.randomUUID();
        LocalDateTime creationDate = LocalDateTime.now();

        ContactInformation contactInfo = new ContactInformation();
        ContactInformationDTO contactDTO = new ContactInformationDTO("email@test.com", "999999999");

        Participants participant = new Participants();
        participant.setId(participantId);
        participant.setFullName("João da Silva");
        participant.setCpfCnpj("12345678901");
        participant.setContactInformation(contactInfo);

        LegalProcess process = new LegalProcess();
        process.setId(1L);
        process.setDescription("Processo de teste");
        process.setCreationDate(creationDate);
        process.setStatus(ProcessStatus.ACTIVE);

        ProcessParticipation participation = new ProcessParticipation();
        participation.setRole(ParticipantsType.DEFENDANT);
        participation.setParticipant(participant);
        participation.setProcess(process);

        LegalActions action = new LegalActions();
        action.setId(actionId);
        action.setDescription("Audiência marcada");
        action.setType(ActionType.PETITION);
        action.setRegistrationDate(LocalDateTime.now());
        action.setProcess(process);

        process.setParticipations(List.of(participation));
        process.setActions(List.of(action));

        ActionResponseDTO actionDTO = new ActionResponseDTO(actionId, "Audiência marcada", ActionType.PETITION, action.getRegistrationDate());

        when(contactInformationMapper.toDto(contactInfo)).thenReturn(contactDTO);
        when(actionsMapper.toResponse(action)).thenReturn(actionDTO);

        ProcessDTO result = manualProcessMapper.toResponse(process);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Processo de teste", result.description());
        assertEquals(creationDate, result.creationDate());
        assertEquals(ProcessStatus.ACTIVE, result.status());

        assertEquals(1, result.participants().size());
        ProcessParticipantDTO participantDTO = result.participants().get(0);
        assertEquals(participantId, participantDTO.id());
        assertEquals("João da Silva", participantDTO.fullName());
        assertEquals("12345678901", participantDTO.cpfCnpj());
        assertEquals(ParticipantsType.DEFENDANT, participantDTO.role());
        assertEquals(contactDTO, participantDTO.contact());

        assertEquals(1, result.actions().size());
        ActionResponseDTO returnedAction = result.actions().get(0);
        assertEquals(actionDTO, returnedAction);

        verify(contactInformationMapper, times(1)).toDto(contactInfo);
        verify(actionsMapper, times(1)).toResponse(action);
    }

    @Test
    void testToResponse_whenNoParticipantsAndNoActions_returnsEmptyLists() {
        LegalProcess process = new LegalProcess();
        process.setId(999L);
        process.setDescription("Processo vazio");
        process.setCreationDate(LocalDateTime.now());
        process.setStatus(ProcessStatus.ARCHIVED);
        process.setParticipations(Collections.emptyList());
        process.setActions(Collections.emptyList());

        ProcessDTO result = manualProcessMapper.toResponse(process);

        assertNotNull(result);
        assertEquals(999L, result.id());
        assertEquals("Processo vazio", result.description());
        assertTrue(result.participants().isEmpty());
        assertTrue(result.actions().isEmpty());
    }

    @Test
    void testToResponse_mapsCorrectRoleFromProcessParticipation() {
        Participants participant = new Participants();
        participant.setId(UUID.randomUUID());
        participant.setFullName("Maria");
        participant.setCpfCnpj("98765432100");
        ContactInformation contactInfo = new ContactInformation();
        participant.setContactInformation(contactInfo);

        ProcessParticipation participation = new ProcessParticipation();
        participation.setParticipant(participant);
        participation.setRole(ParticipantsType.LAWYER);

        LegalProcess process = new LegalProcess();
        process.setId(100L);
        process.setDescription("Verifica Role");
        process.setCreationDate(LocalDateTime.now());
        process.setStatus(ProcessStatus.ACTIVE);
        process.setParticipations(List.of(participation));
        process.setActions(Collections.emptyList());

        ContactInformationDTO contactDTO = new ContactInformationDTO("lawyer@test.com", "55555");
        when(contactInformationMapper.toDto(contactInfo)).thenReturn(contactDTO);

        ProcessDTO result = manualProcessMapper.toResponse(process);

        ProcessParticipantDTO dto = result.participants().get(0);
        assertEquals(ParticipantsType.LAWYER, dto.role());
        assertEquals(contactDTO, dto.contact());
    }
}
