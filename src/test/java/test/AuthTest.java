package test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static data.DataGenerator.Registration.getRegisteredUser;
import static data.DataGenerator.Registration.getUser;
import static data.DataGenerator.getRandomLogin;
import static data.DataGenerator.getRandomPassword;

class AuthTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999/");
    }

    @Test
    void shouldSuccessfulLoginIfRegisteredActiveUser() {
        var registeredUser = getRegisteredUser("active");
        $("[data-test-id='login'] input").val(registeredUser.getLogin());
        $("[data-test-id='password'] input").val(registeredUser.getPassword());
        $("[data-test-id='action-login']").click();
        $("[id='root']").shouldHave(Condition.text("Личный кабинет"));
    }

    @Test
    void shouldGetErrorIfNotRegisteredUser() {
        var notRegisteredUser = getUser("active");
        $("[data-test-id='login'] input").val(notRegisteredUser.getLogin());
        $("[data-test-id='password'] input").val(notRegisteredUser.getPassword());
        $("[data-test-id='action-login']").click();
        $("[class='notification__content']").shouldHave(Condition.text("Ошибка! Неверно указан логин или пароль"));

    }

    @Test
    void shouldGetErrorIfBlockedUser() {
        var blockedUser = getRegisteredUser("blocked");
        $("[data-test-id='login'] input").val(blockedUser.getLogin());
        $("[data-test-id='password'] input").val(blockedUser.getPassword());
        $("[data-test-id='action-login']").click();
        $("[class='notification__content']").shouldHave(Condition.text("Ошибка! Пользователь заблокирован"));
    }

    @Test
    void shouldGetErrorIfWrongLogin() {
        var registeredUser = getRegisteredUser("active");
        var wrongLogin = getRandomLogin();
        $("[data-test-id='login'] input").setValue((wrongLogin));
        $("[data-test-id='password'] input").setValue(registeredUser.getPassword());
        $("[data-test-id='action-login']").click();
        $("[class='notification__content']").shouldHave(Condition.text("Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    void shouldGetErrorIfWrongPassword() {
        var registeredUser = getRegisteredUser("active");
        var wrongPassword = getRandomPassword();
        $("[data-test-id='login'] input").val((registeredUser.getLogin()));
        $("[data-test-id='password'] input").val(wrongPassword);
        $("[data-test-id='action-login']").click();
        $("[class='notification__content']").shouldHave(Condition.text("Ошибка! Неверно указан логин или пароль"));
    }
}