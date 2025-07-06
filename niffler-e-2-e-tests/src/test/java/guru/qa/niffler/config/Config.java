package guru.qa.niffler.config;

public interface Config {

    static Config getInstance() {
        return LocalConfig.INSTANCE;
    }

    String frontUrl();

    String authUrl();

    String gatewayUrl();

    String userDataUrl();

    String spendUrl();

    String ghUrl();

    String authJdbcUrl();

    String userdataJdbcUrl();

    String spendJdbcUrl();

    String currencyJdbcUrl();
}
