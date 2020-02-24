package es.rubencordero.FlyCloud.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLHelper
{
    private Connection sqlConnection;
    private ResultSet resultSet;

    public SQLHelper(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
    }

    public ResultSet Query(String statement, String[] args)
    {
        ResultSet result = null;
        try
        {
            PreparedStatement sqlQuery = sqlConnection.prepareStatement(statement);
            for(int i = 0; i < args.length; i++)
            {
                sqlQuery.setString(i, args[i]);
            }
            result = sqlQuery.executeQuery();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public boolean UserExist(String username)
    {
        try
        {
            String statement = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement sqlQuery = sqlConnection.prepareStatement(statement);
            sqlQuery.setString(1, username);
            resultSet = sqlQuery.executeQuery();
            return resultSet.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean Login(String username, String password)
    {
        try
        {
            String statement = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement sqlQuery = sqlConnection.prepareStatement(statement);
            sqlQuery.setString(1, username);
            sqlQuery.setString(2, password);
            resultSet = sqlQuery.executeQuery();
            return resultSet.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void CreateAccount(String username, String password, String hash)
    {
        try
        {
            String statement = "INSERT INTO Users (username, password, hash) VALUES (?,?,?)";
            PreparedStatement sqlQuery = sqlConnection.prepareStatement(statement);
            sqlQuery.setString(1, username);
            sqlQuery.setString(2, password);
            sqlQuery.setString(3, hash);
            sqlQuery.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public boolean isLogged(String username, String ip)
    {
        try
        {
            String statement = "SELECT * FROM Logged_users WHERE username = ? AND IP = ?";
            PreparedStatement sqlQuery = sqlConnection.prepareStatement(statement);
            sqlQuery.setString(1, username);
            sqlQuery.setString(2, ip);
            resultSet = sqlQuery.executeQuery();
            return resultSet.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void CreateSession(String username, String ip)
    {
        try
        {
            String statement = "INSERT INTO Logged_users (username, IP) VALUES (?, ?)";
            PreparedStatement sqlQuery = sqlConnection.prepareStatement(statement);
            sqlQuery.setString(1, username);
            sqlQuery.setString(2, ip);
            sqlQuery.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public String getString(int id)
    {
        try
        {
            return resultSet.getString(id);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return "Error";
    }
    public ResultSet getResultSet()
    {
        return resultSet;
    }
}
