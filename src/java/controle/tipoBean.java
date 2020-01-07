package controle;

import DAO.TipoDAO;
import Excecoes.NomeInvalidoException;
import Excecoes.TipoInvalidoException;
import entidades.Tipo;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

@SessionScoped
@ManagedBean

public class tipoBean {

    private Tipo tipo = new Tipo();
    private List<Tipo> tipos = new ArrayList<>();
    TipoDAO tipoDAO = new TipoDAO();
    private int pid;
    private Tipo tipoSelecionado;

    public boolean validar() {
        return !this.tipo.getNome().trim().isEmpty();
    }

    public void adicionar() {
        try {
            if (validar()) {
                if (tipo.getId() == pid) {
                    new TipoDAO().Salvar(this.tipo);
                    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                    ec.redirect("");
                } else {
                    tipoDAO.Aterar(this.tipo);
                }
                limpar();
            } else {
                throw new NomeInvalidoException();
            }
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TipoInvalidoException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Tipo já inserido anteriomente!"));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NomeInvalidoException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Preencha o campo com dados válidos e tente novamente!"));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void limpar() {
        try {
            this.pid = tipoDAO.ProximoId();
            tipoSelecionado = new Tipo();
            this.tipo = new Tipo();
            this.tipo.setId(pid);
            listar();
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listar() {
        try {
            this.tipos = this.tipoDAO.Listar();
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editar(Tipo t) {
        this.tipo = t;
        listar();
    }

    public void excluir(Tipo t) {
        try {
            this.tipoDAO.Excluir(t.getId());
            limpar();
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("");
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscar(int id) {
        try {
            tipo = tipoDAO.Buscar(id);
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Tipo getTipo() {
        return this.tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public List<Tipo> getTipos() {
        return tipos;
    }

    public void setTipos(List<Tipo> tipos) {
        this.tipos = tipos;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public Tipo getTipoSelecionado() {
        return tipoSelecionado;
    }

    public void setTipoSelecionado(Tipo tipoSelecionado) {
        this.tipoSelecionado = tipoSelecionado;
    }

}
