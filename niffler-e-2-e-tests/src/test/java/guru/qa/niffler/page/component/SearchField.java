package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent {
    private final SelenideElement clearSearchInput = $("#input-clear");

    public SearchField() {
        super($("input[placeholder='Search']"));
    }

    @Nonnull
    @Step("В строке поиска записываем текст")
    public SearchField search(@Nonnull String text) {
        clearIfNotEmpty();
        self.setValue(text).pressEnter();
        return this;
    }

    @Nonnull
    @Step("Очищаем строку поиска")
    public SearchField clearIfNotEmpty() {
        if (self.is(not(empty))) {
            clearSearchInput.should(visible).click();
            self.should(empty);
        }
        return this;
    }
}