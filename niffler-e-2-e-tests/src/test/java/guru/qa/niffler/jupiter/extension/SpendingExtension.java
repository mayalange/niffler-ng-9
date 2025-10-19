package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;
import static java.util.Arrays.stream;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                            if (ArrayUtils.isNotEmpty(userAnno.spendings())) {
                                final @Nullable UserJson createdUser = UserExtension.createdUser();

                                final List<SpendJson> result = new ArrayList<>();
                                for (Spending spendAnno : userAnno.spendings()) {
                                    final String username = createdUser != null ? createdUser.username() : userAnno.username();
                                    final List<CategoryJson> existingCategories = createdUser != null
                                            ? createdUser.testData().categories()
                                            : stream(CategoryExtension.createdCategory()).toList();
                                    final Optional<CategoryJson> matchedCategory = existingCategories.stream()
                                            .filter(cat -> cat.name().equals(spendAnno.category()))
                                            .findFirst();

                                    SpendJson spend = new SpendJson(
                                            null,
                                            new Date(),
                                            matchedCategory.orElseGet(() -> new CategoryJson(
                                                    null,
                                                    spendAnno.category(),
                                                    username,
                                                    false
                                            )),
                                            spendAnno.currency(),
                                            spendAnno.amount(),
                                            spendAnno.description(),
                                            username
                                    );

                                    result.add(
                                            spendApiClient.create(spend)
                                    );
                                }

                                if (createdUser != null) {
                                    createdUser.testData().spendings().addAll(result);
                                } else {
                                    context.getStore(NAMESPACE).put(
                                            context.getUniqueId(),
                                            result.stream().toArray(SpendJson[]::new)
                                    );
                                }
                            }
                        }
                );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
    }

    @Override
    public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return createdSpending();
    }

    public static SpendJson[] createdSpending() {
        final ExtensionContext methodContext = context();
        return methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), SpendJson[].class);
    }
}