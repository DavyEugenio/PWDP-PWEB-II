package controle;

import DAO.UsuarioAbstratoDAO;
import Excecoes.MatriculaInvalidaException;
import Excecoes.NomeInvalidoException;
import Excecoes.ResponsavelNaoEncontradoException;
import entidades.Aluno;
import entidades.Operador;
import entidades.OutroR;
import entidades.Servidor;
import entidades.UsuarioAbstrato;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@SessionScoped
@ManagedBean

public class usuarioAbstratoBean {

    private UsuarioAbstrato usuario, logado, usuarioSelecionado;
    private List<UsuarioAbstrato> usuarios = new ArrayList<>();
    UsuarioAbstratoDAO usuarioDAO = new UsuarioAbstratoDAO();
    private String nome, matricula, tipo, nsenha, asenha, csenha;
    private boolean disabled;

    @PostConstruct
    public void init() {
        limpar();
    }

    public boolean validar() {
        if (!this.nome.trim().isEmpty()) {
            return !this.matricula.trim().isEmpty();
        } else {
            return false;
        }
    }

    public void adicionar() {
        try {
            if (validar()) {
                if (usuarioDAO.Buscar(this.usuario.getMatricula()) == null) {
                    switch (tipo) {
                        case "S":
                            this.usuario = new Servidor(this.matricula, this.nome, this.matricula);
                            break;
                        case "O":
                            this.usuario = new Operador(this.matricula, this.nome, this.matricula);
                            break;
                        case "A":
                            this.usuario = new Aluno(this.matricula, this.nome, this.matricula);
                            break;
                        case "OR":
                            this.usuario = new OutroR(this.matricula, this.nome, this.matricula);
                            break;
                    }
                    new UsuarioAbstratoDAO().Salvar(this.usuario);
                    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                    ec.redirect("");
                } else {
                    usuario.setNome(this.nome);
                    usuarioDAO.Aterar(this.usuario);
                }
                limpar();
            } else {
                if (this.nome.trim().isEmpty()) {
                    throw new NomeInvalidoException();
                } else {
                    throw new MatriculaInvalidaException();
                }
            }
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(usuarioAbstratoBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NomeInvalidoException | MatriculaInvalidaException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Preencha o campo com dados válidos e tente novamente!"));
            Logger.getLogger(usuarioAbstratoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void limpar() {
        tipo = "A";
        this.nome = "";
        this.matricula = "";
        disabled = false;
        logado = null;
        nsenha = "";
        csenha = "";
        asenha = "";
        HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        logado = (UsuarioAbstrato) sessao.getAttribute("logado");
        if (logado instanceof Operador) {
            this.usuario = new Operador();
        } else {
            this.usuario = logado;
        }
        listar();
    }

    public void listar() {
        try {
            this.usuarios = usuarioDAO.Listar();
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listarResponsaveis() {
        try {
            this.usuarios = usuarioDAO.ListarResponsaveis();
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listarOperadores() {
        try {
            this.usuarios = usuarioDAO.ListarOperadores();
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editar(UsuarioAbstrato ua) {
        this.usuario = ua;
        this.nome = ua.getNome();
        this.matricula = ua.getMatricula();
        disabled = true;
        if (ua instanceof Servidor) {
            this.tipo = "S";
        } else {
            if (ua instanceof Operador) {
                this.tipo = "O";
            } else {
                if (ua instanceof Aluno) {
                    this.tipo = "A";
                } else {
                    this.tipo = "OR";
                }
            }
        }
        listar();
    }

    public void resetSenha(UsuarioAbstrato ua) {
        try {
            ua.setSenha(ua.getMatricula());
            usuarioDAO.Aterar(ua);
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean validarSenha() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        if (!asenha.equals(logado.getSenha())) {
            if (!asenha.isEmpty()) {
                contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Senha invalida!", "Verifique sua senha e tente novamente!"));
            }
            return false;
        } else {
            if (!csenha.equals(nsenha)) {
                contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Senhas diferentes!", "Verifique sua senha e tente novamente!"));
                return false;
            } else {
                contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Senha alterada!", "Senha alterada com êxito!"));
                return true;
            }
        }
    }

    public void redefinirSenha() {
        try {
            if (validarSenha()) {
                logado.setSenha(nsenha);
                usuarioDAO.Aterar(logado);
                limpar();
            }
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void excluir(UsuarioAbstrato t) {
        try {
            this.usuarioDAO.Excluir(t.getMatricula());
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
            Logger.getLogger(usuarioAbstratoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscar(String matricula) {
        try {
            this.usuario = this.usuarioDAO.Buscar(matricula);
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String buscarResponsavel(String matricula) {
        try {
            if (!matricula.trim().isEmpty()) {
                UsuarioAbstrato responsavel = usuarioDAO.BuscarResponsavel(matricula);
                return responsavel.getNome() + " - " + getTipo(responsavel);
            } else {
                return "Preencha o campo acima ↑";
            }
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ResponsavelNaoEncontradoException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, ex.getMessage(), "Insira um matricula e tente novamente"));
            Logger.getLogger(usuarioAbstratoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String tipoMatricula(String tipo) {
        switch (tipo) {
            case "A":
                return "Sage";
            case "S":
                return "Siape";
            case "O":
                return "Siape";
            case "OR":
                return "CPF";
            default:
                return "";
        }
    }

    public UsuarioAbstrato getUsuario() {
        return this.usuario;
    }

    public void setUsuario(UsuarioAbstrato usuario) {
        this.usuario = usuario;
    }

    public List<UsuarioAbstrato> getUsuarios() {
        return this.usuarios;
    }

    public void setUsuarios(List<UsuarioAbstrato> usuarios) {
        this.usuarios = usuarios;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public UsuarioAbstrato getLogado() {
        return logado;
    }

    public void setLogado(UsuarioAbstrato logado) {
        this.logado = logado;
    }

    public String getNsenha() {
        return nsenha;
    }

    public void setNsenha(String nsenha) {
        this.nsenha = nsenha;
    }

    public String getAsenha() {
        return asenha;
    }

    public void setAsenha(String asenha) {
        this.asenha = asenha;
    }

    public String getCsenha() {
        return csenha;
    }

    public void setCsenha(String csenha) {
        this.csenha = csenha;
    }

    public UsuarioAbstrato getUsuarioSelecionado() {
        return usuarioSelecionado;
    }

    public void setUsuarioSelecionado(UsuarioAbstrato usuarioSelecionado) {
        this.usuarioSelecionado = usuarioSelecionado;
    }

    public String mascara() {
        if (tipo.equals("A")) {
            return "9999";
        } else {
            if (tipo.equals("O") | tipo.equals("S")) {
                return "9999999";
            } else {
                return "999.999.999-99";
            }
        }
    }

    public String getTipo(UsuarioAbstrato ua) {
        if (ua instanceof Servidor) {
            return "Servidor";
        } else {
            if (ua instanceof Operador) {
                return "Operador";
            } else {
                if (ua instanceof Aluno) {
                    return "Aluno";
                } else {
                    if (ua instanceof OutroR) {
                        return "Outro";
                    } else {
                        return "";
                    }
                }
            }
        }
    }
}
