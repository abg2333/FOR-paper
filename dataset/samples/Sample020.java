package org.example.app.banji;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("sample020")
public class Sample020 extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String op = request.getParameter("op");
        BanjiDao banjiDao = new BanjiDao();

        if ("selectAll".equals(op)) {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(banjiDao.selectAll());

        } else if ("add".equals(op)) {
            String banji_name = request.getParameter("banji_name");
            String zhuanye = request.getParameter("zhuanye");
            String fudaoyuan = request.getParameter("fudaoyuan");
            String renshu = request.getParameter("renshu");

            BanjiBean bean = new BanjiBean();
            bean.setBanji_name(banji_name);
            bean.setZhuanye(zhuanye);
            bean.setFudaoyuan(fudaoyuan);
            bean.setRenshu(Integer.parseInt(renshu != null && !renshu.isEmpty() ? renshu : "0"));

            banjiDao.save(bean);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
