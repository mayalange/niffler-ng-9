package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityResultSetExtractor;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.authJdbcUrl();

    private static final AuthAuthorityDao AUTHORITY_DAO = new AuthAuthorityDaoJdbc();
    private static final AuthUserDao AUTH_USER_DAO = new AuthUserDaoJdbc();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        AuthUserEntity createdUser = AUTH_USER_DAO.create(user);
        AUTHORITY_DAO.create(user.getAuthorities().toArray(new AuthorityEntity[0]));
        return createdUser;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        try (PreparedStatement preparedStatement = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" SET username = ?, password = ?, enabled = ?, account_non_expired = ?, " +
                        "account_non_locked = ?, credentials_non_expired = ? WHERE id = ?"
        )) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setObject(2, user.getPassword());
            preparedStatement.setBoolean(3, user.getEnabled());
            preparedStatement.setBoolean(4, user.getAccountNonExpired());
            preparedStatement.setBoolean(5, user.getAccountNonLocked());
            preparedStatement.setBoolean(6, user.getCredentialsNonExpired());
            preparedStatement.setObject(7, user.getId());

            preparedStatement.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "select * from \"user\" u join authority a on u.id = a.user_id where u.id = ?"
        )) {
            ps.setObject(1, id);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity user = null;
                List<AuthorityEntity> authorityEntities = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = Objects.requireNonNull(AuthUserEntityResultSetExtractor.instance.extractData(rs)).getFirst();
                    }

                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(user);
                    ae.setId(rs.getObject("a.id", UUID.class));
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorityEntities.add(ae);
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setAuthorities(authorityEntities);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" u join public.authority a on u.id = a.user_id"
        )) {
            try (ResultSet rs = ps.executeQuery()) {
                return AuthUserEntityResultSetExtractor.instance.extractData(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "select * from \"user\" u join authority a on u.id = a.user_id where u.username = ?"
        )) {
            ps.setString(1, username);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity user = null;
                List<AuthorityEntity> authorityEntities = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }

                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(user);
                    ae.setId(rs.getObject("a.id", UUID.class));
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorityEntities.add(ae);
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setAuthorities(authorityEntities);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
