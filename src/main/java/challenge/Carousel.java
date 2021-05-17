package challenge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Carousel {
	private static WebDriver driver;
	private static JavascriptExecutor jse;
	private static String carouselXpath, carouselNextButtonXpath, itemsXpath;

	public static List<String> getCarouselsItems(String carouselName) {
		List<String> itemDetails = new ArrayList<String>();
		carouselXpath = "//div[div/h3/text()='" + carouselName + "']/following-sibling::div";
		carouselNextButtonXpath = carouselXpath + "/div[contains(@class,'swiper-button-next')]";
		itemsXpath = carouselXpath + "//div[@data-qa='product-name']/div";

		// AJAX page - let us slowly scroll to the desired object and bring it into view
		scrollToObject(By.xpath(carouselXpath));

		WebDriverWait wait = new WebDriverWait(driver, 1);
		List<WebElement> products = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(itemsXpath)));
		int productsCount = products.size();
		for (int i = 0; i < productsCount; i++) {
			try {
				WebElement product = wait.until(ExpectedConditions.visibilityOf(products.get(i)));
			} catch (TimeoutException e) {
				driver.findElement(By.xpath(carouselNextButtonXpath)).click();
				products = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(itemsXpath)));
			}
			itemDetails.add(products.get(i).getText());
		}
		Collections.sort(itemDetails); // sorting the list in ascending order
		return itemDetails;
	}

	public static void main(String[] args) {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		jse = (JavascriptExecutor) driver;
		driver.manage().window().maximize();
//		String carouselName = "Top picks in electronics";
		String carouselName = "Top picks in home & kitchen";
		driver.get("https://www.noon.com/uae-en/");
		List<String> products = getCarouselsItems(carouselName);
		System.out.println("No. of products under the carousel \"" + carouselName + "\": " + products.size());
		products.stream().forEach(e -> System.out.println(e));
		driver.close();
	}

	public static void scrollToObject(By locator) {
		Long prevHeight, currentHeight;
		prevHeight = (long) -1;
		currentHeight = (long) 0;
		while (currentHeight > prevHeight) {
			jse.executeScript("window.scrollBy(0,100);");
			prevHeight = currentHeight;
			currentHeight = (Long) jse.executeScript("return window.pageYOffset;");
			try {
				driver.findElement(locator);
				jse.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(locator));
				break; // stop scrolling and break
			} catch (NoSuchElementException e) {
				// continue;
			}
		}
	}
}
