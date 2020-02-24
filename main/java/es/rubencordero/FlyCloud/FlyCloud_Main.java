package es.rubencordero.FlyCloud;


import es.rubencordero.FlyCloud.Sessions.SessionsController;

public class FlyCloud_Main
{
    public static boolean running;

    public static void main(String[] args)
    {
        running = true;
        ConnectionManager serverManager = new ConnectionManager();
        serverManager.StartServer(698);

        SessionsController sessionsController = new SessionsController(serverManager.getSqlManager());
        sessionsController.start();

        while(running)
        {
            serverManager.Listen();
            ClientManager clientManager = new ClientManager(serverManager);
            clientManager.start();
        }

        serverManager.CloseServer();
    }
}
