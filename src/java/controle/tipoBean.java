package controle;

import DAO.TipoDAO;
import Excecoes.DAOTipoVazioException;
import Excecoes.NomeInvalidoException;
import Excecoes.TipoInvalidoException;
import Excecoes.TipoNaoEncontradoException;
import entidades.Tipo;
import entidades.UsuarioAbstrato;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@SessionScoped
@ManagedBean

public class tipoBean {

    private Tipo tipo = new Tipo(), tipoSelecionado;
    private List<Tipo> tipos = new ArrayList<>();
    TipoDAO tipoDAO = new TipoDAO();
    private int pid;
    private String csenha, operacao;

    public boolean validar() {
        return !this.tipo.getNome().trim().isEmpty();
    }

    public void validarComSenha() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        HttpSession sessao = (HttpSession) contexto.getExternalContext().getSession(false);
        UsuarioAbstrato logado = (UsuarioAbstrato) sessao.getAttribute("logado");
        if (!csenha.equals(logado.getSenha())) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Senha invalida!", "Verifique sua senha e tente novamente!"));
        } else {
            csenha = "";
            switch (operacao) {
                case "adicionar":
                    operacao = "";
                    adicionar();
                    break;
                case "excluir":
                    operacao = "";
                    excluir(tipoSelecionado);
                    break;
            }
        }
    }

    public void adicionar() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            if (validar()) {
                if (tipo.getId() == pid) {
                    new TipoDAO().salvar(this.tipo);
                    contexto.getExternalContext().getFlash().setKeepMessages(true);
                    contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação bem sucedida!", "Tipo " + tipo.getNome() + " salvo com êxito!"));
                    contexto.getExternalContext().redirect("");
                } else {
                    tipoDAO.alterar(this.tipo);
                    contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação bem sucedida!", "Tipo " + tipo.getNome() + " alterado com êxito!"));
                }
                limpar();
            } else {
                throw new NomeInvalidoException();
            }
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TipoInvalidoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Tipo já inserido anteriomente!"));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NomeInvalidoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Preencha o campo com dados válidos e tente novamente!"));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TipoNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tipo não encontrado", ex.getMessage()));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void limpar() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            this.pid = tipoDAO.proximoId();
            tipoSelecionado = new Tipo();
            this.tipo = new Tipo();
            this.tipo.setId(pid);
            this.csenha = "";
            this.operacao = "";
            listar();
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listar() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            this.tipos = this.tipoDAO.listar();
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DAOTipoVazioException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não há tipos registrados", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editar(Tipo t) {
        this.tipo = t;
        listar();
    }

    public void excluir(Tipo t) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            this.tipoDAO.excluir(t.getId());
            limpar();
            contexto.getExternalContext().getFlash().setKeepMessages(true);
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação bem sucedida!", "Tipo " + t.getNome() + " excluído com êxito!"));
            contexto.getExternalContext().redirect("");
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TipoNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tipo não encontrado", ex.getMessage()));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscar(int id) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            tipo = tipoDAO.buscar(id);
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TipoNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tipo não encontrado", ex.getMessage()));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
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

    public String getCsenha() {
        return csenha;
    }

    public void setCsenha(String csenha) {
        this.csenha = csenha;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }
}
