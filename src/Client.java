import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.text.MessageFormat;

public class Client {
    public String rawText;
    public Client(){
        try {
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
//            for (FileStore store: FileSystems.getDefault().getFileStores()) {
//                System.out.format("%-20s %s\n", store, store.getAttribute("volume:vsn"));
//            }
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
}
