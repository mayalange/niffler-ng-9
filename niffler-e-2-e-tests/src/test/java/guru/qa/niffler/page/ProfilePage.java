package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

@ParametersAreNonnullByDefault
public class ProfilePage {

    public static String url = Config.getInstance().frontUrl() + "profile";

    private final SelenideElement profileIcon = $x("//*[@data-testid='PersonIcon']//ancestor::button");
    private final SelenideElement profileButton = $x("//*[text()='Profile']");
    private final SelenideElement menu = $x("//*[@data-testid='sentinelStart']/following-sibling::*[contains(@class, 'Menu')]");
    private final SelenideElement category = $x("//*[@id='category']");
    private final SelenideElement archiveButton = $x("//*[text()='Archive']");
    private final SelenideElement unarchiveButton = $x("//*[text()='Unarchive']");
    private final SelenideElement showArchivedSwitch = $x("//*[text()='Show archived']/preceding-sibling::*");
    private final SelenideElement avatar = $("#image__input").parent().$("img");
    private final SelenideElement userName = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement photoInput = $("input[type='file']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement categoryInput = $("input[name='category']");
    private final SelenideElement archivedSwitcher = $(".MuiSwitch-input");
    private final ElementsCollection bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
    private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");

    private final Calendar calendar = new Calendar($(".ProfileCalendar"));


    public ProfilePage openProfile(String username) {
        profileIcon.click();
        menu.shouldBe(visible);
        profileButton.click();
        $x(String.format("//*[@id='username' and (contains(@value, '%s'))]", username)).shouldBe(visible);
        return this;
    }

    @Nonnull
    public ProfilePage setName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    @Nonnull
    public ProfilePage addCategory(String name) {
        category.setValue(name).pressEnter();
        $x(String.format("//*[text()='%s']", name)).shouldBe(visible);
        return this;
    }

    @Nonnull
    public ProfilePage uploadPhotoFromClasspath(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Заархивировать категорию '{0}'")
    public ProfilePage archiveCategory(String name) {
        $x(String.format("//*[text()='%s']/parent::*/following-sibling::*/*[contains(@aria-label, 'Archive')]", name))
                .click();
        archiveButton.click();
        $x(String.format("//*[text()='Category %s is archived']", name)).shouldBe(visible);
        return this;
    }

    @Step("Разархивировать категорию '{0}'")
    public ProfilePage unarchiveCategory(String name) {
        showArchivedSwitch.click();
        $x(String.format("//*[text()='%s']/parent::*/following-sibling::*/*[contains(@aria-label, 'Unarchive')]", name))
                .click();
        unarchiveButton.click();
        $x(String.format("//*[text()='Category %s is unarchived']", name)).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Проверить, что у пользователя есть категория '{0}'")
    public ProfilePage checkCategoryExists(String category) {
        bubbles.find(text(category)).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Проверить, что поле username содержит значение '{0}'")
    public ProfilePage checkUsername(String username) {
        this.userName.should(value(username));
        return this;
    }

    @Nonnull
    @Step("Проверить, что поле name содержит значение '{0}'")
    public ProfilePage checkName(String name) {
        nameInput.shouldHave(value(name));
        return this;
    }

    @Nonnull
    @Step("Проверить, что фото существует")
    public ProfilePage checkPhotoExist() {
        avatar.should(attributeMatching("src", "data:image.*"));
        return this;
    }

    @Nonnull
    @Step("Проверить, что у пользователя есть архивная категория '{0}'")
    public ProfilePage checkArchivedCategoryExists(String category) {
        archivedSwitcher.click();
        bubblesArchived.find(text(category)).shouldBe(visible);
        return this;
    }
}
