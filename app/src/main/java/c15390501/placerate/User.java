package c15390501.placerate;

/**
 * Created by YitChee on 06/11/2017.
 */
//used to store current user's data
public class User {
    private String username;
    private String password;
    private int level;
    private int numRated;

    public User()
    {
        username = "n/a";
        password = "n/a";
        level =0;
        numRated = 0;
    }

    public User(String username, String password, int level, int numRated)
    {
        this.username = username;
        this.password = password;
        this.level = level;
        this.numRated = numRated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNumRated() {
        return numRated;
    }

    public void setNumRated(int numRated) {
        this.numRated = numRated;
    }
}
