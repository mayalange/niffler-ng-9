package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.randomName;

@WebTest
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @User(
            username = "marina",
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin("marina", "052322")
                .checkThatPageLoaded();

        Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
                .checkArchivedCategoryExists(category.name());
    }

    @User(
            username = "marina",
            categories = @Category(
                    archived = false
            )
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin("marina", "052322")
                .checkThatPageLoaded();

        Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
                .checkCategoryExists(category.name());
    }

    @User
    @Test
    void updateProfileName(UserJson user) {
        String newName = randomName();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .getHeader()
                .goProfilePage()
                .checkThatPageLoaded()
                .setName(newName);

        Selenide.refresh();
        new ProfilePage().checkName(newName);
    }

    @User
    @ScreenShotTest(value = "img/expected-avatar.png")
    void shouldUpdateProfileWithAllFieldsSet(UserJson user, BufferedImage expectedAvatar) throws IOException {
        final String newName = randomName();

        ProfilePage profilePage = Selenide.open(LoginPage.URL, LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit(new MainPage())
                .checkThatPageLoaded()
                .getHeader()
                .goProfilePage()
                .uploadPhotoFromClasspath("img/cat.jpeg")
                .setName(newName)
                .submitProfile()
                .checkAlert("Profile successfully updated");
        Selenide.refresh();

        profilePage.checkName(newName)
                .checkPhotoExist()
                .checkPhoto(expectedAvatar);
    }
}