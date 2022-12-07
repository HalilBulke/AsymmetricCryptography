import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class LicenseManager {
    public Signature sr;
    public Client c;
    PrivateKey priKey;
    PublicKey pubKey;
    public LicenseManager(){
        try {
            readKeyFiles();
            this.sr = Signature.getInstance("SHA256WithRSA");
        }
        catch (Exception e){}
        c = new Client(pubKey);
        System.out.println("License Manager Service started...");
        this.verifyLicense();
    }

    public void readKeyFiles() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        byte[] priKeyBytes = Files.readAllBytes(Paths.get("private.key"));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(priKeyBytes);
        this.priKey = keyFactory.generatePrivate(keySpec);
        byte[] keyBytes = Files.readAllBytes(Paths.get("public.key"));
        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.pubKey = kf.generatePublic(spec);
    }
    public void verifyLicense(){
        try {
            c.beginProcess();
            byte[] digitalSignature = Files.readAllBytes(Paths.get("license.txt"));
            c.printMessages(false);
            System.out.print("Client -- Digital Signature: ");
            System.out.println(Base64.getEncoder().encodeToString(digitalSignature));
            sr.initVerify(c.pubKey);
            sr.update(c.hashValue);
            boolean verified = sr.verify(digitalSignature);
            if (!verified) throw new SignatureException();
            System.out.println("Client -- Succeed. The license is correct.");

        }
        catch (NoSuchFileException e){
            System.out.println("Client -- License file is not found.");
            c.printMessages(true);
            this.signLicense();
        }
        catch (SignatureException e){
            System.out.println("Client -- The license file has been broken!!");
            this.signLicense();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
        public void signLicense(){
            try {
                System.out.println("Server -- Server is being requested.");
                System.out.print("Server -- Incoming Encrypted Text: ");
                System.out.println(Base64.getEncoder().encodeToString(c.encryptedBytes));
                Cipher decryptCipher = Cipher.getInstance("RSA");
                decryptCipher.init(Cipher.DECRYPT_MODE, this.priKey);
                byte[] decryptedBytes = decryptCipher.doFinal(c.encryptedBytes);
                System.out.print("Server -- Decrypted Text: ");
                System.out.println(new String(decryptedBytes));
                System.out.print("Server -- MD5 Plain License Text: ");
                System.out.println(new String(c.hashValue));
                sr.initSign(priKey);
                sr.update(c.hashValue);
                System.out.print("Server -- Digital Signature: ");
                byte[] signedBytes = sr.sign();
                System.out.println(Base64.getEncoder().encodeToString(signedBytes));
                Files.write(Paths.get("license.txt"), signedBytes);
                System.out.println("Client -- License is not found");
                System.out.println("Client -- Succeed. The license file content is secured and signed by the server.");
            }
            catch (Exception e){
                e.printStackTrace();
            }
    }
}
