package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserDaoJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.userdataJdbcUrl();


    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement("SELECT * FROM \"user\" WHERE id = ? ")) {
            ps.setObject(1, id);
            ps.execute();
            ResultSet rs = ps.getResultSet();

            if (rs.next()) {
                return Optional.ofNullable(
                        UserdataUserEntityRowMapper.instance.mapRow(rs, rs.getRow())
                );
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
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
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement("SELECT * FROM \"user\" WHERE username = ? ")) {
            ps.setString(1, username);

            ps.execute();
            ResultSet rs = ps.getResultSet();

            if (rs.next()) {
                return Optional.of(
                        UserdataUserEntityRowMapper.instance.mapRow(rs, rs.getRow())
                );
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean delete(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
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
    @Override
    public List<UserEntity> findAll() {
        List<UserEntity> users = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement("SELECT * FROM user")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserEntity user = new UserEntity();
                    user.setId(rs.getObject("id", UUID.class));
                    user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    user.setFirstname(rs.getString("firstname"));
                    user.setFullname(rs.getString("full_name"));
                    user.setPhoto(rs.getBytes("photo"));
                    user.setPhotoSmall(rs.getBytes("photo_small"));
                    user.setSurname(rs.getString("surname"));
                    user.setUsername(rs.getString("username"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users", e);
        }
        return users;
    }

    @Override
    public UserEntity update(UserEntity user) {
        try (PreparedStatement usersPs = holder(URL).connection().prepareStatement(
                """
                      UPDATE "user"
                        SET currency    = ?,
                            firstname   = ?,
                            surname     = ?,
                            photo       = ?,
                            photo_small = ?
                        WHERE id = ?
                    """);

             PreparedStatement friendsPs = holder(URL).connection().prepareStatement(
                     """
                         INSERT INTO friendship (requester_id, addressee_id, status)
                         VALUES (?, ?, ?)
                         ON CONFLICT (requester_id, addressee_id)
                             DO UPDATE SET status = ?
                         """)
        ) {
            usersPs.setString(1, user.getCurrency().name());
            usersPs.setString(2, user.getFirstname());
            usersPs.setString(3, user.getSurname());
            usersPs.setBytes(4, user.getPhoto());
            usersPs.setBytes(5, user.getPhotoSmall());
            usersPs.setObject(6, user.getId());
            usersPs.executeUpdate();

            for (FriendshipEntity fe : user.getFriendshipRequests()) {
                friendsPs.setObject(1, user.getId());
                friendsPs.setObject(2, fe.getAddressee().getId());
                friendsPs.setString(3, fe.getStatus().name());
                friendsPs.setString(4, fe.getStatus().name());
                friendsPs.addBatch();
                friendsPs.clearParameters();
            }
            friendsPs.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

}
