/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author Sizwe Massango
 */
public class Curso {
    protected Long id;
    protected String nome;
    protected int numeroVagas;
    protected  String descricao;
    protected double percentualMerito;
    protected double percentualNecessidade;
    protected LocalDateTime dataInicio;
    protected LocalDateTime dataFim;


    public String getNome() {
        return nome;
    }

    public int getNumeroVagas() {
        return numeroVagas;
    }

    public void setNumeroVagas(int numeroVagas) {
        this.numeroVagas = numeroVagas;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPercentualMerito() {
        return percentualMerito;
    }

    public void setPercentualMerito(double percentualMerito) {
        this.percentualMerito = percentualMerito;
    }

    public double getPercentualNecessidade() {
        return percentualNecessidade;
    }

    public void setPercentualNecessidade(double percentualNecessidade) {
        this.percentualNecessidade = percentualNecessidade;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public int getVagasMerito(){
        return 1;
    }

    public int getVagasNecessidade(){
        return 1;
    }

    public boolean inscricoesAbertas(){
     return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Curso curso = (Curso) o;
        return Objects.equals(getId(), curso.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Curso{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", percentualMerito=" + percentualMerito +
                ", percentualNecessidade=" + percentualNecessidade +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                '}';
    }
}
