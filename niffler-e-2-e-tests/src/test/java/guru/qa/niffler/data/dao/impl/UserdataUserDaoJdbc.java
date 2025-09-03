package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.auth.UserJson;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();
    private final Connection connection;

    public UserdataUserDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\"" +
                        "WHERE id = ?"
        )) {
            ps.setObject(1, id);

            ps.executeUpdate();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(rs.getObject("id", UUID.class));
                    userEntity.setUsername(rs.getString("username"));
                    userEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    userEntity.setFirstname(rs.getString("firstname"));
                    userEntity.setSurname(rs.getString("surname"));
                    userEntity.setFullname(rs.getString("full_name"));
                    userEntity.setPhoto(rs.getBytes("photo"));
                    userEntity.setPhotoSmall(rs.getBytes("photo_small"));
                    return Optional.of(userEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return UserEntity.fromJson(UserJson.fromEntity(user, null));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUserName(String userName) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\" " +
                        "WHERE username = ?"
        )) {
            ps.setString(1, userName);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(rs.getObject("id", UUID.class));
                    userEntity.setUsername(rs.getString("username"));
                    userEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    userEntity.setFirstname(rs.getString("firstname"));
                    userEntity.setSurname(rs.getString("surname"));
                    userEntity.setFullname(rs.getString("full_name"));
                    userEntity.setPhoto(rs.getBytes("photo"));
                    userEntity.setPhotoSmall(rs.getBytes("photo_small"));
                    return Optional.of(userEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean delete(UserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM \"user\" " +
                        "WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());

            int deletedRows = ps.executeUpdate();

            if (deletedRows == 0) {
                throw new SQLException("Rows with id " + user.getId().toString() + " not found");
            }

            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
