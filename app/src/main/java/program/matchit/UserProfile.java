package program.matchit;

public class UserProfile {
    private String Email;
    private String Name;
    private String Score;

    public UserProfile() {

    }

    public UserProfile(String userEmail, String userName, String userScore) {
        this.Score = userScore;
        this.Email = userEmail;
        this.Name = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }
}
