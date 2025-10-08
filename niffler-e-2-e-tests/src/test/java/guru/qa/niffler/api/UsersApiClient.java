package guru.qa.niffler.api;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ParametersAreNonnullByDefault

public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final long MAX_WAIT_TIME = 2000L;
    private static final String defaultPassword = "0523";

    private final AuthApi authApi;
    private final UserDataApi userdataApi;

    public UsersApiClient() {
        authApi = new RestClient.DefaultRestClient(CFG.authUrl()).create(AuthApi.class);
        userdataApi = new RestClient.DefaultRestClient(CFG.userDataUrl()).create(UserDataApi.class);
    }

    @Override
    @Nonnull
    @Step("Create user with name {username} using REST API")
    public UserJson createUser(String username, String password) {
        execute(authApi.getRegisterPage());
        execute(authApi.registerUser(username, password, password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")));

        StopWatch sw = StopWatch.createStarted();
        while (sw.getTime(TimeUnit.MILLISECONDS) < MAX_WAIT_TIME) {
            UserJson userJson = execute(userdataApi.currentUser(username)).body();
            if (userJson != null && userJson.id() != null) {
                return userJson.withPassword(password);
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("User with name " + username + " not created");
    }

    @NotNull
    @Override
    public AuthUserJson update(AuthUserJson authUserJson) {
        return null;
    }

    @NotNull
    @Override
    public Optional<AuthUserJson> getAuthUserById(UUID id) {
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<AuthUserJson> getAuthUserByName(String username) {
        return Optional.empty();
    }

    @NotNull
    @Override
    public List<AuthUserJson> findAll() {
        return List.of();
    }

    @NotNull
    @Override
    public UserJson update(UserJson userJson) {
        return null;
    }

    @NotNull
    @Override
    public Optional<UserJson> getUserById(UUID id) {
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<UserJson> getUserByName(String username) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public List<UserJson> addIncomeInvitation(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, defaultPassword);
                    result.add(newUser);
                    response = userdataApi.sendInvitation(
                            newUser.username(),
                            targetUser.username()
                    ).execute();
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                assertEquals(200, response.code());

                targetUser.testData()
                        .incomeInvitations()
                        .add(newUser);
            }
        }
        return result;
    }

    @Nonnull
    @Override
    public List<UserJson> addOutcomeInvitation(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, defaultPassword);
                    result.add(newUser);
                    response = userdataApi.sendInvitation(
                            targetUser.username(),
                            newUser.username()
                    ).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());

                targetUser.testData()
                        .outcomeInvitations()
                        .add(newUser);
            }
        }
        return result;
    }

    @Override
    public void removeUser(AuthUserJson authUserJson) {

    }

    @Nonnull
    @Override
    public List<UserJson> addFriend(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, defaultPassword);
                    result.add(newUser);
                    userdataApi.sendInvitation(
                            newUser.username(),
                            targetUser.username()
                    ).execute();
                    response = userdataApi.acceptInvitation(targetUser.username(), username).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());

                targetUser.testData()
                        .friends()
                        .add(response.body());
            }
        }
        return result;
    }

    @NotNull
    @Override
    public void addFriend(UserJson requester, UserJson addressee) {

    }

    @Nonnull
    protected <T> Response<T> execute(Call<T> request) {
        Response<T> response;
        try {
            response = request.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertTrue(response.isSuccessful());
        return response;
    }
}
