package guru.qa.niffler.api.core;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

@ParametersAreNonnullByDefault
public enum ThreadSafeCookieStore implements CookieStore {
    INSTANCE;

    private final ThreadLocal<CookieStore> cookieStore = ThreadLocal.withInitial(
            this::cookieStore
    );

    @Override
    public void add(URI uri, HttpCookie cookie) {
        cookieStore.get().add(uri, cookie);
    }

    @Override
    @Nonnull
    public List<HttpCookie> get(URI uri) {
        return cookieStore.get().get(uri);
    }

    @Override
    @Nonnull
    public List<HttpCookie> getCookies() {
        return cookieStore.get().getCookies();
    }

    @Override
    @Nonnull
    public List<URI> getURIs() {
        return cookieStore.get().getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return cookieStore.get().remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return cookieStore.get().removeAll();
    }

    @Nonnull
    private CookieStore cookieStore() {
        return new CookieManager().getCookieStore();
    }

    @Nonnull
    public String cookieValue(String cookieName) {
        return getCookies().stream()
                .filter(c -> c.getName().equals(cookieName))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow();
    }
}
