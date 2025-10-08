package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static guru.qa.niffler.page.Pages.mainPage;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement createNewUserButton = $("#register-button");
    private final SelenideElement registerButton = $("a[href='/register']");
    private final SelenideElement errorContainer = $(".form__error");


    @Nonnull
    @Step("Нажать на кнопку 'Create new account'")
    public RegisterPage doRegister() {
        registerButton.click();
        return new RegisterPage();
    }

    @Step("На странице логина ввести имя '{0}' и пароль '{1}'")
    public LoginPage fillLoginPage(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        return this;
    }

    @Step("Нажать на кнопку 'submit'")
    public MainPage submit() {
        submitButton.click();
        return mainPage;
    }

    @Step("Проверить ошибку с текстом 'Неверные учетные данные пользователя'")
    public LoginPage checkError(String error) {
        errorContainer.shouldHave(text(error));
        return this;
    }

    @Step("Кликнуть на кнопку регистрации нового пользователя")
    public RegisterPage createNewUserButton() {
        createNewUserButton.click();
        return new RegisterPage();
    }

    @Nonnull
    @Step("Проверка успешного логина")
    public MainPage successLogin(String username, String password) {
        fillLoginPage(username, password);
        submit();
        return mainPage;
    }

    @Nonnull
    @Step("Проверка неуспешного логина")
    public LoginPage checkFailedLogin() {
        $x("//*[text() = 'Неверные учетные данные пользователя']").shouldBe(visible);
        return this;
    }
}