package Tests.FrontEndTests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChromeTests {

	// Variables for Create Model tests
	static String pss = "";
	static String modelName = "";
	static String products = "";
	static String accounts = "";
	static int frequency = 0;
	static String date = "";
	static String text = "";
	static File log;
	static FileWriter w;
	static boolean pass = true;

	public static void main(String[] args) throws IOException {

		log = new File("log.txt");
		w = new FileWriter(log, true);

		w.write("===============================================\n");
		w.write("Starting test run - Chrome Tests - " + new Date() + "\n\n");
		// Uses chromedriver to run tests on Google Chrome.

		long start = System.nanoTime();

		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		WebDriver driver = new ChromeDriver();

		// Set the website URL
		driver.get("http://localhost:8080/Diversity/pages/index.html?role_desc=DESIGNER");

		w.write("-----------------------------------------------\n");
		boolean createModel = testCreate(driver);
		w.write("-----------------------------------------------\n");
		boolean viewModel = testView(driver);
		w.write("-----------------------------------------------\n");
		boolean editModel = testEdit(driver);
		w.write("-----------------------------------------------\n");
		boolean deleteModel = testDelete(driver);
		w.write("-----------------------------------------------\n");
		boolean opinionExtraction = testExtraction(driver);
		w.write("-----------------------------------------------\n\n");

		long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

		w.write("Test run reached the end. Results: \n\n");
		w.write("Elapsed time: " + elapsed + " milliseconds\n\n");
		w.write("Test 1 - Create Opinion Model: " + (createModel ? "passed\n" : "failed\n"));
		w.write("Test 2 - View Opinion Model: " + (viewModel ? "passed\n" : "failed\n"));
		w.write("Test 3 - Edit Opinion Model: " + (editModel ? "passed\n" : "failed\n"));
		w.write("Test 4 - Delete Opinion Model: " + (deleteModel ? "passed\n" : "failed\n"));
		w.write("Test 5 - View Opinion Extraction: " + (opinionExtraction ? "passed\n" : "failed\n"));
		w.close();

		// deleteModel(driver); // deletes the model created by the tests

		driver.close();

	}

	/**
	 * Test 1 - Create Opinion Model 
	 * Steps: 
	 * 1. Click Create Opinion Model 
	 * 2. Select PSS (this test always selects the 3rd option) 
	 * 3. Fill in the model name 
	 * 4. Select some final products 
	 * 5. Add social network and user name 
	 * 6. Define update frequency 
	 * 7. Define start date 
	 * 8. Submit 
	 * 9. Open edit page
	 * to check if data matches
	 *
	 * @param driver - the selenium webdriver
	 * @return - true if the test passes, otherwise returns false
	 * @throws IOException - if an error occurs with the log.txt file
	 */
	public static boolean testCreate(WebDriver driver) throws IOException {
		w.write("Starting Create Opinion Model Test\n\n");
		driver.findElement(By.linkText("Create Opinion Model")).click();
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.common.base.Function#apply(java.lang.Object)
			 */
			public Boolean apply(WebDriver d) {
				if (!d.getCurrentUrl().contains("models.html")) {
					try {
						w.write("Page was not redirected to model creation after clicking. Stopping test run.\n");
						pass = false;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				}
				// Find and set the PSS dropdown
				WebElement pssBox = d.findElement(By.id("pss"));
				Select pssList = new Select(pssBox);
				pssList.selectByIndex(2);
				pss = pssList.getFirstSelectedOption().getText();

				// Generate a random 32 bit integer name for the model name to
				// avoid duplicates
				WebElement modelNameBox = d.findElement(By.id("model_name"));
				modelName = (new BigInteger(32, new Random())).toString();
				modelNameBox.sendKeys(modelName);

				// Select the first two options from the product selection tree
				WebElement product1 = d.findElement(By.id("j2_1"));
				WebElement product2 = d.findElement(By.id("j2_2"));
				product1.click();
				product2.click();
				products = product1.getText() + ";" + product2.getText() + ";";

				// Select social network and type a name twice (add two
				// accounts)
				WebElement socialNetworkBox = d.findElement(By.id("new_name"));
				WebElement userNameBox = d.findElement(By.id("new_URI"));
				WebElement button = d.findElement(By.className("glyphicon"));
				userNameBox.sendKeys("First User");
				Select socialNetworkList = new Select(socialNetworkBox);
				socialNetworkList.selectByIndex(0);
				accounts += socialNetworkList.getFirstSelectedOption().getAttribute("value") + " / "
						+ userNameBox.getAttribute("value") + ";";
				button.click();
				userNameBox.sendKeys("Second User");
				socialNetworkList.selectByIndex(1);
				accounts += socialNetworkList.getFirstSelectedOption().getAttribute("value") + " / "
						+ userNameBox.getAttribute("value") + ";";
				button.click();

				// Select the checkboxes added by the account definition
				boolean tmp = false; // to ignore the first checkbox (final
										// product)
				String checkboxes = "//*[@type='checkbox']";
				List<WebElement> elementToClick = d.findElements(By.xpath(checkboxes));
				for (WebElement AllCheck : elementToClick) {
					if (tmp) {
						AllCheck.click();
					} else {
						tmp = true;
					}
				}

				// Set the update frequency to 13 days
				WebElement frequencyBox = d.findElement(By.id("frequency"));
				frequencyBox.clear();
				frequencyBox.sendKeys("3");
				frequency = Integer.parseInt(frequencyBox.getAttribute("value"));

				// Set a start date
				WebElement dateCheck = d.findElement(By.id("start_date"));
				WebElement dateBox = d.findElement(By.id("date_input"));
				dateBox.sendKeys("12152017");
				date = dateBox.getAttribute("value");
				// Submit the form
				d.findElement(By.id("submit")).click();
				(new WebDriverWait(d, 10)).until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver d) {
						Alert alert = d.switchTo().alert();
						text = alert.getText();
						alert.accept();
						return true;
					}
				});

				if (text.contains("Successfully added model")) {
					try {
						w.write("Model " + modelName
								+ " created. The test will now attempt to edit the model to check if the data is correct.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				} else {
					return false;
				}
			}
		});

		if (driver.getCurrentUrl().contains("index.html")) {

		} else {
			w.write("Page was not redirected to index.html after creating model. Stopping test run. \nTest Create Opinion Model failed.\n");
			pass = false;
			return false;
		}
		driver.findElement(By.linkText("Edit Opinion Model")).click();
		Select modelsList = new Select(driver.findElement(By.id("Models")));
		WebElement el = null;
		boolean modelExists = false;
		for (WebElement model : modelsList.getOptions()) {
			if (model.getText().equals(modelName)) {
				el = model;
				break;
			}
		}

		if (el == null) {
			w.write("Model " + modelName
					+ " was not added or was added with an incorrect name. Stopping test run. \nTest Create Opinion Model failed.\n");
			pass = false;
			return false;
		}

		w.write("Attempting to open edit page...\n");
		modelsList.selectByIndex(modelsList.getOptions().indexOf(el));
		driver.findElement(By.id("view_edit")).click();
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.common.base.Function#apply(java.lang.Object)
			 */
			public Boolean apply(WebDriver d) {
				if (!d.getCurrentUrl().contains("models.html")) {
					try {
						w.write("Page was not redirected after clicking Edit Model. Stopping test run\n");

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pass = false;
					return false;
				} else {
					try {
						w.write("Edit page opened successfully. Now checking if data matches...\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// Get all elements on the edit page
				WebElement pssBox = d.findElement(By.id("pss"));
				Select pssList = new Select(pssBox);
				String pssEdit = pssList.getFirstSelectedOption().getText();
				WebElement modelNameBox = d.findElement(By.id("model_name"));
				List<WebElement> productsList = d.findElements(By.className("jstree-clicked"));
				String selectedProducts = "";

				for (WebElement product : productsList) {
					selectedProducts += product.getText() + ";";
				}
				List<WebElement> userList = d.findElements(By.name("user"));
				String selectedUsers = "";
				for (WebElement user : userList) {
					selectedUsers += user.getText() + ";";
				}
				int freq = Integer.parseInt(d.findElement(By.id("frequency")).getAttribute("value"));

				// Check each element to see if it matches with the saved data
				if (!pss.equals(pssEdit)) {
					try {
						w.write("PSS does not match. Expected " + pss + ", got " + pssEdit + " instead.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pass = false;
				}

				if (!modelName.equals(modelNameBox.getAttribute("value"))) {
					try {
						w.write("Model name does not match. Expected " + modelName + ", got "
								+ modelNameBox.getAttribute("value") + " instead.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pass = false;
				}

				if (!products.equals(selectedProducts)) {
					try {
						w.write("Selected products list does not match. Expected " + products + ", got "
								+ selectedProducts + " instead.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pass = false;
				}
				if (!accounts.equals(selectedUsers)) {
					try {
						w.write("User list does not match. Expected " + accounts + ", got " + selectedUsers
								+ " instead.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pass = false;
				}
				if (frequency != freq) {
					try {
						w.write("Update frequency does not match. Expected " + frequency + ", got " + freq
								+ " instead.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pass = false;
				}

				d.findElement(By.id("submit2")).click();

				if (pass) {
					try {
						w.write("All fields match the input data.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return true;
			}
		});

		w.write("Test Create Opinion Model reached the end.\n");
		if (pass) {
			w.write("All steps were completed successfully. \n");
			return true;
		} else {
			w.write("Errors ocurred during the execution of this test. Please check this log for additional details.\n");
			return false;
		}

	}

	/**
	 * Test 2 - View Opinion Model 
	 * Steps: 
	 * 1. Click View Opinion Model 
	 * 2. Select Opinion Model from the dropdown list 
	 * 3. Click View Model 
	 * 4. Open Opinion Model Page in view mode 
	 * 5. Check if all fields are disabled 
	 * 6. Click Back to return home
	 * 
	 * @param driver - the selenium webdriver
	 * @return - true if the test passes, otherwise returns false
	 * @throws IOException - if an error occurs with the log.txt file
	 */
	public static boolean testView(WebDriver driver) throws IOException {
		w.write("Starting View Opinion Model Test\n\n");
		driver.findElement(By.linkText("View Opinion Model")).click();
		Select modelSelect = new Select(driver.findElement(By.id("Models")));
		modelSelect.selectByVisibleText(modelName);
		driver.findElement(By.id("view_select")).click();
		
		w.write("Attempting to open View Model page...\n");
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.common.base.Function#apply(java.lang.Object)
			 */
			public Boolean apply(WebDriver d){
				
				if (!d.getCurrentUrl().contains("models.html")) {
					try {
						w.write("Page was not redirected to model after clicking on view. Stopping test run.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pass = false;
					return false;
				}
				
				try {
					w.write("View Model page opened successfully. Checking if page is in View mode...\n");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if (!d.findElement(By.id("model_name")).getAttribute("readOnly").equals("true")) {
					pass = false;
					try {
						w.write("Model name text box was not disabled. Stopping test run.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				}
				
				if (!d.findElement(By.id("pss")).getAttribute("disabled").equals("true")) {
					pass = false;
					try {
						w.write("PSS dropdown was not disabled. Stopping test run.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				}
				
				if (!d.findElement(By.id("new_name")).getAttribute("disabled").equals("true")) {
					pass = false;
					try {
						w.write("User name text box was not disabled. Stopping test run.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				}
				
				if (!d.findElement(By.id("new_URI")).getAttribute("disabled").equals("true")) {
					pass = false;
					try {
						w.write("Social network dropdown was not disabled. Stopping test run.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				}
				
				if (!d.findElement(By.id("frequency")).getAttribute("readOnly").equals("true")) {
					pass = false;
					try {
						w.write("Update frequency was not disabled. Stopping test run.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				}
				
				String checkboxes = "//*[@type='checkbox']";
				List<WebElement> elementToClick = d.findElements(By.xpath(checkboxes));
				for (WebElement AllCheck : elementToClick) {
					if (!AllCheck.getAttribute("disabled").equals("true")) {
						pass = false;
						try {
							w.write("A checkbox was not disabled. Stopping test run.\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return false;
					}
				}
				try {
					w.write("All fields are disabled, page is in View mode.\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}});
		
		driver.findElement(By.id("submit2")).click();
		if (pass) {
			w.write("All steps were completed successfully.\n");
			return true;
		} else {
			w.write("Errors ocurred during the execution of this test. Please check this log for additional details.\n"); 
			return false;
		}
		
	}

	/**
	 * Test 3 - Edit Opinion Model 
	 * Steps:
	 * 1. Click Edit Opinion Model
	 * 2. Select the model created by the Create Opinion Model test
	 * 3. Click Edit Model
	 * 4. Change the Update frequency field
	 * 5. Submit
	 * 6. Open the Edit Model page again to check if the field matches the updated version
	 * 7. Click Back to return home
	 * 
	 * @param driver
	 * @return
	 * @throws IOException
	 */
	public static boolean testEdit(WebDriver driver) throws IOException {
		driver.findElement(By.linkText("Edit Opinion Model")).click();
		Select modelsList = new Select(driver.findElement(By.id("Models")));
		WebElement el = null;
		boolean modelExists = false;
		for (WebElement model : modelsList.getOptions()) {
			if (model.getText().equals(modelName)) {
				el = model;
				break;
			}
		}

		if (el == null) {
			w.write("Model " + modelName
					+ " was not found. Check this log for additional information. \n");
			pass = false;
			return false;
		}

		w.write("Attempting to open edit page...\n");
		modelsList.selectByIndex(modelsList.getOptions().indexOf(el));
		driver.findElement(By.id("view_edit")).click();
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.common.base.Function#apply(java.lang.Object)
			 */
			public Boolean apply(WebDriver d) {
				if (!d.getCurrentUrl().contains("models.html")) {
					try {
						w.write("Page was not redirected after clicking Edit Model. Stopping test run\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pass = false;
					return false;
				} else {
					try {
						w.write("Edit page opened successfully.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				try {
					w.write("Updating Frequency field (29 days)\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Set the frequency to 29
				d.findElement(By.id("frequency")).clear();
				d.findElement(By.id("frequency")).click();
				d.findElement(By.id("frequency")).sendKeys("29");
				d.findElement(By.id("submit")).click();
				
				(new WebDriverWait(d, 10)).until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver d) {
						Alert alert = d.switchTo().alert();
						text = alert.getText();
						alert.accept();
						return true;
					}
				});
				
				return true;
			}});
		
		if (!driver.getCurrentUrl().contains("index.html")) {
			w.write("Page was not redirected to index.html after submitting the update. Stopping test run.\n");
			pass = false;
			return false;
		}
		
		driver.findElement(By.linkText("Edit Opinion Model")).click();
		w.write("Attempting to open edit page...\n");
		Select modelsList2 = new Select(driver.findElement(By.id("Models")));
		WebElement el2 = null;
		for (WebElement model : modelsList2.getOptions()) {
			if (model.getText().equals(modelName)) {
				el2 = model;
				break;
			}
		}
		modelsList2.selectByIndex(modelsList2.getOptions().indexOf(el2));
		driver.findElement(By.id("view_edit")).click();
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.google.common.base.Function#apply(java.lang.Object)
			 */
			public Boolean apply(WebDriver d) {
				if (!d.getCurrentUrl().contains("models.html")) {
					try {
						w.write("Page was not redirected after clicking Edit Model (2). Stopping test run\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pass = false;
					return false;
				} else {
					try {
						w.write("Edit page opened successfully (2).\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (!d.findElement(By.id("frequency")).getAttribute("value").equals("29")) {
					pass = false;
					return false;
				} 
				
				d.findElement(By.id("submit2")).click();
				return true;
			}
		});
		
		if (pass) {
			w.write("All steps were completed successfully.\n");
			return true;
		} else {
			w.write("Errors ocurred during the execution of this test. Please check this log for additional details.\n"); 
			return false;
		}
	}

	public static boolean testDelete(WebDriver driver) {
		return false;
		// TODO Auto-generated method stub

	}

	public static boolean testExtraction(WebDriver driver) {
		return false;
		// TODO Auto-generated method stub

	}
	
	/**
	 * Deletes the model created by the tests
	 * @param driver
	 */
	private static void deleteModel(WebDriver driver) {
		driver.findElement(By.linkText("Delete Opinion Model")).click();
		Select modelsList2 = new Select(driver.findElement(By.id("Models")));
		WebElement el2 = null;
		for (WebElement model : modelsList2.getOptions()) {
			if (model.getText().equals(modelName)) {
				el2 = model;
				break;
			}
		}
		if (el2 != null) {

			modelsList2.selectByIndex(modelsList2.getOptions().indexOf(el2));
			driver.findElement(By.id("view_delete")).click();

			(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					Alert alert = d.switchTo().alert();
					alert.accept();
					return true;
				}
			});

			(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver d) {
					Alert alert = d.switchTo().alert();
					alert.accept();
					return true;
				}
			});
		}
	}
}