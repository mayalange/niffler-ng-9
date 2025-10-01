package guru.qa.niffler.config;

import javax.annotation.Nonnull;

enum LocalConfig implements Config {
    INSTANCE;

    @Override
    @Nonnull
    public String frontUrl() {
        return "http://127.0.0.1:3000/";
    }

    @Override
    @Nonnull
    public String spendUrl() {
        return "http://127.0.0.1:8093/";
    }

    @Override
    @Nonnull
    public String authUrl() {
        return "http://127.0.0.1:9000/";
    }

    @Override
    @Nonnull
    public String userDataUrl() {
        return "http://127.0.0.1:8089/";
    }

    @Override
    @Nonnull
    public String gatewayUrl() {
        return "http://127.0.0.1:8090/";
    }

    @Override
    @Nonnull
    public String authJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/niffler-auth";
    }

    @Override
    @Nonnull
    public String userdataJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/niffler-userdata";
    }

    @Override
    @Nonnull
    public String spendJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/niffler-spend";
    }

    @Override
    @Nonnull
    public String currencyJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/niffler-currency";
    }
}