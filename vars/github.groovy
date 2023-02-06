@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1')

import groovyx.net.http.RESTClient


def createGithubRepoInOrgOrRaiseException (Map params) {
    def repoName = params.repoName
    def accessToken = params.accessToken


    def githubApi = new RESTClient('https://api.github.com')
    def org_name = 'argocd-manifest-test'
    try{
      def response = githubApi.post(
        path: "orgs${org_name}/repos",
        headers: [
            'Authorization': "Bearer ${accessToken}",
            'Content-Type': 'application/json'
        ],
        body: [
            name: repoName
        ]
      )

      if (response.status == 201) {
        println "Successfully created repository: ${repoName}"
      } else {
        throw new Exception("Received unexpected response status code: ${response.status}")
      }
    } catch (Exception e) {
        throw new Exception("other error: ${response.status}")
    }

}
