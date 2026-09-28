/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author Sizwe Massango
 */
public class Inscricao {
    protected Long id;
    protected Candidato candidato;
    protected double nota;
    protected StatusInscricao status;
    protected LocalDate dataInscricao;
    protected boolean documentosValidados;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public StatusInscricao getStatus() {
        return status;
    }

    public void setStatus(StatusInscricao status) {
        this.status = status;
    }

    public LocalDate getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDate dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public boolean isDocumentosValidados() {
        return documentosValidados;
    }

    public void setDocumentosValidados(boolean documentosValidados) {
        this.documentosValidados = documentosValidados;
    }

    public void aprovar(){

    }
    public void colccarListaEsoera(){

    }
    public void reprovar(){

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Inscricao inscricao = (Inscricao) o;
        return Objects.equals(getId(), inscricao.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Inscricao{" +
                "id=" + id +
                ", candidato=" + candidato +
                ", nota=" + nota +
                ", status=" + status +
                ", dataInscricao=" + dataInscricao +
                ", documentosValidados=" + documentosValidados +
                '}';
    }
}
