package DAO;

import Excecoes.DAOUsuarioVazioException;
import Excecoes.MatriculaInvalidaException;
import Excecoes.ResponsavelNaoEncontradoException;
import Excecoes.OperadorNaoEncontradoException;
import Excecoes.UsuarioNaoEncontradoException;
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

    public boolean validarUsuario(UsuarioAbstrato ua) throws SQLException {
        Connection conexao = FabricaConexao.getConexao();
        boolean validade;
        String sql = "SELECT * FROM `usuario` WHERE `matricula` = ?";
        PreparedStatement pst = conexao.prepareStatement(sql);
        pst.setString(1, ua.getMatricula());
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

    public void salvar(UsuarioAbstrato ua) throws SQLException, MatriculaInvalidaException {
        if (validarUsuario(ua)) {
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
        } else {
            throw new MatriculaInvalidaException();
        }
    }

    public void alterar(UsuarioAbstrato ua) throws SQLException, UsuarioNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("UPDATE `usuario` set `nome`=?,`senha`=? WHERE `matricula`=?");
        pst.setString(1, ua.getNome());
        pst.setString(2, ua.getSenha());
        pst.setString(3, ua.getMatricula());
        if (pst.executeUpdate() == 0) {
            pst.execute();
            FabricaConexao.fecharConexao();
            throw new UsuarioNaoEncontradoException();
        }
        FabricaConexao.fecharConexao();
        pst.close();
    }

    public List<UsuarioAbstrato> listar() throws SQLException, DAOUsuarioVazioException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `usuario`");
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new DAOUsuarioVazioException();
        } else {
            List<UsuarioAbstrato> usuarios = new ArrayList<>();
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
    }

    public List<UsuarioAbstrato> listarResponsaveis() throws SQLException, ResponsavelNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `usuario` WHERE `classe` != 'operador'");
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            return null;
        } else {
            List<UsuarioAbstrato> usuarios = new ArrayList<>();

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
    }

    public List<UsuarioAbstrato> listarOperadores() throws SQLException, OperadorNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `usuario` WHERE `classe` = 'operador'");
        ResultSet rs = pst.executeQuery();
        if (!rs.isBeforeFirst()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new OperadorNaoEncontradoException();
        } else {
            List<UsuarioAbstrato> usuarios = new ArrayList<>();
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
    }

    public UsuarioAbstrato buscar(String matricula) throws SQLException, UsuarioNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `usuario` WHERE `matricula` = ?");
        pst.setString(1, matricula);
        ResultSet rs = pst.executeQuery();
        if (!rs.next()) {
            pst.close();
            FabricaConexao.fecharConexao();
            return null;
        } else {
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
            pst.close();
            FabricaConexao.fecharConexao();
            return usuario;
        }
    }

    public UsuarioAbstrato buscarResponsavel(String matricula) throws SQLException, ResponsavelNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `usuario` WHERE `matricula` = ? AND `classe` != 'operador'");
        pst.setString(1, matricula);
        ResultSet rs = pst.executeQuery();
        if (!rs.next()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new ResponsavelNaoEncontradoException();
        } else {
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
            pst.close();
            FabricaConexao.fecharConexao();
            return usuario;
        }
    }

    public UsuarioAbstrato login(String matricula, String senha) throws SQLException, UsuarioNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("SELECT * FROM `usuario` WHERE `matricula` = ? AND `senha` = ?");
        pst.setString(1, matricula);
        pst.setString(2, senha);
        ResultSet rs = pst.executeQuery();
        if (!rs.next()) {
            pst.close();
            FabricaConexao.fecharConexao();
            throw new UsuarioNaoEncontradoException();
        } else {
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
            pst.close();
            FabricaConexao.fecharConexao();
            return usuario;
        }
    }

    public void excluir(String matricula) throws SQLException, UsuarioNaoEncontradoException {
        Connection conexao = FabricaConexao.getConexao();
        PreparedStatement pst = conexao.prepareStatement("DELETE FROM `usuario` WHERE `matricula` = ?");
        pst.setString(1, matricula);
        if (pst.executeUpdate() == 0) {
            pst.execute();
            FabricaConexao.fecharConexao();
            throw new UsuarioNaoEncontradoException();
        }
        pst.execute();
        FabricaConexao.fecharConexao();
    }
}
