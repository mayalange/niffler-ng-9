package guru.qa.niffler.service;

import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.UserJson;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UsersClient {
    @Nonnull
    UserJson createUser(String username, String password);

    @Nonnull
    AuthUserJson update(AuthUserJson authUserJson);

    @Nonnull
    Optional<AuthUserJson> getAuthUserById(UUID id);

    @Nonnull
    Optional<AuthUserJson> getAuthUserByName(String username);

    @Nonnull
    List<AuthUserJson> findAll();

    @Nonnull
    UserJson update(UserJson userJson);

    @Nonnull
    Optional<UserJson> getUserById(UUID id);

    @Nonnull
    Optional<UserJson> getUserByName(String username);

    @Nonnull
    List<UserJson> addIncomeInvitation(UserJson targetUser, int count);

    @Nonnull
    List<UserJson> addOutcomeInvitation(UserJson targetUser, int count);

    void removeUser(AuthUserJson authUserJson);

    @Nonnull
    List<UserJson> addFriend(UserJson targetUser, int count);

    @Nonnull
    void addFriend(UserJson requester, UserJson addressee);
}