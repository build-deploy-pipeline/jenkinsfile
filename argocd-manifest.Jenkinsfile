pipeline {
    agent any

    environment {
      GIT_RPEO = "https://github.com/build-deploy-pipeline/test-alpine-dockerfile.git"
    }
    stages {
        stage('checkout') {
            steps {
                git branch: 'main', changelog: false, poll: false, url: "${env.GIT_RPEO}"
            }
        }
        stage("Build Docker Image") {
          steps {
            sh "nerdctl build -t ${DOCKER_REPOSITORY}/nginx:1.23.3 ."
          }
        }
        stage("push Docker Image") {
          steps {
            withCredentials([usernamePassword(credentialsId: 'harbor-credentials', passwordVariable: 'HARBOR_PASSWORD', usernameVariable: 'HARBOR_USERNAME')]) {
                sh "nerdctl  login harbor.choilab.xyz -u ${HARBOR_USERNAME} -p ${HARBOR_PASSWORD} --insecure-registry"
                sh "nerdctl push ${DOCKER_REPOSITORY}/nginx:1.23.3 --insecure-registry"
            }
          }
        }
    }
}
