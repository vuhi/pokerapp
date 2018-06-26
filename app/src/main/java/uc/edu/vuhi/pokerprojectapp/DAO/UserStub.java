package uc.edu.vuhi.pokerprojectapp.DAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import uc.edu.vuhi.pokerprojectapp.DTO.UserDTO;

public class UserStub implements IUser {

    /**
     * Given UserDTO, save user to database, note that I check for single field to get specific
     * error msg
     * @param user
     * @throws Exception if email, password, firstName, lastName, nickname is empty
     */
    @Override
    public void save(UserDTO user) throws Exception {

        if(user.getEmail() == null || user.getEmail().isEmpty())
        {
            throw new Exception("Email is empty");
        }
        if(user.getPassword() == null || user.getPassword().isEmpty())
        {
            throw new Exception("Password is empty");
        }
        if(user.getFirstName() == null || user.getFirstName().isEmpty())
        {
            throw new Exception("FirstName is empty");
        }
        if(user.getLastName() == null || user.getLastName().isEmpty())
        {
            throw new Exception("LastName is empty");
        }
        if(user.getNickname() == null || user.getNickname().isEmpty())
        {
            throw new Exception("NickName is empty");
        }
    }

    /**
     * Given user's email, get the user
     * @param id An integer represent user's id in database
     * @return UserDTO match id
     * @throws Exception
     */
    @Override
    public UserDTO fetch(int id) throws Exception {

        //Make sure their is no space or case sensitive
        if(id == 123456789)
        {
            UserDTO Caz = new UserDTO("caz@mail.com","caz12345","Caz",
                "Zac","cazz");
            return Caz;
        }
        else
        {
            throw new Exception("Id does not exist or was deleted");
        }
    }

    /**
     * Get all users in database
     * @return Hashmap of users with user's email as key
     */
    @Override
    public HashMap<String, UserDTO> fetchAll() {

        HashMap allUsers = new HashMap();

        UserDTO hai = new UserDTO();
        hai.setEmail("vuhi@mail.abc");
        hai.setPassword("vuhi0123");
        hai.setFirstName("Hai");
        hai.setLastName("Vu");
        hai.setNickname("vuhi");
        hai.setPoint(200);

        UserDTO jonny = new UserDTO();
        jonny.setEmail("jonny@mail.abc");
        jonny.setPassword("jonny0123");
        jonny.setFirstName("Jonny");
        jonny.setLastName("Smith");
        jonny.setNickname("jonny");
        jonny.setPoint(300);

        UserDTO sing = new UserDTO();
        sing.setEmail("sing@mail.abc");
        sing.setPassword("sing0123");
        sing.setFirstName("Sing");
        sing.setLastName("Jai");
        sing.setNickname("sing");
        sing.setPoint(150);

        UserDTO lux = new UserDTO();
        lux.setEmail("lux@mail.abc");
        lux.setPassword("lux0123");
        lux.setFirstName("Lux");
        lux.setLastName("Nolan");
        lux.setNickname("lux");
        lux.setPoint(4500);

        UserDTO teemo = new UserDTO();
        teemo.setEmail("teemo@mail.abc");
        teemo.setPassword("teemo0123");
        teemo.setFirstName("Teemo");
        teemo.setLastName("Mush");
        teemo.setNickname("teemo");
        teemo.setPoint(25000);

        allUsers.put(hai.getEmail(), hai);
        allUsers.put(jonny.getEmail(), jonny);
        allUsers.put(sing.getEmail(), sing);
        allUsers.put(lux.getEmail(), lux);
        allUsers.put(teemo.getEmail(), teemo);

        return allUsers;
    }
}

