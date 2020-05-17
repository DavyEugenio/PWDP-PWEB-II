package controle;

import DAO.UsuarioAbstratoDAO;
import Excecoes.UsuarioNaoEncontradoException;
import entidades.Operador;
import entidades.UsuarioAbstrato;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@SessionScoped
@ManagedBean
public class loginBean implements Serializable {

    private String matricula;
    private String senha;
    private String tipo;
    private String lmatricula;
    private UsuarioAbstratoDAO dao = null;
    private UsuarioAbstrato usuarioLogado = null;

    @PostConstruct
    public void init() {
        if (dao == null) {
            dao = new UsuarioAbstratoDAO();
        }
        limpar();
    }

    public void limpar() {
        matricula = "";
        senha = "";
        usuarioLogado = null;
        tipo = "A";
    }

    public String login() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            usuarioLogado = dao.login(matricula, senha);
            contexto.getExternalContext().getSessionMap().remove("logado");
            contexto.getExternalContext().getSessionMap().put("logado", usuarioLogado);
            contexto.getExternalContext().getFlash().setKeepMessages(true);
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Seja bem vindo " + usuarioLogado.getNome() + "!", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"))));
            if (usuarioLogado instanceof Operador) {
                return "/Restrito/Operador/index.xhtml?faces-redirect=true";
            } else {
                return "/Restrito/Responsaveis/index.xhtml?faces-redirect=true";
            }
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UsuarioNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário e/ou senha inválidos!", "Verifique seus dados e tente novamente!"));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "/index.xhtml?faces-redirect=false";
    }

    public String sair() throws IOException {
        limpar();
        FacesContext contexto = FacesContext.getCurrentInstance();
        contexto.getExternalContext().getSessionMap().remove("logado");
        contexto.getExternalContext().invalidateSession();
        ExternalContext ec = contexto.getExternalContext();
        ec.redirect("../../index.xhtml?faces-redirect=true");
        return "/index.xhtml?faces-redirect=true";
    }

    public String mascara() {
        if (tipo.equals("A")) {
            return "9999";
        } else {
            if (tipo.equals("S")) {
                return "9999999";
            } else {
                return "999.999.999-99";
            }
        }
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public UsuarioAbstratoDAO getDao() {
        return dao;
    }

    public void setDao(UsuarioAbstratoDAO dao) {
        this.dao = dao;
    }

    public UsuarioAbstrato getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(UsuarioAbstrato usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLmatricula() {
        return lmatricula;
    }

    public void setLmatricula(String lmatricula) {
        this.lmatricula = lmatricula;
    }
}
