package br.edu.ifrn.taskapi.strategy;

import br.edu.ifrn.taskapi.model.Task;

public interface PriorityStrategy {
    String calcularPrioridade(Task tarefa);
}
