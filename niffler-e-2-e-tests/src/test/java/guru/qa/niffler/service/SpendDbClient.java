package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private static final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl());

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl());

    @Nonnull
    @Override
    public SpendJson create(SpendJson spend) {
        return requireNonNull(jdbcTxTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = spendRepository.createCategory(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(spendRepository.create(spendEntity));
                }
        ));
    }

    @Nonnull
    @Override
    public SpendJson update(SpendJson spend) {
        return xaTransactionTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    return SpendJson.fromEntity(spendRepository.update(spendEntity));
                }
        );
    }

    @Nonnull
    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
                    CategoryEntity categoryEntity = spendRepository.createCategory(CategoryEntity.fromJson(category));
                    return CategoryJson.fromEntity(categoryEntity);
                }
        );
    }

    @Nonnull
    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
                    return CategoryJson.fromEntity(spendRepository.updateCategory(categoryEntity));
                }
        );
    }

    @Nonnull
    @Override
    public Optional<CategoryJson> findCategoryById(UUID id) {
        return spendRepository.findCategoryById(id).map(CategoryJson::fromEntity);
    }

    @Nonnull
    @Override
    public Optional<CategoryJson> findCategoryByUsernameAndName(String username, String Name) {
        return spendRepository.findCategoryByUsernameAndName(username, Name).map(CategoryJson::fromEntity);
    }

    @Nonnull
    @Override
    public Optional<SpendJson> findById(UUID id) {
        return spendRepository.findById(id).map(SpendJson::fromEntity);
    }

    @Nonnull
    @Override
    public Optional<SpendJson> findByUsernameAndSpendDescription(String username, String spendDescription) {
        return spendRepository.findByUsernameAndSpendDescription(username, spendDescription).map(SpendJson::fromEntity);
    }

    @Override
    public void remove(SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.remove(SpendEntity.fromJson(spend));
            return null;
        });
    }

    @Override
    public void removeCategory(CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.removeCategory(CategoryEntity.fromJson(category));
            return null;
        });
    }
}