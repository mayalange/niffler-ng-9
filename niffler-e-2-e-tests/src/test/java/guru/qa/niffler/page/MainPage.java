package guru.qa.niffler.page;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    @Getter
    private final Header header = new Header();

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
        spendingTable.$$("tbody tr").find(text(description))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    @Step("Проверить, что в таблице отображаются расходы")
    public MainPage checkThatTableContainsSpending(String description) {
        spendingTable.$$("tbody tr").find(text(description))
                .should(visible);
        return this;
    }

    public ProfilePage goToUserProfilePage() {
        return header.goProfilePage();
    }

    public FriendsPage goToUserFriendsPage() {
        return header.goFriendsPage();
    }

    @Nonnull
    public EditSpendingPage addNewSpending() {
        header.goAddSpendingPage();
        return new EditSpendingPage();
    }
}