package guru.qa.niffler.page;


import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {

    private final SelenideElement searchPeopleInput = $("input[aria-label='search']");
    private final SelenideElement friendsTableShowButton = $x("//h2[text()='Friends']");
    private final SelenideElement allPeopleTableShowButton = $x("//h2[text()='All people']");
    private final SelenideElement requestsToFriendTable = $("#requests");
    private final SelenideElement friendsTable = $("#friends");
    private final SelenideElement allPeopleTable = $("#all");
    private final SelenideElement noUserLabel = $x("//p[text()='There are no users yet']");

    public FriendsPage findPeople(String name) {
        searchPeopleInput.shouldBe(visible).setValue(name).pressEnter();
        return this;
    }

    public FriendsPage clickOnFriendsTable() {
        friendsTableShowButton.click();
        return this;
    }
    public FriendsPage clickOnAllPeopleTable() {
        allPeopleTableShowButton.click();
        return this;
    }
    public FriendsPage verifyRowInAllPeopleTable(String rowName) {
        getRowInTable(allPeopleTable, rowName).shouldBe(visible)
                .$x(".//button[text()='Add friend']").shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Проверить, что отправлено приглашение")
    public FriendsPage verifyOutcomeInvitation(String name) {
        getRowInTable(allPeopleTable, name).shouldBe(visible)
                .$x(".//span[text()='Waiting...']").shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Проверить, что таблица друзей отображается")
    public FriendsPage verifyFriends(String name) {
        getRowInTable(friendsTable, name).shouldBe(visible)
                .$x(".//button[text()='Unfriend']").shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Проверить, что таблица друзей содержит предложение")
    public FriendsPage verifyIncomeInvitation(String name) {
        SelenideElement row = getRowInTable(requestsToFriendTable, name).shouldBe(visible);
        row.$x(".//button[text()='Accept']").shouldBe(visible);
        row.$x(".//button[text()='Decline']").shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Проверить, что таблица друзей не содержит предложений")
    public FriendsPage verifyNoFriend() {
        friendsTable.shouldNotBe(exist);
        noUserLabel.shouldBe(visible);
        return this;
    }
    private SelenideElement getRowInTable(SelenideElement table, String rowName) {
        return table.$x(String.format(".//p[text()='%s']//ancestor::tr", rowName));
    }

    @Step("Проверить, что страница пользователей загрузилась")
    @Nonnull
    @Override
    public FriendsPage checkThatPageLoaded() {
        friendsTableShowButton.shouldBe(visible);
        return this;
    }

    @Step("Проверить, что таблица друзей содержит предложение от '{0}' и принять его")
    @Nonnull
    public FriendsPage acceptFriendRequestFromUser(String user) {
        SelenideElement friendRow = requestsToFriendTable.$$("tr").find(text(user));
        friendRow.$(byText("Accept")).click();
        return this;
    }

    @Step("Проверить, что таблица друзей содержит предложение от '{0}' и отклонить его")
    @Nonnull
    public FriendsPage declineFriendRequestFromUser(String user) {
        SelenideElement friendRow = requestsToFriendTable.$$("tr").find(text(user));
        friendRow.$(byText("Decline")).click();
        $("div[role='dialog']").$(byText("Decline")).click();
        return this;
    }
}