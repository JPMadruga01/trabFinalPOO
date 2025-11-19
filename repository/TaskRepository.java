package repository;

import model.Task;
import java.util.*;

public interface TaskRepository {
    List<Task> findAll();
    Optional<Task> findById(UUID id);
    void save(Task task);
    void deleteById(UUID id);
    void deleteAllById(Collection<UUID> ids);
    default void persist() { /* opcional: implementações que suportam persistência devem sobrescrever */ }
}

class InMemoryTaskRepository implements TaskRepository {

    private final Map<UUID, Task> data = new LinkedHashMap<>();

    public InMemoryTaskRepository() {
        // Dados iniciais para teste
        save(new Task("Comprar mantimentos", "Comprar frutas, vegetais e leite", java.time.LocalDate.now().plusDays(2), model.TaskPriority.ALTA, model.TaskStatus.PENDENTE, "Casa"));
        save(new Task("Reunião com equipe", "Discutir o progresso do projeto", java.time.LocalDate.now().plusDays(1), model.TaskPriority.MEDIA, model.TaskStatus.PENDENTE, "Trabalho"));
        save(new Task("Exercício físico", "Caminhada de 30 minutos no parque", java.time.LocalDate.now().plusDays(3), model.TaskPriority.BAIXA, model.TaskStatus.PENDENTE, "Saúde"));
        save(new Task("Enviar relatório", "Enviar relatório semanal por email", java.time.LocalDate.now().plusDays(4), model.TaskPriority.ALTA, model.TaskStatus.PENDENTE, "Trabalho"));
        save(new Task("Backup", "Fazer backup dos arquivos importantes", java.time.LocalDate.now().plusDays(5), model.TaskPriority.MEDIA, model.TaskStatus.PENDENTE, "Casa"));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(data.get(id)); // Retorna Optional vazio se não encontrar
    }

    @Override
    public void save(Task task) {
        data.put(task.getId(), task);
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public void deleteAllById(Collection<UUID> ids) {
        for (UUID id: ids) {
            data.remove(id);
        }
    }
}
