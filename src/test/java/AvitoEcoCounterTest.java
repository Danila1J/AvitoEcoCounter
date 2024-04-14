import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class AvitoEcoCounterTest {
    private static final Logger logger = LogManager.getLogger(AvitoEcoCounterTest.class);
    private static WebDriver driver;
    private static Properties properties;
    public static LoginPage loginPage;
    public static EcoImpactPage ecoImpactPage;

    @BeforeClass
    public static void setup()  {
        logger.info("Начало настройки окружения для тестов");
        // Загружаем свойства из config.properties
        properties = new Properties();
        try {
            properties.load(AvitoEcoCounterTest.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            logger.error("Не удалось загрузить файл config.properties"+e.getMessage());
        }

        // Инициализируем WebDriver
        driver = new ChromeDriver();

        loginPage = new LoginPage(driver);
        ecoImpactPage = new EcoImpactPage(driver);

        // Открыть окно браузера на весь экран
        driver.manage().window().maximize();

        // Неявное ожидание 10 секунд
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        logger.info("Настройка окружения завершена");
    }

    @Before
    public void setUp() {
        // Переход на страницу экологического воздействия
        driver.get(properties.getProperty("ecoimpactpage"));
    }

    @Test
    public void testCounterDisplayBeforeLogin() {
        logger.info("Запуск тест-кейса 1: Отображение и форматирование данных (до авторизации)");
        // Находим все элементы счетчика
        List<WebElement> counters = ecoImpactPage.getListCountersEco();

        //Делаем скриншоты счетчиков
        ecoImpactPage.takeCounterScreenshots(counters, 1);
        logger.info("Тест-кейс 1 завершен");
    }

    @Test
    public void testCounterDisplayAfterLogin() {
        logger.info("Запуск тест-кейса 2: Отображение и форматирование данных (после авторизации)");
        // Добавление явного ожидания
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Нажать на кнопку "Авторизоваться"
        ecoImpactPage.clcAuthBtn();

        // Ожидание, пока сайт будет содержать два дескриптора окна
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        // Переключаемся в модальное окно
        ecoImpactPage.switchToModalWindow(wait);

        //Пользователь выполняется авторизацию с помощью (телефона/почты и пароля), ВК, Одноклассники, Apple, Google
        if (loginPage.isAuthAppRootDisplayed()) {
            // Вводим имя пользователя и пароль
            loginPage.inputLogin(properties.getProperty("avito.login"));
            loginPage.inputPassword(properties.getProperty("avito.password"));
        } else {
            logger.error("Модальное окно авторизации не отображается.");
        }

        // Нажимаем на кнопку "Войти"
        loginPage.clickSubmitBtn();

        // Если пользователь ввел 5 цифр, нажимаем кнопку "Подтвердить"
        if (loginPage.waitInputConfirmCode()) {
            loginPage.clickConfirmCodeBtn();
        } else {
            logger.error("Не корректный ввод кода подтверждения авторизации");
        }

        // Ожидаем появления значка профиля пользователя, как признак успешного входа в систему
        if (ecoImpactPage.isMenuProfileDisplayed()) {
            // Находим все элементы счетчика
            List<WebElement> counters = ecoImpactPage.getListCountersEco();

            //Делаем скриншоты счетчиков
            ecoImpactPage.takeCounterScreenshots(counters, 2);
        } else {
            logger.error("Значок профиля пользователя не отображается. Авторизация не прошла.");
        }
        logger.info("Тест-кейс 2 завершен");
    }

    @Test
    public void testCounterAdaptabilityOnResize() {
        logger.info("Запуск тест-кейса 3: Проверка адаптивности");
        // Изменяем размер окна браузера
        ecoImpactPage.simulateDifferentWindowSize(
                new Dimension(Integer.parseInt(properties.getProperty("widthWindow")),
                        Integer.parseInt(properties.getProperty("heightWindow"))));

        // Находим все элементы счетчика
        List<WebElement> counters = ecoImpactPage.getListCountersEco();

        //Делаем скриншоты счетчиков
        ecoImpactPage.takeCounterScreenshots(counters, 3);
        logger.info("Тест-кейс 3 завершен");
    }

    @AfterClass
    public static void tearDown() {
        logger.info("Закрытие браузера и завершение тестов");
        driver.quit();
    }
}