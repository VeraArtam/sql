package ru.netology.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;
import ru.netology.page.VerificationPage;

import static com.codeborne.selenide.Selenide.open;
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
            var wrongInfo = new DataHelper.AuthInfo(authInfo.getLogin(), "wrong_password");
            loginPage.login(wrongInfo);
            Selenide.sleep(1000);
        }

        loginPage.login(authInfo);

        loginPage.loginFieldShouldBeVisible();
    }

    @Test
    public void shouldNotLoginWithRandomUser() {
        var loginPage = new LoginPage();
        var randomUser = DataHelper.generateRandomUser();

        loginPage.login(randomUser);

        loginPage.loginFieldShouldBeVisible();
    }
}