package br.edu.ifrn.taskapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifrn.taskapi.dto.TaskRequestDTO;
import br.edu.ifrn.taskapi.dto.TaskResponseDTO;
import br.edu.ifrn.taskapi.service.TaskService;

@RestController
@RequestMapping("/tarefas")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> criar(@RequestBody TaskRequestDTO dto) {
        TaskResponseDTO criada = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @GetMapping
    public List<TaskResponseDTO> listar() {
        return service.listarTodas();
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<TaskResponseDTO> concluir(
            @PathVariable Long id,
            @RequestParam String tipoNotificacao) {
        return ResponseEntity.ok(service.concluir(id, tipoNotificacao));
    }
}
