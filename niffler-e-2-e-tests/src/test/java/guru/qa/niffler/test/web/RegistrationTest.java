package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class RegistrationTest {

  private static final Config CFG = Config.getInstance();

  @Test
  void shouldRegisterNewUser() {
    String newUsername = randomUsername();
    String password = "12345";
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doRegister()
            .fillRegisterPage(newUsername, password, password)
            .successSubmit()
            .successLogin(newUsername, password)
            .checkThatPageLoaded();
  }

  @Test
  void shouldNotRegisterUserWithExistingUsername() {
    String existingUsername = "marina";
    String password = "052322";

    LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
    loginPage.doRegister()
            .fillRegisterPage(existingUsername, password, password)
            .submit();
    loginPage.checkError("Username `" + existingUsername + "` already exists");
  }

  @Test
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    String newUsername = randomUsername();
    String password = "12345";

    LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
    loginPage.doRegister()
            .fillRegisterPage(newUsername, password, "bad password submit")
            .submit();
    loginPage.checkError("Passwords should be equal");
  }


}