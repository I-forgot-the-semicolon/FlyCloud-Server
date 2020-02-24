package es.rubencordero.FlyCloud.Sessions;

import es.rubencordero.FlyCloud.FlyCloud_Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SessionsController extends Thread
{
    public volatile static List<Session> sessions;
    private Connection sqlConnection;

    public SessionsController(Connection sqlConnection)
    {
        this.sqlConnection = sqlConnection;
        sessions = new ArrayList<>();
    }

    @Override
    public void run()
    {
        super.run();
        System.out.println("Starting session system");
        while(FlyCloud_Main.running)
        {
            boolean updated = false;
            for(int i = 0; i < sessions.size() && !updated; i++)
            {
                if(sessions.get(i).checkForTimeOut())
                {
                    System.out.println("Cerrando session: " + sessions.get(i).getUsername());
                    try
                    {
                        String statement = "DELETE FROM Logged_users WHERE username = ? AND IP = ?";
                        PreparedStatement sqlQuery = sqlConnection.prepareStatement(statement);
                        sqlQuery.setString(1, sessions.get(i).getUsername());
                        sqlQuery.setString(2, sessions.get(i).getIp());
                        sqlQuery.execute();
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }

                    sessions.remove(i);
                    updated = true;
                }
            }

            try
            {
                Thread.sleep(10000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
