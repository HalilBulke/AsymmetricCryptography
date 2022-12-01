public class LicenseManager {
    public Client c;
    public LicenseManager(){
        c = new Client();
        System.out.println(c.rawText);
        System.out.println("License Manager Service started...");
    }
}
