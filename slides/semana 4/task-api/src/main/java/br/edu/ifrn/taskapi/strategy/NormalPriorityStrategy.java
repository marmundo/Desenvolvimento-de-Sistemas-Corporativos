package br.edu.ifrn.taskapi.strategy;

import br.edu.ifrn.taskapi.model.Task;

public class NormalPriorityStrategy implements PriorityStrategy {

    @Override
    public String calcularPrioridade(Task tarefa) {
        return "NORMAL";
    }
}
