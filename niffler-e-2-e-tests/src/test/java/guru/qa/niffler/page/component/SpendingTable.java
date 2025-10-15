package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SpendingTable extends BaseComponent {
    private final SelenideElement self = $("#spendings");
    private final ElementsCollection tableRows = self.$$("tbody tr");
    private final SelenideElement delete = self.$("#delete");
    private final SelenideElement dialogWindow = $("div[role='dialog']");
    private final SelenideElement periodMenu = self.$("#period");
    private final ElementsCollection periodMenuItems = $("ul[role='listbox']").$$("li");
    private final SearchField spendingSearch = new SearchField();

    public SpendingTable() {
        super($("#spendings"));
    }


    @Nonnull
    @Step("Указать период")
    public SpendingTable selectPeriod(DataValues period) {
        periodMenu.click();
        periodMenuItems.find(attribute("data-value", period.toString())).click();
        return this;
    }

    @Nonnull
    @Step("Редактировать расход")
    public EditSpendingPage editSpending(String description) {
        searchSpendingByDescription(description);
        SelenideElement row = tableRows.find(text(description));
        row.$$("td").get(5).click();
        return new EditSpendingPage();
    }

    @Nonnull
    @Step("Удалить расход")
    public SpendingTable deleteSpending(String description) {
        searchSpendingByDescription(description);
        tableRows.find(text(description))
                .$$("td")
                .get(0)
                .click();
        delete.click();
        dialogWindow.$(byText("Delete")).click();
        return this;
    }

    @Nonnull
    @Step("Найти расход по описанию")
    public SpendingTable searchSpendingByDescription(String description) {
        spendingSearch.search(description);
        return this;
    }

    @Nonnull
    @Step("Проверить, что таблица содержит расход")
    public SpendingTable checkTableContains(String expectedSpend) {
        searchSpendingByDescription(expectedSpend);
        tableRows.find(text(expectedSpend)).shouldBe(visible);
        return this;
    }

    @Step("Проверить размер таблицы")
    public SpendingTable checkTableSize(int expectedSize) {
        tableRows.shouldHave(size(expectedSize));
        return this;
    }
}
