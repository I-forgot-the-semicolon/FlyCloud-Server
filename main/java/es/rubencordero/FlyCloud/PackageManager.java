package es.rubencordero.FlyCloud;

public class PackageManager
{
    private int fileSize;
    private int nPackages;
    private int sizePerPackage;
    private int remainder;

    PackageManager(int fileSize)
    {
        this.fileSize = fileSize;
    }

    public void SplitFile()
    {
        long targetChunkSize = Runtime.getRuntime().freeMemory() / 2;
        //long targetChunkSize = 65536;
        int tmpNum = (int)(fileSize /targetChunkSize);
        if(tmpNum == 0)
            tmpNum = 1;

        int tmpFileSize = fileSize;
        remainder = 0;

        if(fileSize % tmpNum == 0)
        {
            nPackages = tmpNum;
            sizePerPackage = fileSize / tmpNum;
        }
        else
        {
            while(tmpFileSize % tmpNum != 0)
            {
                tmpFileSize--;
                remainder++;
            }
            nPackages = tmpNum+1;
            sizePerPackage = tmpFileSize / tmpNum;
        }
    }

    public int getnPackages() {
        return nPackages;
    }

    public int getSizePerPackage() {
        return sizePerPackage;
    }

    public int getRemainder() {
        return remainder;
    }
}