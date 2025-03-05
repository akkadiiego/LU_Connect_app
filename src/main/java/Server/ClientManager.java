package Server;

import Client.Client;
import Common.Models.User;
import Server.BusinessLogic.*;
import Server.DataAccess.DatabaseHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientManager extends Thread{
    private Socket clientSocket;
    private Scanner in;
    private PrintWriter out;
    private static DatabaseHandler databaseHandler;
    private AuthenticationService authenticationService;
    private MessageService messageService;
    private FileService fileService;
    private NotificationService notificationService;
    private SecurityModule securityModule;
    public static final Lock lock = new ReentrantLock();
    public User user;

    public ClientManager(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.user = null;
        this.in = new Scanner(clientSocket.getInputStream());
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        System.out.println("Conectado: " + Integer.parseInt(Thread.currentThread().getName().split("-")[3]));
        try {
            while (in.hasNextLine()) {
                switch (in.nextLine()) {
                    case "END":
                        try {
                            endClientConnection();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;

                    case "PING":
                        out.println("PONG");
                        break;
                    // TODO: Implement server functions here

                    case "REGISTER":
                        try {
                            registerUser();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;

                    case "LOGIN":
                        if (user != null) {
                            break;
                        }
                        try {
                            Login();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                }
            }
        } catch (IllegalStateException ignore){

        }
    }

    private void registerUser() throws SQLException {
        ArrayList<String> arguments = new ArrayList<>();

        out.println("Give me the username");
        if (in.hasNextLine()) {
            arguments.add(in.nextLine());
        }

        out.println("Give me the password");
        if (in.hasNextLine()) {
            arguments.add(in.nextLine());
        }

        User newUser = new User(arguments.get(0), arguments.get(1), false);

        HashSet<User> users = null;

        try {
            lock.lock();

            databaseHandler = DatabaseHandler.getInstance();
            if (databaseHandler != null) {
                users = databaseHandler.getUsersData();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseHandler.close();
            lock.unlock();
        }

        authenticationService = new AuthenticationService();
        securityModule = new SecurityModule();

        if (users == null || !authenticationService.authenticateNewUser(newUser, users)) {
            newUser.setPassword(securityModule.cipherString(newUser.getPassword()));

            try {

                lock.lock();
                databaseHandler = DatabaseHandler.getInstance();
                if (databaseHandler != null) {
                    databaseHandler.addUserData(newUser);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                databaseHandler.close();
                lock.unlock();
            }

            out.println("User registered successfully, now you can log in :)");

        }
        else {
            out.println("User already exists");
        }
    }

    public void Login() throws SQLException {
        ArrayList<String> arguments = new ArrayList<>();

        out.println("Give me your username");
        if (in.hasNextLine()) {
            arguments.add(in.nextLine());
        }

        out.println("Give me your password");
        if (in.hasNextLine()) {
            arguments.add(in.nextLine());
        }

        user = new User(arguments.get(0), arguments.get(1), false);
        HashSet<User> users = null;

        try {
            lock.lock();

            databaseHandler = DatabaseHandler.getInstance();
            if (databaseHandler != null) {
                users = databaseHandler.getUsersData();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            databaseHandler.close();
            lock.unlock();
        }

        authenticationService = new AuthenticationService();
        securityModule = new SecurityModule();

        if (users == null || !authenticationService.authenticateUser(user, users)) {
            out.println("This User does not exist!");
            user = null;
        }
        else {
            try {
                lock.lock();
                databaseHandler = DatabaseHandler.getInstance();
                if (!databaseHandler.checkUserState(user)){
                    databaseHandler.changeUserState( user, true);
                    user.setOnline(true);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                databaseHandler.close();
                lock.unlock();
            }

            out.println("You just logged!");
        }
    }

    private void endClientConnection() throws SQLException {
        if (user != null){
            user.setOnline(false);

            try {
                lock.lock();
                databaseHandler = DatabaseHandler.getInstance();
                if (databaseHandler.checkUserState(user)){
                    databaseHandler.changeUserState( user, false);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                databaseHandler.close();
                lock.unlock();
            }
        }
        closeConnection(clientSocket, in, out);
    }
    private void closeConnection(Socket clientSocket, Scanner in, PrintWriter out){
        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        in.close();
        out.close();
    }
}
