package org.example.app.user;

import org.example.app.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Sample004 {

    public void updateUser(UserBean userBean) {
        String sql = "UPDATE t_user SET name = ? pass = ?, xingming = ?, banji = ?, "
                + "zhuanye = ?, xingbie = ?, tel = ? WHERE id = ?";
        try (Connection connection = DB.getConnetion();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userBean.getName());
            statement.setString(2, userBean.getPass());
            statement.setString(3, userBean.getXingming());
            statement.setString(4, userBean.getBanji());
            statement.setString(5, userBean.getZhuanye());
            statement.setString(6, userBean.getXingbie());
            statement.setString(7, userBean.getTel());
            statement.setInt(8, userBean.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
