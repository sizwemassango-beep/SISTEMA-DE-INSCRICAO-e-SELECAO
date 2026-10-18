package controller;

import dao.ConexaoBD;

public abstract class ControllerBase {

    protected final ConexaoBD conexaoBD;

    public ControllerBase() {
        this.conexaoBD = new ConexaoBD();
    }

    public ControllerBase(ConexaoBD conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    public ConexaoBD getConexaoBD() {
        return conexaoBD;
    }

    public boolean isConexaoAtiva() {
        try (var conn = conexaoBD.getConexao()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}
