package DAO;

import Excecoes.DAOEspacoVazioException;
import Excecoes.EspacoInvalidoException;
import Excecoes.EspacoNaoEncontradoException;
import entidades.Espaco;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.FabricaConexao;

public class EspacoDAO {

    public EspacoDAO() {
    }

    public boolean validarEspaco(Espaco espaco, int chamada) throws SQLException {
        Connection conexao = FabricaConexao.getConexao();
        boolean validade;
        String sql;
        if (chamada == 1) {
            sql = "SELECT * FROM `espaco` WHERE `descricao` = ? AND `numero` = ? AND `id_tipo` = ?";
        } else {
            sql = "SELECT * FROM `espaco` WHERE `descricao` = ? AND `numero` = ? AND `id_tipo` = ? AND `id` != ?";
        }
        PreparedStatement pst = conexao.prepareStatement(sql);
        pst.setString(1, espaco.getDescricao());
        pst.setInt(2, espaco.getNumero());
        pst.setInt(3, espaco.getId_tipo());
        if (chamada == 2) {
            pst.setInt(4, espaco.getId());
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

    public void salvar(Espaco espaco) throws SQLException, EspacoInvalidoException {
        if (validarEspaco(espaco, 1)) {
            Connection conexao = FabricaConexao.getConexao();
            PreparedStatement pst = conexao.prepareStatement("INSERT INTO `espaco` (`descricao`,`numero`,`status`,`id_tipo`) value (?,?,?,?)");
            pst.setString(1, espaco.getDescricao());
            if (espaco.getNumero() != 0) {
                pst.setInt(2, espaco.getNumero());
            } else {
                pst.setString(2, null);
            }
            pst.setBoolean(3, espaco.isStatus());
            pst.setInt(4, espaco.getId_tipo());
            pst.execute();
            FabricaConexao.fecharConexao();
            pst.close();
            FabricaConexao.fecharConexao();
        } else {
            throw new EspacoInvalidoException();
        }
    }

    public void alterar(Espaco espaco) throws SQLException, EspacoInvalidoException, EspacoNaoEncontradoException {
        if (validarEspaco(espaco, 2)) {
            Connection conexao = FabricaConexao.getConexao();
            PreparedStatement pst = conexao.prepareStatement("UPDATE `espaco` SET `descricao`=?,`numero`=?,`status`=?,`id_tipo`=? WHERE `id`=?");
            pst.setString(1, espaco.getDescricao());
            if (espaco.getNumero() != 0) {
                pst.setInt(2, espaco.getNumero());
            } else {
                pst.setString(2, null);
            }
            pst.setBoolean(3, espaco.isStatus());
            pst.setInt(4, espaco.getId_tipo());
            pst.setInt(5, espaco.getId());
            if (pst.executeUpdate() == 0) {
                FabricaConexao.fecharConexao();
                pst.close();
                throw new EspacoNaoEncontradoException();
            }
            FabricaConexao.fecharConexao();
            pst.close();
        } else {
            throw new EspacoInvalidoException();
        }
    }

    public List<Espaco> listar() throws SQLException, DAOEspacoVazioException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `espaco`");
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new DAOEspacoVazioException();
        } else {
            List<Espaco> espacos = new ArrayList<>();
            while (rs.next()) {
                Espaco espaco = new Espaco();
                espaco.setId(rs.getInt("id"));
                espaco.setDescricao(rs.getString("descricao"));
                espaco.setNumero(rs.getInt("numero"));
                espaco.setStatus(rs.getBoolean("status"));
                espaco.setId_tipo(rs.getInt("id_tipo"));
                espacos.add(espaco);
            }
            pst.close();
            FabricaConexao.fecharConexao();
            return espacos;
        }
    }

    public Espaco buscar(int id) throws SQLException, EspacoNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `espaco` WHERE `id` = ?");
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (!rs.next()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new EspacoNaoEncontradoException();
        } else {
            Espaco espaco = new Espaco();
            espaco.setId(rs.getInt("id"));
            espaco.setDescricao(rs.getString("descricao"));
            espaco.setNumero(rs.getInt("numero"));
            espaco.setStatus(rs.getBoolean("status"));
            espaco.setId_tipo(rs.getInt("id_tipo"));
            pst.close();
            FabricaConexao.fecharConexao();
            return espaco;
        }
    }
    
    public List<Espaco> buscarPorTipo(int id_tipo) throws SQLException, EspacoNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `espaco` WHERE id_tipo = ?");
        pst.setInt(1, id_tipo);
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new EspacoNaoEncontradoException();
        } else {
            List<Espaco> espacos = new ArrayList<>();
            while (rs.next()) {
                Espaco espaco = new Espaco();
                espaco.setId(rs.getInt("id"));
                espaco.setDescricao(rs.getString("descricao"));
                espaco.setNumero(rs.getInt("numero"));
                espaco.setStatus(rs.getBoolean("status"));
                espaco.setId_tipo(rs.getInt("id_tipo"));
                espacos.add(espaco);
            }
            pst.close();
            FabricaConexao.fecharConexao();
            return espacos;
        }
    }
    
    public List<Espaco> listarDisponiveis() throws SQLException, EspacoNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `espaco` WHERE `status` = true");
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new EspacoNaoEncontradoException();
        } else {
            List<Espaco> espacos = new ArrayList<>();
            while (rs.next()) {
                Espaco espaco = new Espaco();
                espaco.setId(rs.getInt("id"));
                espaco.setDescricao(rs.getString("descricao"));
                espaco.setNumero(rs.getInt("numero"));
                espaco.setStatus(rs.getBoolean("status"));
                espaco.setId_tipo(rs.getInt("id_tipo"));
                espacos.add(espaco);
            }
            pst.close();
            FabricaConexao.fecharConexao();
            return espacos;
        }
    }

    public void excluir(int id) throws SQLException, EspacoNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("DELETE FROM `espaco` WHERE `id` = ?");
        pst.setInt(1, id);
        if (pst.executeUpdate() == 0) {
            FabricaConexao.fecharConexao();
            pst.close();
            throw new EspacoNaoEncontradoException();
        }
        pst.close();
        FabricaConexao.fecharConexao();
    }

    public int proximoId() throws SQLException {
        int pid = 0;
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SHOW TABLE STATUS LIKE 'espaco'");
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
