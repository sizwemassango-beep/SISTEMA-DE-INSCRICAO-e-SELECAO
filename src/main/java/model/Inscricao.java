/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.Objects;

@SuppressWarnings("unused")

/**
 *
 * @author Sizwe Massango
 */
public class Inscricao {
    protected Long id;
    protected Candidato candidato;
    protected Long cursoId;
    protected String cursoNome;
    protected Curso curso;
    protected double nota;
    protected double nota12 = 0.0;
    protected StatusInscricao status = StatusInscricao.PENDENTE;
    protected LocalDate dataInscricao;
    protected boolean documentosSubmetidos;
    protected boolean documentosValidados;
    protected String docIdentificacao;
    protected String docCertificado10;
    protected String docCertificado12OuRecibo;
    protected String tipoComprovativo12 = "Certificado de Conclusão";
    protected String docAtestadoPobreza;
    protected String cartaMotivacao;
    protected String motivoRejeicao;

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

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getCursoNome() {
        return cursoNome;
    }

    public void setCursoNome(String cursoNome) {
        this.cursoNome = cursoNome;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
        if (curso != null) {
            this.cursoId = curso.getId();
            this.cursoNome = curso.getNome();
        }
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public double getNota12() {
        return nota12;
    }

    public void setNota12(double nota12) {
        this.nota12 = nota12;
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

    public boolean isDocumentosSubmetidos() {
        return documentosSubmetidos;
    }

    public void setDocumentosSubmetidos(boolean documentosSubmetidos) {
        this.documentosSubmetidos = documentosSubmetidos;
    }

    public boolean isDocumentosValidados() {
        return documentosValidados;
    }

    public void setDocumentosValidados(boolean documentosValidados) {
        this.documentosValidados = documentosValidados;
    }

    public String getDocIdentificacao() {
        return docIdentificacao;
    }

    public void setDocIdentificacao(String docIdentificacao) {
        this.docIdentificacao = docIdentificacao;
    }

    public String getDocCertificado10() {
        return docCertificado10;
    }

    public void setDocCertificado10(String docCertificado10) {
        this.docCertificado10 = docCertificado10;
    }

    public String getDocCertificado12OuRecibo() {
        return docCertificado12OuRecibo;
    }

    public void setDocCertificado12OuRecibo(String docCertificado12OuRecibo) {
        this.docCertificado12OuRecibo = docCertificado12OuRecibo;
    }

    public String getTipoComprovativo12() {
        return tipoComprovativo12;
    }

    public void setTipoComprovativo12(String tipoComprovativo12) {
        this.tipoComprovativo12 = tipoComprovativo12;
    }

    public String getDocAtestadoPobreza() {
        return docAtestadoPobreza;
    }

    public void setDocAtestadoPobreza(String docAtestadoPobreza) {
        this.docAtestadoPobreza = docAtestadoPobreza;
    }

    public String getCartaMotivacao() {
        return cartaMotivacao;
    }

    public void setCartaMotivacao(String cartaMotivacao) {
        this.cartaMotivacao = cartaMotivacao;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public void aprovar() {
        this.status = StatusInscricao.APROVADO;
    }

    public void colccarListaEsoera() {
        this.status = StatusInscricao.LISTA_ESPERA;
    }

    public void reprovar() {
        this.status = StatusInscricao.NAO_APROVADO;
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
                ", cursoNome='" + cursoNome + '\'' +
                ", nota=" + nota +
                ", nota12=" + nota12 +
                ", status=" + status +
                ", dataInscricao=" + dataInscricao +
                ", documentosSubmetidos=" + documentosSubmetidos +
                ", documentosValidados=" + documentosValidados +
                '}';
    }
}
