/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Sizwe Massango
 */
public class Candidato extends Utilizador{
    protected  String numeroBI;
    protected String Telefone;
    protected String morada;
    protected LocalDate dataDeNascimento;
    protected List<Inscricao> inscricoes;
    protected QuestionarioCarencia questionarioCarencia;

    public Candidato(String nomeCompleto, String email, String senhaHash) {
        super(nomeCompleto, email, senhaHash);
    }

    @Override
    public Perfil getPerfil() {
        return Perfil.Candidato;
    }

    public String getNumeroBI() {
        return numeroBI;
    }

    public void setNumeroBI(String numeroBI) {
        this.numeroBI = numeroBI;
    }

    public String getTelefone() {
        return Telefone;
    }

    public void setTelefone(String telefone) {
        Telefone = telefone;
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
        this.inscricoes = inscricao;
    }

    public QuestionarioCarencia getQuestionarioCarencia() {
        return questionarioCarencia;
    }

    public void setQuestionarioCarencia(QuestionarioCarencia questionarioCarencia) {
        this.questionarioCarencia = questionarioCarencia;
    }

    public void adicionarInscricao(Inscricao inscricao){
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
