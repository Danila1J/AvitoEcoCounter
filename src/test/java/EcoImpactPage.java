import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class EcoImpactPage {
    private static final Logger logger = LogManager.getLogger(EcoImpactPage.class);
    private final WebDriver driver;
    @FindBy(xpath = "//button[contains(., 'Авторизоваться')]")
    private WebElement authButton;
    @FindBy(xpath = "//div[contains(@class, 'desktop-impact-item')]")
    public WebElement panelCountersEco;
    @FindBy(xpath = "//div[contains(@class, 'desktop-impact-item')]")
    private WebElement counterEco;
    @FindBy(xpath = "//div[contains(@class, 'desktop-impact-item')]//div[contains(@class, 'desktop-value')]/../..")
    private List<WebElement> listCountersEco;
    @FindBy(xpath = "//div[@data-marker='header/menu-profile']")
    private WebElement menuProfile;


    public EcoImpactPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public void clcAuthBtn() {
        logger.info("Нажатие на кнопку 'Авторизоваться'");
        authButton.click();
    }

    public void switchToModalWindow(WebDriverWait wait) {
        logger.info("Переключение на модальное окно авторизации");
        // Ожидание открытия модального окна
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        // Переключение на модальное окно
        String mainWindow = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(mainWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }

    public void takeCounterScreenshots(List<WebElement> counters, int testNumber) {
        logger.info("Создание скриншотов счетчиков для тест-кейса " + testNumber);
        int counterNumber = 1;
        for (WebElement counter : counters) {
            if (counter.isDisplayed()) {
                String filename = String.format("output/test%d_counter%d.png", testNumber,counterNumber);
                takeScreenshot(counter, filename);
                logger.info("Сохранение скриншота счетчика " + counterNumber);
            } else {
                logger.warn("Счетчик " + counterNumber + " не виден, скриншот пропускаем");
            }
            counterNumber++;
        }
    }

    private void takeScreenshot(WebElement element, String filename) {
        File screenshotFile = element.getScreenshotAs(OutputType.FILE);
        createOutputDirectory();
        try {
            ImageIO.write(ImageIO.read(screenshotFile), "PNG", new File(filename));
        } catch (IOException e) {
            logger.error("Ошибка при сохранении скриншота", e);
        }
    }

    private void createOutputDirectory() {
        File outputDir = new File("output");
        if (!outputDir.exists() && !outputDir.mkdir()) {
            logger.error("Не удалось создать каталог output/");
        }
    }

    public void simulateDifferentWindowSize(Dimension targetSize) {
        logger.info("Имитация изменения размеров окна браузера " + targetSize.toString());
        driver.manage().window().setSize(targetSize);
    }

    public boolean isMenuProfileDisplayed() {
        return menuProfile.isDisplayed();
    }
    public List<WebElement> getListCountersEco() {
        return listCountersEco;
    }
}