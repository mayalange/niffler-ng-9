package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;
import java.util.List;

@ParametersAreNonnullByDefault
public interface SpendApi {

    @POST("internal/spends/add")
    Call<SpendJson> addSpend(@Body SpendJson spending);

    @PATCH("internal/spends/edit")
    Call<SpendJson> editSpend(@Body SpendJson spending);

    @GET("internal/spends/{id}")
    Call<SpendJson> getSpend(@Path("id") String id, @Query("username") String username);

    @GET("internal/spends/all")
    Call<List<SpendJson>> getSpends(
            @Query("username") String username,
            @Nullable @Query("filterCurrency") CurrencyValues filterCurrency,
            @Nullable @Query("from") Date from,
            @Nullable @Query("to") Date to
    );

    @DELETE("internal/spends/remove")
    Call<Void> removeSpends(@Query("username") String username, @Query("ids") List<String> ids);

    @POST("internal/categories/add")
    Call<CategoryJson> addCategory(@Body CategoryJson category);

    @PATCH("internal/categories/update")
    Call<CategoryJson> updateCategory(@Body CategoryJson category);

    @GET("internal/categories/all")
    Call<List<CategoryJson>> getCategories(
            @Query("username") String username,
            @Query("excludeArchived") boolean excludeArchived
    );
}