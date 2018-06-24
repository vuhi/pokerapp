package uc.edu.vuhi.pokerprojectapp.DAO;

import java.util.List;

import uc.edu.vuhi.pokerprojectapp.DTO.UserDTO;

/**
 * Data access for User
 */
public interface IUser {

    /**
     * Get top 3 Users have most point
     * * @return
     */
    public List<UserDTO> top3();

    /**
     * Create a new user with 100 point by default
     * @param email A text represent user's email
     * @param password A text represent user's password
     * @param nickName A text represent user's nickName
     * @return
     */
    //public UserDTO newAccount(String email, String password, String nickName);
}
