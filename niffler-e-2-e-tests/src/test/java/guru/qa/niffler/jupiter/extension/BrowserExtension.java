package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.SelenideLogger;
import guru.qa.niffler.utils.converter.Browser;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;

import static guru.qa.niffler.utils.SelenideUtils.getConfig;


@ParametersAreNonnullByDefault
public class BrowserExtension implements
        BeforeEachCallback,
        AfterEachCallback,
        TestExecutionExceptionHandler,
        LifecycleMethodExecutionExceptionHandler {

    private final ThreadLocal<SelenideDriver> driver = new ThreadLocal<>();

    public SelenideDriver withDriver(Browser browser) {
        driver.set(new SelenideDriver(getConfig(browser)));
        return driver.get();
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
                .savePageSource(false)
                .screenshots(false)
        );
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        if (driver.get().hasWebDriverStarted()) {
            driver.get().close();
        }
        driver.remove();
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        doScreenshot();
        throw throwable;
    }

    private void doScreenshot() {
        if (driver.get().hasWebDriverStarted()) {
            Allure.addAttachment(
                    "Screen on fail on browser: " + driver.get().getSessionId(),
                    new ByteArrayInputStream(
                            ((TakesScreenshot) driver.get().getWebDriver()).getScreenshotAs(OutputType.BYTES)
                    )
            );
        }
    }

    public SelenideDriver getDriver() {
        return driver.get();
    }
}
