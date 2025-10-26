package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class SpendsCondition {
    public static WebElementsCondition spends(@Nonnull SpendJson... spendings) {
        return new WebElementsCondition() {
            private static final int CATEGORY_INDEX = 2;
            private static final int AMOUNT_INDEX = 3;
            private static final int DESC_INDEX = 4;
            private static final int DATE_INDEX = 5;

            private final List<SpendJson> expectedSpends = Arrays.stream(spendings)
                    .sorted(Comparator.comparing(SpendJson::amount))
                    .toList();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(spendings)) {
                    throw new IllegalArgumentException("No expected spends given");
                }

                final Map<UUID, String> messages = new ConcurrentHashMap<>();

                final List<SpendJson> actualSpends = elements.stream().map(
                                e -> new SpendJson(
                                        null,
                                        parseDate(e.findElements(By.cssSelector("span")).get(DATE_INDEX).getText()),
                                        new CategoryJson(
                                                null,
                                                e.findElements(By.cssSelector("span")).get(CATEGORY_INDEX).getText(),
                                                null,
                                                false
                                        ),
                                        null,
                                        parseAmount(e.findElements(By.cssSelector("span")).get(AMOUNT_INDEX).getText()),
                                        e.findElements(By.cssSelector("span")).get(DESC_INDEX).getText(),
                                        null
                                )
                        )
                        .sorted(Comparator.comparing(SpendJson::amount))
                        .toList();


                for (int i = 0; i < expectedSpends.size(); i++) {
                    SpendJson expected = expectedSpends.get(i);
                    SpendJson actual = actualSpends.get(i);
                    if (!compare(parseDateToStartDay(expected.spendDate()), actual.spendDate())) {
                        messages.put(expected.id(), String.format("Spend date mismatch (expected: %s, actual: %s)",
                                expected, actual.spendDate()));
                    }
                    if (!compare(expected.category().name(), actual.category().name())) {
                        messages.put(expected.id(), String.format("Spend category mismatch (expected: %s, actual: %s)",
                                expected.category().name(), actual.category().name()));
                    }
                    if (!compare(expected.amount(), actual.amount())) {
                        messages.put(expected.id(), String.format("Spend amount mismatch (expected: %s, actual: %s)",
                                expected.amount(), actual.amount()));
                    }
                    if (!compare(expected.description(), actual.description())) {
                        messages.put(expected.id(), String.format("Spend description mismatch (expected: %s, actual: %s)",
                                expected.description(), actual.description()));
                    }
                }

                if (!messages.isEmpty()) {
                    return rejected(messages.toString(), actualSpends.toString());
                }
                return accepted();
            }

            @Nonnull
            @Override
            public String toString() {
                return expectedSpends.toString();
            }
        };
    }

    private static Double parseAmount(String amount) {
        return Double.valueOf(amount.substring(0, amount.length() - 1).trim());
    }

    private static Date parseDate(String date) {
        final SimpleDateFormat inputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
        final SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy 00:00:00", Locale.ENGLISH);
        try {
            var parsed = inputFormat.parse(date);
            var formatted = outputFormat.format(parsed);
            return outputFormat.parse(formatted);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static Date parseDateToStartDay(Date date) {
        final SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy 00:00:00", Locale.ENGLISH);
        try {
            var formatted = outputFormat.format(date);
            return outputFormat.parse(formatted);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean compare(Date expected, Date actual) {
        return expected.equals(actual);
    }

    private static boolean compare(Double expected, Double actual) {
        double epsilon = 1e-9;
        return Math.abs(expected - actual) < epsilon;
    }

    private static boolean compare(String expected, String actual) {
        return expected.equals(actual);
    }
}