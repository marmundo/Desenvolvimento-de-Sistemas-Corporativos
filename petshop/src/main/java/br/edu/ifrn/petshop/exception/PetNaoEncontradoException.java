package br.edu.ifrn.petshop.exception;

public class PetNaoEncontradoException extends RuntimeException {
    public PetNaoEncontradoException(Long id) {
        super("Pet não encontrado: " + id);
    }
}
