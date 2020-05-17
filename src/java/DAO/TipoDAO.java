package DAO;

import Excecoes.DAOTipoVazioException;
import Excecoes.TipoInvalidoException;
import Excecoes.TipoNaoEncontradoException;
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

    public boolean validarTipo(Tipo tipo, int chamada) throws SQLException {
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

    public void salvar(Tipo tipo) throws SQLException, TipoInvalidoException {
        if (validarTipo(tipo, 1)) {
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

    public void alterar(Tipo tipo) throws SQLException, TipoInvalidoException, TipoNaoEncontradoException {
        if (validarTipo(tipo, 2)) {
            Connection conexao = FabricaConexao.getConexao();
            PreparedStatement pst = conexao.prepareStatement("UPDATE `tipo` set `nome`=? WHERE `id`=?");
            pst.setString(1, tipo.getNome());
            pst.setInt(2, tipo.getId());
            if (pst.executeUpdate() == 0) {
                pst.execute();
                FabricaConexao.fecharConexao();
                throw new TipoNaoEncontradoException();
            }
            pst.close();
            FabricaConexao.fecharConexao();
        } else {
            throw new TipoInvalidoException();
        }
    }

    public List<Tipo> listar() throws SQLException, DAOTipoVazioException {
        List<Tipo> tipos = new ArrayList<>();
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `tipo`");
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new DAOTipoVazioException();
        } else {
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
    }

    public Tipo buscar(int id) throws SQLException, TipoNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `tipo` WHERE `id` = ?");
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (!rs.next()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new TipoNaoEncontradoException();
        } else {
            Tipo tipo = new Tipo();
            tipo.setId(rs.getInt("id"));
            tipo.setNome(rs.getString("nome"));
            pst.close();
            FabricaConexao.fecharConexao();
            return tipo;
        }
    }

    public void excluir(int id) throws SQLException, TipoNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("DELETE FROM `tipo` WHERE `id` = ?");
        pst.setInt(1, id);
        if (pst.executeUpdate() == 0) {
            pst.execute();
            FabricaConexao.fecharConexao();
            throw new TipoNaoEncontradoException();
        }
        pst.execute();
        FabricaConexao.fecharConexao();
    }

    public int proximoId() throws SQLException {
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
