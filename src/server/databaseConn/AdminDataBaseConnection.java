package server.databaseConn;

import shared.UserType;
import transferobjects.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AdminDataBaseConnection {
    public void handlePendingEmployee(String username, boolean accept) throws SQLException {
        try(Connection connection = DatabaseConnImp.getConnection()) {
            PreparedStatement statement;
            if(accept) {
                String str = "UPDATE employee SET accepted = true\n" +
                        "WHERE username = " + username;
                statement = connection.prepareStatement(str);
            } else {
                String str = "DELETE from employee\n" +
                        "where username = " + username;
                statement = connection.prepareStatement(str);
            }
            statement.execute();
        }
    }

    public ArrayList<User> getAllPendingEmployees() throws SQLException {
        ArrayList<User> pendingEmployees = new ArrayList<>();
        try(Connection connection = DatabaseConnImp.getConnection()) {
            String str = "SELECT username, firstName, lastName, employee.accepted\n" +
                    "FROM employee\n" +
                    "WHERE employee.accepted=false;";
            PreparedStatement statement = connection.prepareStatement(str);
            ResultSet set = statement.executeQuery();
            if(set.next())
            {
                String userName = set.getString("username");
                String firstName = set.getString("firstname");
                String lastName = set.getString("lastname");
                boolean accepted = set.getBoolean("accepted");
                if(accepted == false){
                    User user = new User(userName, UserType.EMPLOYEE, firstName, lastName);
                    pendingEmployees.add(user);
                }
            }
            return pendingEmployees;
        }
    }
}

class Main {
    public static void main(String[] args) throws SQLException {
        ArrayList<User> pendingEmployees = new ArrayList<>();
        try(Connection connection = DatabaseConnImp.getConnection()) {
            String str = "SELECT username, firstName, lastName\n" +
                    "FROM employee\n" +
                    "WHERE employee.accepted=false;";
            PreparedStatement statement = connection.prepareStatement(str);
            ResultSet set = statement.executeQuery();
            while(set.next())
            {
                String userName = set.getString("username");
                String firstName = set.getString("firstname");
                String lastName = set.getString("lastname");
                    User user = new User(userName, UserType.EMPLOYEE, firstName, lastName);
                    pendingEmployees.add(user);

            }
            for (int i = 0; i < pendingEmployees.size(); i++) {
                System.out.println(i+" " + pendingEmployees.get(i)+"\n");
            }
        }

    }
}
