package student.data.dao;

import student.data.entity.spend.SpendEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {
    SpendEntity createSpend(SpendEntity spendEntity);

    Optional<SpendEntity> findSpendById(UUID id);

    List<SpendEntity> findSpendsByUsername(String username);
    List<SpendEntity> findAll();

    void deleteSpendById(SpendEntity spendEntity);


    void deleteSpendByCategoryId(UUID id);
}
