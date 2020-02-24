package es.rubencordero.FlyCloud.Sessions;

public class Session
{
    private String username;
    private String ip;
    private long lifeDuration;
    private long startTime;

    public Session(String username, String ip, long lifeDuration)
    {
        this.username = username;
        this.ip = ip;
        this.lifeDuration = lifeDuration;
        startTime = System.currentTimeMillis();
    }

    public boolean checkForTimeOut()
    {
        //System.out.println("Tiempo: " + (System.currentTimeMillis() - startTime)/1000);
        return (System.currentTimeMillis() - startTime)/1000 >= lifeDuration;
    }


    public String getUsername()
    {
        return username;
    }

    public String getIp()
    {
        return ip;
    }
}
