package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserdataUserDao {
    @Nonnull
    UserEntity create(UserEntity user);

    @Nonnull
    Optional<UserEntity> findById(UUID id);

    @Nonnull
    Optional<UserEntity> findByUsername(String userName);

    @Nonnull
    Boolean delete(UserEntity user);

    @Nonnull
    List<UserEntity> findAll();

    @Nonnull
    UserEntity update(UserEntity user);
}
