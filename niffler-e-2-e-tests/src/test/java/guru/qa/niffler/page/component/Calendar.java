package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Calendar extends BaseComponent {

    private static final DateTimeFormatter HEADER_CALENDAR_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    private final SelenideElement calendarButton = $("button[aria-label*='Choose date']");
    private final SelenideElement currentMonthAndYear = self.$(".MuiPickersCalendarHeader-label");
    private final ElementsCollection selectYear = self.$$(".MuiPickersYear-yearButton");
    private final SelenideElement prevMonthButton = self.$("button[title='Previous month']");
    private final SelenideElement nextMonthButton = self.$("button[title='Next month']");
    private final ElementsCollection days = self.$$(".MuiPickersSlideTransition-root button");

    public Calendar() {
        super($(".MuiPickersLayout-root"));
    }

    @Step("Выбрать дату в календаре")
    @Nonnull
    public Calendar selectDateInCalendar(Date date) {
        long timestamp = date.getTime();
        self.$("button[data-timestamp=" + timestamp + "]").should(visible).click();
        return this;
    }
}