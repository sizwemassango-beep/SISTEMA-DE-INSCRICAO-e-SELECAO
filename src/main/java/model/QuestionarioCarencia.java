/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.util.Objects;


/**
 *
 * @author Vilker001
 */
    

public class QuestionarioCarencia {

    public static final int PONTUACAO_MAXIMA = 100;
    public static final int LIMIAR_CARENCIA_ALTA = 70;
    public static final int LIMIAR_CARENCIA_MEDIA = 40;
    public static final double DISTANCIA_MAXIMA_KM = 2000;

    public enum FaixaRendimento {
        ATE_5000("Até 5.000 MT", 40),
        DE_5001_A_10000("5.001 a 10.000 MT", 32),
        DE_10001_A_20000("10.001 a 20.000 MT", 22),
        DE_20001_A_40000("20.001 a 40.000 MT", 10),
        ACIMA_40000("Acima de 40.000 MT", 0);

        private final String descricao;
        private final int pontos;

        FaixaRendimento(String descricao, int pontos) {
            this.descricao = descricao;
            this.pontos = pontos;
        }

        public String getDescricao() {
            return descricao;
        }

        public int getPontos() {
            return pontos;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum SituacaoLaboral {
        DESEMPREGADO("Desempregado", 20),
        TRABALHO_INFORMAL("Trabalho informal", 14),
        TRABALHO_PRECARIO("Trabalho temporário", 10),
        EMPREGADO("Empregado", 3),
        ESTUDANTE_SEM_RENDA("Estudante sem rendimento", 15);

        private final String descricao;
        private final int pontos;

        SituacaoLaboral(String descricao, int pontos) {
            this.descricao = descricao;
            this.pontos = pontos;
        }

        public String getDescricao() {
            return descricao;
        }

        public int getPontos() {
            return pontos;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum NivelCarencia {
        ALTA("Carência alta"),
        MEDIA("Carência média"),
        BAIXA("Carência baixa");

        private final String descricao;

        NivelCarencia(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    private Long id;
    private Long candidatoId;
    private FaixaRendimento rendimentoFamiliar;
    private SituacaoLaboral situacaoLaboral;
    private double distanciaKm;
    private boolean orfaoOuChefeFamilia;
    private boolean recebeApoioFinanceiro;

    public QuestionarioCarencia() {
    }

    public QuestionarioCarencia(FaixaRendimento rendimentoFamiliar, SituacaoLaboral situacaoLaboral,
                                double distanciaKm, boolean orfaoOuChefeFamilia,
                                boolean recebeApoioFinanceiro) {
        this.rendimentoFamiliar = rendimentoFamiliar;
        this.situacaoLaboral = situacaoLaboral;
        this.distanciaKm = distanciaKm;
        this.orfaoOuChefeFamilia = orfaoOuChefeFamilia;
        this.recebeApoioFinanceiro = recebeApoioFinanceiro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCandidatoId() {
        return candidatoId;
    }

    public void setCandidatoId(Long candidatoId) {
        this.candidatoId = candidatoId;
    }

    public FaixaRendimento getRendimentoFamiliar() {
        return rendimentoFamiliar;
    }

    public void setRendimentoFamiliar(FaixaRendimento rendimentoFamiliar) {
        this.rendimentoFamiliar = rendimentoFamiliar;
    }

    public SituacaoLaboral getSituacaoLaboral() {
        return situacaoLaboral;
    }

    public void setSituacaoLaboral(SituacaoLaboral situacaoLaboral) {
        this.situacaoLaboral = situacaoLaboral;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public boolean isOrfaoOuChefeFamilia() {
        return orfaoOuChefeFamilia;
    }

    public void setOrfaoOuChefeFamilia(boolean orfaoOuChefeFamilia) {
        this.orfaoOuChefeFamilia = orfaoOuChefeFamilia;
    }

    public boolean isRecebeApoioFinanceiro() {
        return recebeApoioFinanceiro;
    }

    public void setRecebeApoioFinanceiro(boolean recebeApoioFinanceiro) {
        this.recebeApoioFinanceiro = recebeApoioFinanceiro;
    }

    public boolean estaPreenchido() {
        return rendimentoFamiliar != null && situacaoLaboral != null;
    }

    public boolean isValido() {
        return estaPreenchido() && distanciaKm >= 0 && distanciaKm <= DISTANCIA_MAXIMA_KM;
    }

    public int calcularPontosRendimento() {
        return rendimentoFamiliar == null ? 0 : rendimentoFamiliar.getPontos();
    }

    public int calcularPontosSituacaoLaboral() {
        return situacaoLaboral == null ? 0 : situacaoLaboral.getPontos();
    }

    public int calcularPontosDistancia() {
        if (distanciaKm >= 100) {
            return 15;
        }
        if (distanciaKm >= 50) {
            return 11;
        }
        if (distanciaKm >= 20) {
            return 7;
        }
        if (distanciaKm >= 5) {
            return 3;
        }
        return 0;
    }

    public int calcularPontosVulnerabilidade() {
        return orfaoOuChefeFamilia ? 15 : 0;
    }

    public int calcularPontosApoio() {
        return recebeApoioFinanceiro ? 0 : 10;
    }

    public int calcularPontuacao() {
        if (!isValido()) {
            return 0;
        }
        int total = calcularPontosRendimento()
                + calcularPontosSituacaoLaboral()
                + calcularPontosDistancia()
                + calcularPontosVulnerabilidade()
                + calcularPontosApoio();
        return Math.min(total, PONTUACAO_MAXIMA);
    }

    public double calcularPercentagem() {
        return calcularPontuacao() * 100.0 / PONTUACAO_MAXIMA;
    }

    public NivelCarencia classificar() {
        int pontuacao = calcularPontuacao();
        if (pontuacao >= LIMIAR_CARENCIA_ALTA) {
            return NivelCarencia.ALTA;
        }
        if (pontuacao >= LIMIAR_CARENCIA_MEDIA) {
            return NivelCarencia.MEDIA;
        }
        return NivelCarencia.BAIXA;
    }

    public boolean elegivelParaVagaReservada() {
        return isValido() && classificar() != NivelCarencia.BAIXA;
    }

    public int compararCarencia(QuestionarioCarencia outro) {
        return Integer.compare(outro.calcularPontuacao(), this.calcularPontuacao());
    }

    public String gerarResumo() {
        return "Rendimento: " + (rendimentoFamiliar == null ? "-" : rendimentoFamiliar.getDescricao())
                + " | Situação laboral: " + (situacaoLaboral == null ? "-" : situacaoLaboral.getDescricao())
                + " | Distância: " + distanciaKm + " km"
                + " | Órfão/Chefe de família: " + (orfaoOuChefeFamilia ? "Sim" : "Não")
                + " | Apoio financeiro: " + (recebeApoioFinanceiro ? "Sim" : "Não")
                + " | Pontuação: " + calcularPontuacao() + "/" + PONTUACAO_MAXIMA
                + " (" + classificar().getDescricao() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionarioCarencia that = (QuestionarioCarencia) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QuestionarioCarencia{" +
                "id=" + id +
                ", candidatoId=" + candidatoId +
                ", rendimentoFamiliar=" + rendimentoFamiliar +
                ", situacaoLaboral=" + situacaoLaboral +
                ", distanciaKm=" + distanciaKm +
                ", orfaoOuChefeFamilia=" + orfaoOuChefeFamilia +
                ", recebeApoioFinanceiro=" + recebeApoioFinanceiro +
                ", pontuacao=" + calcularPontuacao() +
                '}';
    }
}