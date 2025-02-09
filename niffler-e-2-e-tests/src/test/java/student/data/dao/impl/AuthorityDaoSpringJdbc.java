package student.data.dao.impl;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import student.data.dao.AuthorityDao;
import student.data.entity.auth.AuthUserEntity;
import student.data.entity.auth.AuthorityEntity;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuthorityDaoSpringJdbc implements AuthorityDao {

    private final DataSource datasource;

    public AuthorityDaoSpringJdbc(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public void createAuthority(AuthorityEntity... authorityEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority)"
                        + "VALUES (?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(@NotNull PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setObject(1,authorityEntity[i].getId() );
                        preparedStatement.setObject(2,authorityEntity[i].getAuthority().name() );
                    }

                    @Override
                    public int getBatchSize() {
                        return authorityEntity.length;
                    }
                }
        );
    }

    @Override
    public void deleteUser(AuthUserEntity authUserEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "delete from authority where user_id = ?");
            preparedStatement.setObject(1, authUserEntity.getId());
            return preparedStatement;
        });
    }
}
