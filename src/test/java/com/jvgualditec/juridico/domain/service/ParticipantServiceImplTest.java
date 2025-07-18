package com.jvgualditec.juridico.domain.service;

import com.jvgualditec.juridico.api.dto.ContactInformationDTO;
import com.jvgualditec.juridico.api.dto.ParticipantCreationDTO;
import com.jvgualditec.juridico.domain.entity.ContactInformation;
import com.jvgualditec.juridico.domain.entity.Participants;
import com.jvgualditec.juridico.domain.repository.ParticipantsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipantServiceImplTest {

    @Mock
    private ParticipantsRepository participantsRepo;

    @InjectMocks
    private ParticipantServiceImpl participantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getById retorna participante quando existe")
    void getById_whenExists_shouldReturnParticipant() {
        UUID id = UUID.randomUUID();
        Participants mocked = new Participants();
        mocked.setId(id);

        when(participantsRepo.findById(id)).thenReturn(Optional.of(mocked));

        Participants result = participantService.getById(id);

        verify(participantsRepo).findById(id);
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("getById lança EntityNotFoundException quando não existe")
    void getById_whenNotExists_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(participantsRepo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> participantService.getById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    @DisplayName("getByCpfCnpj retorna participante existente")
    void getByCpfCnpj_whenExists_shouldReturnParticipant() {
        String cpf = "11122233344";
        Participants mocked = new Participants();
        mocked.setCpfCnpj(cpf);

        when(participantsRepo.findByCpfCnpj(cpf)).thenReturn(Optional.of(mocked));

        Participants result = participantService.getByCpfCnpj(cpf);

        verify(participantsRepo).findByCpfCnpj(cpf);
        assertThat(result.getCpfCnpj()).isEqualTo(cpf);
    }

    @Test
    @DisplayName("getByCpfCnpj lança EntityNotFoundException quando não existe")
    void getByCpfCnpj_whenNotExists_shouldThrow() {
        String cpf = "00011122233";
        when(participantsRepo.findByCpfCnpj(cpf)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> participantService.getByCpfCnpj(cpf))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("CPF/CNPJ");
    }

    @Test
    @DisplayName("create persiste participante e retorna salvo")
    void create_shouldSaveAndReturnParticipant() {
        var contactDto = new ContactInformationDTO("a@b.com", "1234-5678");
        var creationDto = new ParticipantCreationDTO("Nome Teste", "99988877766", contactDto);

        Participants toSave = new Participants();
        toSave.setFullName("Nome Teste");
        toSave.setCpfCnpj("99988877766");
        toSave.setContactInformation(new ContactInformation("a@b.com", "1234-5678"));

        Participants saved = new Participants();
        saved.setId(UUID.randomUUID());
        saved.setFullName(toSave.getFullName());
        saved.setCpfCnpj(toSave.getCpfCnpj());
        saved.setContactInformation(toSave.getContactInformation());

        when(participantsRepo.save(any(Participants.class))).thenReturn(saved);

        Participants result = participantService.create(creationDto);

        ArgumentCaptor<Participants> captor = ArgumentCaptor.forClass(Participants.class);
        verify(participantsRepo).save(captor.capture());

        Participants passed = captor.getValue();
        assertThat(passed.getFullName()).isEqualTo("Nome Teste");
        assertThat(passed.getCpfCnpj()).isEqualTo("99988877766");
        assertThat(passed.getContactInformation().getEmail()).isEqualTo("a@b.com");

        assertThat(result).isSameAs(saved);
    }
}
