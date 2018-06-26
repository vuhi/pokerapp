package uc.edu.vuhi.pokerprojectapp.DTO;

/**
 * User has Id, email, password, nickname, point
 */
public class UserDTO {

    private int id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String nickname;
    private int point;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Empty constructor, create a empty UserDTO
     */
    public UserDTO(){

    };

    /**
     * Given params, create new UserDTO
     * @param email a Text represent user's email
     * @param password a Text represent user's password
     * @param firstName a Text represent user's firstName
     * @param lastName a Text represent user's lastName
     * @param nickname a Text represent user's nickname
     */
    public UserDTO(String email, String password, String firstName, String lastName, String nickname){
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.point = 100;
    }
}
