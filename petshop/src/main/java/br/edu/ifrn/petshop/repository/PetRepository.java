package br.edu.ifrn.petshop.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import br.edu.ifrn.petshop.model.Pet;

/**
 * Repositório em memória: os dados ficam em uma List e são perdidos ao reiniciar a aplicação.
 */
@Repository
public class PetRepository {

    private final List<Pet> pets = new ArrayList<>();
    private final AtomicLong proximoId = new AtomicLong(1);

    public synchronized List<Pet> listar() {
        return new ArrayList<>(pets);
    }

    public synchronized Optional<Pet> buscarPorId(Long id) {
        return pets.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public synchronized Pet salvar(Pet pet) {
        if (pet.getId() == null) {
            pet.setId(proximoId.getAndIncrement());
            pets.add(pet);
        }
        return pet;
    }

    public synchronized boolean remover(Long id) {
        return pets.removeIf(p -> p.getId().equals(id));
    }
}
