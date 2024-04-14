import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private static final Logger logger = LogManager.getLogger(LoginPage.class);
    private final WebDriver driver;
    @FindBy(xpath = "//div[@data-marker='auth-app-root']")
    private WebElement authAppRoot;
    @FindBy(xpath = "//input[@data-marker='login-form/login/input']")
    private WebElement loginField;
    @FindBy(xpath = "//input[@data-marker='login-form/password/input']")
    private WebElement passwordField;
    @FindBy(xpath = "//button[@data-marker='login-form/submit']")
    private WebElement submitButton;

    // Элементы связанные с кодом подтверждения
    @FindBy(xpath = "//input[contains(@data-marker, 'confirm/code-input/input')]")
    private WebElement confirmCodeInput;
    @FindBy(xpath = "//button[contains(@data-marker, 'confirm/confirm')]")
    private WebElement confirmButton;

    public LoginPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public void inputLogin(String login) {
        logger.info("Ввод логина: " + login);
        loginField.sendKeys(login);
    }

    public void inputPassword(String password) {
        logger.info("Ввод пароля: " + password);
        passwordField.sendKeys(password);
    }

    public void clickSubmitBtn() {
        logger.info("Нажатие на кнопку 'Войти'");
        submitButton.click();
    }

    public boolean waitInputConfirmCode() {
        logger.info("Ожидание ввода кода подтверждения");
        // Создания явного ожидания ввода 5 цифр кода подтверждения - 120c
        WebDriverWait waitCode = new WebDriverWait(driver, Duration.ofSeconds(120));

        return waitCode.until(driver -> { // Ожидание ввода пяти цифр в поле ввода кода подтверждения
            String currentValue = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].value", confirmCodeInput);
            return currentValue.matches("^\\d{5}$");
        });
    }

    public void clickConfirmCodeBtn() {
        logger.info("Нажатие на кнопку 'Подтвердить'");
        confirmButton.click();
    }

    public boolean isAuthAppRootDisplayed() {
        return authAppRoot.isDisplayed();
    }
}