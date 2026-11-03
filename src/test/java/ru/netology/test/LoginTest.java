package ru.netology.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.SQLHelper.cleanAuthCodes;
import static ru.netology.data.SQLHelper.cleanDatabase;

public class LoginTest {
    LoginPage loginPage;
    DataHelper.AuthInfo authInfo = DataHelper.getAuthInfoWithTestData();

    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
    }

    @AfterEach
    void tearDown() {
        cleanAuthCodes();
    }

    @BeforeEach
    public void setUp() {
        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @Test
    public void shouldLoginSuccessfully() {
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    public void shouldBlockAfterThreeInvalidPasswords() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfoWithTestData();

        for (int i = 0; i < 3; i++) {
            var wrongInfo = new DataHelper.AuthInfo(authInfo.getLogin(), DataHelper.generateWrongPassword());
            loginPage.login(wrongInfo);
            loginPage.verifyErrorNotification("Неверно указан логин или пароль");
        }

        loginPage.login(authInfo);

        loginPage.loginFieldShouldBeVisible();

        String actualStatus = SQLHelper.getUserStatus(authInfo.getLogin());
        assertEquals("blocked", actualStatus, "Пользователь должен быть заблокирован после 3 неверных попыток!");
    }

    @Test
    public void shouldNotLoginWithRandomUser() {
        var loginPage = new LoginPage();
        var randomUser = DataHelper.generateRandomUser();

        loginPage.login(randomUser);

        loginPage.verifyErrorNotification("Неверно указан логин или пароль");
    }
}