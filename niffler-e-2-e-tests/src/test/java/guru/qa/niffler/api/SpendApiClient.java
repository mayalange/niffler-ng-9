package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.RestClient;
import retrofit2.Response;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public final class SpendApiClient extends RestClient {

  private final SpendApi spendApi;

  public SpendApiClient() {
    super(CFG.spendUrl());
    this.spendApi = create(SpendApi.class);
  }

  @Nullable
  public SpendJson create(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.addSpend(spend).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(201, response.code());
    return response.body();
  }

  @Nullable
  public SpendJson update(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.editSpend(spend).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  @Nullable
  public CategoryJson createCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.addCategory(category).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  @Nullable
  public CategoryJson updateCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.updateCategory(category).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public Optional<CategoryJson> findCategoryById(UUID id) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Optional<CategoryJson> findCategoryByUsernameAndName(String username, String spendName) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Optional<SpendJson> findById(UUID id) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Optional<SpendJson> findByUsernameAndSpendDescription(String username, String spendDescription) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public void remove(SpendJson spend) {
    final Response<Void> response;
    try {
      response = spendApi.removeSpends(spend.username(), List.of(spend.id().toString())).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
  }

  public void removeCategory(CategoryJson category) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Nullable
  public SpendJson getSpend(SpendJson spendJson) {
    final Response<SpendJson> response;
    try {
      response = spendApi.editSpend(spendJson).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  @Nullable
  public SpendJson getSpend(String id, String username) {
    final Response<SpendJson> response;
    try {
      response = spendApi.getSpend(id, username).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  @Nullable
  public List<SpendJson> getSpends(
          String username,
          CurrencyValues filterCurrency,
          Date from,
          Date to
  ) {
    final Response<List<SpendJson>> response;
    try {
      response = spendApi.getSpends(username, filterCurrency, from, to).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  @Nullable
  public List<CategoryJson> getCategories(String username, boolean excludeArchived) {
    final Response<List<CategoryJson>> response;
    try {
      response = spendApi.getCategories(username, excludeArchived).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }
}