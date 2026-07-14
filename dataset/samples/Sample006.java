package org.example.app.banji;

import com.alibaba.fastjson.JSON;
import org.example.app.db.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Sample006 {

    public String selectBanji(String banji_name) {
        List<BanjiBean> list = new ArrayList<>();
        String json = "";
        try (Connection connetion = DB.getConnetion();
             Statement statement = connetion.createStatement()) {
            String sql = "select * from t_banji where banji_name like '%" + banji_name + "%'";
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    BanjiBean bean = new BanjiBean();
                    bean.setId(resultSet.getInt("id"));
                    bean.setBanji_name(resultSet.getString("banji_name"));
                    bean.setZhuanye(resultSet.getString("zhuanye"));
                    bean.setFudaoyuan(resultSet.getString("fudaoyuan"));
                    bean.setRenshu(resultSet.getInt("renshu"));
                    list.add(bean);
                }
            }
            json = JSON.toJSONString(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return json;
    }
}
