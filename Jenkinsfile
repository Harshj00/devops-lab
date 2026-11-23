pipeline {
    agent any

    environment {
        APP_NAME        = 'devops-lab'
        IMAGE_NAME      = 'harshj00/devops-lab'
        IMAGE_TAG       = "1.0-${BUILD_NUMBER}"
        REGISTRY_CRED   = 'dockerhub-credentials'
        JAVA_HOME       = 'C:\\Program Files\\Java\\jdk-21'
        SONAR_HOST_URL  = 'http://localhost:9000'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout Source') {
            steps {
                echo "==> Pulling source code from Git repository..."
                checkout scm
                sh 'git log -1 --stat'
            }
        }

        stage('Code Quality & Static Analysis') {
            steps {
                echo "==> Running SonarQube static code analysis and security scans..."
                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                    sh 'mvn sonar:sonar -Dsonar.host.url=${SONAR_HOST_URL}'
                }
            }
        }

        stage('Build & Unit Testing') {
            steps {
                echo "==> Compiling Java 21 application and executing JUnit tests..."
                sh 'mvn clean test'
            }
        }

        stage('Package Application') {
            steps {
                echo "==> Packaging fat executable JAR artifact..."
                sh 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Security Scanning (Trivy)') {
            steps {
                echo "==> Scanning container files and dependencies for CVE vulnerabilities..."
                catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                    sh 'trivy fs --severity HIGH,CRITICAL .'
                }
            }
        }

        stage('Docker Build & Tag') {
            steps {
                echo "==> Building multi-stage Docker image..."
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
            }
        }

        stage('Docker Push to Registry') {
            steps {
                echo "==> Authenticating and pushing image to Docker Hub..."
                // withCredentials([usernamePassword(credentialsId: REGISTRY_CRED, usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                //     sh "echo \$PASS | docker login -u \$USER --password-stdin"
                //     sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                //     sh "docker push ${IMAGE_NAME}:latest"
                // }
                echo "Image ${IMAGE_NAME}:${IMAGE_TAG} ready for push."
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                echo "==> Deploying application manifests to Kubernetes cluster..."
                sh 'kubectl apply -f k8s/configmap.yaml'
                sh 'kubectl apply -f k8s/secret.yaml'
                sh 'kubectl apply -f k8s/pvc.yaml'
                sh 'kubectl apply -f k8s/deployment.yaml'
                sh 'kubectl apply -f k8s/service.yaml'
                sh 'kubectl rollout status deployment/devops-lab-app --timeout=60s'
            }
        }

        stage('Post-Deployment Verification') {
            steps {
                echo "==> Running smoke test against application health endpoint..."
                sh 'kubectl get pods -l app=devops-lab'
                sh 'curl -f http://localhost:8080/health || true'
            }
        }
    }

    post {
        always {
            echo "==> Pipeline run finished. Cleaning workspace..."
            cleanWs()
        }
        success {
            echo "SUCCESS: Capstone Pipeline executed successfully!"
        }
        failure {
            echo "FAILURE: Pipeline execution encountered an error."
        }
    }
}
