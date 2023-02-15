pipeline {
    agent any

    parameters {
      string(name : 'application_name', defaultValue : '', description : 'application_name and used to manifest. this must be unique')
      string(name : 'github_link', defaultValue : 'https://github.com/build-deploy-pipeline/test-alpine-dockerfile.git', description : 'github link of user')
      string(name : 'github_branch', defaultValue : 'main', description : 'github branch of user')
    }

    environment {
      // Argo manifest가 있는 github organization
      ORG_NAME = "argocd-manifest-test"
      TEMPLATE_URL = "TEMPLATE_URL"
    }
    stages {
        stage('checkout') {
            steps {
              git branch: "${params.github_branch}", changelog: false, poll: false, url: "${params.github_link}"
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
              def argo_manifest_repo = "https://github.com/${ORG_NAME}/${params.application_name}"
              def chcek_repo_response = sh(
                returnStdout: true,
                script: "curl -w \'%{http_code}\' -o /dev/null " + argo_manifest_repo
              )

              if (chcek_repo_response == "404"){
                println "argo_manifest_repo(${argo_manifest_repo}) is not exist. create manifest repo."

                // argo manifest repo가 없으면 repo를 생성하고 초기화
                withCredentials([string(credentialsId: 'github_token', variable: 'TOKEN')]){
                  // step1. manifest repo 생성
                  def github_create_repo_api = "https://api.github.com/orgs/${ORG_NAME}/repos "
                  def github_auth = "-H \"Authorization: Bearer ${TOKEN}\" "
                  def github_create_repo_body = "-d '{\"name\": \"${params.application_name}\"}'"

                  def create_repo_response = sh(
                    returnStdout: true,
                    script: "curl -w \'%{http_code}\' -o /dev/null "  + github_auth + github_create_repo_api + github_create_repo_body
                  )
                  if (create_repo_response != "201"){
                    error("Failed to create repository, response code: ${response}")
                  }

                  // step2. 초기화
                  // sh "git clone ${argo_manifest_repo} ./template"
                  // sh 'cd ${REPO_NAME} || wget ${TEMPLATE_URL} || unzip'
                  // sh 'git config'
                  // sh 'git add -A | git commit -m "Bot: Initlaize" | git push'
                }
              }else {
                println "argo_manifest_repo(${argo_manifest_repo}) alreay exist. skip create manifest repo."
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
            // sh 'argocd repo add'
            // sh 'argocd app create'
            // sh 'argocd app sync'
          }
        }
    }
}
