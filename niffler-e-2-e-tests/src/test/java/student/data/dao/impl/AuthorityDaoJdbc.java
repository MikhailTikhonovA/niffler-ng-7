package student.data.dao.impl;

import student.config.Config;
import student.data.dao.AuthorityDao;
import student.data.entity.auth.AuthUserEntity;
import student.data.entity.auth.AuthorityEntity;

import java.sql.*;
import java.util.UUID;

public class AuthorityDaoJdbc implements AuthorityDao {

    private final Connection connection;

    public AuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createAuthority(AuthorityEntity... authorityEntity) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO authority (user_id, authority)"
                        + "VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            for (AuthorityEntity authority : authorityEntity) {
                preparedStatement.setObject(1, authority.getUser());
                preparedStatement.setObject(2, authority.getAuthority().toString());
                preparedStatement.addBatch();
                preparedStatement.clearParameters();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(AuthUserEntity authUserEntity) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "delete from authority where user_id = ?"
        )) {
            preparedStatement.setObject(1, authUserEntity.getId());
            var result = preparedStatement.execute();
            System.out.println("delete from authority = " + result + " " + authUserEntity.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
