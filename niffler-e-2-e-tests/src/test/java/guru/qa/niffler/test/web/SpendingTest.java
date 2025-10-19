package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();


  @User(
          spendings = @Spending(
                  amount = 89990.00,
                  description = "Advanced 9 поток!",
                  category = "Обучение"
          )
  )
  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
    final SpendJson spend = user.testData().spendings().getFirst();
    final String newDescription = ":)";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin(user.username(), user.testData().password())
            .checkThatPageLoaded()
            .editSpending(spend.description())
            .setNewSpendingDescription(newDescription)
            .save()
            .checkThatTableContainsSpending(newDescription);
  }

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 79990
          )
  )
  @ScreenShotTest("img/expected-stat.png")
  void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getStatComponent()
            .checkStatisticBubblesContains("Обучение 79990 ₽")
            .checkStatisticImage(expected);
  }

  @User(
          categories = {
                  @Category(name = "Поездки"),
                  @Category(name = "Ремонт", archived = true),
                  @Category(name = "Страховка", archived = true)
          },
          spendings = {
                  @Spending(
                          category = "Поездки",
                          description = "В Москву",
                          amount = 9500
                  ),
                  @Spending(
                          category = "Ремонт",
                          description = "Цемент",
                          amount = 100
                  ),
                  @Spending(
                          category = "Страховка",
                          description = "ОСАГО",
                          amount = 3000
                  )
          }
  )
  @ScreenShotTest(value = "img/expected-stat-archived.png")
  void statComponentShouldDisplayArchivedCategories(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getStatComponent()
            .checkStatisticBubblesContains("Поездки 9500 ₽", "Archived 3100 ₽")
            .checkStatisticImage(expected);
  }
}