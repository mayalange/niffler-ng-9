package guru.qa.niffler.utils.converter;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BrowserConverter implements ArgumentConverter {

    @RegisterExtension
    private final BrowserExtension browsersExtension = new BrowserExtension();

    @Nonnull
    @Override
    @SneakyThrows
    public Object convert(@Nonnull Object source, ParameterContext context) throws ArgumentConversionException {
        Class<?> target = context.getParameter().getType();
        if (!SelenideDriver.class.equals(target)) {
            throw new ArgumentConversionException("Waiting SelenideDriver.class type in Method Parameters but given: " + target);
        }
        return browsersExtension.withDriver(Browser.valueOf(source.toString()));
    }
}