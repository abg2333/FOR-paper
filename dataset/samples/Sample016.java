package org.example.app.banji;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/sample016")
public class Sample016 extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String op = request.getParameter("op");
        BanjiDao banjiDao = new BanjiDao();

        if ("selectAll".equals(op)) {
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(banjiDao.selectAll());

        } else if ("add".equals(op)) {
            String banji_name = request.getParameter("banji_name").trim();
            String zhuanye = request.getParameter("zhuanye");
            String fudaoyuan = request.getParameter("fudaoyuan");
            String renshu = request.getParameter("renshu");

            if (renshu == null || !renshu.matches("\\d+")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            BanjiBean bean = new BanjiBean();
            bean.setBanji_name(banji_name);
            bean.setZhuanye(zhuanye);
            bean.setFudaoyuan(fudaoyuan);
            bean.setRenshu(Integer.parseInt(renshu));

            banjiDao.save(bean);

        } else if ("deleteBanji".equals(op)) {
            String id = request.getParameter("id");
            banjiDao.deleteBanji(id);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
