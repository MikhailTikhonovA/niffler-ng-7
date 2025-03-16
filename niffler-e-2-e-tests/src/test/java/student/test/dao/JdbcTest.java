package student.test.dao;

import org.junit.jupiter.api.Test;
import student.model.CategoryJson;
import student.model.CurrencyValues;
import student.model.SpendJson;
import student.model.UserJson;
import student.service.AuthDbClient;
import student.service.SpendDbClient;

import java.util.Date;

public class JdbcTest {

    @Test
    void springJdbcTest() {
        AuthDbClient user = new AuthDbClient();
        UserJson userJson = user.createUserSpringJdbc(
                new UserJson(
                        null,
                        "testVasya",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        System.out.println(userJson);
    }

    @Test
    void txTest() throws Exception {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-3",
                                "testVasya",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx",
                        "testVasya"
                )
        );

        System.out.println(spend);
    }

    @Test
    void xaTxTest() {
        AuthDbClient usersDbClient = new AuthDbClient();
        UserJson user = usersDbClient.createUserTxTemplate(
                new UserJson(
                        null,
                        "testVasya-xatx",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void chainedTxTest() {
        AuthDbClient usersDbClient = new AuthDbClient();
        UserJson user = usersDbClient.createUserTxTemplate(
                new UserJson(
                        null,
                        "testVasya-chainedTx",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        System.out.println(user);
    }
}
