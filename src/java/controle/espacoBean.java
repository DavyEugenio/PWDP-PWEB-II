package controle;

import DAO.EspacoDAO;
import Excecoes.DAOEspacoVazioException;
import Excecoes.DescricaoInvalidaException;
import Excecoes.EspacoInvalidoException;
import Excecoes.EspacoNaoEncontradoException;
import entidades.Espaco;
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
public class espacoBean {

    private Espaco espaco = new Espaco(), espacoSelecionado;
    private List<Espaco> espacos = new ArrayList<>();
    EspacoDAO espacoDAO = new EspacoDAO();
    private int pid;
    private boolean desc, num;
    private String csenha, operacao;

    public boolean validar() {
        if (desc) {
            return !this.espaco.getDescricao().trim().isEmpty();
        } else {
            return true;
        }
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
                    excluir(espacoSelecionado);
                    break;
            }
        }
    }
    
    public void adicionar() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            if (!desc) {
                espaco.setDescricao(null);
            }
            if (!num) {
                espaco.setNumero(0);
            }
            if (validar()) {
                if (espaco.getId() == this.pid) {
                    espacoDAO.salvar(this.espaco);
                    contexto.getExternalContext().getFlash().setKeepMessages(true);
                    contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação bem sucedida!", "Espaço " + nome(espaco.getId()) + " salvo com êxito!"));
                    contexto.getExternalContext().redirect("");
                } else {
                    espacoDAO.alterar(this.espaco);
                    contexto.getExternalContext().getFlash().setKeepMessages(true);
                    contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação bem sucedida!", "Espaço " + nome(espaco.getId()) + " alterado com êxito!"));
                    contexto.getExternalContext().redirect("");
                }
                limpar();
            } else {
                throw new DescricaoInvalidaException();
            }
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(espacoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DescricaoInvalidaException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Preencha o campo dados válidos e tente novamente!"));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EspacoInvalidoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Espaço já inserido anteriomente!"));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EspacoNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Espaço não encontrado!", ex.getMessage()));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void limpar() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            this.pid = espacoDAO.proximoId();
            this.espaco = new Espaco();
            espaco.setId(pid);
            this.desc = false;
            this.num = false;
            this.espacoSelecionado = new Espaco();
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
            espacos = espacoDAO.listar();
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DAOEspacoVazioException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nenhum espaço encontrado!", ex.getMessage()));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listarDisponiveis() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            espacos = espacoDAO.listarDisponiveis();
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EspacoNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Espaço não encontrado!", "Não há espaços disponiveis no sistema!"));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editar(Espaco e) {
        num = e.getNumero() != 0;
        desc = e.getDescricao() != null;
        espaco = e;
        listar();
    }

    public void excluir(Espaco e) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            String nome = nome(e.getId());
            espacoDAO.excluir(e.getId());
            limpar();
            contexto.getExternalContext().getFlash().setKeepMessages(true);
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação bem sucedida!", "Espaço " + nome + " excluído com êxito!"));
            contexto.getExternalContext().redirect("");
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(espacoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EspacoNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Espaço não encontrado!", ex.getMessage()));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscar(int id) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            espaco = espacoDAO.buscar(id);
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EspacoNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Espaço não encontrado!", ex.getMessage()));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String nome(int id) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            if (id != 0) {
                tipoBean tb = new tipoBean();
                Espaco esp = espacoDAO.buscar(id);
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
            }
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EspacoNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Espaço não encontrado!", ex.getMessage()));
            Logger.getLogger(tipoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String numero(int numero) {
        if (numero != 0) {
            return numero + "";
        } else {
            return "-";
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
