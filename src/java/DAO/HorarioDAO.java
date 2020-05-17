package DAO;

import Excecoes.DAOHorarioVazioException;
import Excecoes.HorarioIntercaladoException;
import Excecoes.HorarioNaoEncontradoException;
import util.Conversoes;
import entidades.Horario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import util.FabricaConexao;

public class HorarioDAO {

    private Conversoes c = new Conversoes();

    public HorarioDAO() {
    }

    private boolean validarHorario(Horario h, int chamada) throws HorarioIntercaladoException, SQLException {
        Connection conexao = FabricaConexao.getConexao();
        boolean validade;
        String sql;
        if (chamada == 1) {
            sql = "SELECT * FROM `horario` WHERE `entrada` <= ? AND `saida` >= ? AND `status` = 'Em espera' AND `id_espaco` = ? OR `entrada` <= ?  AND `saida` >= ?  AND `status` = 'Em espera' AND `id_espaco` = ? OR `entrada` >= ? AND `entrada` <= ? AND `status` = 'Em espera' AND `id_espaco` = ? OR `saida` >= ? AND `saida` <= ? AND `status` = 'Em espera' AND `id_espaco` = ?";
        } else {
            sql = "SELECT * FROM `horario` WHERE `entrada` <= ? AND `saida` >= ? AND `status` = 'Em espera' AND `id_espaco` = ? AND `id` !=? OR `entrada` <= ? AND `saida` >= ?  AND `status` = 'Em espera' AND `id_espaco` = ? AND `id` !=? OR `entrada` >= ? AND `entrada` <= ? AND `status` = 'Em espera' AND `id_espaco` = ? AND `id` !=? OR `saida` >= ? AND `saida` <= ? AND `status` = 'Em espera' AND `id_espaco` = ? AND `id` !=?";
        }
        PreparedStatement pst = conexao.prepareStatement(sql);
        pst.setString(1, h.getEntrada().format(c.formatoLDT));
        pst.setString(2, h.getEntrada().format(c.formatoLDT));
        pst.setInt(3, h.getId_espaco());
        if (chamada == 1) {
            pst.setString(4, h.getSaida().format(c.formatoLDT));
            pst.setString(5, h.getSaida().format(c.formatoLDT));
            pst.setInt(6, h.getId_espaco());
            pst.setString(7, h.getEntrada().format(c.formatoLDT));
            pst.setString(8, h.getSaida().format(c.formatoLDT));
            pst.setInt(9, h.getId_espaco());
            pst.setString(10, h.getEntrada().format(c.formatoLDT));
            pst.setString(11, h.getSaida().format(c.formatoLDT));
            pst.setInt(12, h.getId_espaco());
        } else {
            pst.setInt(4, h.getId());
            pst.setString(5, h.getSaida().format(c.formatoLDT));
            pst.setString(6, h.getSaida().format(c.formatoLDT));
            pst.setInt(7, h.getId_espaco());
            pst.setInt(8, h.getId());
            pst.setString(9, h.getEntrada().format(c.formatoLDT));
            pst.setString(10, h.getSaida().format(c.formatoLDT));
            pst.setInt(11, h.getId_espaco());
            pst.setInt(12, h.getId());
            pst.setString(13, h.getEntrada().format(c.formatoLDT));
            pst.setString(14, h.getSaida().format(c.formatoLDT));
            pst.setInt(15, h.getId_espaco());
            pst.setInt(16, h.getId());
        }
        ResultSet resultset = pst.executeQuery();
        if (resultset.next()) {
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

    public void salvar(Horario horario) throws SQLException, HorarioIntercaladoException {
        if (validarHorario(horario, 1) == true) {
            Connection conexao = FabricaConexao.getConexao();
            PreparedStatement pst = conexao.prepareStatement("INSERT INTO `horario` (`entrada`,`saida`,`matricula_usuario`,`id_espaco`,`status`) value (?,?,?,?,?)");
            pst.setString(1, horario.getEntrada().format(c.formatoLDT));
            pst.setString(2, horario.getSaida().format(c.formatoLDT));
            pst.setString(3, horario.getMatricula_responsavel());
            pst.setInt(4, horario.getId_espaco());
            pst.setString(5, horario.getStatus());
            pst.execute();
            FabricaConexao.fecharConexao();
            pst.close();
        } else {
            throw new HorarioIntercaladoException();
        }
    }

    public void alterar(Horario horario) throws SQLException, HorarioIntercaladoException, HorarioNaoEncontradoException {
        if (validarHorario(horario, 2) | !(horario.getStatus().equals("Em espera"))) {
            Connection conexao = FabricaConexao.getConexao();
            PreparedStatement pst = conexao.prepareStatement("UPDATE `horario` SET `entrada`=?,`saida`=?,`matricula_usuario`=?,`id_espaco`=?,`status`=? WHERE id = ?");
            pst.setString(1, horario.getEntrada().format(c.formatoLDT));
            pst.setString(2, horario.getSaida().format(c.formatoLDT));
            pst.setString(3, horario.getMatricula_responsavel());
            pst.setInt(4, horario.getId_espaco());
            pst.setString(5, horario.getStatus());
            pst.setInt(6, horario.getId());
            if (pst.executeUpdate() == 0) {
                FabricaConexao.fecharConexao();
                pst.close();
                throw new HorarioNaoEncontradoException();
            }
            FabricaConexao.fecharConexao();
            pst.close();
        } else {
            throw new HorarioIntercaladoException();
        }
    }

    public List<Horario> listar() throws SQLException, DAOHorarioVazioException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario`");
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new DAOHorarioVazioException();
        } else {
            List<Horario> horarios = new ArrayList<>();
            while (rs.next()) {
                Horario horario = new Horario();
                horario.setId(rs.getInt("id"));
                horario.setEntrada(LocalDateTime.parse(rs.getString("entrada"), c.formatoLDT));
                horario.setSaida(LocalDateTime.parse(rs.getString("saida"), c.formatoLDT));
                horario.setMatricula_responsavel(rs.getString("matricula_usuario"));
                horario.setId_espaco(rs.getInt("id_espaco"));
                horario.setStatus(rs.getString("status"));
                horarios.add(horario);
            }
            pst.close();
            FabricaConexao.fecharConexao();
            return horarios;
        }
    }

    public List<Horario> listarEmEsperaOuEmAndamento() throws SQLException, HorarioNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `status` = 'Em espera' OR `status` = 'Em andamento'");
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            return new ArrayList<>();
        } else {
            List<Horario> horarios = new ArrayList<>();
            while (rs.next()) {
                Horario horario = new Horario();
                horario.setId(rs.getInt("id"));
                horario.setEntrada(LocalDateTime.parse(rs.getString("entrada"), c.formatoLDT));
                horario.setSaida(LocalDateTime.parse(rs.getString("saida"), c.formatoLDT));
                horario.setMatricula_responsavel(rs.getString("matricula_usuario"));
                horario.setId_espaco(rs.getInt("id_espaco"));
                horario.setStatus(rs.getString("status"));
                horarios.add(horario);
            }
            pst.close();
            FabricaConexao.fecharConexao();
            return horarios;
        }
    }

    public Horario buscar(int id) throws SQLException, HorarioNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `id` = ?");
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (!rs.next()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new HorarioNaoEncontradoException();
        } else {
            Horario horario = new Horario();
            horario.setId(rs.getInt("id"));
            horario.setEntrada(LocalDateTime.parse(rs.getString("entrada"), c.formatoLDT));
            horario.setSaida(LocalDateTime.parse(rs.getString("saida"), c.formatoLDT));
            horario.setMatricula_responsavel(rs.getString("matricula_usuario"));
            horario.setId_espaco(rs.getInt("id_espaco"));
            horario.setStatus(rs.getString("status"));
            pst.close();
            FabricaConexao.fecharConexao();
            return horario;
        }
    }
    
    public List<Horario> buscarPorEspaco(int id_espaco) throws SQLException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `id_espaco` = ?");
        pst.setInt(1, id_espaco);
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            return null;
        } else {
            List<Horario> horarios = new ArrayList<>();
            while (rs.next()) {
                Horario horario = new Horario();
                horario.setId(rs.getInt("id"));
                horario.setEntrada(LocalDateTime.parse(rs.getString("entrada"), c.formatoLDT));
                horario.setSaida(LocalDateTime.parse(rs.getString("saida"), c.formatoLDT));
                horario.setMatricula_responsavel(rs.getString("matricula_usuario"));
                horario.setId_espaco(rs.getInt("id_espaco"));
                horario.setStatus(rs.getString("status"));
                horarios.add(horario);
            }
            pst.close();
            FabricaConexao.fecharConexao();
            return horarios;
        }
    }

    public List<Horario> buscarPorResponsavel(String matricula) throws SQLException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `matricula_usuario` = ?");
        pst.setString(1, matricula);
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            return null;
        } else {
            List<Horario> horarios = new ArrayList<>();
            while (rs.next()) {
                Horario horario = new Horario();
                horario.setId(rs.getInt("id"));
                horario.setEntrada(LocalDateTime.parse(rs.getString("entrada"), c.formatoLDT));
                horario.setSaida(LocalDateTime.parse(rs.getString("saida"), c.formatoLDT));
                horario.setMatricula_responsavel(rs.getString("matricula_usuario"));
                horario.setId_espaco(rs.getInt("id_espaco"));
                horario.setStatus(rs.getString("status"));
                horarios.add(horario);
            }
            pst.close();
            FabricaConexao.fecharConexao();
            return horarios;
        }
    }

    public List<Horario> buscarPorEmEsperaOuEmAndamento(String matricula) throws SQLException, HorarioNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `matricula_usuario` = ? AND `status` = 'Em espera' OR `matricula_usuario` = ? AND `status` = 'Em andamento'");
        pst.setString(1, matricula);
        pst.setString(2, matricula);
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            return null;
        } else {
            List<Horario> horarios = new ArrayList<>();
            while (rs.next()) {
                Horario horario = new Horario();
                horario.setId(rs.getInt("id"));
                horario.setEntrada(LocalDateTime.parse(rs.getString("entrada"), c.formatoLDT));
                horario.setSaida(LocalDateTime.parse(rs.getString("saida"), c.formatoLDT));
                horario.setMatricula_responsavel(rs.getString("matricula_usuario"));
                horario.setId_espaco(rs.getInt("id_espaco"));
                horario.setStatus(rs.getString("status"));
                horarios.add(horario);
            }
            pst.close();
            FabricaConexao.fecharConexao();
            return horarios;
        }
    }

    public List<Horario> buscarPorIntervalo(LocalDateTime inicio, LocalDateTime fim) throws SQLException, HorarioNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `entrada` >= ? AND `entrada` <= ? OR `saida` >= ? AND `saida` <= ?");
        pst.setString(1, inicio.format(c.formatoLDT));
        pst.setString(2, fim.format(c.formatoLDT));
        pst.setString(3, inicio.format(c.formatoLDT));
        pst.setString(4, fim.format(c.formatoLDT));
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new HorarioNaoEncontradoException();
        } else {
            List<Horario> horarios = new ArrayList<>();
            while (rs.next()) {
                Horario horario = new Horario();
                horario.setId(rs.getInt("id"));
                horario.setEntrada(LocalDateTime.parse(rs.getString("entrada"), c.formatoLDT));
                horario.setSaida(LocalDateTime.parse(rs.getString("saida"), c.formatoLDT));
                horario.setMatricula_responsavel(rs.getString("matricula_usuario"));
                horario.setId_espaco(rs.getInt("id_espaco"));
                horario.setStatus(rs.getString("status"));
                horarios.add(horario);
            }
            pst.close();
            FabricaConexao.fecharConexao();
            return horarios;
        }
    }

    public List<Horario> buscarPorIntervaloPorResponsavel(LocalDateTime inicio, LocalDateTime fim, String matricula) throws SQLException, HorarioNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `entrada` >= ? AND `entrada` <= ? AND `matricula_usuario` = ? OR `saida` >= ? AND `saida` <= ? AND `matricula_usuario` = ?");
        pst.setString(1, inicio.format(c.formatoLDT));
        pst.setString(2, fim.format(c.formatoLDT));
        pst.setString(3, matricula);
        pst.setString(4, inicio.format(c.formatoLDT));
        pst.setString(5, fim.format(c.formatoLDT));
        pst.setString(6, matricula);
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new HorarioNaoEncontradoException();
        } else {
            List<Horario> horarios = new ArrayList<>();
            while (rs.next()) {
                Horario horario = new Horario();
                horario.setId(rs.getInt("id"));
                horario.setEntrada(LocalDateTime.parse(rs.getString("entrada"), c.formatoLDT));
                horario.setSaida(LocalDateTime.parse(rs.getString("saida"), c.formatoLDT));
                horario.setMatricula_responsavel(rs.getString("matricula_usuario"));
                horario.setId_espaco(rs.getInt("id_espaco"));
                horario.setStatus(rs.getString("status"));
                horarios.add(horario);
            }
            pst.close();
            FabricaConexao.fecharConexao();
            return horarios;
        }
    }

    public List<Horario> buscarPorIntervaloEmEsperaOuEmAndamento(LocalDateTime inicio, LocalDateTime fim) throws SQLException, HorarioNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement(""
                + "SELECT * FROM `horario` WHERE "
                + "`entrada` >= ? AND `entrada` <= ? AND `status` = 'Em espera' OR "
                + "`entrada` >= ? AND `entrada` <= ? AND `status` = 'Em andamento' OR "
                + "`saida` >= ? AND `saida` <= ? AND `status` = 'Em espera' OR "
                + "`saida` >= ? AND `saida` <= ? AND `status` = 'Em andamento'");
        pst.setString(1, inicio.format(c.formatoLDT));
        pst.setString(2, fim.format(c.formatoLDT));
        pst.setString(3, inicio.format(c.formatoLDT));
        pst.setString(4, fim.format(c.formatoLDT));
        pst.setString(5, inicio.format(c.formatoLDT));
        pst.setString(6, fim.format(c.formatoLDT));
        pst.setString(7, inicio.format(c.formatoLDT));
        pst.setString(8, fim.format(c.formatoLDT));
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new HorarioNaoEncontradoException();
        } else {
            List<Horario> horarios = new ArrayList<>();
            while (rs.next()) {
                Horario horario = new Horario();
                horario.setId(rs.getInt("id"));
                horario.setEntrada(LocalDateTime.parse(rs.getString("entrada"), c.formatoLDT));
                horario.setSaida(LocalDateTime.parse(rs.getString("saida"), c.formatoLDT));
                horario.setMatricula_responsavel(rs.getString("matricula_usuario"));
                horario.setId_espaco(rs.getInt("id_espaco"));
                horario.setStatus(rs.getString("status"));
                horarios.add(horario);
            }
            pst.close();
            FabricaConexao.fecharConexao();
            return horarios;
        }
    }

    public void excluir(int id) throws SQLException, HorarioNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("DELETE FROM `horario` WHERE `id` = ?");
        pst.setInt(1, id);
        if (pst.executeUpdate() == 0) {
            FabricaConexao.fecharConexao();
            pst.close();
            throw new HorarioNaoEncontradoException();
        }
        FabricaConexao.fecharConexao();
    }
    
    public void atualizarStatus() throws SQLException, HorarioIntercaladoException, HorarioNaoEncontradoException {
        List<Horario> horarios = this.listarEmEsperaOuEmAndamento();
        for (Horario horario : horarios) {
            if (horario.getSaida().compareTo(LocalDateTime.now()) <= 0) {
                horario.setStatus("Finalizado");
                alterar(horario);
            } else {
                if (horario.getEntrada().compareTo(LocalDateTime.now()) <= 0) {
                    horario.setStatus("Em andamento");
                    alterar(horario);
                }
            }
        }
    }

    public int proximoId() throws SQLException {
        int pid = 0;
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SHOW TABLE STATUS LIKE 'horario'");
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
