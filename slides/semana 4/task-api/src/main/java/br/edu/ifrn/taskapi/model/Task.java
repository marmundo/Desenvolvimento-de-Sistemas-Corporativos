package br.edu.ifrn.taskapi.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {

    private Long id;
    private String titulo;
    private String descricao;
    private LocalDate prazo;
    private boolean concluida;
    private String prioridade;

    // Campos "internos" que não deveriam vazar para o consumidor da API
    // (usados na Parte A do laboratório para discutir o vazamento de Entity)
    private LocalDateTime ultimaModificacaoInterna;
    private int versaoOtimista;

    public Task(String titulo, String descricao, LocalDate prazo) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.prazo = prazo;
        this.concluida = false;
        this.ultimaModificacaoInterna = LocalDateTime.now();
        this.versaoOtimista = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDateTime getUltimaModificacaoInterna() {
        return ultimaModificacaoInterna;
    }

    public int getVersaoOtimista() {
        return versaoOtimista;
    }
}
