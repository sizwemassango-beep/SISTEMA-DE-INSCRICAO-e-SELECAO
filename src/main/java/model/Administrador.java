/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Sizwe Massango
 */
public class Administrador extends Utilizador{
    private String cargo;

    public Administrador() {
    }

    public Administrador(String nome, String email, String senha, String cargo) {
        super(nome, email, senha);
        this.cargo = cargo;
    }

    @Override
    public Perfil getPerfil() {
        return Perfil.Administardor;
    }


    public void definirVagas(Curso curso, int vagas) {
        curso.setNumeroVagas(vagas);
    }


    public void registarNota(Inscricao inscricao, double nota) {
        inscricao.setNota(nota);
    }

    public void validarDocumentos(Inscricao inscricao) {
        inscricao.setDocumentosValidados(true);
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    @Override
    public String toString() {
        return "Administrador{id=" + getId() + ", nome='" + getNomeCompleto()
                + "', email='" + getEmail() + "', cargo='" + cargo + "'}";
    }
    
}
