import Server.BusinessLogic.SecurityModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class SecurityModuleTest {
    private SecurityModule securityModule;

    @Before
    public void setUp() throws Exception {
        securityModule = new SecurityModule();
    }

    @After
    public void tearDown() throws Exception {
        securityModule = null;
    }

    @Test
    public void TestEncryptDecrypt() {
        String message = "hello world!";
        SecurityModule sm = new SecurityModule();

        String encrypted = sm.cipherString(message);
        assertNotEquals(message, encrypted);

        System.out.println(encrypted);

        String decrypted = sm.decipherString(encrypted);
        assertEquals(message, decrypted);

        System.out.println(decrypted);

    }
}
