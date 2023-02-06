pipeline {
    agent any

    environment {
      GIT_RPEO = "https://github.com/build-deploy-pipeline/test-alpine-dockerfile.git"
      ORG_NAME = "argocd-manifest-test"
      REPO_NAME = "hello-world1"
      TEMPLATE_URL = "TEMPLATE_URL"
    }
    stages {
        stage('checkout') {
            steps {
              git branch: 'main', changelog: false, poll: false, url: "${env.GIT_RPEO}"
            }
        }
        stage("Build Docker Image") {
          steps {
            sh "nerdctl -n k8s.io build -t ${DOCKER_REPOSITORY}/nginx:1.23.3 ."
          }
        }
        stage("push Docker Image") {
          steps {
            withCredentials([usernamePassword(credentialsId: 'harbor-credentials', passwordVariable: 'HARBOR_PASSWORD', usernameVariable: 'HARBOR_USERNAME')]) {
              sh "nerdctl login harbor.choilab.xyz -u ${HARBOR_USERNAME} -p ${HARBOR_PASSWORD} --insecure-registry"
              sh "nerdctl -n k8s.io push ${DOCKER_REPOSITORY}/nginx:1.23.3 --insecure-registry"
            }
          }
        }
        stage("check exist manifset repo. if not exist, create repo"){
          steps {
            script {
              // repo가 있는지 확인하고 없으면 repo 생성
              def chcek_repo_response = sh(returnStdout: true, script: 'curl -w \'%{http_code}\' -o /dev/null https://github.com/${ORG_NAME}/${REPO_NAME}')
              if (chcek_repo_response == "404"){
                withCredentials([string(credentialsId: 'github_token', variable: 'TOKEN')]){
                  def create_repo_response = sh(returnStdout: true, script: 'curl -w \'%{http_code}\' -o /dev/null -H "Authorization: Bearer ${TOKEN}" https://api.github.com/orgs/${ORG_NAME}/repos -d \'{"name":"' + "${REPO_NAME}" + '"}\'')
                  if (create_repo_response != "201"){
                    error("Failed to create repository, response code")
                  }

                  //todo 템플릿 복사
                  // sh 'git clone https://github.com/${ORG_NAME}/${REPO_NAME}'
                  // sh 'cd ${REPO_NAME} || wget ${TEMPLATE_URL} || unzip'
                  // sh 'git config'
                  // sh 'git add -A | git commit -m "Bot: Initlaize" | git push'
                }
              }
            }
          }
        }
        stage("update manifest"){
          steps{
            sh 'echo "update manifest"'
            // helm values update
            // sh 'yq ...'
          }
        }
        stage("argocd sync"){
          steps{
            sh 'echo "argocd sync"'
            sh 'argocd repo add'
            sh 'argocd app create'
            sh 'argocd app sync'
          }
        }
    }
}
