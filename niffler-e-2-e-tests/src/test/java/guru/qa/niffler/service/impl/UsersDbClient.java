package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserDataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.auth.FriendshipStatus;
import guru.qa.niffler.service.UsersClient;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final AuthUserRepository authUserRepository = new AuthUserRepositorySpringJdbc();
    private final UserDataUserRepository userdataUserRepository = new UserdataUserRepositorySpringJdbc();
    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJdbcUrl())
            )
    );
    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Nonnull
    @Override
    public UserJson createUser(String username, String password) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            authUserRepository.create(authUserEntity(username, password));
            return UserJson.fromEntity(
                    userdataUserRepository.create(userEntity(username)),
                    null
            );
        }
        ));
    }

    @Nonnull
    @Override
    public AuthUserJson update(AuthUserJson authUserJson) {
        return xaTransactionTemplate.execute(() -> AuthUserJson.fromEntity(authUserRepository.update(AuthUserEntity.fromJson(authUserJson))));
    }

    @Nonnull
    @Override
    public Optional<AuthUserJson> getAuthUserById(UUID id) {
        return authUserRepository.findById(id).map(AuthUserJson::fromEntity);
    }

    @Nonnull
    @Override
    public Optional<AuthUserJson> getAuthUserByName(String username) {
        return authUserRepository.findByUsername(username).map(AuthUserJson::fromEntity);
    }

    @Nonnull
    @Override
    public List<AuthUserJson> findAll() {
        return xaTransactionTemplate.execute(() -> {
                    List<AuthUserEntity> authUserEntities = authUserRepository.findAll();
                    return authUserEntities.stream()
                            .map(AuthUserJson::fromEntity)
                            .toList();
                }
        );
    }

    @Nonnull
    @Override
    public UserJson update(UserJson userJson) {
        return xaTransactionTemplate.execute(() -> UserJson.fromEntity(userdataUserRepository.update(UserEntity.fromJson(userJson)), null));
    }

    @Nonnull
    @Override
    public Optional<UserJson> getUserById(UUID id) {
        return userdataUserRepository.findById(id).map(user -> UserJson.fromEntity(user, null));
    }

    @Nonnull
    @Override
    public Optional<UserJson> getUserByName(String username) {
        return userdataUserRepository.findByUsername(username).map(user -> UserJson.fromEntity(user, null));
    }

    @Nonnull
    @Override
    public List<UserJson> addIncomeInvitation(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.addFriendshipRequest(adressee, targetEntity);
                            result.add(UserJson.fromEntity(
                                    adressee,
                                    FriendshipStatus.INVITE_RECEIVED
                            ));
                            return null;
                        }
                );
            }
        }
        return result;
    }

    @Nonnull
    @Override
    public List<UserJson> addOutcomeInvitation(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.addFriendshipRequest(targetEntity, adressee);
                    result.add(UserJson.fromEntity(
                            adressee,
                            FriendshipStatus.INVITE_RECEIVED
                    ));
                            return null;
                        }
                );
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
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.addFriend(targetEntity, adressee);
                    result.add(UserJson.fromEntity(
                            adressee,
                            FriendshipStatus.FRIEND
                    ));
                            return null;
                        }
                );
            }
        }
        return result;
    }

    @Nonnull
    @Override
    public void addFriend(UserJson requester, UserJson addressee) {

    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }
}