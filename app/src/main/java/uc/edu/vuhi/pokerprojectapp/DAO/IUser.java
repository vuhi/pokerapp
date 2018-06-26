package uc.edu.vuhi.pokerprojectapp.DAO;

import java.util.HashMap;
import java.util.List;

import uc.edu.vuhi.pokerprojectapp.DTO.UserDTO;

/**
 * Data access for User
 */
public interface IUser {

    /**
     * Save user to database
     * @param user the user to save
     * @throws Exception if any empty field in user
     */
    public void save(UserDTO user) throws  Exception;

    /**
     * Fetch user with given email
     * @param id A integer represent user's id in database
     * @return
     * @throws Exception
     */
    public UserDTO fetch(int id) throws Exception;

    /**
     * Get all users in database as Hashtable
     * @return a collection of users
     */
    public HashMap<String, UserDTO> fetchAll();
}
