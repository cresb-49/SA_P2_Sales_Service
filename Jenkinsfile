pipeline {
    agent any

    environment {
        BUILD_DIR = 'target'
        FINAL_JAR_NAME = 'sales-service.jar'
        DEST_DIR = '/var/api/pf_sa'
        JAR_EXCLUDE_PATTERN = 'sources\\|javadoc'
    }

    stages {
        stage('Compile') {
            steps {
                sh 'mvn -B compile'
            }
        }

        stage('Clean Install') {
            steps {
                sh 'mvn -B clean install'
            }
        }

        stage('Publish Coverage Report') {
            steps {
                script {
                    def jacocoReport = "${BUILD_DIR}/site/jacoco/jacoco.xml"
                    if (fileExists(jacocoReport)) {
                        recordCoverage(
                            tools: [[parser: 'JACOCO', reportFile: jacocoReport]]
                        )
                    } else {
                        echo "JaCoCo XML report not found at ${jacocoReport}"
                    }
                }
            }
        }

        stage('Deploy JAR') {
            when {
                expression { fileExists("${BUILD_DIR}") }
            }
            steps {
                script {
                    def jarListing = sh(
                        script: "ls ${BUILD_DIR}/*.jar 2>/dev/null | grep -v '${JAR_EXCLUDE_PATTERN}' || true",
                        returnStdout: true
                    ).trim()

                    if (!jarListing) {
                        error "No JAR artifact found in ${BUILD_DIR}"
                    }

                    def jarPath = jarListing.tokenize('\n')[0]
                    def renamedJarPath = "${BUILD_DIR}/${FINAL_JAR_NAME}"

                    if (jarPath != renamedJarPath) {
                        sh "mv ${jarPath} ${renamedJarPath}"
                    }

                    sh """
                        set -e
                        install -d ${DEST_DIR}
                        tmp_file=\$(mktemp ${DEST_DIR}/${FINAL_JAR_NAME}.XXXXXX)
                        cp ${renamedJarPath} \"\${tmp_file}\"
                        mv \"\${tmp_file}\" ${DEST_DIR}/${FINAL_JAR_NAME}
                    """
                }
            }
        }
    }
}
