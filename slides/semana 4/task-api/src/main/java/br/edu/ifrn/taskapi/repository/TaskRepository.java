package br.edu.ifrn.taskapi.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import br.edu.ifrn.taskapi.model.Task;

@Repository
public class TaskRepository {

    private final List<Task> tarefas = new ArrayList<>();
    private final AtomicLong proximoId = new AtomicLong(1);

    public Task salvar(Task tarefa) {
        tarefa.setId(proximoId.getAndIncrement());
        tarefas.add(tarefa);
        return tarefa;
    }

    public List<Task> listarTodas() {
        return tarefas;
    }

    public Optional<Task> buscarPorId(Long id) {
        return tarefas.stream()
                .filter(tarefa -> tarefa.getId().equals(id))
                .findFirst();
    }
}
