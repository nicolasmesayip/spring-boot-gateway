pipeline {
    agent any

    environment {
        ENV = "uat"
        REGISTRY = "nicomesa"
    }

    stages {
        stage('Checkout') {
            steps {
                sh '''
                if [ -d spring-boot-gateway ]; then
                    cd spring-boot-gateway
                    git reset --hard
                    git clean -fd
                    git pull
                else
                    git clone git@github.com:nicolasmesayip/spring-boot-gateway.git
                fi
                '''
            }
        }

        stage('Get Tag') {
                    steps {
                        script {
                            def tag = sh(script: "git describe --tags --exact-match || echo '0.0.0'", returnStdout: true).trim()
                            env.GIT_TAG = tag
                            echo "Tag: ${env.GIT_TAG}"
                        }
                    }
                }

        stage('Build Docker Images') {
            steps {
                script {
                    APPS = readFile("deployments/apps-${ENV}.txt").split("\n")
                    COMMIT_SHA = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    VERSION = "${env.GIT_TAG}-${COMMIT_SHA}"

                    for (APP in APPS) {
                        sh "docker build -f ${APP}/Dockerfile -t ${REGISTRY}/${APP}:${VERSION} ."
                        docker.withRegistry('https://index.docker.io/v1/', 'docker-hub-creds') {
                            docker.image("${REGISTRY}/${APP}:${VERSION}").push()
                        }
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([string(credentialsId: 'k8s-service-account', variable: 'TOKEN')]) {
                    sh '''
                        kubectl config set-cluster cluster --server=https://172.17.48.1:6443 --insecure-skip-tls-verify=true
                        kubectl config set-credentials jenkins --token=$TOKEN
                        kubectl config set-context jenkins --cluster=cluster --user=jenkins
                        kubectl config use-context jenkins

                        bash deployments/deploy.sh ${ENV} ${VERSION}
                      '''
                    }
            }
        }
    }
}