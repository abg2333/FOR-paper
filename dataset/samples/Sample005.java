package org.example.app.user;

import org.example.app.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Sample005 {

    public void save(UserBean bean) {
        String sql = "INSERT INTO t_user "
                + "(name, pass, xingming, banji, zhuanye, xingbie, tel) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DB.getConnetion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bean.getName());
            statement.setString(2, bean.getPass());
            statement.setString(3, bean.getZhuanye());
            statement.setString(4, bean.getBanji());
            statement.setString(5, bean.getXingming());
            statement.setString(6, bean.getXingbie());
            statement.setString(7, bean.getTel());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
