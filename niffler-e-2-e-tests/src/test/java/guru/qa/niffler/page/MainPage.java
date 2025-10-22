package guru.qa.niffler.page;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    @Getter
    private final Header header = new Header();

    private final SpendingTable spends = new SpendingTable();
    protected final StatComponent statComponent = new StatComponent();

    private final SelenideElement searchSpendingInput = $("input[aria-label='search']");
    private final SelenideElement spendingTable = $("#spendings");
    private final SelenideElement spendingChart = $("#chart");
    private final SelenideElement spendingLegend = $("#legend-container");

    @Nonnull
    @Step("Проверить, что на главной странице отображается таблица расходов")
    public MainPage checkThatPageLoaded() {
        spendingTable.should(visible);
        spendingChart.should(visible);
        spendingLegend.should(visible);
        return this;
    }

    @Step("Найти расход")
    public MainPage findSpending(String spendingDescription) {
        searchSpendingInput.shouldBe(visible).setValue(spendingDescription).pressEnter();
        return this;
    }

    @Nonnull
    @Step("Редактировать расход")
    public EditSpendingPage editSpending(String description) {
        return spends.editSpending(description);
    }

    @Step("Проверить, что в таблице отображаются расходы")
    public void checkThatTableContainsSpending(String description) {
        spends.searchSpendingByDescription(description);
    }

    public ProfilePage goToUserProfilePage() {
        return header.goProfilePage();
    }

    public FriendsPage goToUserFriendsPage() {
        return header.goFriendsPage();
    }

    @Nonnull
    public StatComponent getStatComponent() {
        return statComponent;
    }

    @Nonnull
    public EditSpendingPage addNewSpending() {
        header.goAddSpendingPage();
        return new EditSpendingPage();
    }
}