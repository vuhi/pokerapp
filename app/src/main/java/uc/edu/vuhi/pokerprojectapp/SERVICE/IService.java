package uc.edu.vuhi.pokerprojectapp.SERVICE;

import java.util.List;

import uc.edu.vuhi.pokerprojectapp.DTO.UserDTO;

public interface IService {

    /**
     * Create new record of user in database
     * @param user The new user
     * @throws Exception if there is an underlying error in the persistence mechanism.
     */
    public void register(UserDTO user) throws Exception;

    /**
     * Attempt to login the user with the given username and password.
     * @param email
     * @param password
     * @return true if successful, false if not
     * @throws Exception if there is an underlying error in the persistence mechanism.
     */
    public boolean login(String email, String password) throws Exception;

    /**
     * Retrieve the given user
     * @param email The email of user that want to retrieve
     * @return the user retrieved
     * @throws Exception if there is an underlying error in the persistence mechanism.
     */
    public UserDTO getUser(String email) throws  Exception;

    /**
     * Get top 3 users that have the most point
     * @return collection of 3 users with the most point
     */
    public List<UserDTO> getTop3();

}
