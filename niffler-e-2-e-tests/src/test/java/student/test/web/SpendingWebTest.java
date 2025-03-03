package student.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import student.config.Config;
import student.jupiter.annotaion.Category;
import student.jupiter.annotaion.Spending;
import student.jupiter.annotaion.meta.User;
import student.jupiter.annotaion.meta.WebTest;
import student.model.SpendJson;
import student.pages.LoginPage;
import student.pages.MainPage;

import static student.util.DataGenerator.userName;
import static student.util.DataGenerator.userPassword;

@WebTest
public class SpendingWebTest {

    private static final Config CFG = Config.getInstance();

    @User(
            username = userName,
            categories = @Category(),
            spendings = @Spending(description = "NEW", amount = 4000)
    )
    @Test
    void categoryDescriptionShouldBeChangedFromTable(SpendJson spend) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUserName(userName)
                .setPassword(userPassword)
                .clickSubmitButton()
                .editSpending(spend.description())
                .setDescription(newDescription)
                .save();

        new MainPage().checkThatTableContainsSpending(newDescription);
    }
}
