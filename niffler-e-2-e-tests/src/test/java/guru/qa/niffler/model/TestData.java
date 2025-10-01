package guru.qa.niffler.model;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record TestData(
        String password,
        List<UserJson> friends,
        List<UserJson> incomeInvitations,
        List<UserJson> outcomeInvitations,
        List<CategoryJson> categories,
        List<SpendJson> spendings
) {

    @Nonnull
    public TestData addCategories(List<CategoryJson> categories) {
        return new TestData(
                this.password,
                this.friends,
                this.incomeInvitations,
                this.outcomeInvitations,
                categories,
                this.spendings
        );
    }

    @Nonnull
    public TestData addSpendings(List<SpendJson> spendings) {
        return new TestData(
                this.password,
                this.friends,
                this.incomeInvitations,
                this.outcomeInvitations,
                this.categories,
                spendings
        );
    }
}