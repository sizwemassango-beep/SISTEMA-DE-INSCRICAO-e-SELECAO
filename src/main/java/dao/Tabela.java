package dao;

import model.Administrador;

import java.util.List;

public interface Tabela <T> {
    void  criar();
    void salvar(T t);
    //void registrarTabela(T t);
    <T> Administrador buscarId(Long id);
    List<T> listarTabela();
    void atualizar(T object);
    void eleminar(T object);

}
