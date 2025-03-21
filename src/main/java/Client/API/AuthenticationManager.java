package Client.API;

public class AuthenticationManager {
    private static final String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z0-9_]{3,14}$";    // Source: stackoverflow.com/questions/12018245
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{0,14}$"; // Source: stackoverflow.com/questions/21456918
    private static String myUsername;
    private static char[] myPassword;

    public AuthenticationManager(String username, char[] password){
        myUsername = username;
        myPassword = password;
    }

    public static boolean isValidUsername(){
        if (myUsername == null ||myUsername.isEmpty()){
            return false;
        }
        return myUsername.matches(USERNAME_REGEX);
    }

    public static boolean isValidPassword(){
        if (myPassword == null || myPassword.length == 0){
            return false;
        }
        return (new String(myPassword)).matches(PASSWORD_REGEX);
    }

}
