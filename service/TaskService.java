package service;

import model.Task;
import model.TaskStatus;
import repository.TaskRepository;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class TaskService {
    
    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public List<Task> listar(String texto,
                             TaskStatus status,
                             boolean apenasAtrasadas,
                             String projeto) {

        return repo.findAll().stream()
                .filter(t -> texto == null || texto.isBlank()
                        || t.getTitulo().toLowerCase().contains(texto.toLowerCase())
                        || (t.getDescricao() != null
                            && t.getDescricao().toLowerCase().contains(texto.toLowerCase())))
                .filter(t -> status == null || t.getStatus() == status)
                // Quando 'apenasAtrasadas' for true, filtra apenas as tarefas que estão realmente atrasadas
                .filter(t -> !apenasAtrasadas || t.atrasada())
                .filter(t -> projeto == null || projeto.isBlank()
                        || projeto.equalsIgnoreCase(t.getProjeto()))
                .sorted(Comparator.comparing(
                        t -> t.getPrazo(),
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .collect(Collectors.toList());
    }
    
    public void criar(Task t) {
        repo.save(t);
    }

    public void atualizar(Task t) {
        repo.save(t);
    }

    public void deletar(java.util.List<UUID> ids) {
        repo.deleteAllById(ids);
    }

    public void marcarConcluida(java.util.List<UUID> ids) {
        ids.forEach(id -> repo.findById(id).ifPresent(t -> {
            t.setStatus(TaskStatus.CONCLUIDA);
            repo.save(t);
        }));
    }

    public TaskRepository getRepository() {
        return this.repo;
    }

    // Retorna o repositório usado pelo serviço. Útil para ações da UI
    // (ex.: comando Salvar no menu que chama persist() do repositório).
}
