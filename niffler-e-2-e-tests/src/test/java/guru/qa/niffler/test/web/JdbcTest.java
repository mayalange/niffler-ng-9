package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserDataUserRepository;
import guru.qa.niffler.data.repository.impl.UserDataUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositorySpringJdbc;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class JdbcTest {

    @Test
    void userRepositoryJdbcTest() {
        UserDataUserRepository userRepository = new UserDataUserRepositoryJdbc();
        var users = userRepository.findAll();
        System.out.println(users.size());
        users.forEach(user -> System.out.println(user.getId() + " " + user.getUsername()));
    }

    @Test
    void friendshipRepositoryJdbcTest() {
        UserDataUserRepository userRepository = new UserDataUserRepositoryJdbc();
        var firstUser = userRepository.create(
                new UserEntity(randomUsername() + "-bestFriends", CurrencyValues.RUB));
        var secondUser = userRepository.create(
                new UserEntity(randomUsername() + "-bestFriends", CurrencyValues.RUB));
        userRepository.addFriend(firstUser, secondUser);
    }

    @Test
    void userRepositorySpringJdbcTest() {
        UserDataUserRepository userRepository = new UserdataUserRepositorySpringJdbc();
        var users = userRepository.findAll();
        System.out.println(users.size());
        users.forEach(user -> System.out.println(user.getId() + " " + user.getUsername()));
    }

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-3",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx-3",
                        "duck"
                )
        );

        System.out.println(spend);
    }

    static UserDbClient usersDbClient = new UserDbClient();

    @ValueSource(strings = {
            "valentin-12"
    })
    @ParameterizedTest
    void springJdbcTest(String uname) {
        UserJson user = usersDbClient.createUser(
                uname,
                "12345"
        );
        usersDbClient.addIncomeInvitation(user, 1);
        usersDbClient.addOutcomeInvitation(user, 1);
        usersDbClient.addFriend(user, 1);
    }

}