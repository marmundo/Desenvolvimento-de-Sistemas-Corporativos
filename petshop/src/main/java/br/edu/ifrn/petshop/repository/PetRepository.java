package br.edu.ifrn.petshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.petshop.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
}
