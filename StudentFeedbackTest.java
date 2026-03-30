package com.selenium.test;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class StudentFeedbackTest {

    // ── Driver setup ────────────────────────────────────────────────────────

    public static WebDriver initDriver() {
        // Use chromedriver from project folder (Jenkins-friendly)
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        return driver;
    }

    public static void openForm(WebDriver driver) throws InterruptedException {
        // Load HTML from current workspace
        String path = System.getProperty("user.dir") + "/index.html";
        driver.get("file:///" + path.replace("\\", "/"));
        Thread.sleep(2000);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    public static void fillValidForm(WebDriver driver) {
        driver.findElement(By.id("studentName")).clear();
        driver.findElement(By.id("studentName")).sendKeys("Arjun Sharma");

        driver.findElement(By.id("emailId")).clear();
        driver.findElement(By.id("emailId")).sendKeys("arjun.sharma@college.edu");

        driver.findElement(By.id("mobile")).clear();
        driver.findElement(By.id("mobile")).sendKeys("9876543210");

        Select dept = new Select(driver.findElement(By.id("department")));
        dept.selectByValue("CSE");

        driver.findElement(By.cssSelector("input[name='gender'][value='Male']")).click();

        driver.findElement(By.id("feedback")).clear();
        driver.findElement(By.id("feedback")).sendKeys(
                "The teaching quality this semester was excellent and the faculty were very helpful and supportive.");
    }

    public static void handleAlert(WebDriver driver) {
        try {
            Thread.sleep(1000);
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            // No alert → ignore
        }
    }

    public static boolean isDisplayed(WebDriver driver, String elementId) {
        try {
            WebElement el = driver.findElement(By.id(elementId));
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Test Cases ───────────────────────────────────────────────────────────

    public static void testFormOpens(WebDriver driver) throws InterruptedException {
        System.out.println("\n===== TC-01: Form Opens =====");

        boolean titleOk = driver.getTitle().contains("Student Feedback");
        System.out.println("Page Title Check       : " + (titleOk ? "✅ PASS" : "❌ FAIL") +
                " → " + driver.getTitle());

        boolean formVisible = driver.findElement(By.id("feedbackForm")).isDisplayed();
        System.out.println("Form Element Visible   : " + (formVisible ? "✅ PASS" : "❌ FAIL"));

        String[] fields = {"studentName", "emailId", "mobile", "department", "feedback"};
        for (String fieldId : fields) {
            boolean found = driver.findElement(By.id(fieldId)) != null;
            System.out.println("Field #" + fieldId + " Present : " + (found ? "✅ PASS" : "❌ FAIL"));
        }
    }

    public static void testValidSubmission(WebDriver driver) throws InterruptedException {
        System.out.println("\n===== TC-02: Valid Submission =====");

        fillValidForm(driver);
        driver.findElement(By.id("submitBtn")).click();
        Thread.sleep(500);
        handleAlert(driver);

        boolean toastVisible = isDisplayed(driver, "successToast");
        System.out.println("Success Toast Visible  : " + (toastVisible ? "✅ PASS" : "❌ FAIL"));

        if (toastVisible) {
            String toastText = driver.findElement(By.id("successToast")).getText().toLowerCase();
            boolean toastTextOk = toastText.contains("submitted successfully");
            System.out.println("Toast Text Check       : " + (toastTextOk ? "✅ PASS" : "❌ FAIL") +
                    " → \"" + toastText + "\"");
        }

        Thread.sleep(1000);
    }

    public static void testBlankFieldErrors(WebDriver driver) throws InterruptedException {
        System.out.println("\n===== TC-03: Blank Field Errors =====");

        driver.findElement(By.id("submitBtn")).click();
        Thread.sleep(300);
        handleAlert(driver);

        String[] errorIds = {"nameErr", "emailErr", "mobileErr", "genderErr", "deptErr", "feedbackErr"};
        String[] labels   = {"Name Error", "Email Error", "Mobile Error", "Gender Error", "Dept Error", "Feedback Error"};

        for (int i = 0; i < errorIds.length; i++) {
            boolean errVisible = isDisplayed(driver, errorIds[i]);
            System.out.println(labels[i] + " Shown  : " + (errVisible ? "✅ PASS" : "❌ FAIL"));
        }

        Thread.sleep(500);
    }

    public static void testEmailValidation(WebDriver driver) throws InterruptedException {
        System.out.println("\n===== TC-04: Email Validation =====");

        String[] badEmails = {
            "notanemail",
            "missing@domain",
            "@nodomain.com",
            "spaces in@email.com",
            "double@@at.com"
        };

        for (String badEmail : badEmails) {
            driver.findElement(By.id("emailId")).clear();
            driver.findElement(By.id("emailId")).sendKeys(badEmail);
            driver.findElement(By.id("emailId")).sendKeys(Keys.TAB);
            Thread.sleep(200);

            boolean errVisible = isDisplayed(driver, "emailErr");
            System.out.println("Invalid Email \"" + badEmail + "\" → Error Shown: " +
                    (errVisible ? "✅ PASS" : "❌ FAIL"));
        }

        driver.findElement(By.id("emailId")).clear();
        driver.findElement(By.id("emailId")).sendKeys("valid.user@university.ac.in");
        driver.findElement(By.id("emailId")).sendKeys(Keys.TAB);
        Thread.sleep(200);

        boolean errHidden = !isDisplayed(driver, "emailErr");
        System.out.println("Valid Email - Error Hidden  : " + (errHidden ? "✅ PASS" : "❌ FAIL"));
    }

    public static void testMobileValidation(WebDriver driver) throws InterruptedException {
        System.out.println("\n===== TC-05: Mobile Validation =====");

        String[] badMobiles = {
            "12345",
            "abcdefghij",
            "123456789",
            "12345678901",
            "98765-43210"
        };

        for (String badMobile : badMobiles) {
            driver.findElement(By.id("mobile")).clear();
            driver.findElement(By.id("mobile")).sendKeys(badMobile);
            driver.findElement(By.id("mobile")).sendKeys(Keys.TAB);
            Thread.sleep(200);

            boolean errVisible = isDisplayed(driver, "mobileErr");
            System.out.println("Invalid Mobile \"" + badMobile + "\" → Error Shown: " +
                    (errVisible ? "✅ PASS" : "❌ FAIL"));
        }

        driver.findElement(By.id("mobile")).clear();
        driver.findElement(By.id("mobile")).sendKeys("9876543210");
        driver.findElement(By.id("mobile")).sendKeys(Keys.TAB);
        Thread.sleep(200);

        boolean errHidden = !isDisplayed(driver, "mobileErr");
        System.out.println("Valid Mobile - Error Hidden : " + (errHidden ? "✅ PASS" : "❌ FAIL"));
    }

    public static void testDropdownSelection(WebDriver driver) throws InterruptedException {
        System.out.println("\n===== TC-06: Dropdown Selection =====");

        Select sel = new Select(driver.findElement(By.id("department")));

        boolean defaultEmpty = sel.getFirstSelectedOption().getAttribute("value").equals("");
        System.out.println("Default Option Empty       : " + (defaultEmpty ? "✅ PASS" : "❌ FAIL"));

        String[][] deptOptions = {
            {"CSE", "Computer Science"},
            {"ECE", "Electronics"},
            {"ME",  "Mechanical"},
            {"IT",  "Information Technology"},
            {"DS",  "Data Science"}
        };

        for (String[] dept : deptOptions) {
            sel.selectByValue(dept[0]);
            Thread.sleep(200);
            String selectedVal  = sel.getFirstSelectedOption().getAttribute("value");
            String selectedText = sel.getFirstSelectedOption().getText();

            boolean valOk  = selectedVal.equals(dept[0]);
            boolean textOk = selectedText.contains(dept[1]);
            System.out.println("Dept " + dept[0] + " Selected  : " +
                    (valOk && textOk ? "✅ PASS" : "❌ FAIL") + " → " + selectedText);
        }
    }

    public static void testButtonBehavior(WebDriver driver) throws InterruptedException {
        System.out.println("\n===== TC-07: Button Behavior =====");

        WebElement submitBtn = driver.findElement(By.id("submitBtn"));
        boolean submitOk = submitBtn.isDisplayed() && submitBtn.isEnabled();
        System.out.println("Submit Button Visible+Enabled : " + (submitOk ? "✅ PASS" : "❌ FAIL"));

        driver.findElement(By.id("studentName")).sendKeys("Test Student");
        driver.findElement(By.id("resetBtn")).click();
        Thread.sleep(200);
        boolean nameCleared = driver.findElement(By.id("studentName")).getAttribute("value").equals("");
        System.out.println("Reset Clears Name Field       : " + (nameCleared ? "✅ PASS" : "❌ FAIL"));

        fillValidForm(driver);
        driver.findElement(By.id("resetBtn")).click();
        Thread.sleep(200);

        boolean allCleared =
                driver.findElement(By.id("studentName")).getAttribute("value").equals("") &&
                driver.findElement(By.id("emailId")).getAttribute("value").equals("") &&
                driver.findElement(By.id("mobile")).getAttribute("value").equals("") &&
                driver.findElement(By.id("feedback")).getAttribute("value").equals("");
        System.out.println("Reset Clears All Fields       : " + (allCleared ? "✅ PASS" : "❌ FAIL"));

        Select sel = new Select(driver.findElement(By.id("department")));
        boolean deptReset = sel.getFirstSelectedOption().getAttribute("value").equals("");
        System.out.println("Reset Clears Department       : " + (deptReset ? "✅ PASS" : "❌ FAIL"));

        driver.findElement(By.id("submitBtn")).click();
        Thread.sleep(300);
        handleAlert(driver);
        driver.findElement(By.id("resetBtn")).click();
        Thread.sleep(200);

        String[] errIds = {"nameErr", "emailErr", "mobileErr", "genderErr", "deptErr", "feedbackErr"};
        boolean errorsCleared = true;
        for (String errId : errIds) {
            if (isDisplayed(driver, errId)) {
                errorsCleared = false;
            }
        }
        System.out.println("Reset Clears All Errors       : " + (errorsCleared ? "✅ PASS" : "❌ FAIL"));
    }

    // ── Entry Point ──────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = initDriver();
        openForm(driver);

        testFormOpens(driver);

        driver.navigate().refresh();
        Thread.sleep(1000);
        testValidSubmission(driver);

        driver.navigate().refresh();
        Thread.sleep(1000);
        testBlankFieldErrors(driver);

        driver.navigate().refresh();
        Thread.sleep(1000);
        testEmailValidation(driver);

        driver.navigate().refresh();
        Thread.sleep(1000);
        testMobileValidation(driver);

        driver.navigate().refresh();
        Thread.sleep(1000);
        testDropdownSelection(driver);

        driver.navigate().refresh();
        Thread.sleep(1000);
        testButtonBehavior(driver);

        System.out.println("\n===== All Test Cases Completed ✅ =====");
        Thread.sleep(2000);
        driver.quit();
    }
}