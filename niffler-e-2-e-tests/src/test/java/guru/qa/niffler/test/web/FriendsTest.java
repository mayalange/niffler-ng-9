package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class FriendsTest {
    private static final Config CFG = Config.getInstance();

    @Test
    @User(
            friends = 1
    )
    void friendShouldBePresentInFriendsTable(UserJson user) {
        final UserJson friend = user.testData().friends().getFirst();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .goToUserFriendsPage()
                .checkThatPageLoaded()
                .findPeople(friend.username())
                .verifyFriends(friend.username());
    }

    @Test
    @User
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .goToUserFriendsPage()
                .verifyNoFriend();
    }

    @Test
    @User(
            incomeInvitations = 1
    )
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        final UserJson income = user.testData().incomeInvitations().getFirst();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .getHeader()
                .goFriendsPage()
                .checkThatPageLoaded()
                .verifyIncomeInvitation(income.username());
    }

    @Test
    @User(
            outcomeInvitations = 1
    )
    void outcomeInvitationBePresentInAllPeopleTable(UserJson user) {
        UserJson outcome = user.testData().outcomeInvitations().getFirst();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .getHeader()
                .goFriendsPage()
                .checkThatPageLoaded()
                .clickOnAllPeopleTable()
                .verifyOutcomeInvitation(outcome.username());
    }

    @Test
    @User(
            incomeInvitations = 1
    )
    void acceptIncomeInvitationFriendTable(UserJson user) {
        final UserJson income = user.testData().incomeInvitations().getFirst();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .getHeader()
                .goFriendsPage()
                .checkThatPageLoaded()
                .verifyIncomeInvitation(income.username())
                .acceptFriendRequestFromUser(income.username());
    }

    @Test
    @User(
            outcomeInvitations = 1
    )
    void declineIncomeInvitationFriendTable(UserJson user) {
        UserJson outcome = user.testData().outcomeInvitations().getFirst();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .getHeader()
                .goFriendsPage()
                .checkThatPageLoaded()
                .clickOnAllPeopleTable()
                .verifyOutcomeInvitation(outcome.username())
                .declineFriendRequestFromUser(outcome.username());
    }
}