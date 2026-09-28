/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package model;

/**
 *
 * @author Sizwe Massango
 */



/**
 * Estados possíveis de uma inscrição ao longo do processo de seleção.
 * Uma inscrição começa sempre como PENDENTE e só muda depois de o
 * administrador executar a seleção.
 */
public enum StatusInscricao {

    PENDENTE("Pendente"),
    APROVADO("Aprovado"),
    LISTA_ESPERA("Lista de espera"),
    NAO_APROVADO("Não aprovado");

    private final String descricao;

    StatusInscricao(String descricao) {
        this.descricao = descricao;
    }

    /** Texto amigavel para mostrar nas telas (com acentos e espaços). */
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}