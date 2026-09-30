package br.edu.ifrn.taskapi.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifrn.taskapi.dto.TaskRequestDTO;
import br.edu.ifrn.taskapi.dto.TaskResponseDTO;
import br.edu.ifrn.taskapi.factory.Notification;
import br.edu.ifrn.taskapi.factory.NotificationFactory;
import br.edu.ifrn.taskapi.model.Task;
import br.edu.ifrn.taskapi.repository.TaskRepository;
import br.edu.ifrn.taskapi.strategy.LowPriorityStrategy;
import br.edu.ifrn.taskapi.strategy.NormalPriorityStrategy;
import br.edu.ifrn.taskapi.strategy.PriorityStrategy;
import br.edu.ifrn.taskapi.strategy.UrgentPriorityStrategy;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final NotificationFactory notificationFactory;

    public TaskService(TaskRepository repository, NotificationFactory notificationFactory) {
        this.repository = repository;
        this.notificationFactory = notificationFactory;
    }

    public TaskResponseDTO criar(TaskRequestDTO dto) {
        Task tarefa = new Task(dto.titulo(), dto.descricao(), dto.prazo());
        PriorityStrategy estrategia = escolherEstrategia(dto.prazo());
        tarefa.setPrioridade(estrategia.calcularPrioridade(tarefa));
        Task salva = repository.salvar(tarefa);
        return toResponseDTO(salva);
    }

    public List<TaskResponseDTO> listarTodas() {
        return repository.listarTodas().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public TaskResponseDTO concluir(Long id, String tipoNotificacao) {
        Task tarefa = repository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada: " + id));

        tarefa.setConcluida(true);

        Notification notificacao = notificationFactory.criar(tipoNotificacao);
        notificacao.enviar("Tarefa \"" + tarefa.getTitulo() + "\" foi concluída!");

        return toResponseDTO(tarefa);
    }

    private PriorityStrategy escolherEstrategia(LocalDate prazo) {
        if (prazo == null) {
            return new LowPriorityStrategy();
        }
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), prazo);
        if (dias <= 1) {
            return new UrgentPriorityStrategy();
        } else if (dias <= 7) {
            return new NormalPriorityStrategy();
        }
        return new LowPriorityStrategy();
    }

    private TaskResponseDTO toResponseDTO(Task tarefa) {
        return new TaskResponseDTO(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.isConcluida(),
                tarefa.getPrioridade()
        );
    }
}
