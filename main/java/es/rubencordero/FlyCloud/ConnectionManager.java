package es.rubencordero.FlyCloud;

import es.rubencordero.FlyCloud.Utils.AddressInfo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager
{
    public ServerSocket serverSocket;
    public Socket localSocket;
    private Connection sqlManager;
    private AddressInfo addressInfo;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private int bytesReceived;
    private boolean connected;

    ConnectionManager()
    {
        System.out.println("Creating connection manager");
    }

    public void Copy(ConnectionManager tmpConManager)
    {
        try
        {
            this.localSocket = tmpConManager.localSocket;
            this.sqlManager = tmpConManager.sqlManager;

            this.localSocket.setTcpNoDelay(true);
            this.inputStream = new DataInputStream(new BufferedInputStream(localSocket.getInputStream()));
            this.outputStream = new DataOutputStream(localSocket.getOutputStream());
            this.connected = localSocket.isConnected();

            addressInfo = new AddressInfo();
            addressInfo.ParseFromTo(localSocket.getRemoteSocketAddress().toString(), '/', ':');
            System.out.println("New connection from: " + addressInfo.getIP());

        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
    }

    public void StartServer(int port)
    {
        try
        {
            serverSocket = new ServerSocket(port);
            String url = "jdbc:mysql://localhost:3306/FlyCloud_DB";
            sqlManager = DriverManager.getConnection(url, "root", "g6g94eqede704fdddnevermind");
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
        catch (SQLException e)
        {
            System.out.println("Error: " + e);
        }


    }

    public void Listen()
    {
        try
        {
            localSocket = serverSocket.accept();
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
    }

    public void CloseServer()
    {
        try
        {
            inputStream.close();
            outputStream.close();
            serverSocket.close();
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);

        }
    }

    public void CloseConnection()
    {
        try
        {
            inputStream.close();
            outputStream.close();
            localSocket.close();
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
    }

    public void Connect(String ip, int port)
    {
        try
        {
            localSocket = new Socket(ip, port);
            inputStream = new DataInputStream(new BufferedInputStream(localSocket.getInputStream()));
            outputStream = new DataOutputStream(localSocket.getOutputStream());
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
    }

    public int GetNumber()
    {
        int number = 0;
        try
        {
            number = inputStream.readInt();
            //System.out.println("Recibido: " + number);
        }
        catch (IOException err)
        {
            System.out.println("Error at reading: " + err);
            System.exit(-1);
        }
        return number;
    }

    public void SendNumber(int number)
    {
        try
        {
            outputStream.writeInt(number);
            //System.out.println("Sending: " + number);
        } catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
    }

    public char GetChar()
    {
        char inputChar = ' ';
        try
        {
            inputChar = inputStream.readChar();
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
        return inputChar;
    }

    public void SendChar(char character)
    {
        try
        {
            outputStream.writeChar(character);
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-1);
        }
    }

    /*
     * Primero obtenemos la longitud de la cadena a recibir
     * luego obtenemos la cadena de esa longitud.
     * */
    public String GetString()
    {
        String outputString = null;
        int filenameSize = GetNumber();
        byte[] buffer = new byte[filenameSize];
        try
        {
            int bytesReceived = inputStream.read(buffer);
            outputString = new String(buffer);
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-5);
        }
        return outputString;
    }

    public void SendString(String filename)
    {
        SendNumber(filename.length());
        byte[] buffer = filename.getBytes();
        try
        {
            outputStream.write(buffer);
        }
        catch (IOException err)
        {
            System.out.println("Error: " + err);
            System.exit(-5);
        }
    }

    public byte[] GetBytes(int size)
    {
        byte[] tmpBuffer = new byte[size];
        byte[] buffer = new byte[size];
        try
        {
            bytesReceived = inputStream.read(tmpBuffer);
            buffer = new byte[bytesReceived];
            //System.out.println("Bytes received: " + bytesReceived);
            //System.out.println("Copying array... ");

            System.arraycopy(tmpBuffer, 0, buffer, 0, bytesReceived);
        }
        catch (IOException err)
        {
            System.out.println("Error al recibir datos binarios: " + err);
            System.exit(-6);
        }
        return buffer;
    }

    public void SendBytes(byte[] bytes)
    {
        try
        {
            outputStream.write(bytes);
        }
        catch (IOException err)
        {
            System.out.println("Error al enviar datos binarios: " + err);
            System.exit(-6);
        }
    }

    public void SendFile(FileManager fileManager, int nPackages, int sizePackages, int remainder)
    {
        int averageSpeed = 0, countSpeed = 0, sumSpeed = 0;
        long len = 0;
        int packagesCount = 0;
        long startProgram = System.currentTimeMillis();
        while (len < fileManager.getFileSize())
        {
            int chunkSize = (packagesCount == nPackages-1 && remainder != 0) ? remainder : sizePackages;
            System.out.println("###################################################");
            System.out.println("Sending Package: " + (packagesCount+1) + " / " + nPackages);

            byte[] dataBuffer = fileManager.ReadBytes(chunkSize);
            int realBytesSent = 0;

            System.out.println("Enviando: " + chunkSize + " Bytes");

            long startTime = System.currentTimeMillis();
            SendBytes(dataBuffer);

            /*while (realBytesSent != chunkSize)
            {
                int tmp = clientManager.GetNumber();
                realBytesSent += tmp;
                System.out.println("The server received " + realBytesSent + "/" + chunkSize + " Bytes");
            }*/

            realBytesSent = GetNumber();
            len += realBytesSent;
            packagesCount++;

            long actualTime = System.currentTimeMillis()-startTime;
            if(actualTime > 0)
            {
                countSpeed += realBytesSent/actualTime;
            }
            sumSpeed++;
            averageSpeed = countSpeed/sumSpeed*1000;
            if(averageSpeed > 0)
                System.out.println("Average Transfer speed: " + Math.round(averageSpeed/Math.pow(2,20)) + " MiB/s " + Math.round(averageSpeed/Math.pow(2,10)) + " KiB/s");
            System.out.println((Math.round(System.currentTimeMillis()-startProgram)/1000) + " of " + Math.round(fileManager.getFileSize()/(double)averageSpeed) + " seconds");
        }
        System.out.println("Enviado en " + ((System.currentTimeMillis()-startProgram)/1000) + " segundos");
    }

    public void ReceiveFile(String filename, int fileSize, int nPackages, int sizePackages, int remainder)
    {
        try
        {
            OutputStream outputStream = new FileOutputStream(filename);
            int i = 0, len = 0;

            while(len < fileSize)
            {
                int size = (i == nPackages-1 && remainder != 0) ? remainder : sizePackages;
                System.out.println("Getting package number: " + (i+1) + " expecting a size of " + size);

                int bytesReceived = 0;
                byte[] buffer;
                while(bytesReceived != size)
                {
                    buffer = GetBytes(size);
                    int tmp = getBytesReceived();
                    bytesReceived += tmp;
                    //SendNumber(tmp);
                    System.out.println("Bytes received: " + bytesReceived + " / " + size);
                    outputStream.write(buffer);
                }

                SendNumber(bytesReceived);

                len += bytesReceived;
                i++;
                System.out.println(len + "/" + fileSize);
            }
        }
        catch (FileNotFoundException err)
        {
            System.out.println("Error al abrir archivo: " + err);
            System.exit(-10);
        } catch (IOException err)
        {
            System.out.println("Error al escribir archivo: " + err);
            System.exit(-10);
        }
    }

    public Connection getSqlManager()
    {
        return sqlManager;
    }

    public boolean isConnected()
    {
        return connected;
    }

    public int getBytesReceived()
    {
        return bytesReceived;
    }

    public AddressInfo getAddressInfo()
    {
        return addressInfo;
    }
}
