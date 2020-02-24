package es.rubencordero.FlyCloud;

import es.rubencordero.FlyCloud.Sessions.Session;
import es.rubencordero.FlyCloud.Sessions.SessionsController;
import es.rubencordero.FlyCloud.Utils.SQLHelper;
import es.rubencordero.FlyCloud.Utils.Status;

import java.sql.SQLException;

public class ClientManager extends Thread
{
    private ConnectionManager serverManager;

    ClientManager(ConnectionManager serverManager)
    {
        this.serverManager = new ConnectionManager();
        this.serverManager.Copy(serverManager);
    }

    @Override
    public void run()
    {
        FlowController();
    }

    public void FlowController()
    {
        int mode = serverManager.GetNumber();
        System.out.println("Modo: " + mode);

        if(mode == Status.Modes.NewAccount.ordinal())
        {
            String username = serverManager.GetString();
            String password = serverManager.GetString();
            String hash = serverManager.GetString();

            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            System.out.println("Hash: " + hash);

            int returnCode = Status.Codes.OK.ordinal();

            SQLHelper sqlHelper = new SQLHelper(serverManager.getSqlManager());
            boolean userExist = sqlHelper.UserExist(username);

            if(userExist)
            {
                System.out.println("Existe: " + sqlHelper.getString(2));
                returnCode = Status.Codes.Collision.ordinal();
            }
            else
            {
                System.out.println("Creating new account!");
                sqlHelper.CreateAccount(username, password, hash);
            }
            serverManager.SendNumber(returnCode);
        }
        else if(mode == Status.Modes.Login.ordinal())
        {
            String username = serverManager.GetString();
            int returnCode = Status.Codes.OK.ordinal();

            SQLHelper sqlHelper = new SQLHelper(serverManager.getSqlManager());
            boolean userExist = sqlHelper.UserExist(username);
            if(userExist)
            {
                String hash = sqlHelper.getString(4);
                serverManager.SendNumber(returnCode);
                serverManager.SendString(hash);
                String password = serverManager.GetString();
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);
                System.out.println("Hash: " + hash);

                boolean loginCorrect = sqlHelper.Login(username, password);
                if(loginCorrect)
                {
                    boolean isLogged = sqlHelper.isLogged(username, serverManager.getAddressInfo().getIP());
                    if(isLogged)
                    {
                        returnCode = Status.Codes.AlreadyLogged.ordinal();
                    }
                    else
                    {
                        sqlHelper.CreateSession(username, serverManager.getAddressInfo().getIP());
                        Session session = new Session(username, serverManager.getAddressInfo().getIP(), 15);
                        SessionsController.sessions.add(session);
                        System.out.println("Sesion iniciada : " + SessionsController.sessions.size());
                    }
                }
                else
                {
                    returnCode = Status.Codes.PasswdErr.ordinal();
                }
            }
            else
            {
                returnCode = Status.Codes.NotFound.ordinal();
            }

            serverManager.SendNumber(returnCode);
        }
        else if(mode == Status.Modes.Upload.ordinal())
        {
            String username = serverManager.GetString();
            SQLHelper sqlHelper = new SQLHelper(serverManager.getSqlManager());
            if(sqlHelper.isLogged(username, serverManager.getAddressInfo().getIP()))
            {
                serverManager.SendNumber(Status.Codes.OK.ordinal());
                int fileSize = serverManager.GetNumber();
                System.out.println("Filesize: " + fileSize);
                String filename = serverManager.GetString();
                System.out.println("Filename: " + filename);

                PackageManager packageManager = new PackageManager(fileSize);
                packageManager.SplitFile();

                int nPackages = packageManager.getnPackages();
                int sizePackages = packageManager.getSizePerPackage();
                int residuous = packageManager.getRemainder();

                serverManager.SendNumber(nPackages);
                serverManager.SendNumber(sizePackages);
                serverManager.SendNumber(residuous);


                serverManager.ReceiveFile(filename, fileSize, nPackages, sizePackages, residuous);
            }
            else
            {
                serverManager.SendNumber(Status.Codes.NoLogged.ordinal());
            }
        }
        else if(mode == Status.Modes.Download.ordinal())
        {
            String filename = serverManager.GetString();
            System.out.println("Filename: " + filename);

            FileManager fileManager = new FileManager(filename);
            int fileSize = (int)fileManager.getFileSize();
            System.out.println("Filesize: " + fileSize);
            serverManager.SendNumber(fileSize);

            PackageManager packageManager = new PackageManager(fileSize);
            packageManager.SplitFile();

            int nPackages = packageManager.getnPackages();
            int sizePackages = packageManager.getSizePerPackage();
            int remainder = packageManager.getRemainder();

            serverManager.SendNumber(nPackages);
            serverManager.SendNumber(sizePackages);
            serverManager.SendNumber(remainder);

            serverManager.SendFile(fileManager, nPackages, sizePackages, remainder);
        }
        System.out.println("Bye client!");
        serverManager.CloseConnection();
    }
}