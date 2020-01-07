package filtro;

import entidades.Operador;
import entidades.UsuarioAbstrato;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(filterName = "Filtro", urlPatterns = {"/Restrito/*"})
public class Filtro implements Filter {

    public Filtro() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        System.out.println("Caminho atual: " + req.getRequestURL());
        System.out.println("Atributo de Sessão: " + req.getSession().getAttribute("logado"));
        UsuarioAbstrato sessaoUser = (UsuarioAbstrato) req.getSession().getAttribute("logado");
        String caminho = req.getRequestURL().toString();
        if (sessaoUser != null) {
            if (caminho.contains("PWDP/index.xhtml")) {
                if (sessaoUser instanceof Operador) {
                    res.sendRedirect(req.getContextPath() + "/Restrito/Operador/");
                } else {
                    res.sendRedirect(req.getContextPath() + "/Restrito/Responsaveis/");
                }
            } else {
                if (caminho.contains("PWDP/faces/Restrito/Operador/")) {
                    if (sessaoUser instanceof Operador) {
                        chain.doFilter(request, response);
                    } else {
                        res.sendRedirect(req.getContextPath() + "/faces/Restrito/Responsaveis/index.xhtml");
                    }
                } else {
                    if (caminho.contains("PWDP/faces/Restrito/Responsaveis/")) {
                        if (!(sessaoUser instanceof Operador)) {
                            chain.doFilter(request, response);
                        } else {
                            res.sendRedirect(req.getContextPath() + "/faces/Restrito/Operador/index.xhtml");
                        }
                    } else {
                        chain.doFilter(request, response);
                    }
                }
            }
        } else {
            if (caminho.contains("/Restrito/")) {
                res.sendRedirect(req.getContextPath() + "/");
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
    }

}
