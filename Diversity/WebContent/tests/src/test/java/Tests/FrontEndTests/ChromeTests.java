package Tests.FrontEndTests;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ChromeTests  {
	
	static final int NUM_TESTS = 5; // UPDATE THIS NUMBER WHEN MORE TESTS ARE CREATED
	
    static String pss = "";
    static String modelName  = "";
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
		w.write("Starting test run - Chrome Tests - " + new Date() +  "\n\n");
		
		long start = System.nanoTime();
		
		// Uses chromedriver to run tests on Google Chrome.
		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
		WebDriver driver = new ChromeDriver();

		// Set the website URL and permissions

		driver.get("http://localhost:8080/Diversity/pages/index.html?role_desc=DEVELOPER");
		JavascriptExecutor JavascriptExecutor = ((JavascriptExecutor)driver);
		JavascriptExecutor.executeScript("document.cookie = \"JSESSIONID=3D43211234DDDFFGGT542; expires=Fri, 31 Dec 9999 23:59:59 GMT\";");
		driver.get("http://localhost:8080/Diversity/pages/index.html?role_desc=DEVELOPER");

		boolean create = testCreate(driver);
		boolean edit = testEdit(driver);
		boolean view = testView(driver);
		boolean extract = testExtraction(driver);
		boolean delete = testDelete(driver);
		
		long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

		w.write("-----------------------------------------------\n");
		w.write("Test run finished. Results: \n");
		w.write("Test 1 - Create Opinion Model: " + (create ? "passed\n" : "failed\n"));
		w.write("Test 2 - Edit Opinion Model: " + (edit ? "passed\n" : "failed\n"));
		w.write("Test 3 - View Opinion Model: " + (view ? "passed\n" : "failed\n"));
		w.write("Test 4 - View Opinion Extraction: " + (extract ? "passed\n" : "failed\n"));
		w.write("Test 5 - Delete Opinion Model: " + (delete ? "passed\n" : "failed\n"));
		
		int passed = 0;
		
		if (create) {
			passed++;
		}
		
		if (edit) {
			passed++;
		}
		
		if (view) {
			passed++;
		}
		
		if (extract) {
			passed++;
		}
		
		if (delete) {
			passed++;
		}
		
		w.write("\nTests passed: " + passed);
		w.write("\nTests failed: " + (NUM_TESTS - passed));
		w.write("\nElapsed time: " + elapsed + " milliseconds\n");
		
		w.close();
		driver.close();
	}

	/** Test 1 - Create new model
     * Steps: 
     * 1. Click Create Opinion Model
     * 2. Select PSS (this test always selects the 3rd option)
     * 3. Fill in the model name
     * 4. Select some final products
     * 5. Add social network and user name
     * 6. Define update frequency
     * 7. Define start date
     * 8. Submit
     * 9. Open edit page to check if data matches
     * 10. Delete the created test model
     *
     * @param driver - the selenium webdriver
     * @return - true if the test passes, otherwise returns false
     * @throws IOException - if an error occurs with the log.txt file
     */
    public static boolean testCreate(WebDriver driver) throws IOException {
    	w.write("Starting Create Opinion Model Test\n");
    	w.write("-----------------------------------\n\n");
    	w.write("Clicking: 'Create Opinion Model'...\n");
    	try {
    		driver.findElement(By.linkText("Create Opinion Model")).click();
    	} catch (NoSuchElementException e) {
    		w.write("ERROR: 'Create Opinion Model' link was not found.\n");
    		return false;
    	}
        
    	w.write("Waiting for page to redirect to 'models.html'...\n");
    	
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            /* (non-Javadoc)
             * @see com.google.common.base.Function#apply(java.lang.Object)
             */
            public Boolean apply(final WebDriver d) {
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
            	try {
					w.write("Page redirected to 'models.html'.\n");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            	//Find and set the PSS dropdown
            	
            	try {
	            	WebElement pssBox = d.findElement(By.id("pss"));
	            	Select pssList = new Select(pssBox);
	            	pssList.selectByIndex(2);
	            	pss = pssList.getFirstSelectedOption().getText();
            	} catch (NoSuchElementException e2) {
            		try {
						w.write("ERROR: Failed to select PSS from dropdown.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		return false;
            	}
            	
            	try {
            		//Generate a random 32 bit integer name for the model name to avoid duplicates
	            	WebElement modelNameBox = d.findElement(By.id("model_name"));
	            	modelName = (new BigInteger(32,new Random())).toString();
	            	modelNameBox.sendKeys(modelName);
            	} catch (NoSuchElementException e3) {
            		try {
						w.write("ERROR: Failed to type model name (text box not found).\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		return false;
            	}
            	//Select the first two options from the product selection tree
            	try {
            		d.findElement(By.id("final")).click();
	            	WebElement product1 = d.findElement(By.id("j2_1"));
	            	WebElement product2 = d.findElement(By.id("j2_2"));
	            	product1.click();
	            	product2.click();
	            	products = product1.getText() + ";" + product2.getText() + ";";
            	} catch (NoSuchElementException e) {
            		try {
						w.write("ERROR: Failed to select final product from tree view.\n");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		return false;
            	} catch (StaleElementReferenceException e) {
            		try {
						w.write("ERROR: Tree view is no longer accessible.\n");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		return false;
            	}
            	
               	// Select social network and type a name twice (add two accounts)
            	try {
	            	WebElement socialNetworkBox = d.findElement(By.id("new_name"));
	            	WebElement userNameBox = d.findElement(By.id("new_URI"));
	            	WebElement button = d.findElement(By.className("glyphicon"));
	            	userNameBox.sendKeys("First User");
	            	Select socialNetworkList = new Select(socialNetworkBox);
	            	socialNetworkList.selectByIndex(0);
	            	accounts += socialNetworkList.getFirstSelectedOption().getAttribute("value") + " / " + userNameBox.getAttribute("value") + ";";
	            	button.click();
	            	userNameBox.sendKeys("Second User");
	            	socialNetworkList.selectByIndex(1);
	            	accounts += socialNetworkList.getFirstSelectedOption().getAttribute("value") + " / " + userNameBox.getAttribute("value") + ";";
	            	button.click();
            	} catch (NoSuchElementException e) {
            		try {
						w.write("ERROR: Failed add account.\n");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		return false;
            	}
            	// Select the checkboxes added by the account definition
            	boolean tmp = false; // to ignore the first checkbox (final product)
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
            	try {
	            	WebElement frequencyBox = d.findElement(By.id("frequency"));
	            	frequencyBox.clear();
	            	frequencyBox.sendKeys("3");
	            	frequency = Integer.parseInt(frequencyBox.getAttribute("value"));
            	} catch (NoSuchElementException e) {
            		try {
						w.write("ERROR: Failed to find update frequency box.\n");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		return false;
            	}
            	
            	// Set a start date
            	try {
            	WebElement dateCheck = d.findElement(By.id("start_date"));
            	WebElement dateBox = d.findElement(By.id("date_input"));
            	dateBox.sendKeys("12152011");
            	date = dateBox.getAttribute("value");
            	} catch (NoSuchElementException e) {
            		try {
						w.write("ERROR: Failed to find start date box.\n");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		return false;
            	}
            	// Submit the form
        		d.findElement(By.id("submit")).click();
        		(new WebDriverWait(d, 10)).until(new ExpectedCondition<Boolean>() {

					public Boolean apply(WebDriver arg0) {
						// TODO Auto-generated method stub
						d.findElement(By.id("no")).click();;
						return true;
						
					}
        			
        		});
        		

        		try {
					w.write("Model " + modelName + " created.\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	  	return true;

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
            w.write("Model " + modelName + " was not added or was added with an incorrect name. Stopping test run. \nTest Create Opinion Model failed.\n");
        	pass = false;
            return false;
        }
        
        w.write("Attempting to open edit page...\n");
        modelsList.selectByIndex(modelsList.getOptions().indexOf(el));
        driver.findElement(By.id("view_edit")).click();
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            /* (non-Javadoc)
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
            	//Get all elements on the edit page
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
				
            	//Check each element to see if it matches with the saved data
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
						w.write("Model name does not match. Expected " + modelName + ", got " + modelNameBox.getAttribute("value") + " instead.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		pass = false;
            	}
            	
            	if (!products.equals(selectedProducts)) {
            		try {
						w.write("Selected products list does not match. Expected " + products + ", got " + selectedProducts + " instead.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		pass = false;
            	}
            	if (!accounts.equals(selectedUsers)) {
            		try {
						w.write("User list does not match. Expected " + accounts + ", got " + selectedUsers + " instead.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		pass = false;
            	}
            	if (frequency != freq) {
            		try {
						w.write("Update frequency does not match. Expected " + frequency + ", got " + freq + " instead.\n");
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
            }});
        
        
        w.write("Test Create Opinion Model reached the end.\n");
        if (pass) {
        	w.write("All steps were completed successfully. \n");
        	return true;
        } else {
        	w.write("Errors ocurred during the execution of this test. Please check this log for additional details.\n");
        	return false;
        }
        
    }
    
    private static boolean testExtraction(WebDriver driver) throws IOException {
    	w.write("Starting View Opinion Extraction test.\n");
    	w.write("-----------------------------------\n\n");
    	driver.findElement(By.id("model_box")).click();
    	
    	(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				if (!d.getCurrentUrl().contains("opinion_extraction")) {
					try {
						w.write("Failed to open Opinion Extraction Page.");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pass = false;
					return false;
				}
				try {
					w.write("Opinion Extraction page opened successfully.\nChecking if dropdowns disable correctly.\n");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				WebDriverWait wait = new WebDriverWait(d, 10);
				wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("overlay")));
				
				List<String> ids = new ArrayList<String>();
				
				ids.add("Gender");
				ids.add("Location");
				ids.add("Age_radio");
				ids.add("Final");
				
				for (String id : ids) {
					d.findElement(By.id(id)).click();
					if (id.equals("Age_radio")) {
						pass = Boolean.parseBoolean(d.findElement(By.id("agefilt")).getAttribute("disabled"));
					} else {
						pass = Boolean.parseBoolean(d.findElement(By.id(id.toLowerCase() + "filt")).getAttribute("disabled"));
					}

					if (!pass) {
						try {
							w.write(id + " dropdown was not disabled after selecting " + id + " filter. Stopping test run\n");
							return pass;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				if (pass) {
					try {
						w.write("All dropdowns behave as expected.\nStarting Top 5 table tests.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if (d instanceof JavascriptExecutor) {
					try {
						w.write("Selecting chart point...\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					((JavascriptExecutor) d).executeScript("bottom_right.setSelection([{column:1, row:3}]);google.visualization.events.trigger(bottom_right, 'select');");
				}
				
				WebElement table = d.findElement(By.id("posts"));
				
				List<WebElement> tableCells= table.findElements(By.xpath("//table/tbody/tr/td[count(//table/thead/tr/th[.=\"Date\"]/preceding-sibling::th)+1]"));
				
				for (WebElement t : tableCells) {
					
					//needs to be changed every month
					if (!t.getText().split("-")[1].equals("05")) { 
						try {
							w.write("Top 5 table did not update correctly after clicking chart. Stopping test run.\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						pass = false;
						return false;
					}
				}
				
				try {
					w.write("Top 5 table updated successfully.\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return true;
			}
		});
    	w.write("Test View Opinion Extraction reached the end.\nAll steps completed successfully.\n");
    	driver.findElement(By.id("home")).click();
		return pass;
		
	}

	private static boolean testDelete(WebDriver driver) throws IOException {
		w.write("Starting Delete Opinion Model Test\n");
    	w.write("-----------------------------------\n\n");
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
    				d.findElement(By.id("yes")).click();
            		return true;
    			}
    		});
    		
    		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
    			public Boolean apply(WebDriver d) {
    				d.findElement(By.id("ok")).click();
            		return true;
    			}
    		});
        } else {
        	w.write("Test Delete Opinion Model failed.");
        	return false;
        }
        w.write("Test Delete Opinion Model reached the end.\nAll steps completed successfully.\n");
		return true;
		
	}

	private static boolean testView(WebDriver driver) throws IOException {
		w.write("Starting View Opinion Model Test\n");
    	w.write("-----------------------------------\n\n");
		driver.findElement(By.linkText("View Opinion Model")).click();
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
            driver.findElement(By.id("view_select")).click();
        
    		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
    			public Boolean apply(WebDriver d) {
    				if (!d.getCurrentUrl().contains("models.html")) {
    					try {
							w.write("Page was not redirected successfully. Stopping test run.\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					pass = false;
    					return false;
    				}
    				
    				if (d.findElement(By.tagName("h1")).getText().contains("View")) {
    					try {
							w.write("View opinion model page was opened successfully.\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					pass = true;
    					return true;
    				}
    				
    				
    				return false;
    			}
    		});
        }
        
        driver.findElement(By.id("submit2")).click();
        
        w.write("Test View Opinion Model reached the end.\n");
        if (pass) {
        	w.write("All steps were completed successfully. \n");
        	return true;
        } else {
        	w.write("Errors ocurred during the execution of this test. Please check this log for additional details.\n");
        	return false;
        }
	}

	private static boolean testEdit(WebDriver driver) throws IOException {
		w.write("Starting Edit Opinion Model Test\n");
    	w.write("-----------------------------------\n\n");
		driver.findElement(By.linkText("Edit Opinion Model")).click();
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
            driver.findElement(By.id("view_edit")).click();
        
    		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
    			public Boolean apply(WebDriver d) {
    				if (!d.getCurrentUrl().contains("models.html")) {
    					try {
							w.write("Page was not redirected successfully. Stopping test run.\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					pass = false;
    					return false;
    				}
    				
    				if (d.findElement(By.tagName("h1")).getText().contains("Edit")) {
    					try {
							w.write("Edit opinion model page was opened successfully.\n");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    					pass = true;
    					return true;
    				}
    				return false;
    			}
    		});
        }
        
        driver.findElement(By.id("submit2")).click();
        
        w.write("Test Edit Opinion Model reached the end.\n");
        if (pass) {
        	w.write("All steps were completed successfully. \n");
        	return true;
        } else {
        	w.write("Errors ocurred during the execution of this test. Please check this log for additional details.\n");
        	return false;
        }
	}

	private static boolean testSetup(WebDriver driver) throws IOException {
		w.write("Starting Chart Setup Test\n");
    	w.write("-----------------------------------\n\n");
		driver.findElement(By.linkText("Chart Setup")).click();
		
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {

			public Boolean apply(WebDriver d) {

				if (!d.getCurrentUrl().contains("chart_setup.html")) {
					try {
						w.write("Page was not redirected successfully. Stopping test run.\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pass = false;
					return false;
				}
				return null;
			}
			
		});
		
		return pass;
		
	}
}

 
