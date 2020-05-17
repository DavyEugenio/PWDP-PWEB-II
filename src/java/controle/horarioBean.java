package controle;

import DAO.HorarioDAO;
import DAO.UsuarioAbstratoDAO;
import Excecoes.DAOHorarioVazioException;
import Excecoes.HorarioIntercaladoException;
import Excecoes.HorarioInvalidoException;
import Excecoes.HorarioNaoEncontradoException;
import Excecoes.MatriculaInvalidaException;
import Excecoes.StatusInvalidoException;
import Excecoes.UsuarioNaoEncontradoException;
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
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

@SessionScoped
@ManagedBean

public class horarioBean {

    private Horario horario = new Horario(), horarioSelecionado;
    private final Conversoes c = new Conversoes();
    HorarioDAO horarioDAO = new HorarioDAO();
    private List<Horario> horarios = new ArrayList<>(), horariosEmEsperaOuEmAndamento = new ArrayList<>();
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
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            horario.setEntrada(c.conversorDT(data, horaE));
            horario.setSaida(c.conversorDT(data, horaS));
            if (!disabled) {
                horario.setStatus(status);
            }
            if (horario.getId() == pid) {
                if (validar()) {
                    horarioDAO.salvar(horario);
                    contexto.getExternalContext().getFlash().setKeepMessages(true);
                    contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação bem sucedida!", "Reserva " + formatarData(horario.getEntrada()) + " - " + formatarHora(horario.getEntrada()) + " - " + formatarHora(horario.getSaida()) + " salva com êxito!"));
                    contexto.getExternalContext().redirect("");
                } else {
                    throw new HorarioInvalidoException();
                }
            } else {
                if (status.equals("Cancelado")) {
                    horario = horarioDAO.buscar(horario.getId());
                    horario.setStatus(status);
                    horarioDAO.alterar(horario);
                    contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação bem sucedida!", "Reserva " + formatarData(horario.getEntrada()) + " - " + formatarHora(horario.getEntrada()) + " - " + formatarHora(horario.getSaida()) + " cancelada com êxito!"));
                } else {
                    if (horario.getStatus().equals("Em andamento")) {
                        horario = horarioDAO.buscar(horario.getId());
                        horario.setSaida(LocalDateTime.now());
                        horario.setStatus(status);
                        horarioDAO.alterar(horario);
                        contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação bem sucedida!", "Reserva " + formatarData(horario.getEntrada()) + " - " + formatarHora(horario.getEntrada()) + " - " + formatarHora(horario.getSaida()) + " finalizanda com êxito!"));
                    } else {
                        throw new StatusInvalidoException();
                    }
                }
            }
            limpar();
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HorarioIntercaladoException | HorarioInvalidoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Insira um horário válido e tente novamente!"));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MatriculaInvalidaException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Preencha o campo com dados válidos e tente novamente!"));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (StatusInvalidoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), "Não é possivel atribuir status de \"Finalizado\" à uma reserva que não esteja \"Em andamento\""));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HorarioNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Reserva não encontrada!", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void limpar() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            horario = new Horario();
            horarioSelecionado = new Horario();
            horaE = c.conversorLDT(LocalDateTime.now());
            horaS = c.conversorLDT(LocalDateTime.now());
            data = c.conversorLDT(LocalDateTime.now());
            inicioB = c.conversorLDT(LocalDateTime.now());
            finalB = c.conversorLDT(LocalDateTime.now());
            pid = horarioDAO.proximoId();
            horario.setId(pid);
            disabled = false;
            status = "Em espera";
            AtualizarHorarios();
            HttpSession sessao = (HttpSession) contexto.getExternalContext().getSession(false);
            UsuarioAbstrato logado = (UsuarioAbstrato) sessao.getAttribute("logado");
            if (!(logado instanceof Operador)) {
                horario.setMatricula_responsavel(logado.getMatricula());
                horarios = horarioDAO.buscarPorResponsavel(logado.getMatricula());
                listarEmEsperaOuEmAndamento();
            } else {
                listar();
            }
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cancelarBusca() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            inicioB = c.conversorLDT(LocalDateTime.now());
            finalB = c.conversorLDT(LocalDateTime.now());
            HttpSession sessao = (HttpSession) contexto.getExternalContext().getSession(false);
            UsuarioAbstrato logado = (UsuarioAbstrato) sessao.getAttribute("logado");
            if (!(logado instanceof Operador)) {
                horarios = horarioDAO.buscarPorResponsavel(logado.getMatricula());
                listarEmEsperaOuEmAndamento();
            } else {
                listar();
            }
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listar() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            horarios = horarioDAO.listar();
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DAOHorarioVazioException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não há reservas registradas", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listarEmEsperaOuEmAndamento() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            if (horarioDAO.listarEmEsperaOuEmAndamento().isEmpty()) {
                throw new HorarioNaoEncontradoException();
            } else {
                horariosEmEsperaOuEmAndamento = horarioDAO.listarEmEsperaOuEmAndamento();
            }
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HorarioNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Reserva não encontrada!", "Não há reservas \"Em espera\" ou \"Em andamento\""));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void editar(Horario h) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            horario = h;
            status = h.getStatus();
            data = c.conversorLDT(h.getEntrada());
            horaE = c.conversorLDT(h.getEntrada());
            horaS = c.conversorLDT(h.getSaida());
            disabled = true;
        } catch (ParseException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void excluir(Horario h) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            horarioDAO.excluir(h.getId());
            limpar();
            contexto.getExternalContext().getFlash().setKeepMessages(true);
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação bem sucedida!", "Reserva " + formatarData(horario.getEntrada()) + " - " + formatarHora(horario.getEntrada()) + " - " + formatarHora(horario.getSaida()) + " excluída com êxito!"));
            contexto.getExternalContext().redirect("");
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HorarioNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Reserva não encontrada!", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscar(int id) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            horario = horarioDAO.buscar(id);
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HorarioNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Reserva não encontrada!", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscarPorResponsavel(String matricula) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            horarios = horarioDAO.buscarPorResponsavel(matricula);
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String qtdHorariosR(String matricula) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            UsuarioAbstratoDAO uadao = new UsuarioAbstratoDAO();
            if (uadao.buscar(matricula) instanceof Operador) {
                return "-";
            } else {
                buscarPorResponsavel(matricula);
                if (horarios == null) {
                    return "0";
                } else {
                    return horarios.size() + "";
                }
            }
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UsuarioNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário não encontrado!", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "-";
    }

    public String qtdHorariosEmEsperaOuEmAndamento(String matricula) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            UsuarioAbstratoDAO uadao = new UsuarioAbstratoDAO();
            if (uadao.buscar(matricula) instanceof Operador) {
                return "-";
            } else {
                if (horarioDAO.buscarPorEmEsperaOuEmAndamento(matricula) == null) {
                    HttpSession sessao = (HttpSession) contexto.getExternalContext().getSession(false);
                    UsuarioAbstrato logado = (UsuarioAbstrato) sessao.getAttribute("logado");
                    if (!(logado instanceof Operador)) {
                        throw new HorarioNaoEncontradoException();
                    }
                    return 0 + "";
                } else {
                    return horarioDAO.buscarPorEmEsperaOuEmAndamento(matricula).size() + "";
                }
            }
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HorarioNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Reserva não encontrada!", "Não há reservas \"Em espera\" ou \"Em andamento\""));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UsuarioNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Usuário não encontrado!", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0 + "";
    }

    public void buscarPorEspaco(int id) {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            horarios = horarioDAO.buscarPorEspaco(id);
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
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
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            horarios = horarioDAO.buscarPorIntervalo(c.conversorDT2(inicioB), c.conversorDT2(finalB));
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HorarioNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Reserva não encontrada!", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscarIntervaloEmEsperaOuEmAndamento() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            horariosEmEsperaOuEmAndamento = horarioDAO.buscarPorIntervaloEmEsperaOuEmAndamento(c.conversorDT2(inicioB), c.conversorDT2(finalB));
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HorarioNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Reserva não encontrada!", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void buscarIntervaloResponsaveis() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            HttpSession sessao = (HttpSession) contexto.getExternalContext().getSession(false);
            UsuarioAbstrato logado = (UsuarioAbstrato) sessao.getAttribute("logado");
            horarios = horarioDAO.buscarPorIntervaloPorResponsavel(c.conversorDT2(inicioB), c.conversorDT2(finalB), logado.getMatricula());
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HorarioNaoEncontradoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Reserva não encontrada!", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void AtualizarHorarios() {
        FacesContext contexto = FacesContext.getCurrentInstance();
        try {
            horarioDAO.atualizarStatus();
        } catch (SQLException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro SQL", ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HorarioNaoEncontradoException ex) {
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (HorarioIntercaladoException ex) {
            contexto.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            Logger.getLogger(horarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean disabledEditar(Horario horario) {
        if (horario.getStatus().equals("Em espera")) {
            return false;
        } else {
            return !horario.getStatus().equals("Em andamento");
        }
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

    public List<Horario> getHorariosEmEsperaOuEmAndamento() {
        return horariosEmEsperaOuEmAndamento;
    }

    public void setHorariosEmEsperaOuEmAndamento(List<Horario> horariosEmEsperaOuEmAndamento) {
        this.horariosEmEsperaOuEmAndamento = horariosEmEsperaOuEmAndamento;
    }
}
