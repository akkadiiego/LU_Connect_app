import Common.Models.FileData;
import Common.Models.textMessage;
import Common.Models.User;
import Server.DataAccess.DatabaseHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
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
            assertFalse(databaseHandler.getUsersData().isEmpty());

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




            String testFilePath = "src/test/resources/example.txt";
            //String testFilePath = "src/test/resources/fileTest.pdf";
            //String testFilePath = "src/test/resources/example.jpg";
            FileInputStream fis = new FileInputStream(new File(testFilePath));
            InputStream is = new ByteArrayInputStream(fis.readAllBytes());
            int fileSize = is.available();
            byte[] data = new byte[fileSize];
            is.read(data, 0, fileSize);
            is.close();


            FileData fileTest = new FileData("testName", "testName", LocalDateTime.now(), "fileTest", fileSize , data);
            databaseHandler.appendPendMessage(fileTest);

            FileData fileReceived = (FileData) databaseHandler.getNextPendMsg(Test);
            databaseHandler.removeAllTestMsg();
            databaseHandler.removeUser(Test.getUsername());

            assertNotNull(fileReceived);


            String savedFilePath = "src/test/resources/savedExample.txt";
            //String savedFilePath = "src/test/resources/savedFile.pdf";
            //String savedFilePath = "src/test/resources/savedExample.jpg";

            OutputStream os = new FileOutputStream(new File(savedFilePath));
            os.write(fileReceived.getData());
            os.close();

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    @After
    public void tearDown() throws Exception {
        databaseHandler.close();
    }
}
