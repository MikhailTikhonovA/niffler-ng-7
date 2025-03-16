package student.service;


import student.config.Config;
import student.data.dao.CategoryDao;
import student.data.dao.SpendDao;
import student.data.dao.impl.CategoryDaoJdbc;
import student.data.dao.impl.SpendDaoJdbc;
import student.data.entity.spend.CategoryEntity;
import student.data.entity.spend.SpendEntity;
import student.data.tmpl.JdbcTransactionTemplate;
import student.model.CategoryJson;
import student.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient {
    private static final Config CFG = Config.getInstance();

    private final CategoryDao categoryDao = new CategoryDaoJdbc();
    private final SpendDao spendDao = new SpendDaoJdbc();

    private final JdbcTransactionTemplate jdbcTransactionTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    public SpendJson createSpend(SpendJson spend) throws Exception {
        return jdbcTransactionTemplate.execute(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            var jdbc = categoryDao;
            var userCategories = jdbc.findCategoryByUsernameAndCategoryName(spend.username(), spend.category().name());
            if (userCategories.isEmpty()) {
                CategoryEntity categoryEntity = jdbc.createCategory(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            } else {
                spendEntity.setCategory(userCategories.stream().findFirst().get());
            }
            return SpendJson.fromEntity(
                    spendDao.createSpend(spendEntity));
        });
    }

    public CategoryJson createCategory(CategoryJson categoryJson) throws Exception {
        return jdbcTransactionTemplate.execute(() -> {
            var jdbc = categoryDao;
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            return CategoryJson.fromEntity(jdbc.createCategory(categoryEntity));
        });
    }

    public void deleteSpend(SpendJson spendingJson) throws Exception {
        jdbcTransactionTemplate.execute(() -> {
            var jdbc = spendDao;
            SpendEntity spendEntity = SpendEntity.fromJson(spendingJson);
            jdbc.deleteSpendById(spendEntity);
            return null;
        });
    }

    public void deleteSpendByCategoryId(UUID id) throws Exception {
        jdbcTransactionTemplate.execute(() -> {
            var jdbc = spendDao;
            jdbc.deleteSpendByCategoryId(id);
            return null;
        });
    }

    public void deleteCategory(CategoryJson categoryJson) throws Exception {
        jdbcTransactionTemplate.execute(() -> {
            var jdbc = categoryDao;
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            jdbc.deleteCategoryById(categoryEntity);
            return null;
        });
    }

    public Optional<CategoryEntity> findCategoryById(UUID id) throws Exception {
        return jdbcTransactionTemplate.execute(() -> {
            var jdbc = categoryDao;
            return jdbc.findByCategoryId(id);
        });
    }

    public Optional<SpendEntity> findSpendById(UUID id) throws Exception {
        return jdbcTransactionTemplate.execute(() -> {
            var jdbc = spendDao;
            return jdbc.findSpendById(id);
        });
    }

    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) throws Exception {
        return jdbcTransactionTemplate.execute(() -> {
            var jdbc = categoryDao;
            return jdbc.findCategoryByUsernameAndCategoryName(username, categoryName);
        });
    }

    public List<SpendEntity> findAllSpendsByUserName(String userName) throws Exception {
        return jdbcTransactionTemplate.execute(() -> {
            var jdbc = spendDao;
            return jdbc.findSpendsByUsername(userName);
        });
    }
}
