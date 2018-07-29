package uc.edu.vuhi.pokerprojectapp.DTO;

/**
 * User has Id, email, password, nickname, point
 */
public class UserDTO {

    private int id;
    private String email;
    private String nickname;
    private String imagePath;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    /**
     * Empty constructor, create a empty UserDTO
     */
    public UserDTO(){};

    /**
     * Given params, create new UserDTO
     * @param email a Text represent user's email
     * @param nickname a Text represent user's nickname
     */
    public UserDTO(String email, String nickname, String imagePath){
        this.email = email;
        this.nickname = nickname;
        this.imagePath = imagePath;
        this.point = 100;
    }

    public UserDTO(String email, String nickname){
        this.email = email;
        this.nickname = nickname;
        this.point = 100;
    }

}
