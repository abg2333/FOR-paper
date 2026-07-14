package org.example.app.user;

import org.example.app.db.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sample001 {

    public boolean login(String name, String pass) {
        try (Connection connetion = DB.getConnetion();
             Statement statement = connetion.createStatement()) {
            String sql = "select * from t_user where name='" + name + "' and pass='" + pass + "'";
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
