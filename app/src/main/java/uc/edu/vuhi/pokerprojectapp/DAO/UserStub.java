package uc.edu.vuhi.pokerprojectapp.DAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uc.edu.vuhi.pokerprojectapp.DTO.UserDTO;

public class UserStub implements IUser {
    /**
     * Get top 3 users that sort by user's point
     */
    @Override
    public List<UserDTO> top3(){

        List<UserDTO> allUsers = new ArrayList<UserDTO>();

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

        allUsers.add(hai);
        allUsers.add(jonny);
        allUsers.add(sing);
        allUsers.add(lux);
        allUsers.add(teemo);

        Comparator<UserDTO> comparator = new Comparator<UserDTO>() {
            @Override
            public int compare(UserDTO left, UserDTO right) {
                return right.getPoint() - left.getPoint();
            }
        };
        Collections.sort(allUsers, comparator);
        List<UserDTO> top3 = allUsers.subList(0,3);

        return top3;
    }

/*    @Override
    public UserDTO createUser(String email, String password, String nickName){
        UserDTO newUser = new UserDTO();

        return ;
    }*/
}
