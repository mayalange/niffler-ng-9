package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.utils.converter.Browser;
import guru.qa.niffler.utils.converter.BrowserConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import static com.codeborne.selenide.Condition.text;

public class BrowserTest {

    private static final String FRONT_URL = Config.getInstance().frontUrl();

    @ParameterizedTest(name = "Тест должен запуститься в браузере {0} в параллель с другим браузером")
    @EnumSource(Browser.class)
    void browsersTest(@ConvertWith(BrowserConverter.class) SelenideDriver driver) {
        driver.open(FRONT_URL);
        driver.$(".logo-section__text").shouldHave(text("Niffler"));
    }
}