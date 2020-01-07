package controle;

import DAO.HorarioDAO;
import Excecoes.HorarioIntercaladoException;
import Excecoes.HorarioInvalidoException;
import Excecoes.MatriculaInvalidaException;
import util.Conversoes;
import entidades.Horario;
import entidades.Operador;
import entidades.UsuarioAbstrato;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@SessionScoped
@ManagedBean

public class horarioBean {

    private Horario horario = new Horario(), horarioSelecionado;
    private final Conversoes c = new Conversoes();
    HorarioDAO horarioDAO = new HorarioDAO();
    private List<Horario> horarios = new ArrayList<>(), horariosEmEspera = new ArrayList<>();
    private Date horaE, horaS, data;
    private Date inicioB, finalB;
    private String status;
    private int pid;
    boolean disabled, disabledEditar;

    public boolean validar() throws HorarioInvalidoException, MatriculaInvalidaException {
        LocalDateTime entrada = c.conversorDT(data, horaE);
        if (!horario.getMatricula_responsavel().trim().isEmpty()) {
            return c.conversorT(horaS).compareTo(c.conversorT(horaE)) > 0 & entrada.compareTo(LocalDateTime.now()) >= 0;
        } else {
            throw new MatriculaInvalidaException();
        }
    }

    public void adicionar() {
        try {
            horario.setEntrada(c.conversorDT(data, horaE));
            horario.setSaida(c.conversorDT(data, horaS));
            if (!disabled) {
                horario.setStatus(status);
            }
            if (horario.getId() == pid) {
                if (validar()) {
                    horarioDAO.Salvar(horario);
                    ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                    ec.redirect("");
                } else {
                    throw new HorarioInvalidoException();
                }
            } else {
                horario = horarioDAO.Buscar(horario.getId());
                horario.setStatus(status);
                horarioDAO.Alterar(horario);
            }
            limpar();
        } catch (SQLException | IOException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HorarioIntercaladoException | HorarioInvalidoException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Insira um horário válido e tente novamente!"));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MatriculaInvalidaException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Preencha o campo com dados válidos e tente novamente!"));
            Logger.getLogger(usuarioAbstratoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void limpar() {
        try {
            horario = new Horario();
            horarioSelecionado = new Horario();
            horaE = c.conversorLDT(LocalDateTime.now());
            horaS = c.conversorLDT(LocalDateTime.now());
            data = c.conversorLDT(LocalDateTime.now());
            inicioB = c.conversorLDT(LocalDateTime.now());
            finalB = c.conversorLDT(LocalDateTime.now());
            pid = horarioDAO.ProximoId();
            horario.setId(pid);
            disabled = false;
            status = "Em espera";
            AtualizarHorarios();
            HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            UsuarioAbstrato logado = (UsuarioAbstrato) sessao.getAttribute("logado");
            if (!(logado instanceof Operador)) {
                horario.setMatricula_responsavel(logado.getMatricula());
                horarios = horarioDAO.BuscarPorResponsavel(logado.getMatricula());
            } else {
                listar();
            }
            listarEmEspera();
        } catch (SQLException | ParseException | HorarioIntercaladoException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cancelarBusca() {
        try {
            inicioB = c.conversorLDT(LocalDateTime.now());
            finalB = c.conversorLDT(LocalDateTime.now());
            HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            UsuarioAbstrato logado = (UsuarioAbstrato) sessao.getAttribute("logado");
            if (!(logado instanceof Operador)) {
                horarios = horarioDAO.BuscarPorResponsavel(logado.getMatricula());
                listarEmEspera();
            } else {
                listar();
            }
        } catch (SQLException | ParseException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listar() {
        try {
            horarios = horarioDAO.Listar();
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listarEmEspera() {
        try {
            horariosEmEspera = horarioDAO.ListarEmEspera();
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editar(Horario h) {
        try {
            horario = h;
            status = h.getStatus();
            data = c.conversorLDT(h.getEntrada());
            horaE = c.conversorLDT(h.getEntrada());
            horaS = c.conversorLDT(h.getSaida());
            disabled = true;
        } catch (ParseException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void excluir(Horario h) {
        try {
            horarioDAO.Excluir(h.getId());
            limpar();
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect("");
        } catch (SQLException | IOException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscar(int id) {
        try {
            horario = horarioDAO.Buscar(id);
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscarPorResponsavel(String matricula) {
        try {
            horarios = horarioDAO.BuscarPorResponsavel(matricula);
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int qtdHorariosR(String matricula) {
        buscarPorResponsavel(matricula);
        if (horarios == null) {
            return 0;
        } else {
            return horarios.size();
        }
    }

    public int qtdHorariosEmEspera(String matricula) {
        try {
            if (horarioDAO.BuscarPorEmEspera(matricula) == null) {
                return 0;
            } else {
                return horarioDAO.BuscarPorEmEspera(matricula).size();
            }
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void buscarPorEspaco(int id) {
        try {
            horarios = horarioDAO.BuscarPorEspaco(id);
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int qtdHorariosE(int id) {
        buscarPorEspaco(id);
        if (horarios == null) {
            return 0;
        } else {
            return horarios.size();
        }
    }

    public void buscarIntervalo() {
        try {
            horarios = horarioDAO.BuscarPorIntervalo(c.conversorDT2(inicioB), c.conversorDT2(finalB));
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscarIntervaloEmEspera() {
        try {
            horariosEmEspera = horarioDAO.BuscarPorIntervaloEmEspera(c.conversorDT2(inicioB), c.conversorDT2(finalB));
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscarIntervaloResponsaveis() {
        try {
            HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            UsuarioAbstrato logado = (UsuarioAbstrato) sessao.getAttribute("logado");
            horarios = horarioDAO.BuscarPorIntervaloPorResponsavel(c.conversorDT2(inicioB), c.conversorDT2(finalB), logado.getMatricula());
        } catch (SQLException ex) {
            FacesContext contexto = FacesContext.getCurrentInstance();
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void AtualizarHorarios() throws SQLException, HorarioIntercaladoException {
        horarioDAO.AtualizarStatus();
    }

    public boolean disabledEditar(Horario horario) {
        return !horario.getStatus().equals("Em espera");
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }

    public Date getHoraE() {
        return horaE;
    }

    public void setHoraE(Date horaE) {
        this.horaE = horaE;
    }

    public Date getHoraS() {
        return horaS;
    }

    public void setHoraS(Date horaS) {
        this.horaS = horaS;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public HorarioDAO getHorarioDAO() {
        return horarioDAO;
    }

    public void setHorarioDAO(HorarioDAO horarioDAO) {
        this.horarioDAO = horarioDAO;
    }

    public Date getInicioB() {
        return inicioB;
    }

    public void setInicioB(Date inicioB) {
        this.inicioB = inicioB;
    }

    public Date getFinalB() {
        return finalB;
    }

    public void setFinalB(Date finalB) {
        this.finalB = finalB;
    }

    public boolean isDisabledEditar() {
        return disabledEditar;
    }

    public void setDisabledEditar(boolean disabledEditar) {
        this.disabledEditar = disabledEditar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Horario getHorarioSelecionado() {
        return horarioSelecionado;
    }

    public void setHorarioSelecionado(Horario horarioSelecionado) {
        this.horarioSelecionado = horarioSelecionado;
    }

    public List<Horario> getHorariosEmEspera() {
        return horariosEmEspera;
    }

    public void setHorariosEmEspera(List<Horario> horariosEmEspera) {
        this.horariosEmEspera = horariosEmEspera;
    }
    
    public String formatarHora(LocalDateTime h) {
        if (h != null) {
            return h.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return "";
    }

    public String formatarData(LocalDateTime h) {
        if (h != null) {
            return h.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }
}
