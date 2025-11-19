pipeline {
    agent any

    parameters {
        choice(name: 'ENV', choices: ['uat','prod'], description: 'Environment')
    }

    environment {
        REGISTRY = "nicomesa"
    }

    stages {
        stage('Checkout') {
            steps {
                git 'git@github.com:nicomesa/spring-boot-gateway.git'
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    APPS = readFile("deployments/apps-${params.ENV}.txt").split("\n")
                    COMMIT_SHA = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    VERSION = "1.0.0-${COMMIT_SHA}"

                    for (APP in APPS) {
                        sh "docker build -t ${REGISTRY}/${APP}:${VERSION} ./deployments/${APP}"
                        sh "docker push ${REGISTRY}/${APP}:${VERSION}"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh "./deployments/deploy-env.sh ${params.ENV} ${VERSION}"
            }
        }
    }
}