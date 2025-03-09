package student.data.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import student.data.dao.AuthDao;
import student.data.entity.auth.AuthUserEntity;
import student.data.mapper.AuthUserEntityRowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class AuthDaoSpringJdbc implements AuthDao {

    private final DataSource datasource;

    public AuthDaoSpringJdbc(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO public.\"user\" (username, \"password\", enabled, account_non_expired, account_non_locked, credentials_non_expired)"
                                    + "VALUES (?,?,?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    preparedStatement.setString(1, user.getUsername());
                    preparedStatement.setString(2, user.getPassword());
                    preparedStatement.setBoolean(3, user.getEnabled());
                    preparedStatement.setBoolean(4, user.getAccountNonExpired());
                    preparedStatement.setBoolean(5, user.getAccountNonLocked());
                    preparedStatement.setBoolean(6, user.getCredentialsNonExpired());
                    return preparedStatement;
                },
                keyHolder);
        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        AuthUserEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public void deleteUser(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "delete from public.\"user\" where id = ?");
            preparedStatement.setObject(1, user.getId());
            return preparedStatement;
        });
    }
}
