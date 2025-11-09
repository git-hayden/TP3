package application;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
    private String userName;
    private String password;
    private String role;
    private boolean reviewerRequestPending;

    // Constructor to initialize a new User object with userName, password, and role.
    public User( String userName, String password, String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.reviewerRequestPending = false;
    }
    
    // Sets the role of the user.
    public void setRole(String role) {
    	this.role=role;
    }

    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    //reviewer request related methods
    public boolean isRequestPending() {
        return reviewerRequestPending;
    }

    public void setReviewerRequestPending(boolean reviewerRequestPending) {
        this.reviewerRequestPending = reviewerRequestPending;
    }

    public boolean isReviewer() {
        return "Reviewer".equals(role);
    }
}
