package Client.API;

public class AuthenticationManager {
    private static final int USER_MAX = 15;
    private static final int PASSWORD_MAX = 15;
    private static final String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z0-9_]{0,14}$";
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{0,14}$";
    private static String myUsername;
    private static char[] myPassword;

    public AuthenticationManager(String username, char[] password){
        myUsername = username;
        myPassword = password;
    }

    public static boolean isValidUsername(){
        if (myUsername.isEmpty()){
            return false;
        }
        return myUsername.matches(USERNAME_REGEX);
    }

    public static boolean isValidPassword(){
        if (myPassword.length == 0){
            return false;
        }
        return (new String(myPassword)).matches(PASSWORD_REGEX) && myPassword.length <= PASSWORD_MAX;
    }

}
