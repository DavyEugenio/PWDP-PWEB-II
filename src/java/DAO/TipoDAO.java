package DAO;

import Excecoes.TipoInvalidoException;
import entidades.Tipo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.FabricaConexao;

public class TipoDAO {

    public TipoDAO() {
    }

    public boolean ValidarTipo(Tipo tipo, int chamada) throws SQLException {
        Connection conexao = FabricaConexao.getConexao();
        boolean validade;
        String sql;
        if (chamada == 1) {
            sql = "SELECT * FROM `tipo` WHERE `nome` = ?";
        } else {
            sql = "SELECT * FROM `tipo` WHERE `nome` = ? AND `id` != ?";
        }
        PreparedStatement pst = conexao.prepareStatement(sql);
        pst.setString(1, tipo.getNome());
        if (chamada == 2) {
            pst.setInt(2, tipo.getId());
        }
        ResultSet resultset = pst.executeQuery();
        if (resultset.next() == true) {
            validade = false;
            pst.close();
            FabricaConexao.fecharConexao();
        } else {
            validade = true;
            pst.close();
            FabricaConexao.fecharConexao();
        }
        return validade;
    }

    public void Salvar(Tipo tipo) throws SQLException, TipoInvalidoException {
        if (ValidarTipo(tipo, 1)) {
            Connection conexao = FabricaConexao.getConexao();
            PreparedStatement pst = conexao.prepareStatement("INSERT INTO `tipo` (`nome`) value (?)");
            pst.setString(1, tipo.getNome());
            pst.execute();
            FabricaConexao.fecharConexao();
            pst.close();
            FabricaConexao.fecharConexao();
        } else {
            throw new TipoInvalidoException();
        }
    }

    public void Aterar(Tipo tipo) throws SQLException, TipoInvalidoException {
        if (ValidarTipo(tipo, 2)) {
            Connection conexao = FabricaConexao.getConexao();
            PreparedStatement pst = conexao.prepareStatement("UPDATE `tipo` set `nome`=? WHERE `id`=?");
            pst.setString(1, tipo.getNome());
            pst.setInt(2, tipo.getId());
            pst.execute();
            FabricaConexao.fecharConexao();
            pst.close();
            FabricaConexao.fecharConexao();
        } else {
            throw new TipoInvalidoException();
        }
    }

    public List<Tipo> Listar() throws SQLException {
        List<Tipo> tipos = new ArrayList<>();
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `tipo`");
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            Tipo tipo = new Tipo();
            tipo.setId(rs.getInt("id"));
            tipo.setNome(rs.getString("nome"));
            tipos.add(tipo);
        }
        pst.close();
        FabricaConexao.fecharConexao();
        return tipos;
    }

    public Tipo Buscar(int id) throws SQLException {
        Tipo tipo = null;
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `tipo` WHERE `id` = ?");
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next() == false) {
            pst.close();
            FabricaConexao.fecharConexao();
        } else {
            tipo = new Tipo();
            tipo.setId(rs.getInt("id"));
            tipo.setNome(rs.getString("nome"));
        }
        pst.close();
        FabricaConexao.fecharConexao();
        return tipo;
    }

    public void Excluir(int id) throws SQLException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("DELETE FROM `tipo` WHERE `id` = ?");
        pst.setInt(1, id);
        pst.execute();
        FabricaConexao.fecharConexao();
    }

    public int ProximoId() throws SQLException {
        int pid = 0;
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SHOW TABLE STATUS LIKE 'tipo'");
        ResultSet rs = pst.executeQuery();
        if (rs.next() == false) {
            pst.close();
            FabricaConexao.fecharConexao();
        } else {
            pid = rs.getInt("AUTO_INCREMENT");
        }
        pst.close();
        FabricaConexao.fecharConexao();
        return pid;
    }
}
