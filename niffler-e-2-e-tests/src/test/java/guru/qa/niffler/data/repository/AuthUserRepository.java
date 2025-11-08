package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.AuthUserRepositorySpringJdbc;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthUserRepository {
    @Nonnull
    AuthUserEntity create(AuthUserEntity user);

    @Nonnull
    AuthUserEntity update(AuthUserEntity user);

    @Nonnull
    Optional<AuthUserEntity> findById(UUID id);

    @Nonnull
    Optional<AuthUserEntity> findByUsername(String username);

    @Nonnull
    List<AuthUserEntity> findAll();

    @Nonnull
    static AuthUserRepository getInstance() {
        return switch (System.getProperty("repository.impl", "jpa")) {
            case "jdbc" -> new AuthUserRepositoryJdbc();
            case "spring-jdbc" -> new AuthUserRepositorySpringJdbc();
            default -> new AuthUserRepositoryHibernate();
        };
    }
}
