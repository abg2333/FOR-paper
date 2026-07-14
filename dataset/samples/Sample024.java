package org.example.app.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebFilter(filterName = "Sample024", urlPatterns = {"main.jsp"})
public class Sample024 implements Filter {

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;

        String loginname = (String) request.getSession().getAttribute("yonghu");
        if (loginname == null) {
            String tishi = "请输入账号";
            response.setContentType("text/html;charset=UTF-8");
            String url = "'" + request.getContextPath() + "/login.jsp'";
            PrintWriter writer = response.getWriter();
            writer.write("<script>");
            writer.write("alert('" + tishi + "');");
            writer.write("top.document.location.href=" + url);
            writer.write("</script>");
            writer.close();
            return;
        }
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {
    }
}
