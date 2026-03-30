# Student Feedback Registration Form — Project Documentation

## Project Overview

This project implements a complete **Student Feedback Registration Form** system spanning five sub-tasks:
HTML structure → CSS styling → JavaScript validation → Selenium automated testing → Jenkins CI/CD.

---

## File Structure

```
student_feedback_project/
├── index.html              ← Sub Tasks 1, 2 (HTML + internal CSS), 3 (JS validation)
├── style.css               ← Sub Task 2 (external CSS)
├── test_feedback_form.py   ← Sub Task 4 (Selenium test suite)
├── Jenkinsfile             ← Sub Task 5 (Jenkins pipeline)
└── README.md               ← This file
```

---

## Sub Task 1 — HTML Form Structure

**File:** `index.html`

The form contains all required fields:

| Field | Element Type | ID |
|---|---|---|
| Student Name | `<input type="text">` | `studentName` |
| Email ID | `<input type="email">` | `emailId` |
| Mobile Number | `<input type="tel">` | `mobile` |
| Department | `<select>` | `department` |
| Gender | `<input type="radio">` | name=`gender` |
| Feedback Comments | `<textarea>` | `feedback` |
| Submit | `<button type="submit">` | `submitBtn` |
| Reset | `<button type="button">` | `resetBtn` |

The form uses semantic HTML5 elements, `aria-describedby` for accessibility, and `novalidate` to let JavaScript handle validation.

---

## Sub Task 2 — CSS Styling

### External CSS (`style.css`)
- **Aesthetic:** Academic editorial with ink-and-paper warmth
- **Typography:** `Playfair Display` (headings) + `Source Sans 3` (body), imported from Google Fonts
- **Color palette:** Deep navy (`#1a1a2e`), warm paper (`#fdf8f0`), crimson accent (`#c0392b`), antique gold (`#d4a017`)
- **Features:** CSS custom properties (variables), card layout with shadow, animated background texture (lined-paper via `repeating-linear-gradient`), responsive grid, entry animations (`@keyframes cardIn`, `fadeUp`, `shake`), smooth transitions

### Internal CSS (inside `<style>` in `index.html`)
- Progress fill strip at the top of the card
- Word-count hint row beneath the textarea
- Section headings with Playfair Display and crimson underline
- Tick (✓) checkmark positioned inside input wrappers for valid fields

---

## Sub Task 3 — JavaScript Validation

**Location:** `<script>` block at the bottom of `index.html`

### Validation Rules

| Field | Rule |
|---|---|
| Student Name | Must not be empty (`.trim().length > 0`) |
| Email ID | Must match `/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/` |
| Mobile Number | Must match `/^\d{10}$/` (exactly 10 digits) |
| Department | `<select>` value must not be `""` |
| Gender | At least one `input[name="gender"]` radio must be checked |
| Feedback Comments | `countWords()` must return ≥ 10 |

### Features
- **Live validation** on `blur` and `input` events — instant feedback as the user types
- **Visual states:** `.valid` (green border) / `.invalid` (red border + shake animation)
- **Progress strip** fills proportionally as fields pass validation
- **Live word counter** updates on every keystroke
- **Success toast** appears on valid submission
- **Reset** clears all fields, states, errors, and counter

---

## Sub Task 4 — Selenium Test Suite

**File:** `test_feedback_form.py`

### Prerequisites

```bash
pip install selenium pytest pytest-html webdriver-manager
```

`webdriver-manager` auto-downloads the matching ChromeDriver — no manual setup needed.

### Running Tests

```bash
# Basic run
python -m pytest test_feedback_form.py -v

# With HTML report
python -m pytest test_feedback_form.py -v --html=report.html --self-contained-html
```

### Test Classes & Cases

| Class | TC | Description |
|---|---|---|
| `TestFormOpens` | TC-01 | Page loads, title correct, all fields present |
| `TestValidSubmission` | TC-02 | Fill valid data → success toast appears |
| `TestBlankFieldErrors` | TC-03 | Submit empty form → each error message visible |
| `TestEmailValidation` | TC-04 | 5 invalid email formats + 1 valid (parametrized) |
| `TestMobileValidation` | TC-05 | 5 invalid mobiles + 1 valid (parametrized) |
| `TestDropdownSelection` | TC-06 | Default empty, select 5 departments |
| `TestButtonBehavior` | TC-07 | Submit/Reset visibility, Reset clears fields & errors |

Total test cases: **~28 individual assertions**

### Chrome Headless Mode

The driver runs in `--headless` mode (no GUI window). Remove that argument to watch the browser during debugging.

---

## Sub Task 5 — Jenkins Setup & Pipeline

**File:** `Jenkinsfile`

### Step-by-step Jenkins Setup

#### 1. Install Jenkins
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk -y
wget -q -O - https://pkg.jenkins.io/debian/jenkins.io.key | sudo apt-key add -
sudo sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
sudo apt update && sudo apt install jenkins -y
sudo systemctl start jenkins
sudo systemctl enable jenkins
# Access at http://localhost:8080
```

#### 2. Install Required Jenkins Plugins
Go to **Manage Jenkins → Plugins → Available** and install:
- Git Plugin
- Pipeline
- HTML Publisher Plugin
- JUnit Plugin

#### 3. Create a Pipeline Job
1. Dashboard → **New Item** → name it `StudentFeedbackTests` → choose **Pipeline** → OK
2. Under **Pipeline**, set **Definition** to `Pipeline script from SCM`
3. Set **SCM** to `Git`, enter your repository URL (or use `Pipeline script` and paste the Jenkinsfile content directly)
4. Set **Script Path** to `Jenkinsfile`
5. Click **Save**

#### 4. Run the Job
- Click **Build Now**
- Monitor progress in **Console Output**
- After completion, view:
  - **Test Results** (JUnit XML) in the build's test report page
  - **report.html** under **Build Artifacts**

#### 5. Interpret Results
| Status | Meaning |
|---|---|
| 🟢 SUCCESS | All Selenium tests passed |
| 🔴 FAILURE | Build/environment error |
| 🟡 UNSTABLE | Some tests failed (JUnit threshold) |

### Pipeline Stages

```
Environment Info → Checkout → Setup Python Env → Prepare Dirs → Run Selenium Tests
```

Post-build: JUnit results published, HTML report archived.

---

## Quick Start (Local)

```bash
# 1. Open the form in a browser
open index.html          # macOS
xdg-open index.html      # Linux
start index.html         # Windows

# 2. Run tests
pip install selenium pytest webdriver-manager
python -m pytest test_feedback_form.py -v
```

---

## Validation Summary Table

| Validation | Method | Pass Condition |
|---|---|---|
| Name not empty | JS `.trim().length > 0` | Non-blank string |
| Email format | Regex `/^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/` | Valid email pattern |
| Mobile digits | Regex `/^\d{10}$/` | Exactly 10 numeric digits |
| Gender selected | `querySelector(':checked')` | At least one radio selected |
| Department selected | `select.value !== ""` | Non-empty option chosen |
| Feedback length | `countWords() >= 10` | 10 or more words entered |
