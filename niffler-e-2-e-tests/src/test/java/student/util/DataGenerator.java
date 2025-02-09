package student.util;

import com.github.javafaker.Faker;
import student.model.CurrencyValues;
import student.model.UserJson;

import java.util.Locale;
import java.util.UUID;

public class DataGenerator {

    private static final Faker faker = new Faker(Locale.US);
    public static final String userName = "mike";
    public static final String userPassword = "123456!@";

    public static final String category = "defaultCategory";


    public static UserJson getUser() {
        return new UserJson(UUID.randomUUID(),
                faker.name().username(),
                faker.name().name(),
                faker.name().lastName(),
                faker.name().fullName(),
                CurrencyValues.RUB,
                null,
                null,
                null);
    }
}
