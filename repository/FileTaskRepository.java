package repository;

import model.Task;
import model.TaskPriority;
import model.TaskStatus;

import java.io.*;
import java.util.*;

public class FileTaskRepository implements TaskRepository {

    private final Map<UUID, Task> data = new LinkedHashMap<>();
    private final File file;

    public FileTaskRepository(String filename) {
        this.file = new File(filename);
        if (file.exists()) {
            loadFromFile();
        } else {
            // inicializando com algumas tarefas de exemplo (5) para atender à exigência
            save(new Task("Comprar mantimentos", "Comprar frutas, vegetais e leite", java.time.LocalDate.now().plusDays(2), TaskPriority.ALTA, TaskStatus.PENDENTE, "Casa"));
            save(new Task("Reunião com equipe", "Discutir o progresso do projeto", java.time.LocalDate.now().plusDays(1), TaskPriority.MEDIA, TaskStatus.PENDENTE, "Trabalho"));
            save(new Task("Exercício físico", "Caminhada de 30 minutos", java.time.LocalDate.now().plusDays(3), TaskPriority.BAIXA, TaskStatus.PENDENTE, "Saúde"));
            save(new Task("Enviar relatório", "Enviar relatório semanal por email", java.time.LocalDate.now().plusDays(4), TaskPriority.ALTA, TaskStatus.PENDENTE, "Trabalho"));
            save(new Task("Backup", "Fazer backup dos arquivos importantes", java.time.LocalDate.now().plusDays(5), TaskPriority.MEDIA, TaskStatus.PENDENTE, "Casa"));
            persist();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                Map<UUID, Task> loaded = (Map<UUID, Task>) obj;
                data.clear();
                data.putAll(loaded);
            }
        } catch (Exception e) {
            // falha ao carregar, iniciar vazio
            System.err.println("Falha ao carregar tasks: " + e.getMessage());
        }
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public void save(Task task) {
        data.put(task.getId(), task);
        persist();
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
        persist();
    }

    @Override
    public void deleteAllById(Collection<UUID> ids) {
        for (UUID id : ids) data.remove(id);
        persist();
    }

    @Override
    public void persist() {
        // Persiste o mapa de tarefas em disco usando serialização Java.
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(new LinkedHashMap<>(data));
        } catch (IOException e) {
            System.err.println("Falha ao salvar tasks: " + e.getMessage());
        }
    }
}
