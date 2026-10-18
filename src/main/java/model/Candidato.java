/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Sizwe Massango
 */


public class Candidato extends Utilizador {
    protected String numeroBI;
    protected String telefone;
    protected String provincia;
    protected String distrito;
    protected String morada;
    protected LocalDate dataDeNascimento;
    protected LocalDate dataInscricao;
    protected List<Inscricao> inscricoes = new ArrayList<>();
    protected QuestionarioCarencia questionarioCarencia;

    public Candidato(String nomeCompleto, String email, String senhaHash) {
        super(nomeCompleto, email, senhaHash);
    }

    public Candidato() {
    }

    @Override
    public Perfil getPerfil() {
        return Perfil.Candidato;
    }

    public LocalDate getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDate dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public String getNumeroBI() {
        return numeroBI;
    }

    public void setNumeroBI(String numeroBI) {
        this.numeroBI = numeroBI;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public LocalDate getDataDeNascimento() {
        return dataDeNascimento;
    }

    public void setDataDeNascimento(LocalDate dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public List<Inscricao> getInscricao() {
        return inscricoes;
    }

    public void setInscricao(List<Inscricao> inscricao) {
        this.inscricoes = inscricao == null ? new ArrayList<>() : inscricao;
    }

    public QuestionarioCarencia getQuestionarioCarencia() {
        return questionarioCarencia;
    }

    public void setQuestionarioCarencia(QuestionarioCarencia questionarioCarencia) {
        this.questionarioCarencia = questionarioCarencia;
        if (questionarioCarencia != null && id != null) {
            questionarioCarencia.setCandidatoId(id);
        }
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
        if (questionarioCarencia != null) {
            questionarioCarencia.setCandidatoId(id);
        }
    }

    public void adicionarInscricao(Inscricao inscricao) {
        inscricoes.add(inscricao);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Candidato candidato = (Candidato) o;
        return Objects.equals(getNumeroBI(), candidato.getNumeroBI());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getNumeroBI());
    }
}