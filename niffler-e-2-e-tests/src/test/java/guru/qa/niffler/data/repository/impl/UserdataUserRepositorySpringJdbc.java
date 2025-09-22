package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityResultSetExtractor;
import guru.qa.niffler.data.repository.UserDataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class UserdataUserRepositorySpringJdbc implements UserDataUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                Objects.requireNonNull(jdbcTemplate.query(
                        "SELECT * FROM \"user\" u left join friendship f ON u.id = f.requester_id or (u.id = f.addressee_id and status = 'PENDING') WHERE u.id = ?",
                        UserdataUserEntityResultSetExtractor.instance,
                        id
                )).getFirst()
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                Objects.requireNonNull(jdbcTemplate.query(
                        "SELECT * FROM \"user\" u left join friendship f ON u.id = f.requester_id or (u.id = f.addressee_id and status = 'PENDING') WHERE u.username = ?",
                        UserdataUserEntityResultSetExtractor.instance,
                        username
                )).getFirst()
        );
    }

    @Override
    public void delete(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update("DELETE FROM \"user\" WHERE id = ?", user.getId());
        jdbcTemplate.update("DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?",
                user.getId(), user.getId());
    }

    @Override
    public List<UserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM \"user\" u left join friendship f ON u.id = f.requester_id or (u.id = f.addressee_id and status = 'PENDING')",
                UserdataUserEntityResultSetExtractor.instance
        );
    }

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)",
                requester.getId(), addressee.getId(),
                FriendshipStatus.PENDING.name(), new Date(System.currentTimeMillis())
        );
    }

    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        addIncomeInvitation(requester, addressee);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.batchUpdate(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        if (i == 0) {
                            ps.setObject(1, requester.getId());
                            ps.setObject(2, addressee.getId());
                        } else {
                            ps.setObject(1, addressee.getId());
                            ps.setObject(2, requester.getId());
                        }
                        ps.setObject(3, FriendshipStatus.ACCEPTED.name());
                        ps.setObject(4, new Date(System.currentTimeMillis()));
                    }

                    @Override
                    public int getBatchSize() {
                        return 2;
                    }
                }
        );
    }
}
