"""
Sub Task 4 – Selenium Test Suite
File  : test_feedback_form.py
Run   : python -m pytest test_feedback_form.py -v
Deps  : pip install selenium pytest webdriver-manager
"""

import time
import os
import pytest
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import Select, WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager


# ── Helpers ────────────────────────────────────────────────────────────────

def get_form_url():
    """Return absolute file:// URL for index.html."""
    here = os.path.dirname(os.path.abspath(__file__))
    html = os.path.join(here, "index.html")
    return "file:///" + html.replace("\\", "/")


def make_driver():
    """Create a headless Chrome WebDriver."""
    options = webdriver.ChromeOptions()
    options.add_argument("--headless")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--window-size=1280,900")
    service = Service(ChromeDriverManager().install())
    return webdriver.Chrome(service=service, options=options)


def fill_valid_form(driver):
    """Fill all fields with valid data (helper reused across tests)."""
    driver.find_element(By.ID, "studentName").clear()
    driver.find_element(By.ID, "studentName").send_keys("Arjun Sharma")

    driver.find_element(By.ID, "emailId").clear()
    driver.find_element(By.ID, "emailId").send_keys("arjun.sharma@college.edu")

    driver.find_element(By.ID, "mobile").clear()
    driver.find_element(By.ID, "mobile").send_keys("9876543210")

    Select(driver.find_element(By.ID, "department")).select_by_value("CSE")

    driver.find_element(By.CSS_SELECTOR, "input[name='gender'][value='Male']").click()

    driver.find_element(By.ID, "feedback").clear()
    driver.find_element(By.ID, "feedback").send_keys(
        "The teaching quality this semester was excellent and the faculty were very helpful and supportive."
    )


# ── Fixtures ────────────────────────────────────────────────────────────────

@pytest.fixture(scope="function")
def driver():
    """Per-test Chrome driver; navigates to the form page."""
    d = make_driver()
    d.get(get_form_url())
    wait = WebDriverWait(d, 10)
    wait.until(EC.presence_of_element_located((By.ID, "feedbackForm")))
    yield d
    d.quit()


# ── Test Cases ──────────────────────────────────────────────────────────────

class TestFormOpens:
    """TC-01: Verify the form page loads successfully."""

    def test_page_title(self, driver):
        assert "Student Feedback" in driver.title, \
            f"Expected 'Student Feedback' in title but got: {driver.title}"

    def test_form_element_present(self, driver):
        form = driver.find_element(By.ID, "feedbackForm")
        assert form.is_displayed(), "The feedback form element should be visible."

    def test_required_fields_present(self, driver):
        """All six required input elements must exist."""
        ids = ["studentName", "emailId", "mobile", "department", "feedback"]
        for field_id in ids:
            el = driver.find_element(By.ID, field_id)
            assert el is not None, f"Field #{field_id} not found on page."


class TestValidSubmission:
    """TC-02: Enter valid data and verify successful submission."""

    def test_valid_form_shows_toast(self, driver):
        fill_valid_form(driver)
        driver.find_element(By.ID, "submitBtn").click()
        time.sleep(0.5)

        toast = driver.find_element(By.ID, "successToast")
        assert toast.is_displayed(), "Success toast should appear after valid submission."

    def test_toast_text(self, driver):
        fill_valid_form(driver)
        driver.find_element(By.ID, "submitBtn").click()
        time.sleep(0.5)

        toast_text = driver.find_element(By.ID, "successToast").text
        assert "submitted successfully" in toast_text.lower(), \
            f"Unexpected toast text: {toast_text}"


class TestBlankFieldErrors:
    """TC-03: Leave mandatory fields blank and check error messages."""

    def test_empty_name_shows_error(self, driver):
        # Submit without touching name field
        driver.find_element(By.ID, "submitBtn").click()
        time.sleep(0.3)

        err = driver.find_element(By.ID, "nameErr")
        assert err.is_displayed(), "Name error should be visible when field is empty."

    def test_empty_email_shows_error(self, driver):
        driver.find_element(By.ID, "submitBtn").click()
        time.sleep(0.3)

        err = driver.find_element(By.ID, "emailErr")
        assert err.is_displayed(), "Email error should be visible when field is empty."

    def test_empty_mobile_shows_error(self, driver):
        driver.find_element(By.ID, "submitBtn").click()
        time.sleep(0.3)

        err = driver.find_element(By.ID, "mobileErr")
        assert err.is_displayed(), "Mobile error should be visible when field is empty."

    def test_no_gender_shows_error(self, driver):
        driver.find_element(By.ID, "submitBtn").click()
        time.sleep(0.3)

        err = driver.find_element(By.ID, "genderErr")
        assert err.is_displayed(), "Gender error should be visible when none selected."

    def test_no_department_shows_error(self, driver):
        driver.find_element(By.ID, "submitBtn").click()
        time.sleep(0.3)

        err = driver.find_element(By.ID, "deptErr")
        assert err.is_displayed(), "Department error should be visible when not selected."

    def test_empty_feedback_shows_error(self, driver):
        driver.find_element(By.ID, "submitBtn").click()
        time.sleep(0.3)

        err = driver.find_element(By.ID, "feedbackErr")
        assert err.is_displayed(), "Feedback error should be visible when field is empty."


