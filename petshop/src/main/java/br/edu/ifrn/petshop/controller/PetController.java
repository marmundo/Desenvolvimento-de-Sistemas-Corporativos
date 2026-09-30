package br.edu.ifrn.petshop.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifrn.petshop.dto.PetRequestDTO;
import br.edu.ifrn.petshop.dto.PetResponseDTO;
import br.edu.ifrn.petshop.service.PetService;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService service;

    public PetController(PetService service) {
        this.service = service;
    }

    @GetMapping
    public List<PetResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public PetResponseDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<PetResponseDTO> criar(@RequestBody PetRequestDTO dto) {
        PetResponseDTO criado = service.criar(dto);
        return ResponseEntity.created(URI.create("/pets/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public PetResponseDTO atualizar(@PathVariable Long id, @RequestBody PetRequestDTO dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
