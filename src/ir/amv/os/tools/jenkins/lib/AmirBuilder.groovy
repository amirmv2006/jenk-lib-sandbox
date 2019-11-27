package ir.amv.os.tools.jenkins.lib

import com.cloudbees.groovy.cps.NonCPS
import ir.amv.os.tools.jenkins.lib.my.GroovyClass

def execute(Map pipelineParams) {
//    def slave = detectSlaveType(pipelineParams)
    node('') {
        stage('amir') {
            withDockerContainer(image: 'maven:3-jdk-8') {
                checkout scm
                withCredentials([string(credentialsId: 'GithubPass', variable: 'password')]) {
                    sh 'git reset --hard'
                    sh 'git config --global user.email "you@example.com"'
                    sh 'git config --global user.name "Your Name"'
                    sh 'mvn -B release:prepare release:perform -Dusername=amirmv2006 -Dpassword=$password'
                }
            }
        }
    }
}

@NonCPS
def detectSlaveType(Map pipelineParams) {
    return new GroovyClass(this)
}

def getParamOrEnvVariable(Map pipelineParams, String paramName) {
    def param = pipelineParams.getOrDefault(paramName, System.getenv(paramName))
    pipelineParams.putIfAbsent(paramName, param)
    param
}