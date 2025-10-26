package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.model.ui.Bubble;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.open;
import static guru.qa.niffler.model.CurrencyValues.RUB;

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

    open(CFG.frontUrl(), LoginPage.class)
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
    open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getStatComponent()
            .checkStatisticBubblesContains("Обучение 79990 ₽")
            .checkStatisticImage(expected);
  }

  @User(
          categories = {
                  @Category(name = "Путешествия"),
                  @Category(name = "Ипотека", archived = true),
                  @Category(name = "Страховка", archived = true)
          },
          spendings = {
                  @Spending(
                          category = "Путешествия",
                          description = "В Японию",
                          amount = 400000
                  ),
                  @Spending(
                          category = "Ипотека",
                          description = "Сбербанк",
                          amount = 90000
                  ),
                  @Spending(
                          category = "Страховка",
                          description = "Каско",
                          amount = 80000
                  )
          }
  )
  @ScreenShotTest(value = "img/expected-stat-archived.png")
  void statComponentShouldDisplayArchivedCategories(UserJson user, BufferedImage expected) throws IOException {
    open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getStatComponent()
            .checkStatisticBubblesContains("Поездки 9500 ₽", "Archived 3100 ₽")
            .checkStatisticImage(expected);
  }

    @User(
            spendings = @Spending(
                    category = "Подписка netflix",
                    description = "Подписка",
                    amount = 3000
            )
    )
    @Test
    @DisplayName("Созданный расход отображается в Bubbles")
    void statBubblesContentShouldContainValidTextAndColor(UserJson user) {
        open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkStatBubbles(
                        Bubble.from(user, RUB, Color.yellow, 0)
                );
    }

    @User(
            spendings = {@Spending(
                    category = "Отдых",
                    description = "отдых в санатории",
                    amount = 100000
            ),
                    @Spending(
                            category = "Автомобиль",
                            description = "Каско",
                            amount = 100000
                    ),
                    @Spending(
                            category = "Подписка",
                            description = "Покупка подписки google",
                            amount = 2000
                    )}
    )
    @Test
    @DisplayName("Созданный расход отображается в Bubbles")
    void statBubblesContentShouldBeAssertedInAnyOrder(UserJson user) {
        open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkStatBubblesAnyOrder(
                        Bubble.from(user, RUB, Color.yellow, 1),
                        Bubble.from(user, RUB, Color.green, 0),
                        Bubble.from(user, RUB, Color.blue100, 2)
                );
    }

    @User(
            spendings = {@Spending(
                    category = "Отдых",
                    description = "отдых в санатории",
                    amount = 100000
            ),
                    @Spending(
                            category = "Обучение",
                            description = "Новый курс",
                            amount = 20000
                    )}
    )
    @Test
    @DisplayName("Созданный расход отображается")
    void statBubblesContentShouldContain(UserJson user) {
        open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkStatBubblesContains(
                        Bubble.from(user, RUB, Color.green, 0)
                );
    }
}