package uc.edu.vuhi.pokerprojectapp.SERVICE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import uc.edu.vuhi.pokerprojectapp.DAO.IUser;
import uc.edu.vuhi.pokerprojectapp.DAO.UserStub;
import uc.edu.vuhi.pokerprojectapp.DTO.UserDTO;

public class IServiceStub implements IService {

    private IUser userDAO = new UserStub();

    /**
     * Create new record of user in database with default 100 point
     * @param user The register user
     * @throws Exception if any field in user are empty ( except Id, point)
     */
    @Override
    public void register(UserDTO user) throws Exception {

        userDAO.save(user);
    }

    /**
     * Authenticate email, password
     * @param email A text represents user's email
     * @param password A text represents user's password
     * @return true if successful, false if failed
     * @throws Exception if any error happen in system mechanism
     */
    @Override
    public boolean login(String email, String password) throws Exception {

        if(userDAO.fetchAll().containsKey(email))
        {
            //Password is case sensitive
            if(userDAO.fetchAll().get(email).getPassword().equals(password))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    /**
     *
     * @param id The id of user that want to retrieve
     * @return The user found with matching id
     * @throws Exception if there is an underlying error in the persistence mechanism.
     */
    @Override
    public UserDTO getUser(int id) throws Exception {
        //Search by integer is the fastest
        return  userDAO.fetch(id);
    }

    /**
     * Get top 3 user in ranking
     * @return 3 users that have the most point
     */
    @Override
    public List<UserDTO> getTop3(){

        //Retrieve Hash table of user
        HashMap<String, UserDTO> allUsersHash = userDAO.fetchAll();
        //Convert to ArrayList to use sort functionality
        List<UserDTO> allUsers = new ArrayList<UserDTO>(allUsersHash.values());
        //Create comparator
        Comparator<UserDTO> comparator = new Comparator<UserDTO>() {
            @Override
            public int compare(UserDTO left, UserDTO right) {
                return right.getPoint() - left.getPoint();
            }
        };
        //Use comparator to sort list with the most point user desc
        Collections.sort(allUsers, comparator);
        //Get top 3 of the list
        List<UserDTO> top3 = allUsers.subList(0,3);

        return top3;
    }
}

