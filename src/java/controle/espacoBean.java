package controle;

import DAO.EspacoDAO;
import Excecoes.DescricaoInvalidaException;
import Excecoes.EspacoInvalidoException;
import entidades.Espaco;
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
public class espacoBean {

    private Espaco espaco = new Espaco(), espacoSelecionado;
    private List<Espaco> espacos = new ArrayList<>();
    EspacoDAO espacoDAO = new EspacoDAO();
    private int pid;
    private boolean desc;
    private boolean num;

    public boolean validar() {
        if (desc) {
            return !this.espaco.getDescricao().trim().isEmpty();
        } else {
            return true;
        }
    }

    public void adicionar() {
        try {
            if (!desc) {
                espaco.setDescricao(null);
            }
            if (!num) {
                espaco.setNumero(0);
            }
            if (validar()) {
                if (espaco.getId() == this.pid) {
                    espacoDAO.Salvar(this.espaco);
                    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                    ec.redirect("");
                } else {
                    espacoDAO.Aterar(this.espaco);
                }
                limpar();
            } else {
                throw new DescricaoInvalidaException();
            }
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(espacoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DescricaoInvalidaException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Preencha o campo dados válidos e tente novamente!"));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EspacoInvalidoException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Espaço já inserido anteriomente!"));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void limpar() {
        try {
            this.pid = espacoDAO.ProximoId();
            this.espaco = new Espaco();
            espaco.setId(pid);
            this.desc = false;
            this.num = false;
            this.espacoSelecionado = new Espaco();
            listar();
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listar() {
        try {
            espacos = espacoDAO.Listar();
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listarDisponiveis() {
        try {
            espacos = espacoDAO.ListarDisponiveis();
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editar(Espaco e) {
        num = e.getNumero() != 0;
        desc = e.getDescricao() != null;
        espaco = e;
        listar();
    }

    public void excluir(Espaco e) {
        try {
            espacoDAO.Excluir(e.getId());
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
            Logger.getLogger(espacoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscar(int id) {
        try {
            espaco = espacoDAO.Buscar(id);
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean disabledNum() {
        return !num;
    }

    public boolean disabledDesc() {
        return !desc;
    }

    public Espaco getEspaco() {
        return espaco;
    }

    public void setEspaco(Espaco Espaco) {
        this.espaco = Espaco;
    }

    public List<Espaco> getEspacos() {
        return espacos;
    }

    public void setEspacos(List<Espaco> Espacos) {
        this.espacos = Espacos;
    }

    public boolean isDesc() {
        return desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    public boolean isNum() {
        return num;
    }

    public void setNum(boolean num) {
        this.num = num;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public Espaco getEspacoSelecionado() {
        return espacoSelecionado;
    }

    public void setEspacoSelecionado(Espaco espacoSelecionado) {
        this.espacoSelecionado = espacoSelecionado;
    }

    public String nome(int id) {
        try {
            if (id != 0) {
                tipoBean tb = new tipoBean();
                Espaco esp = espacoDAO.Buscar(id);
                tb.buscar(esp.getId_tipo());
                String nome;
                String numero;
                if (esp.getDescricao() == null) {
                    nome = "";
                } else {
                    nome = esp.getDescricao();
                }
                if (esp.getNumero() == 0) {
                    numero = "";
                } else {
                    numero = "nº" + esp.getNumero();
                }
                return tb.getTipo().getNome() + " " + nome + " " + numero;
            } else {
                return "";
            }
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public String numero(int numero) {
        if (numero != 0) {
            return numero + "";
        } else {
            return "";
        }
    }
}