class TestEmailValidation:
    """TC-04: Enter invalid email formats and verify validation error."""

    @pytest.mark.parametrize("bad_email", [
        "notanemail",
        "missing@domain",
        "@nodomain.com",
        "spaces in@email.com",
        "double@@at.com",
    ])
    def test_invalid_email_shows_error(self, driver, bad_email):
        field = driver.find_element(By.ID, "emailId")
        field.send_keys(bad_email)
        field.send_keys(Keys.TAB)  # trigger blur
        time.sleep(0.2)

        err = driver.find_element(By.ID, "emailErr")
        assert err.is_displayed(), \
            f"Email error not shown for invalid input: '{bad_email}'"

    def test_valid_email_hides_error(self, driver):
        field = driver.find_element(By.ID, "emailId")
        field.send_keys("valid.user@university.ac.in")
        field.send_keys(Keys.TAB)
        time.sleep(0.2)

        err = driver.find_element(By.ID, "emailErr")
        assert not err.is_displayed(), "Email error should NOT appear for a valid email."


class TestMobileValidation:
    """TC-05: Enter invalid mobile numbers and verify validation."""

    @pytest.mark.parametrize("bad_mobile", [
        "12345",           # too short
        "abcdefghij",      # letters
        "123456789",       # 9 digits
        "12345678901",     # 11 digits
        "98765-43210",     # hyphen
    ])
    def test_invalid_mobile_shows_error(self, driver, bad_mobile):
        field = driver.find_element(By.ID, "mobile")
        field.send_keys(bad_mobile)
        field.send_keys(Keys.TAB)
        time.sleep(0.2)

        err = driver.find_element(By.ID, "mobileErr")
        assert err.is_displayed(), \
            f"Mobile error not shown for invalid input: '{bad_mobile}'"

    def test_valid_mobile_hides_error(self, driver):
        field = driver.find_element(By.ID, "mobile")
        field.send_keys("9876543210")
        field.send_keys(Keys.TAB)
        time.sleep(0.2)

        err = driver.find_element(By.ID, "mobileErr")
        assert not err.is_displayed(), "Mobile error should NOT appear for a valid number."


class TestDropdownSelection:
    """TC-06: Verify dropdown (department) selection works correctly."""

    def test_default_option_is_empty(self, driver):
        sel = Select(driver.find_element(By.ID, "department"))
        assert sel.first_selected_option.get_attribute("value") == "", \
            "Default department selection should be empty."

    @pytest.mark.parametrize("dept_value,dept_text", [
        ("CSE", "Computer Science"),
        ("ECE", "Electronics"),
        ("ME",  "Mechanical"),
        ("IT",  "Information Technology"),
        ("DS",  "Data Science"),
    ])
    def test_can_select_department(self, driver, dept_value, dept_text):
        sel = Select(driver.find_element(By.ID, "department"))
        sel.select_by_value(dept_value)
        selected = sel.first_selected_option
        assert selected.get_attribute("value") == dept_value, \
            f"Expected department '{dept_value}' to be selected."
        assert dept_text in selected.text, \
            f"Selected option text should contain '{dept_text}'."


class TestButtonBehavior:
    """TC-07: Verify Submit and Reset button behaviors."""

    def test_submit_button_exists_and_clickable(self, driver):
        btn = driver.find_element(By.ID, "submitBtn")
        assert btn.is_displayed() and btn.is_enabled(), \
            "Submit button should be visible and enabled."

    def test_reset_button_clears_name(self, driver):
        name_field = driver.find_element(By.ID, "studentName")
        name_field.send_keys("Test Student")
        assert name_field.get_attribute("value") == "Test Student"

        driver.find_element(By.ID, "resetBtn").click()
        time.sleep(0.2)
        assert name_field.get_attribute("value") == "", \
            "Name field should be cleared after Reset."

    def test_reset_clears_all_fields(self, driver):
        fill_valid_form(driver)
        driver.find_element(By.ID, "resetBtn").click()
        time.sleep(0.2)

        assert driver.find_element(By.ID, "studentName").get_attribute("value") == ""
        assert driver.find_element(By.ID, "emailId").get_attribute("value") == ""
        assert driver.find_element(By.ID, "mobile").get_attribute("value") == ""
        assert driver.find_element(By.ID, "feedback").get_attribute("value") == ""

        sel = Select(driver.find_element(By.ID, "department"))
        assert sel.first_selected_option.get_attribute("value") == "", \
            "Department should reset to empty."

    def test_reset_clears_error_messages(self, driver):
        # Trigger errors first
        driver.find_element(By.ID, "submitBtn").click()
        time.sleep(0.3)

        driver.find_element(By.ID, "resetBtn").click()
        time.sleep(0.2)

        # After reset, all error messages should be hidden
        for err_id in ["nameErr", "emailErr", "mobileErr", "genderErr", "deptErr", "feedbackErr"]:
            err = driver.find_element(By.ID, err_id)
            assert not err.is_displayed(), \
                f"Error message #{err_id} should be hidden after Reset."


# ── Entry point ──────────────────────────────────────────────────────────────

if __name__ == "__main__":
    pytest.main([__file__, "-v", "--tb=short"])
