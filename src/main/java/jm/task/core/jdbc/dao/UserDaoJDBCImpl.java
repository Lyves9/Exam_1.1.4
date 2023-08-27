package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {

    Util util = new Util();

    public UserDaoJDBCImpl(Util util) {
        this.util = util;
    }

    private static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS users " +
            "(id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), lastName VARCHAR(255), age INT)";

    private static String DELETE_TABLE = "DROP TABLE IF EXISTS Users;";

    private static String INSERT_USER = "INSERT INTO Users(name, lastName, age) VALUES (?,?,?);";
    private static String DELETE_USER = "DELETE FROM Users WHERE id=?;";

    private static Connection connection = Util.getConnection();


    public UserDaoJDBCImpl() {

    }

    public void createUsersTable() {
        try(PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TABLE)) {
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void dropUsersTable() {
        try(PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TABLE)) {
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    public void saveUser(String name, String lastName, byte age) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }


        public void removeUserById ( long id){
            try(PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER)) {
                preparedStatement.setLong(1,id);
                preparedStatement.executeUpdate();
                connection.commit();
            }  catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        public List<User> getAllUsers () {
            List<User> userList = new ArrayList<>();
            try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Users;")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {

                    Long id = resultSet.getLong("id");
                    String name = resultSet.getString("name");

                    String lastName = resultSet.getString("lastName");

                    Byte age = resultSet.getByte("age");

                    User user = new User(name, lastName, age);
                    user.setId(id);
                    userList.add(user);
                }
                connection.commit();
            } catch (SQLException e) {

                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return userList;
        }

    public void cleanUsersTable() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE users");
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

