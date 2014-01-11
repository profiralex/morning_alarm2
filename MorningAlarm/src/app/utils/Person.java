package app.utils;

/**
 * Created by alexandr on 1/10/14.
 */
public class Person {
    private String email;
    private int groupId;
    private boolean accepted;

    public Person(int groupId, String email, boolean accepted){
        this.groupId = groupId;
        this.email = email;
        this.accepted = accepted;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
