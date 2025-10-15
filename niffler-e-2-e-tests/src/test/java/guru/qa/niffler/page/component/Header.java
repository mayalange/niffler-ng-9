package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent {
    private final SelenideElement self = $("#root header");
    private final SelenideElement mainPageLink = self.$(By.linkText("Niffler"));
    private final SelenideElement addSpendingBtn = self.$(By.linkText("New spending"));
    private final SelenideElement menu = self.$("button[aria-label='Menu']");
    private final ElementsCollection menuItems = $("ul[role='menu']").$$("li");

    @Step("Проверить, что в header отображается текст 'Niffler'")
    public void checkHeaderText() {
        self.$("h1").shouldHave(text("Niffler"));
    }

    @Nonnull
    @Step("Перейти на страницу друзей")
    public FriendsPage goFriendsPage() {
        menu.click();
        menuItems.find(text("Friends")).click();
        return new FriendsPage();
    }

    @Nonnull
    @Step("Перейти на страницу профиля")
    public ProfilePage goProfilePage() {
        menu.click();
        menuItems.find(text("Profile")).click();
        return new ProfilePage();
    }

    @Nonnull
    @Step("Добавить новый расход")
    public EditSpendingPage goAddSpendingPage() {
        addSpendingBtn.click();
        return new EditSpendingPage();
    }

    @Nonnull
    @Step("Перейти на главную страницу")
    public MainPage goMainPage() {
        mainPageLink.click();
        return new MainPage();
    }

    @Step("Кликнуть на 'Sign Out'")
    public LoginPage signOut () {
        self.$("button").click();
        menuItems.find(text("Sign out")).click();
        return new LoginPage();
    }

    public Header() {
        super($("#root header"));
    }
}