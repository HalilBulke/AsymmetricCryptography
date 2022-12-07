import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.text.MessageFormat;
import java.util.Base64;

public class Client {
    public static void main(String[] args) {
        LicenseManager manager = new LicenseManager();
    }
    public String rawText;
    public byte[] encryptedBytes;
    public byte[] hashValue;
    public PublicKey pubKey;

    public Client(PublicKey pubKey){
        try {
            this.pubKey = pubKey;
            String username = "whoami";
            String userSerialNumber ="3052-2512-5690";
            System.out.println("Client started...");
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();
            String[] hexadecimal = new String[hardwareAddress.length];
            for (int i = 0; i < hardwareAddress.length; i++) {
                hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
            }
            String macAddress = String.join(":", hexadecimal);
            System.out.println("My Mac: "+ macAddress);
            FileStore store = FileSystems.getDefault().getFileStores().iterator().next();
            String diskID = store.getAttribute("volume:vsn").toString();
            System.out.println("My Disk ID: " + diskID);

            String command = "wmic baseboard get serialnumber";
            String MBSerialNumber=new String();
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n" + "   (\"Select * from Win32_BaseBoard\") \n"
                    + "For Each objItem in colItems \n" + "    Wscript.StdOut.Writeline objItem.SerialNumber \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = new String();
            while ((line = input.readLine()) != null) {
                MBSerialNumber += line;
            }
            input.close();
            System.out.println("My Motherboard ID: " + MBSerialNumber);
            this.rawText = MessageFormat.format("{0}${1}${2}${3}${4}", username,userSerialNumber,macAddress,
                    diskID, MBSerialNumber);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void beginProcess() throws Exception{
        byte[] bytesToEncrypt = this.rawText.getBytes();
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, this.pubKey);
        this.encryptedBytes=encryptCipher.doFinal(bytesToEncrypt);
        MessageDigest digest = MessageDigest.getInstance("MD5");
        this.hashValue = digest.digest(this.rawText.getBytes());
    }
    public void printMessages(boolean printEncrypted){
        System.out.print("Client -- Raw License Text: ");
        System.out.println(this.rawText);
        if (printEncrypted) {
            System.out.print("Client -- Encrypted License Text: ");
            System.out.println(Base64.getEncoder().encodeToString(this.encryptedBytes));
        }
        System.out.print("Client -- MD5 License Text: ");
        System.out.println(new String(this.hashValue));
    }
}
