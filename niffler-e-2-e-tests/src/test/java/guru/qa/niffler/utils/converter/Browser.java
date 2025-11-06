package guru.qa.niffler.utils.converter;

import com.codeborne.selenide.SelenideConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Browser {
    CHROME(new SelenideConfig().browser("chrome").pageLoadStrategy("eager").timeout(5000L)),
    FIREFOX(new SelenideConfig().browser("firefox").pageLoadStrategy("eager").timeout(5000L));

    private final SelenideConfig config;
}