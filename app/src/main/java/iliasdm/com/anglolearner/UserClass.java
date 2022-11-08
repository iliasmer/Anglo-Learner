package iliasdm.com.anglolearner;

public class UserClass {

    private String userName;
    private String userPassword;
    private String signupDate;
    private String lastPractice;
    private String testsToday;
    private String dayStreakSince;
    private String consecutiveCorrectAnswers;

    public UserClass () {}

    public UserClass(String userName, String userPassword, String signupDate, String lastPractice, String testsToday, String dayStreakSince, String consecutiveCorrectAnswers) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.signupDate = signupDate;
        this.lastPractice = lastPractice;
        this.testsToday = testsToday;
        this.dayStreakSince = dayStreakSince;
        this.consecutiveCorrectAnswers = consecutiveCorrectAnswers;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getSignupDate() {
        return signupDate;
    }

    public String getLastPractice() {
        return lastPractice;
    }

    public String getTestsToday() {
        return testsToday;
    }

    public String getDayStreakSince() {
        return dayStreakSince;
    }

    public String getConsecutiveCorrectAnswers() {
        return consecutiveCorrectAnswers;
    }
}

