import Common.Models.FileData;
import Common.Models.textMessage;
import Common.Models.User;
import Server.DataAccess.DatabaseHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class DatabaseHandlerTest {
    private DatabaseHandler databaseHandler;

    @Before
    public void setUp() throws Exception {
        databaseHandler = DatabaseHandler.getInstance();
    }

    @Test
    public void TestDatabaseConnection() {
        assertNotNull("Connection should work here", databaseHandler);

        try {
            User Test = new User( "testName" , "testPassword", true);
            databaseHandler.addUserData(Test);
            System.out.println(databaseHandler.getUsersData().toString());
            assertTrue(databaseHandler.getUsersData().next());

            textMessage textTest = new textMessage("testName", "testName", "testMessage", LocalDateTime.now());
            databaseHandler.appendPendMessage(textTest);
            try {
                assertNotNull(databaseHandler.getNextPendMsg(Test));
                System.out.println(databaseHandler.getNextPendMsg(Test).toString());
            }catch (AssertionError e){
                databaseHandler.removeAllTestMsg();
                databaseHandler.removeUser(Test.getUsername());
                throw e;
            }
            databaseHandler.removeAllTestMsg();




            String testFilePath = "src/test/resources/testFile.pdf";
            FileInputStream fis = new FileInputStream(new File(testFilePath));
            byte[] fileBytes = fis.readAllBytes();

            FileData fileTest = new FileData("testName", "testName", LocalDateTime.now(), "fileTest", fileBytes.length , fileBytes);
            databaseHandler.appendPendMessage(fileTest);

            FileData fileReceived = (FileData) databaseHandler.getNextPendMsg(Test);
            databaseHandler.removeAllTestMsg();
            databaseHandler.removeUser(Test.getUsername());

            assertNotNull(fileReceived);
            assertEquals(fileTest.hashCode(), fileReceived.hashCode());


            String savedFilePath = "src/test/resources/savedFile.pdf";
            File newfile = new File(savedFilePath);
            try {
                newfile.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    @After
    public void tearDown() throws Exception {
        databaseHandler.close();
    }
}
