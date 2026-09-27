
package model;

import java.util.Objects;

/**
 * Classe abstrata que representa qualquer utilizador do sistema.
 * Reune os campos e comportamentos comuns a todos os perfis (Candidato, Administrador).
 * Sem anotações de ORM: o mapeamento para as tabelas é feito manualmente nas classes DAO.
 */
public abstract class Utilizador {

    protected Long id;
    protected String nomeCompleto;
    protected String email;
    protected String senhaHash;

    protected Utilizador() {
    }

    protected Utilizador(String nomeCompleto, String email, String senhaHash) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senhaHash = senhaHash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Se 'o' for um obejcto igual a objecto que chamar o metodo 
        if (!(o instanceof Utilizador)) return false; // se 'o ' nao for instancia de Utilizador 
        Utilizador that = (Utilizador) o;
        return Objects.equals(email, that.email); // dois objetos sao iguais se tiverem o mesmo email
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return nomeCompleto + " <" + email + ">";
    }
}
