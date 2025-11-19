package model;

// Modelo: Representa uma tarefa

import java.time.LocalDate;
import java.util.UUID;


public class Task implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private String titulo;
    private String descricao;
    private LocalDate prazo;
    private TaskPriority prioridade;
    private TaskStatus status;
    private String projeto;

    public Task(String titulo, String descricao, LocalDate prazo, TaskPriority prioridade, TaskStatus status, String projeto) {
        this.id = UUID.randomUUID();
        if (titulo == null || titulo.trim().isEmpty()) throw new IllegalArgumentException("Título é obrigatório");
        this.titulo = titulo.trim();
        this.descricao = descricao;
        this.prazo = prazo;
        this.prioridade = (prioridade == null ? TaskPriority.MEDIA : prioridade);
        this.status = (status == null ? TaskStatus.PENDENTE : status);
        this.projeto = (projeto == null ? "" : projeto);
    }

    // Construtor que aceita um id — usado ao carregar a partir de persistência JSON
    public Task(UUID id, String titulo, String descricao, LocalDate prazo, TaskPriority prioridade, TaskStatus status, String projeto) {
        this.id = id == null ? UUID.randomUUID() : id;
        if (titulo == null || titulo.trim().isEmpty()) throw new IllegalArgumentException("Título é obrigatório");
        this.titulo = titulo.trim();
        this.descricao = descricao;
        this.prazo = prazo;
        this.prioridade = (prioridade == null ? TaskPriority.MEDIA : prioridade);
        this.status = (status == null ? TaskStatus.PENDENTE : status);
        this.projeto = (projeto == null ? "" : projeto);
    }

    public UUID getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public TaskPriority getPrioridade() {
        return prioridade;
    }
    public TaskStatus getStatus() {
        return status;
    }

    public String getProjeto() {
        return projeto;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public void setPrioridade(TaskPriority prioridade) {
        this.prioridade = prioridade;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setProjeto(String projeto) {
        this.projeto = projeto;
    }

    public boolean atrasada() {
        return this.prazo != null && this.prazo.isBefore(LocalDate.now()) && this.status != TaskStatus.CONCLUIDA;
    }
}

