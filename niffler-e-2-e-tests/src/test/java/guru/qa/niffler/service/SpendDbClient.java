package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(SpendJson spend, int transactionLevel) {
        return transaction(connection -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = new CategoryDaoJdbc(connection)
                                .create(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(
                            new SpendDaoJdbc(connection).create(spendEntity)
                    );
                },
                CFG.spendJdbcUrl(),
                transactionLevel
        );
    }

    public Optional<SpendEntity> findSpendById(UUID id, int transactionLevel) {
        return transaction(connection -> {
                    Optional<SpendEntity> spend = new SpendDaoJdbc(connection)
                            .findSpendById(id);
                    return spend;
                },
                CFG.spendJdbcUrl(),
                transactionLevel
        );
    }

    public List<SpendEntity> findAllByUsername(String username, int transactionLevel) {
        return transaction(connection -> {
                    List<SpendEntity> spend = new SpendDaoJdbc(connection)
                            .findAllByUsername(username);
                    return spend;
                },
                CFG.spendJdbcUrl(),
                transactionLevel
        );
    }

    public Boolean deleteSpend(UUID id, int transactionLevel) {
        return transaction(connection -> {
                    Boolean spend = new SpendDaoJdbc(connection)
                            .deleteSpend(id);
                    return spend;
                },
                CFG.spendJdbcUrl(),
                transactionLevel
        );
    }

    public CategoryEntity createCategory(CategoryJson categoryJson, int transactionLevel) {
        return transaction(connection -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
                    return new CategoryDaoJdbc(connection)
                            .create(categoryEntity);
                },
                CFG.spendJdbcUrl(),
                transactionLevel
        );
    }

    public List<CategoryEntity> findAllByUserName(String username, int transactionLevel) {
        return transaction(connection -> {
                    List<CategoryEntity> categories = new CategoryDaoJdbc(connection)
                            .findAllByUserName(username);
                    return categories;
                },
                CFG.spendJdbcUrl(),
                transactionLevel
        );
    }

    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName, int transactionLevel) {
        return transaction(connection -> {
                    Optional<CategoryEntity> categories = new CategoryDaoJdbc(connection)
                            .findCategoryByUsernameAndCategoryName(username, categoryName);
                    return categories;
                },
                CFG.spendJdbcUrl(),
                transactionLevel
        );
    }

    public Boolean updateCategory(CategoryJson category, int transactionLevel) {
        return transaction(connection -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return new CategoryDaoJdbc(connection)
                            .update(categoryEntity);
                },
                CFG.spendJdbcUrl(),
                transactionLevel
        );
    }

    public Boolean deleteCategory(UUID id, int transactionLevel) {
        return transaction(connection -> {
                    Boolean categories = new CategoryDaoJdbc(connection)
                            .delete(id);
                    return categories;
                },
                CFG.spendJdbcUrl(),
                transactionLevel
        );
    }
}