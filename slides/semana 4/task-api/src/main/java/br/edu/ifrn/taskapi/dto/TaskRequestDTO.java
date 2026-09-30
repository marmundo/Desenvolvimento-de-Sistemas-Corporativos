package br.edu.ifrn.taskapi.dto;

import java.time.LocalDate;

public record TaskRequestDTO(
        String titulo,
        String descricao,
        LocalDate prazo
) {}
