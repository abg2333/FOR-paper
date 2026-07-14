package org.example.app.user;

import com.alibaba.fastjson.JSON;
import org.example.app.db.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Sample002 {

    public String seletcUser(String xingming) {
        List list = new ArrayList();
        String json = "";
        try (Connection connetion = DB.getConnetion();
             Statement statement = connetion.createStatement()) {
            String sql = "select * from t_user where xingming like '%" + xingming + "%'";
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    UserBean userBean = new UserBean();
                    userBean.setId(resultSet.getInt("id"));
                    userBean.setName(resultSet.getString("name"));
                    userBean.setPass(resultSet.getString("pass"));
                    userBean.setXingming(resultSet.getString("xingming"));
                    userBean.setBanji(resultSet.getString("banji"));
                    userBean.setZhuanye(resultSet.getString("zhuanye"));
                    userBean.setXingbie(resultSet.getString("xingbie"));
                    userBean.setTel(resultSet.getString("tel"));
                    list.add(userBean);
                }
            }
            json = JSON.toJSONString(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return json;
    }
}
