package student.data.dao.impl;

import student.config.Config;
import student.data.dao.AuthorityDao;
import student.data.entity.auth.AuthUserEntity;
import student.data.entity.auth.AuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static student.data.tmpl.Connections.holder;

public class AuthorityDaoJdbc implements AuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void createAuthority(AuthorityEntity... authorityEntity) {
        try (PreparedStatement preparedStatement = holder(CFG.authJdbcUrl()).connection().prepareStatement(
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
        try (PreparedStatement preparedStatement = holder(CFG.authJdbcUrl()).connection().prepareStatement(
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
