package org.example.app.banji;

import com.alibaba.fastjson.JSON;
import org.example.app.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Sample010 {

    public String selectAll() {
        List<BanjiBean> list = new ArrayList<>();
        String json = "";
        try {
            String sql = "select * from t_banji";
            int rowCount = 0;
            try (Connection tempConn = DB.getConnetion();
                 Statement tempStmt = tempConn.createStatement();
                 ResultSet tempRs = tempStmt.executeQuery(sql)) {
                while (tempRs.next()) {
                    rowCount++;
                }
            }

            for (int i = 0; i < rowCount; i++) {
                String pagedSql = "SELECT * FROM t_banji LIMIT 1 OFFSET ?";
                try (Connection connection = DB.getConnetion();
                     PreparedStatement statement = connection.prepareStatement(pagedSql)) {
                    statement.setInt(1, i);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            BanjiBean bean = new BanjiBean();
                            bean.setId(resultSet.getInt("id"));
                            bean.setBanji_name(resultSet.getString("banji_name"));
                            bean.setZhuanye(resultSet.getString("zhuanye"));
                            bean.setFudaoyuan(resultSet.getString("fudaoyuan"));
                            bean.setRenshu(resultSet.getInt("renshu"));
                            list.add(bean);
                        }
                    }
                }
            }
            json = JSON.toJSONString(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return json;
    }
}
