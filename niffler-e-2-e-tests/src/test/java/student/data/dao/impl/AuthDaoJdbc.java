package student.data.dao.impl;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import student.config.Config;
import student.data.dao.AuthDao;
import student.data.entity.auth.AuthUserEntity;
import student.data.mapper.AuthUserEntityRowMapper;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class AuthDaoJdbc implements AuthDao {

    private static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private static final Config CFG = Config.getInstance();

    private final Connection connection;

    public AuthDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO public.\"user\" (username, \"password\", enabled, account_non_expired, account_non_locked, credentials_non_expired)"
                        + "VALUES (?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, PASSWORD_ENCODER.encode(user.getPassword()));
            preparedStatement.setBoolean(3, user.getEnabled());
            preparedStatement.setBoolean(4, user.getAccountNonExpired());
            preparedStatement.setBoolean(5, user.getAccountNonLocked());
            preparedStatement.setBoolean(6, user.getCredentialsNonExpired());

            preparedStatement.executeUpdate();
            final UUID generatedKey;
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    generatedKey = resultSet.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Cant find id in RS");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM \"user\" WHERE id = ?")) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.ofNullable(
                            AuthUserEntityRowMapper.instance.mapRow(rs, rs.getRow())
                    );
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(AuthUserEntity user) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "delete from public.\"user\" where id = ?"
        )) {
            preparedStatement.setObject(1, user.getId());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
