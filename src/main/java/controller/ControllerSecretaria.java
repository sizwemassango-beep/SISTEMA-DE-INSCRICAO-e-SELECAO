package controller;

import dao.ConexaoBD;
import service.DashboardService;
import service.RelatorioPdfService;

public abstract class ControllerSecretaria extends ControllerBase {

    protected final DashboardService dashboardService;
    protected final RelatorioPdfService relatorioPdfService;

    public ControllerSecretaria() {
        super();
        this.dashboardService = new DashboardService(this.conexaoBD);
        this.relatorioPdfService = new RelatorioPdfService();
    }

    public ControllerSecretaria(ConexaoBD conexaoBD) {
        super(conexaoBD);
        this.dashboardService = new DashboardService(this.conexaoBD);
        this.relatorioPdfService = new RelatorioPdfService();
    }

    public DashboardService getDashboardService() {
        return dashboardService;
    }

    public RelatorioPdfService getRelatorioPdfService() {
        return relatorioPdfService;
    }
}
