package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;


import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault

public interface SpendClient {
    @Nonnull
    SpendJson create(SpendJson spend);

    @Nonnull
    SpendJson update(SpendJson spend);

    @Nonnull
    CategoryJson createCategory(CategoryJson category);

    @Nonnull
    CategoryJson updateCategory(CategoryJson category);

    @Nonnull
    Optional<CategoryJson> findCategoryById(UUID id);

    @Nonnull
    Optional<CategoryJson> findCategoryByUsernameAndName(String username, String spendName);

    @Nonnull
    Optional<SpendJson> findById(UUID id);

    @Nonnull
    Optional<SpendJson> findByUsernameAndSpendDescription(String username, String spendDescription);

    void remove(SpendJson spend);

    void removeCategory(CategoryJson category);
}