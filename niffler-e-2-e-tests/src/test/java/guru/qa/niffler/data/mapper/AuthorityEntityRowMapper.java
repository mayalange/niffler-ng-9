package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.ParametersAreNonnullByDefault;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthorityEntityRowMapper implements RowMapper<AuthorityEntity> {

    public static final AuthorityEntityRowMapper instance = new AuthorityEntityRowMapper();

    private AuthorityEntityRowMapper() {
    }

    @Override
    @Nullable
    public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthorityEntity ae = new AuthorityEntity();
        ae.setId(rs.getObject("id", UUID.class));
        ae.setAuthority(Authority.valueOf(rs.getString("authority")));
        return ae;
    }
}