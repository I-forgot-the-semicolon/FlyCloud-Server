package es.rubencordero.FlyCloud.Utils;

public class Status
{
    public enum Modes { Login, Logout, Upload, Download, NewAccount}
    public enum Codes {Collision, OK, NotFound, PasswdErr, AlreadyLogged, NoLogged};
}
