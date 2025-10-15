package guru.qa.niffler.test.api;

import guru.qa.niffler.api.UsersApiClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserApiTests {

    private final UsersApiClient usersApiClient = new UsersApiClient();

    @User()
    @Test
    @Order(1)
    @DisplayName("Users table should be empty before all tests execution")
    void usersListShouldBeEmptyBeforeTests(UserJson user) {
        List<UserJson> users = usersApiClient.allUsers(user.username(), null);
        assertTrue(users.isEmpty());
    }

    @User()
    @Test
    @Order(Integer.MAX_VALUE)
    @DisplayName("Users table should be not empty after all tests execution")
    void usersListShouldNotBeEmptyAfterTests(UserJson user) {
        List<UserJson> users = usersApiClient.allUsers(user.username(), null);
        assertFalse(users.isEmpty());
    }
}
