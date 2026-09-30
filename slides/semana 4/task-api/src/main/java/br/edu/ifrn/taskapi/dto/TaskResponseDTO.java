package br.edu.ifrn.taskapi.dto;

public record TaskResponseDTO(
        Long id,
        String titulo,
        boolean concluida,
        String prioridade
) {}
