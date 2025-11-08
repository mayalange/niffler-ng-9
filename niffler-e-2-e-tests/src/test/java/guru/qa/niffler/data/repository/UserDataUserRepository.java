package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.impl.UserDataUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositorySpringJdbc;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserDataUserRepository {

    @Nonnull
    static UserDataUserRepository getInstance() {
        return switch (System.getProperty("repository.impl", "jpa")) {
            case "jdbc" -> new UserDataUserRepositoryJdbc();
            case "spring-jdbc" -> new UserdataUserRepositorySpringJdbc();
            default -> new UserdataUserRepositoryHibernate();
        };
    }

    @Nonnull
    UserEntity create(UserEntity user);

    @Nonnull
    Optional<UserEntity> findById(UUID id);

    @Nonnull
    Optional<UserEntity> findByUsername(String username);

    void delete(UserEntity user);

    @Nonnull
    List<UserEntity> findAll();

    void addFriendshipRequest(UserEntity requester, UserEntity addressee);

    void addFriend(UserEntity requester, UserEntity addressee);

    @Nonnull
    UserEntity update(UserEntity user);
}
