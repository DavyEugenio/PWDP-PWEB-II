package DAO;

import Excecoes.ResponsavelNaoEncontradoException;
import entidades.Aluno;
import entidades.Operador;
import entidades.OutroR;
import entidades.Servidor;
import entidades.UsuarioAbstrato;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.FabricaConexao;

public class UsuarioAbstratoDAO {

    public UsuarioAbstratoDAO() {
    }

    public void Salvar(UsuarioAbstrato ua) throws SQLException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("INSERT INTO `usuario` (`matricula`,`nome`,`senha`,`classe`) value (?,?,?,?)");
        pst.setString(1, ua.getMatricula());
        pst.setString(2, ua.getNome());
        pst.setString(3, ua.getSenha());
        if (ua instanceof Servidor) {
            pst.setString(4, "servidor");
        } else {
            if (ua instanceof Operador) {
                pst.setString(4, "operador");
            } else {
                if (ua instanceof Aluno) {
                    pst.setString(4, "aluno");
                } else {
                    pst.setString(4, "outro");
                }
            }
        }
        pst.execute();
        FabricaConexao.fecharConexao();
        pst.close();

    }

    public void Aterar(UsuarioAbstrato ua) throws SQLException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("UPDATE `usuario` set `nome`=?,`senha`=? WHERE `matricula`=?");
        pst.setString(1, ua.getNome());
        pst.setString(2, ua.getSenha());
        pst.setString(3, ua.getMatricula());
        pst.execute();
        FabricaConexao.fecharConexao();
        pst.close();

    }

    public List<UsuarioAbstrato> Listar() throws SQLException {
        List<UsuarioAbstrato> usuarios = new ArrayList<>();
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `usuario`");
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            UsuarioAbstrato usuario = null;
            switch (rs.getString("classe")) {
                case "servidor":
                    usuario = new Servidor();
                    break;
                case "operador":
                    usuario = new Operador();
                    break;
                case "aluno":
                    usuario = new Aluno();
                    break;
                case "outro":
                    usuario = new OutroR();
                    break;
            }
            usuario.setMatricula(rs.getString("matricula"));
            usuario.setNome(rs.getString("nome"));
            usuario.setSenha(rs.getString("senha"));
            usuarios.add(usuario);
        }
        pst.close();
        FabricaConexao.fecharConexao();
        return usuarios;
    }

    public List<UsuarioAbstrato> ListarResponsaveis() throws SQLException {
        List<UsuarioAbstrato> usuarios = new ArrayList<>();
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `usuario` WHERE `classe` != 'operador'");
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            UsuarioAbstrato usuario = null;
            switch (rs.getString("classe")) {
                case "servidor":
                    usuario = new Servidor();
                    break;
                case "aluno":
                    usuario = new Aluno();
                    break;
                case "outro":
                    usuario = new OutroR();
                    break;
            }
            usuario.setMatricula(rs.getString("matricula"));
            usuario.setNome(rs.getString("nome"));
            usuario.setSenha(rs.getString("senha"));
            usuarios.add(usuario);
        }
        pst.close();
        FabricaConexao.fecharConexao();
        return usuarios;
    }

    public List<UsuarioAbstrato> ListarOperadores() throws SQLException {
        List<UsuarioAbstrato> usuarios = new ArrayList<>();
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `usuario` WHERE `classe` = 'operador'");
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            UsuarioAbstrato usuario = new Operador();
            usuario.setMatricula(rs.getString("matricula"));
            usuario.setNome(rs.getString("nome"));
            usuario.setSenha(rs.getString("senha"));
            usuarios.add(usuario);
        }
        pst.close();
        FabricaConexao.fecharConexao();
        return usuarios;
    }

    public UsuarioAbstrato Buscar(String matricula) throws SQLException {
        UsuarioAbstrato usuario = null;
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `usuario` WHERE `matricula` = ?");
        pst.setString(1, matricula);
        ResultSet rs = pst.executeQuery();
        if (rs.next() == false) {
            pst.close();
            FabricaConexao.fecharConexao();
        } else {
            switch (rs.getString("classe")) {
                case "servidor":
                    usuario = new Servidor();
                    break;
                case "operador":
                    usuario = new Operador();
                    break;
                case "aluno":
                    usuario = new Aluno();
                    break;
                case "outro":
                    usuario = new OutroR();
                    break;
            }
            usuario.setMatricula(rs.getString("matricula"));
            usuario.setNome(rs.getString("nome"));
            usuario.setSenha(rs.getString("senha"));
        }
        pst.close();
        FabricaConexao.fecharConexao();
        return usuario;
    }
    
    public UsuarioAbstrato BuscarResponsavel(String matricula) throws SQLException, ResponsavelNaoEncontradoException {
        UsuarioAbstrato usuario = null;
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `usuario` WHERE `matricula` = ? AND `classe` != 'operador'");
        pst.setString(1, matricula);
        ResultSet rs = pst.executeQuery();
        if (rs.next() == false) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new ResponsavelNaoEncontradoException();
        } else {
            switch (rs.getString("classe")) {
                case "servidor":
                    usuario = new Servidor();
                    break;
                case "aluno":
                    usuario = new Aluno();
                    break;
                case "outro":
                    usuario = new OutroR();
                    break;
            }
            usuario.setMatricula(rs.getString("matricula"));
            usuario.setNome(rs.getString("nome"));
            usuario.setSenha(rs.getString("senha"));
        }
        pst.close();
        FabricaConexao.fecharConexao();
        return usuario;
    }

    public UsuarioAbstrato Login(String matricula, String senha) throws SQLException {
        UsuarioAbstrato usuario = null;
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("select * from `usuario` WHERE `matricula` = ? AND `senha` = ?");
        pst.setString(1, matricula);
        pst.setString(2, senha);
        ResultSet rs = pst.executeQuery();
        if (rs.next() == false) {
            pst.close();
            FabricaConexao.fecharConexao();
        } else {
            switch (rs.getString("classe")) {
                case "servidor":
                    usuario = new Servidor();
                    break;
                case "operador":
                    usuario = new Operador();
                    break;
                case "aluno":
                    usuario = new Aluno();
                    break;
                case "outro":
                    usuario = new OutroR();
                    break;
            }
            usuario.setMatricula(rs.getString("matricula"));
            usuario.setNome(rs.getString("nome"));
            usuario.setSenha(rs.getString("senha"));
        }
        pst.close();
        FabricaConexao.fecharConexao();
        return usuario;
    }

    public void Excluir(String matricula) throws SQLException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("DELETE FROM `usuario` WHERE `matricula` = ?");
        pst.setString(1, matricula);
        pst.execute();
        FabricaConexao.fecharConexao();
    }
}
