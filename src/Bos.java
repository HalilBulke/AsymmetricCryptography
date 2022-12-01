public class Bos {
    //            String result = null;
//            Process p = Runtime.getRuntime().exec("wmic baseboard get serialnumber");
//            BufferedReader input
//                    = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line;
//            while ((line = input.readLine()) != null)
//            {
//                result += line;
//            }
//            if (result.equalsIgnoreCase(" ")) {
//                System.out.println("Result is empty");
//            } else
//            {
//                System.out.println(result);
//            }
//            input.close();
//

//            File file = File.createTempFile("realhowto", ".vbs");
//            file.deleteOnExit();
//            FileWriter fw = new java.io.FileWriter(file);
//
//            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
//                    + "Set colItems = objWMIService.ExecQuery _ \n" + "   (\"Select * from Win32_BaseBoard\") \n"
//                    + "For Each objItem in colItems \n" + "    Wscript.StdOut.Writeline objItem.SerialNumber \n"
//                    + "    exit for  ' do the first cpu only! \n" + "Next \n";
//            fw.write(vbs);
//            fw.close();
//            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
//            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line, result = new String();
//            while ((line = input.readLine()) != null) {
//                result += line;
//            }
//            if (result.equalsIgnoreCase(" ")) {
//                System.out.println("Result is empty");
//            } else {
//                System.out.println("Result :>" + result);
//            }
//            input.close();
}
