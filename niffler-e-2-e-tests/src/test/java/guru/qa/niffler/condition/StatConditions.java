package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.ui.Bubble;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions {
    @Nonnull
    public static WebElementCondition color(@Nonnull Color expectedColor) {
        return new WebElementCondition("color " + expectedColor.rgb) {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String rgba = element.getCssValue("background-color");
                return new CheckResult(
                        expectedColor.rgb.equals(rgba),
                        rgba
                );
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubbles(@Nonnull Bubble... bubbles) {
        return new WebElementsCondition() {
            private final String expectedBubbles = Arrays.stream(bubbles).toList().toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(bubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (bubbles.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", bubbles.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                final List<Bubble> actualBubbles = new ArrayList<>();

                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Color colorToCheck = bubbles[i].color();
                    final String textToCheck = bubbles[i].text();

                    final String rgba = elementToCheck.getCssValue("background-color");
                    final String text = elementToCheck.getText();

                    actualBubbles.add(new Bubble(Color.getByValue(rgba), text));
                    if (passed) {
                        passed = colorToCheck.rgb.equals(rgba) && textToCheck.equals(text);
                    }
                }

                if (!passed) {
                    final String message = String.format(
                            "List bubbles mismatch (expected: %s, actual: %s)", expectedBubbles, actualBubbles
                    );
                    return rejected(message, actualBubbles);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubbles;
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubblesInAnyOrder(@Nonnull Bubble... bubbles) {
        return new WebElementsCondition() {
            private final String expectedBubbles = Arrays.stream(bubbles).toList().toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(bubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (bubbles.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", bubbles.length, elements.size());
                    return rejected(message, elements);
                }

                final List<Bubble> expectedBubbles = new ArrayList<>(Arrays.asList(bubbles));

                final List<Bubble> actualBubbles = elements.stream()
                        .map(e -> new Bubble(
                                        Color.getByValue(e.getCssValue("background-color")),
                                        e.getText()
                                )
                        ).collect(Collectors.toList());

                boolean allMatched = expectedBubbles.equals(actualBubbles);

                if (!allMatched) {
                    final String message = String.format(
                            "List bubbles mismatch (expected: %s, actual: %s)", expectedBubbles, actualBubbles
                    );
                    return rejected(message, actualBubbles);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubbles;
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubblesContains(@Nonnull Bubble... bubbles) {
        return new WebElementsCondition() {
            private final String expectedBubbles = Arrays.stream(bubbles).toList().toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(bubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                final Set<Bubble> actualBubbles = elements.stream()
                        .map(e -> new Bubble(Color.getByValue(e.getCssValue("background-color")), e.getText()))
                        .collect(Collectors.toSet());

                boolean containsAllExpected = Arrays.stream(bubbles).allMatch(b ->
                        elements.stream().anyMatch(e ->
                                b.text().equals(e.getText()) &&
                                        b.color().rgb.equals(e.getCssValue("background-color"))
                        )
                );

                if (!containsAllExpected) {
                    final String message = String.format(
                            "List bubbles mismatch (expected: %s, actual: %s)", expectedBubbles, actualBubbles
                    );
                    return rejected(message, actualBubbles);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubbles;
            }
        };
    }
}