package br.edu.ifrn.petshop.dto;

import br.edu.ifrn.petshop.model.Pet;

public record PetResponseDTO(Long id, String nome, String especie, String raca, Integer idade) {

    public static PetResponseDTO from(Pet pet) {
        return new PetResponseDTO(pet.getId(), pet.getNome(), pet.getEspecie(), pet.getRaca(), pet.getIdade());
    }
}
