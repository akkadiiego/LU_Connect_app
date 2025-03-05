package Server.BusinessLogic;

import Server.Interfaces.ISecurityModule;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class SecurityModule implements ISecurityModule {
    private static final String KEY = "lu_connect";

    @Override
    public String cipherString(String str) {
        try {
            SecretKeySpec secretKeySpec = createKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte [] toEncrypt = str.getBytes("UTF-8");
            byte [] encrypted = cipher.doFinal(toEncrypt);
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public String decipherString(String str) {

        try {
            SecretKeySpec secretKeySpec = createKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte [] toDecrypt = Base64.getDecoder().decode(str);
            byte [] decrypted = cipher.doFinal(toDecrypt);
            return new String(decrypted);

        } catch (Exception e) {
            return "";
        }
    }

    private SecretKeySpec createKey() {

        try {
            byte[] str = KEY.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            str = md.digest(str);
            str = Arrays.copyOf(str, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(str, "AES");
            return secretKeySpec;
        } catch (Exception e) {
            return null;
        }
    }
}
