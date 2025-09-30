package guru.qa.niffler.page;


import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
public class MainPage {
    @Getter
    private final Header header = new Header();

    private final SelenideElement searchSpendingInput = $("input[aria-label='search']");
    private final SelenideElement spendingTable = $("#spendings");
    private final SelenideElement spendingChart = $("#chart");
    private final SelenideElement spendingLegend = $("#legend-container");
    public MainPage checkThatPageLoaded() {
        spendingTable.should(visible);
        spendingChart.should(visible);
        spendingLegend.should(visible);
        return this;
    }

    public MainPage findSpending(String spendingDescription) {
        searchSpendingInput.shouldBe(visible).setValue(spendingDescription).pressEnter();
        return this;
    }

    public EditSpendingPage editSpending(String description) {
        spendingTable.$$("tbody tr").find(text(description))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }
    public MainPage checkThatTableContainsSpending(String description) {
        spendingTable.$$("tbody tr").find(text(description))
                .should(visible);
        return this;
    }
    public ProfilePage goToUserProfilePage() {
        return header.openMenu().clickOnProfileButton();
    }
    public FriendsPage goToUserFriendsPage() {
        return header.openMenu().clickOnFriendsButton();
    }
}