package student.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import student.config.Config;
import student.jupiter.annotaion.meta.WebTest;
import student.pages.LoginPage;
import student.util.DataGenerator;

import static student.util.DataGenerator.userPassword;

@WebTest
public class RegistrationTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void shouldRegisterNewUser() {
        var user = DataGenerator.getUser();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterButton()
                .setUserName(user.username())
                .setPassword(userPassword)
                .setPasswordSubmit(userPassword)
                .clickSubmitButton()
                .verifySuccessfulRegistration()
                .clickLoginButton()
                .setUserName(user.username())
                .setPassword(userPassword)
                .clickSubmitButton()
                .verifyTitleMainPage();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        var user = DataGenerator.getUser();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterButton()
                .setUserName(user.username())
                .setPassword(userPassword)
                .setPasswordSubmit(userPassword)
                .clickSubmitButton()
                .verifySuccessfulRegistration()
                .clickLoginButton()
                .clickRegisterButton()
                .setUserName(user.username())
                .setPassword(userPassword)
                .setPasswordSubmit(userPassword)
                .verifyErrorAlreadyExistsUserMessage(user.username());
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        var user = DataGenerator.getUser();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterButton()
                .setUserName(user.username())
                .setPassword(userPassword)
                .setPasswordSubmit(user.username())
                .verifyErrorPasswordNotEqualMessage();
    }

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        var user = DataGenerator.getUser();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterButton()
                .setUserName(user.username())
                .setPassword(userPassword)
                .setPasswordSubmit(userPassword)
                .clickSubmitButton()
                .verifySuccessfulRegistration()
                .clickLoginButton()
                .setUserName(user.username())
                .setPassword(userPassword)
                .clickSubmitButton()
                .verifyTitleMainPage();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        var user = DataGenerator.getUser();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .clickRegisterButton()
                .setUserName(user.username())
                .setPassword(userPassword)
                .setPasswordSubmit(userPassword)
                .clickSubmitButton()
                .verifySuccessfulRegistration()
                .clickLoginButton()
                .setUserName(user.username())
                .setPassword(user.username())
                .verifyErrorCredentialsMessage()
                .verifyLoginPage();
    }
}
