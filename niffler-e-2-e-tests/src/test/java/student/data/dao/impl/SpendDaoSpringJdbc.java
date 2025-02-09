package student.data.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import student.data.dao.SpendDao;
import student.data.entity.spend.SpendEntity;
import student.data.mapper.SpendEntityRowMapper;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoSpringJdbc implements SpendDao {

    private final DataSource datasource;

    public SpendDaoSpringJdbc(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public SpendEntity createSpend(SpendEntity spendEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO spend (username, spend_date, currency, amount, description, category_id)"
                                    + "VALUES (?,?,?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    preparedStatement.setObject(1, spendEntity.getUsername());
                    preparedStatement.setDate(2, spendEntity.getSpendDate());
                    preparedStatement.setString(3, spendEntity.getCurrency().name());
                    preparedStatement.setDouble(4, spendEntity.getAmount());
                    preparedStatement.setString(5, spendEntity.getDescription());
                    preparedStatement.setObject(6, spendEntity.getCategory().getId());
                    return preparedStatement;
                },
                keyHolder);
        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        spendEntity.setId(generatedKey);
        return spendEntity;
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        return Optional.ofNullable(jdbcTemplate.queryForObject("select * from spend where id = ?", SpendEntityRowMapper.instance,
                id));

    }

    @Override
    public List<SpendEntity> findSpendsByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        return jdbcTemplate.query(
                "select * from spend where username = ?",
                SpendEntityRowMapper.instance,
                username
        );
    }

    @Nonnull
    public List<SpendEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        return jdbcTemplate.query(
                "SELECT * FROM spend",
                SpendEntityRowMapper.instance
        );
    }

    @Override
    public void deleteSpendById(SpendEntity spendEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "delete from spend where id = ?");
            preparedStatement.setObject(1, spendEntity.getId());
            return preparedStatement;
        });
    }

    @Override
    public void deleteSpendByCategoryId(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "delete from spend where category_id = ?");
            preparedStatement.setObject(1, id);
            return preparedStatement;
        });
    }
}
