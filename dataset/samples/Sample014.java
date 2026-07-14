package org.example.app.user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@WebServlet("/sample014")
public class Sample014 extends HttpServlet {

    private final StringBuilder requestAudit = new StringBuilder();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("op");
        requestAudit.append(Instant.now())
                .append(':')
                .append(op)
                .append('\n');
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(requestAudit.toString());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
