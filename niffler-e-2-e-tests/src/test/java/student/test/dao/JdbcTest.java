package student.test.dao;

import org.junit.jupiter.api.Test;
import student.model.CurrencyValues;
import student.model.UserJson;
import student.service.AuthDbClient;

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
}
