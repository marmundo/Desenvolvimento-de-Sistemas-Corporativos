package br.edu.ifrn.petshop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.petshop.dto.PetRequestDTO;
import br.edu.ifrn.petshop.dto.PetResponseDTO;
import br.edu.ifrn.petshop.exception.PetNaoEncontradoException;
import br.edu.ifrn.petshop.model.Pet;
import br.edu.ifrn.petshop.repository.PetRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository repository;

    @Transactional(readOnly = true)
    public List<PetResponseDTO> listar() {
        return repository.findAll().stream().map(PetResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public PetResponseDTO buscar(Long id) {
        return PetResponseDTO.from(buscarEntidade(id));
    }

    @Transactional
    public PetResponseDTO criar(PetRequestDTO dto) {
        Pet pet = new Pet(null, dto.nome(), dto.especie(), dto.raca(), dto.idade());
        return PetResponseDTO.from(repository.save(pet));
    }

    @Transactional
    public PetResponseDTO atualizar(Long id, PetRequestDTO dto) {
        Pet pet = buscarEntidade(id);
        pet.setNome(dto.nome());
        pet.setEspecie(dto.especie());
        pet.setRaca(dto.raca());
        pet.setIdade(dto.idade());
        return PetResponseDTO.from(repository.save(pet));
    }

    @Transactional
    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new PetNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }

    private Pet buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new PetNaoEncontradoException(id));
    }
}
