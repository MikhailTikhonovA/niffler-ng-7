package student.data.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import student.data.dao.UserdataDao;
import student.data.entity.user.UserEntity;
import student.data.mapper.UserdataUserEntityRowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoSpringJdbc implements UserdataDao {

    private final DataSource datasource;

    public UserdataUserDaoSpringJdbc(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " + "VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, userEntity.getUsername());
            preparedStatement.setString(2, userEntity.getCurrency().name());
            preparedStatement.setString(3, userEntity.getFirstname());
            preparedStatement.setString(4, userEntity.getSurname());
            preparedStatement.setBytes(5, userEntity.getPhoto());
            preparedStatement.setBytes(6, userEntity.getPhotoSmall());
            preparedStatement.setString(7, userEntity.getFullname());
            return preparedStatement;
        }, keyHolder);
        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        userEntity.setId(generatedKey);
        return userEntity;
    }

    @Override
    public Optional<UserEntity> findUserById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM \"user\" WHERE id = ?", UserdataUserEntityRowMapper.instance, id));
    }

    @Override
    public Optional<UserEntity> findUserByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM \"user\" WHERE name = ?", UserdataUserEntityRowMapper.instance, username));
    }

    @Override
    public void deleteUser(UserEntity userEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM \"user\" WHERE id = ?");
            return preparedStatement;
        });
    }
}
