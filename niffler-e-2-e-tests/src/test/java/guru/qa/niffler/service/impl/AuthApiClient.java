package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.AuthClient;
import guru.qa.niffler.service.RestClient;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class AuthApiClient extends RestClient implements AuthClient {

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true);
        authApi = create(AuthApi.class);
    }

    @NotNull
    @Override
    public UserJson register(String username, String password, String passwordSubmit) {
        return null;
    }

    @Override
    public void authorize(String codeChallenge) {
        Response<Void> response;
        try {
            response = authApi.authorize(
                    "code",
                    "client",
                    "openid",
                    CFG.frontUrl() + "authorized",
                    codeChallenge,
                    "S256"

            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
    }

    @Override
    public String login(String username, String password) {
        Response<Void> response;
        try {
            response = authApi.login(
                    username,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
        return response.raw().request().url().toString().split("\\?code=")[1];
    }

    @Override
    public String token(String code, String codeVerifier) {
        Response<JsonNode> response;
        try {
            response = authApi.token(
                    code,
                    CFG.frontUrl() + "authorized",
                    codeVerifier,
                    "authorization_code",
                    "client"
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
        return response.body().get("id_token").asText();
    }
}