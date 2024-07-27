# Jenkins CI/CD Pipeline Setup for Maven Java Application with Docker

This guide outlines the steps to set up a Jenkins CI/CD pipeline to build, push, and deploy a Maven-based Java application using Docker. The repository is private, and DockerHub credentials are required.

## Prerequisites

1. **Jenkins Installation**: Ensure Jenkins is installed and running on your machine or server.
2. **Jenkins Plugins**: Install the following Jenkins plugins:
   - Docker Pipeline
   - Git
   - Pipeline

3. **Docker Installation**: Docker must be installed on the Jenkins server and the Jenkins user must have permission to access Docker.

4. **Credentials**:
   - **GitHub Credentials**: Create a Jenkins credential with your GitHub access token for accessing the private repository.
   - **DockerHub Credentials**: Create a Jenkins credential with your DockerHub username and password for pushing Docker images.

## Jenkins Pipeline Setup

1. **Create a Jenkins Pipeline Job**:
   - Open Jenkins and create a new pipeline job.

2. **Pipeline Script**:
   - Use the following pipeline script in your Jenkins pipeline configuration:

     ```groovy
     pipeline {
         agent any
         environment {
             // DockerHub credentials
             DOCKER_CREDENTIALS_ID = 'dockerhub-credentials-id'
             // DockerHub repository
             DOCKER_REPO = 'your-dockerhub-repo/java-app'
             // GitHub credentials
             GIT_CREDENTIALS_ID = 'github-credentials-id'
         }
         stages {
             stage('Clone Repository') {
                 steps {
                     git credentialsId: "${GIT_CREDENTIALS_ID}", url: 'https://github.com/your-username/your-repo.git', branch: 'main'
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
                         sh '''
                         #!/bin/bash
                         docker pull ${DOCKER_REPO}:${env.BUILD_ID}
                         docker stop java-app-container || true
                         docker rm java-app-container || true
                         docker run -d --name java-app-container -p 8081:8081 ${DOCKER_REPO}:${env.BUILD_ID}
                         '''
                     }
                 }
             }
             stage('Print Docker Logs') {
                 steps {
                     script {
                         sh '''
                         #!/bin/bash
                         echo "Fetching logs for java-app-container..."
                         docker logs java-app-container
                         '''
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
     ```

3. **Configure Jenkins Credentials**:
   - **GitHub Credentials**:
     - Go to Jenkins Dashboard → Manage Jenkins → Manage Credentials.
     - Add a new "Username with password" credential with your GitHub token. Use the ID you set as `GIT_CREDENTIALS_ID` in the pipeline script.

   - **DockerHub Credentials**:
     - Go to Jenkins Dashboard → Manage Jenkins → Manage Credentials.
     - Add a new "Username with password" credential for DockerHub. Use the ID you set as `DOCKER_CREDENTIALS_ID` in the pipeline script.

4. **Run the Pipeline**:
   - Save the pipeline script and run the Jenkins job. The pipeline will clone the repository, build the Docker image, push it to DockerHub, deploy the container, and print Docker logs.

## Notes

- **Private GitHub Repository**: Ensure your GitHub credentials have access to the private repository.
- **Docker Permissions**: Verify that Docker commands work correctly on the Jenkins server.

For any issues or further customization, refer to Jenkins and Docker documentation or seek help from the community.
