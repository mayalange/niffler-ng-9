package guru.qa.niffler.config;

import javax.annotation.Nonnull;

public interface Config {

    @Nonnull
    static Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.INSTANCE
                : LocalConfig.INSTANCE;
    }

    @Nonnull
    String frontUrl();

    @Nonnull
    String authUrl();

    @Nonnull
    String gatewayUrl();

    @Nonnull
    String userDataUrl();

    @Nonnull
    String spendUrl();

    @Nonnull
    default String ghUrl() {
        return "https://api.github.com/";
    }

    @Nonnull
    String authJdbcUrl();

    @Nonnull
    String userdataJdbcUrl();

    @Nonnull
    String spendJdbcUrl();

    @Nonnull
    String currencyJdbcUrl();
}
