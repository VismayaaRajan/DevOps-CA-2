// ─────────────────────────────────────────────────────────
// Jenkinsfile  –  Sub Task 5: Jenkins Pipeline
// Declarative Pipeline for running Selenium test suite
// ─────────────────────────────────────────────────────────

pipeline {

    // Run on any available agent (or specify 'label "selenium-node"')
    agent any

    // ── Pipeline-level environment variables ──────────────
    environment {
        PROJECT_DIR   = "${WORKSPACE}"
        PYTHON_BIN    = "python3"                          // or 'python' on Windows
        VENV_DIR      = "${WORKSPACE}/.venv"
        REPORT_DIR    = "${WORKSPACE}/reports"
        REPORT_HTML   = "${REPORT_DIR}/report.html"
        REPORT_XML    = "${REPORT_DIR}/junit.xml"
    }

    // ── Pipeline options ──────────────────────────────────
    options {
        timestamps()                          // prefix every log line with time
        timeout(time: 15, unit: 'MINUTES')    // abort if job hangs
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    // ── Stages ────────────────────────────────────────────
    stages {

        // 1. Print environment info
        stage('Environment Info') {
            steps {
                echo "=== Build #${BUILD_NUMBER} started on ${NODE_NAME} ==="
                sh "${PYTHON_BIN} --version"
                sh "google-chrome --version || chromium-browser --version || echo 'Chrome not found – install it on agent'"
            }
        }

        // 2. Checkout source (connects project folder or GitHub repo)
        stage('Checkout') {
            steps {
                echo 'Checking out project from SCM…'
                // If using GitHub, configure SCM in the job settings and
                // Jenkins will auto-checkout here.  For a local folder job,
                // this step is a no-op.
                checkout scm
            }
        }

        // 3. Set up Python virtual environment & install dependencies
        stage('Setup Python Environment') {
            steps {
                echo 'Creating virtual environment and installing dependencies…'
                sh """
                    ${PYTHON_BIN} -m venv ${VENV_DIR}
                    source ${VENV_DIR}/bin/activate
                    pip install --upgrade pip
                    pip install selenium pytest pytest-html webdriver-manager
                """
            }
        }

        // 4. Prepare report output directory
        stage('Prepare Directories') {
            steps {
                sh "mkdir -p ${REPORT_DIR}"
            }
        }

        // 5. Run Selenium test suite via pytest
        stage('Run Selenium Tests') {
            steps {
                echo 'Executing Selenium test cases…'
                sh """
                    source ${VENV_DIR}/bin/activate
                    cd ${PROJECT_DIR}
                    pytest test_feedback_form.py \
                        --html=${REPORT_HTML} \
                        --self-contained-html \
                        --junitxml=${REPORT_XML} \
                        -v \
                        --tb=short \
                        || true
                """
                // '|| true' prevents pipeline failure here so the
                // post-stage always publishes reports even on test failure.
            }
        }
    }

    // ── Post-build actions ────────────────────────────────
    post {

        always {
            echo 'Publishing test reports…'

            // Publish JUnit XML so Jenkins shows test results natively
            junit allowEmptyResults: true,
                  testResults: 'reports/junit.xml'

            // Archive the HTML report as a build artifact
            archiveArtifacts artifacts: 'reports/report.html',
                             allowEmptyArchive: true

            // Archive the HTML form files as well
            archiveArtifacts artifacts: 'index.html, style.css',
                             allowEmptyArchive: true
        }

        success {
            echo '✅  BUILD SUCCESSFUL – All Selenium tests passed!'
        }

        failure {
            echo '❌  BUILD FAILED – One or more Selenium tests failed. Check the report.'
        }

        unstable {
            echo '⚠️   BUILD UNSTABLE – Some tests failed. Review the JUnit results.'
        }
    }
}
