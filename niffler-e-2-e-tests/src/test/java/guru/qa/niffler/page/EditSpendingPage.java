package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.page.Pages.mainPage;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitButton = $("#save");
  private final SelenideElement inputAmount = $("#amount");
  private final SelenideElement categoryInput = $("#category");
  private final Calendar calendar = new Calendar();


  @Nonnull
  @Step("Задать описание")
  public EditSpendingPage setNewSpendingDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    return this;
  }

  @Nonnull
  @Step("Сохранить")
  public MainPage save() {
    submitButton.click();
    return mainPage;
  }

  @Nonnull
  @Step("Задать расходу сумму на '{0}'")
  public EditSpendingPage setNewSpendingAmount(double amount) {
    inputAmount.setValue(String.valueOf(amount));
    return this;
  }

  @Step("Проверить, что страница расходов загрузилась")
  @Nonnull
  @Override
  public EditSpendingPage checkThatPageLoaded() {
    submitButton.shouldBe(visible);
    return this;
  }
}