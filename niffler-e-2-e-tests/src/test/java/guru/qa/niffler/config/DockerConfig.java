package guru.qa.niffler.config;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

enum DockerConfig implements Config {
    INSTANCE;

    @Nonnull
    @Override
    public String frontUrl() {
        return "";
    }

    @Override
    public @Nonnull String authUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String gatewayUrl() {
        return "";
    }

    @NotNull
    @Override
    public String userDataUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String userdataJdbcUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String spendUrl() {
        return "";
    }

    @NotNull
    @Override
    public String ghUrl() {
        return Config.super.ghUrl();
    }

    @Nonnull
    @Override
    public String spendJdbcUrl() {
        return "";
    }

    @Nonnull
    @Override
    public String currencyJdbcUrl() {
        return "";
    }
}
