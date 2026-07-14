package org.example.app.banji;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/sample018")
public class Sample018 extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String op = request.getParameter("op");
        BanjiDao banjiDao = new BanjiDao();

        if ("search".equals(op)) {
            String banji_name = request.getParameter("banji_name");
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.write("<html><body>");
            out.write("<h2>搜索结果：</h2>");
            out.write("<p>您搜索的班级名称是：" + banji_name + "</p>");
            out.write("</body></html>");
            out.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
