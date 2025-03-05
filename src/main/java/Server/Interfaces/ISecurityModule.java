package Server.Interfaces;

import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface ISecurityModule {

    String cipherString(String str);
    String decipherString(String str);
}
