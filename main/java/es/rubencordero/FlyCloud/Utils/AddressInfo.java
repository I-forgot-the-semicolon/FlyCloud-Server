package es.rubencordero.FlyCloud.Utils;

public class AddressInfo
{
    private String IP;

    public AddressInfo()
    {

    }

    public String ParseFromTo(String input, char from, char to)
    {
        char[] tmpArray = input.toCharArray();
        boolean startCount = false;
        int startPosition = 0, endPosition = 0;
        int offset = 0, lengthCounter = 0;

        for(int i = 0; i < input.length(); i++)
        {
            if(tmpArray[i] == from)
            {
                startPosition = i+1;
                startCount = true;
            }

            if(startCount)
                    lengthCounter++;

            if(tmpArray[i] == to)
            {
                endPosition = i;
                startCount = false;
            }
        }

        char[] IPArray = new char[lengthCounter];
        input.getChars(startPosition, endPosition, IPArray, 0);
        this.IP = new String(IPArray);

        return this.IP;
    }

    public String getIP()
    {
        return IP;
    }
}
