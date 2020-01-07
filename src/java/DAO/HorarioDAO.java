package DAO;

import Excecoes.HorarioIntercaladoException;
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

    private boolean ValidarHorario(Horario h, int chamada) throws HorarioIntercaladoException, SQLException {
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

    public void Salvar(Horario horario) throws SQLException, HorarioIntercaladoException {
        if (ValidarHorario(horario, 1) == true) {
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

    public void Alterar(Horario horario) throws SQLException, HorarioIntercaladoException {
        if (ValidarHorario(horario, 2) | !(horario.getStatus().equals("Em espera"))) {
            Connection conexao = FabricaConexao.getConexao();
            PreparedStatement pst = conexao.prepareStatement("UPDATE `horario` SET `entrada`=?,`saida`=?,`matricula_usuario`=?,`id_espaco`=?,`status`=? WHERE id = ?");
            pst.setString(1, horario.getEntrada().format(c.formatoLDT));
            pst.setString(2, horario.getSaida().format(c.formatoLDT));
            pst.setString(3, horario.getMatricula_responsavel());
            pst.setInt(4, horario.getId_espaco());
            pst.setString(5, horario.getStatus());
            pst.setInt(6, horario.getId());
            pst.execute();
            FabricaConexao.fecharConexao();
            pst.close();
        } else {
            throw new HorarioIntercaladoException();
        }
    }

    public List<Horario> Listar() throws SQLException {
        List<Horario> horarios = new ArrayList<>();
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario`");
        ResultSet rs = pst.executeQuery();
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

    public List<Horario> ListarEmEspera() throws SQLException {
        List<Horario> horarios = new ArrayList<>();
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `status` = 'Em espera'");
        ResultSet rs = pst.executeQuery();
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
    
    public Horario Buscar(int id) throws SQLException {
        Horario horario = null;
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `id` = ?");
        pst.setInt(1, id);
        ResultSet rs = pst.executeQuery();
        if (rs.next() == false) {
            pst.close();
            FabricaConexao.fecharConexao();
        } else {
            horario = new Horario();
            horario.setId(rs.getInt("id"));
            horario.setEntrada(LocalDateTime.parse(rs.getString("entrada"), c.formatoLDT));
            horario.setSaida(LocalDateTime.parse(rs.getString("saida"), c.formatoLDT));
            horario.setMatricula_responsavel(rs.getString("matricula_usuario"));
            horario.setId_espaco(rs.getInt("id_espaco"));
            horario.setStatus(rs.getString("status"));
        }
        pst.close();
        FabricaConexao.fecharConexao();

        return horario;
    }

    public void Excluir(int id) throws SQLException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("DELETE FROM `horario` WHERE `id` = ?");
        pst.setInt(1, id);
        pst.execute();
        FabricaConexao.fecharConexao();

    }

    public List<Horario> BuscarPorEspaco(int id_espaco) throws SQLException {
        List<Horario> horarios = null;
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `id_espaco` = ?");
        pst.setInt(1, id_espaco);
        ResultSet rs = pst.executeQuery();
        if (rs.next() == false) {
            pst.close();
            FabricaConexao.fecharConexao();
        } else {
            horarios = new ArrayList<>();
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
        }
        pst.close();
        FabricaConexao.fecharConexao();
        return horarios;
    }

    public List<Horario> BuscarPorResponsavel(String matricula) throws SQLException {
        List<Horario> horarios = new ArrayList<>();
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `matricula_usuario` = ?");
        pst.setString(1, matricula);
        ResultSet rs = pst.executeQuery();
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

    public List<Horario> BuscarPorEmEspera(String matricula) throws SQLException {
        List<Horario> horarios = new ArrayList<>();
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `matricula_usuario` = ? AND `status` = 'Em espera'");
        pst.setString(1, matricula);
        ResultSet rs = pst.executeQuery();
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

    public List<Horario> BuscarPorIntervalo(LocalDateTime inicio, LocalDateTime fim) throws SQLException {
        List<Horario> horarios = new ArrayList<>();
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `entrada` >= ? AND `entrada` <= ? OR `saida` >= ? AND `saida` <= ?");
        pst.setString(1, inicio.format(c.formatoLDT));
        pst.setString(2, fim.format(c.formatoLDT));
        pst.setString(3, inicio.format(c.formatoLDT));
        pst.setString(4, fim.format(c.formatoLDT));
        ResultSet rs = pst.executeQuery();
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
    
    public List<Horario> BuscarPorIntervaloPorResponsavel(LocalDateTime inicio, LocalDateTime fim, String matricula) throws SQLException {
        List<Horario> horarios = new ArrayList<>();
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `entrada` >= ? AND `entrada` <= ? AND `matricula_usuario` = ? OR `saida` >= ? AND `saida` <= ? AND `matricula_usuario` = ?");
        pst.setString(1, inicio.format(c.formatoLDT));
        pst.setString(2, fim.format(c.formatoLDT));
        pst.setString(3, matricula);
        pst.setString(4, inicio.format(c.formatoLDT));
        pst.setString(5, fim.format(c.formatoLDT));
        pst.setString(6, matricula);
        ResultSet rs = pst.executeQuery();
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
    
    public List<Horario> BuscarPorIntervaloEmEspera(LocalDateTime inicio, LocalDateTime fim) throws SQLException {
        List<Horario> horarios = new ArrayList<>();
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `horario` WHERE `entrada` >= ? AND `entrada` <= ? AND `status` = 'Em espera' OR `saida` >= ? AND `saida` <= ? AND `status` = 'Em espera'");
        pst.setString(1, inicio.format(c.formatoLDT));
        pst.setString(2, fim.format(c.formatoLDT));
        pst.setString(3, inicio.format(c.formatoLDT));
        pst.setString(4, fim.format(c.formatoLDT));
        ResultSet rs = pst.executeQuery();
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
    
    public void AtualizarStatus() throws SQLException, HorarioIntercaladoException{
        List<Horario> horarios = this.ListarEmEspera();
        for (Horario horario : horarios) {
            if(horario.getSaida().compareTo(LocalDateTime.now()) <= 0){
                horario.setStatus("Finalizado");
                Alterar(horario);
            }
        }
    }
    
    public int ProximoId() throws SQLException {
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
