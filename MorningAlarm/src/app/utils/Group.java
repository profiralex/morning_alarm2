package app.utils;

/**
 * Created by alexandr on 1/10/14.
 */
public class Group {
    private String name;
    private String message;
    private int id;
    private String alarmId;

    public Group(int id,String name, String message, String alarmId){
        this.id = id;
        this.name = name;
        this.message = message;
        this.alarmId = alarmId;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }
}

