package student.data.dao.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import student.data.dao.CategoryDao;
import student.data.entity.spend.CategoryEntity;
import student.data.mapper.CategoryEntityRowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private final DataSource datasource;

    public CategoryDaoSpringJdbc(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO category (username, name, archived)"
                                    + "VALUES (?,?,?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    preparedStatement.setString(1, category.getUsername());
                    preparedStatement.setString(2, category.getName());
                    preparedStatement.setBoolean(3, category.isArchived());
                    return preparedStatement;
                },
                keyHolder);
        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        category.setId(generatedKey);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findByCategoryId(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "select * from category where id = ?",
                        CategoryEntityRowMapper.instance,
                        id
                )
        );
    }

    public List<CategoryEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        return jdbcTemplate.query(
                "select * from category where id = ?",
                CategoryEntityRowMapper.instance
        );
    }

    @Override
    public List<CategoryEntity> findCategoriesByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        return jdbcTemplate.query(
                "select * from category where username = ?",
                CategoryEntityRowMapper.instance,
                username
        );
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "select * from category where name = ? and username = ?",
                        CategoryEntityRowMapper.instance,
                        categoryName, username
                )
        );
    }

    @Override
    public void deleteCategoryById(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(datasource);
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "delete from category where id = ?");
            preparedStatement.setObject(1, category.getId());
            return preparedStatement;
        });
    }
}
