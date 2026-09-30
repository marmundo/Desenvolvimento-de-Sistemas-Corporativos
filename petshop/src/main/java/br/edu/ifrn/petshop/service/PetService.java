package br.edu.ifrn.petshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifrn.petshop.dto.PetRequestDTO;
import br.edu.ifrn.petshop.dto.PetResponseDTO;
import br.edu.ifrn.petshop.exception.PetNaoEncontradoException;
import br.edu.ifrn.petshop.model.Pet;
import br.edu.ifrn.petshop.repository.PetRepository;

@Service
public class PetService {

    private final PetRepository repository;

    public PetService(PetRepository repository) {
        this.repository = repository;
    }

    public List<PetResponseDTO> listar() {
        return repository.listar().stream().map(PetResponseDTO::from).toList();
    }

    public PetResponseDTO buscar(Long id) {
        return PetResponseDTO.from(buscarEntidade(id));
    }

    public PetResponseDTO criar(PetRequestDTO dto) {
        Pet pet = new Pet(null, dto.nome(), dto.especie(), dto.raca(), dto.idade());
        return PetResponseDTO.from(repository.salvar(pet));
    }

    public PetResponseDTO atualizar(Long id, PetRequestDTO dto) {
        Pet pet = buscarEntidade(id);
        pet.setNome(dto.nome());
        pet.setEspecie(dto.especie());
        pet.setRaca(dto.raca());
        pet.setIdade(dto.idade());
        return PetResponseDTO.from(repository.salvar(pet));
    }

    public void remover(Long id) {
        if (!repository.remover(id)) {
            throw new PetNaoEncontradoException(id);
        }
    }

    private Pet buscarEntidade(Long id) {
        return repository.buscarPorId(id).orElseThrow(() -> new PetNaoEncontradoException(id));
    }
}
