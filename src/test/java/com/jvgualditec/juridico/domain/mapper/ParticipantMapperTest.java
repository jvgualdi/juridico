package com.jvgualditec.juridico.domain.mapper;

import com.jvgualditec.juridico.api.dto.ContactInformationDTO;
import com.jvgualditec.juridico.api.dto.ParticipantResponseDTO;
import com.jvgualditec.juridico.domain.entity.ContactInformation;
import com.jvgualditec.juridico.domain.entity.Participants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ParticipantMapperTest {

    @Autowired
    private ParticipantMapper mapper;

    @SpyBean
    private ContactInformationMapper contactInformationMapper;


    @Test
    void toResponse_shouldMapFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        ContactInformation contact = new ContactInformation("email@teste.com", "11999998888");
        Participants participant = new Participants(id, "João da Silva", "12345678900", null, contact);

        ParticipantResponseDTO dto = mapper.toResponse(participant);

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals("João da Silva", dto.fullName());
        assertEquals("12345678900", dto.cpfCnpj());

        assertNotNull(dto.contact());
        assertEquals("email@teste.com", dto.contact().email());
        assertEquals("11999998888", dto.contact().phoneNumber());

        verify(contactInformationMapper).toDto(contact);
    }


    @Test
    void toResponse_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.toResponse(null));
    }
}
