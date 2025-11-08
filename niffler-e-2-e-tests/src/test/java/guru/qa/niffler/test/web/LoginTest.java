package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

@WebTest
public class LoginTest {

  @User
  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) throws InterruptedException {
    System.out.println("Пытаемся войти под пользователем: " + user.username());
    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .checkThatPageLoaded();
  }

  @Test
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
    Selenide.open(LoginPage.URL, LoginPage.class)
            .submit(new LoginPage())
            .checkError("Неверные учетные данные пользователя");
  }
}