package com.jvgualditec.juridico.domain.mapper;

import com.jvgualditec.juridico.api.dto.ContactInformationDTO;
import com.jvgualditec.juridico.domain.entity.ContactInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ContactInformationMapperTest {

    private  ContactInformationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ContactInformationMapper.class);
    }

    @Test
    void toDto_shouldMapFieldsCorrectly() {
        ContactInformation entity = new ContactInformation("email@test.com", "11999998888");

        ContactInformationDTO dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals("email@test.com", dto.email());
        assertEquals("11999998888", dto.phoneNumber());
    }

    @Test
    void toDto_shouldReturnNull_whenInputIsNull() {
        ContactInformationDTO dto = mapper.toDto(null);

        assertNull(dto);
    }
}
