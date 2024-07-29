pipeline {
    agent any
    environment {
        // DockerHub credentials
        DOCKER_CREDENTIALS_ID = 'nensiravaliya28'
        // DockerHub repository
        DOCKER_REPO = 'nensiravaliya28/java-app'
        // GitHub credentials
        GIT_CREDENTIALS_ID = 'gtk0'
    }
    stages {
        stage('Clone Repository') {
            steps {
                git credentialsId: "${GIT_CREDENTIALS_ID}", url: 'https://github.com/Nency-Ravaliya/Day15.git', branch: 'main'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image and tag it with BUILD_ID
                    def image = docker.build("${DOCKER_REPO}:${env.BUILD_ID}")
                }
            }
        }
        stage('Push Docker Image') {
            steps {
                script {
                    // Push the image with both the BUILD_ID tag and the 'latest' tag
                    docker.withRegistry('https://index.docker.io/v1/', "${DOCKER_CREDENTIALS_ID}") {
                        def image = docker.image("${DOCKER_REPO}:${env.BUILD_ID}")
                        image.push("${env.BUILD_ID}") // Push with BUILD_ID tag
                        image.push('latest') // Optionally, also push with 'latest' tag
                    }
                }
            }
        }
        stage('Deploy Container') {
            steps {
                script {
                    sh """
                    #!/bin/bash
                    docker pull ${DOCKER_REPO}:${env.BUILD_ID}
                    docker stop java-app-container || true
                    docker rm java-app-container || true
                    docker run -d --name java-app-container -p 8081:8081 ${DOCKER_REPO}:${env.BUILD_ID}
                    """
                }
            }
        }
        stage('Print Docker Logs') {
            steps {
                script {
                    sh """
                    #!/bin/bash
                    echo "Fetching logs for java-app-container..."
                    docker logs java-app-container
                    """
                }
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
