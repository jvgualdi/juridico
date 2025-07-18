package com.jvgualditec.juridico.domain.service;

import com.jvgualditec.juridico.api.dto.ParticipantCreationDTO;
import com.jvgualditec.juridico.domain.entity.ContactInformation;
import com.jvgualditec.juridico.domain.entity.Participants;
import com.jvgualditec.juridico.domain.repository.ParticipantsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ParticipantServiceImpl implements ParticipantService {
    private final ParticipantsRepository repository;

    public ParticipantServiceImpl(ParticipantsRepository repo) {
        this.repository = repo;
    }

    @Override
    public Participants getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Participante não encontrado: " + id));
    }

    @Override
    public Participants getByCpfCnpj(String cpfCnpj) {
        return repository.findByCpfCnpj(cpfCnpj).orElseThrow(() ->
                new EntityNotFoundException("Participante não encontrado com CPF/CNPJ: " + cpfCnpj));
    }

    @Override
    public Participants create(ParticipantCreationDTO dto) {
        Participants participant = new Participants();
        participant.setFullName(dto.fullName());
        participant.setCpfCnpj(dto.cpfCnpj());
        var contact = new ContactInformation(dto.contact().email(), dto.contact().phoneNumber());
        participant.setContactInformation(contact);

        return repository.save(participant);
    }
}
