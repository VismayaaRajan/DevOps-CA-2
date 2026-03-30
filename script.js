/* ============================================================
   script.js  –  External JavaScript for Student Feedback Form
   Sub Task 3: Form Validation
   ============================================================ */

/* ── Utility helpers ── */
const $ = id => document.getElementById(id);

function setValid(el, errEl) {
  el.classList.remove('invalid');
  el.classList.add('valid');
  errEl.classList.remove('visible');
}

function setInvalid(el, errEl) {
  el.classList.remove('valid');
  el.classList.add('invalid');
  errEl.classList.add('visible');
}

function clearState(el, errEl) {
  el.classList.remove('valid', 'invalid');
  if (errEl) errEl.classList.remove('visible');
}

/* ── Count words in a string ── */
function countWords(str) {
  return str.trim().split(/\s+/).filter(Boolean).length;
}

/* ── Live word counter for textarea ── */
const feedbackTA  = $('feedback');
const wordCountEl = $('wordCount');

feedbackTA.addEventListener('input', () => {
  const wc = countWords(feedbackTA.value);
  wordCountEl.textContent = wc;
  wordCountEl.classList.toggle('ok', wc >= 10);
});

/* ── Progress strip: fraction of valid fields ── */
function updateProgress() {
  const fields = ['studentName', 'emailId', 'mobile', 'department', 'feedback'];
  let filled = 0;
  fields.forEach(id => {
    const el = $(id);
    if (el && el.classList.contains('valid')) filled++;
  });
  // also count gender
  if (document.querySelector('input[name="gender"]:checked')) filled++;
  const pct = Math.round((filled / 6) * 100);
  $('progressStrip').style.width = pct + '%';
}

/* ── Individual field validators ── */

// 1. Student Name – must not be empty
function validateName() {
  const el = $('studentName'), err = $('nameErr');
  const ok = el.value.trim().length > 0;
  ok ? setValid(el, err) : setInvalid(el, err);
  updateProgress();
  return ok;
}

// 2. Email – must match standard email pattern
function validateEmail() {
  const el = $('emailId'), err = $('emailErr');
  const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
  const ok = pattern.test(el.value.trim());
  ok ? setValid(el, err) : setInvalid(el, err);
  updateProgress();
  return ok;
}

// 3. Mobile – exactly 10 digits
function validateMobile() {
  const el = $('mobile'), err = $('mobileErr');
  const pattern = /^\d{10}$/;
  const ok = pattern.test(el.value.trim());
  ok ? setValid(el, err) : setInvalid(el, err);
  updateProgress();
  return ok;
}

// 4. Department – must not be empty
function validateDept() {
  const el = $('department'), err = $('deptErr');
  const ok = el.value !== '';
  ok ? setValid(el, err) : setInvalid(el, err);
  updateProgress();
  return ok;
}

// 5. Gender – at least one radio selected
function validateGender() {
  const err = $('genderErr');
  const selected = document.querySelector('input[name="gender"]:checked');
  if (selected) {
    err.classList.remove('visible');
  } else {
    err.classList.add('visible');
  }
  updateProgress();
  return !!selected;
}

// 6. Feedback – not blank and >= 10 words
function validateFeedback() {
  const el = $('feedback'), err = $('feedbackErr');
  const ok = countWords(el.value) >= 10;
  ok ? setValid(el, err) : setInvalid(el, err);
  updateProgress();
  return ok;
}

/* ── Attach live-validation listeners ── */
$('studentName').addEventListener('blur',  validateName);
$('studentName').addEventListener('input', validateName);

$('emailId').addEventListener('blur',  validateEmail);
$('emailId').addEventListener('input', validateEmail);

$('mobile').addEventListener('blur',  validateMobile);
$('mobile').addEventListener('input', validateMobile);

$('department').addEventListener('change', validateDept);

document.querySelectorAll('input[name="gender"]').forEach(r =>
  r.addEventListener('change', validateGender)
);

$('feedback').addEventListener('blur',  validateFeedback);
$('feedback').addEventListener('input', validateFeedback);

/* ── Form Submit ── */
$('feedbackForm').addEventListener('submit', function(e) {
  e.preventDefault();

  // Run all validators; gather results
  const results = [
    validateName(),
    validateEmail(),
    validateMobile(),
    validateDept(),
    validateGender(),
    validateFeedback()
  ];

  const allValid = results.every(Boolean);

  if (allValid) {
    // Show success toast
    const toast = $('successToast');
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 3500);

    // Reset after a delay
    setTimeout(() => {
      $('feedbackForm').reset();
      ['studentName','emailId','mobile','department','feedback'].forEach(id => {
        clearState($(id), null);
      });
      wordCountEl.textContent = '0';
      wordCountEl.classList.remove('ok');
      $('progressStrip').style.width = '0%';
    }, 3600);
  } else {
    // Scroll to first invalid field
    const firstInvalid = document.querySelector('.invalid');
    if (firstInvalid) firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
});

/* ── Reset Button ── */
$('resetBtn').addEventListener('click', () => {
  $('feedbackForm').reset();
  ['studentName','emailId','mobile','department','feedback'].forEach(id => {
    clearState($(id), null);
  });
  document.querySelectorAll('.error-msg').forEach(e => e.classList.remove('visible'));
  wordCountEl.textContent = '0';
  wordCountEl.classList.remove('ok');
  $('progressStrip').style.width = '0%';
});
