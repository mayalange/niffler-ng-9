package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserDataUserRepository;
import guru.qa.niffler.data.repository.impl.UserDataUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositorySpringJdbc;
import guru.qa.niffler.jupiter.extension.ClientResolver;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UsersDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@ExtendWith(ClientResolver.class)
public class JdbcTest {

    private SpendClient spendClient;
    private UsersClient usersClient;

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
        SpendJson spend = spendClient.create(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-4",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx-4",
                        "duck"
                )
        );

        System.out.println(spend);
    }

    static UsersDbClient usersDbClient = new UsersDbClient();

    @ValueSource(strings = {
            "valentin-12"
    })
    @ParameterizedTest
    void springJdbcTest(String uname) {
        UserJson user = usersClient.createUser(
                uname,
                "12345"
        );
        usersClient.addIncomeInvitation(user, 1);
        usersClient.addOutcomeInvitation(user, 1);
        usersClient.addFriend(user, 1);
    }

}