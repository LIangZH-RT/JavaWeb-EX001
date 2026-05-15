package cloud.liang.User;

public class User {
    public static final String ACTIVE_STATUS = "ACTIVE";
    public static final String CANCELLED_STATUS = "CANCELLED";

    private int ID;
    private char sex;
    private String name;
    private String password;
    private String email;
    private String status;


    public User(){}

    public User(int ID,String name,String password, String email ,char sex){
        this(ID, name, password, email, sex, ACTIVE_STATUS);
    }

    public User(int ID, String name, String password, String email, char sex, String status) {
        this.ID = ID;
        this.name = name;
        this.password = password;
        this.email = email;
        this.sex = sex;
        this.status = status == null || status.isBlank() ? ACTIVE_STATUS : status;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public char getSex() {
        return sex;
    }

    public String getSexText() {
        return sex == '\0' ? "" : String.valueOf(sex);
    }

    public int getID() {
        return ID;
    }

    public int getId() {
        return ID;
    }

    public String getStatus() {
        return status;
    }

    public boolean isActive() {
        return ACTIVE_STATUS.equals(status);
    }

    public String getStatusText() {
        if (CANCELLED_STATUS.equals(status)) {
            return "已注销";
        }

        return "正常";
    }

}
