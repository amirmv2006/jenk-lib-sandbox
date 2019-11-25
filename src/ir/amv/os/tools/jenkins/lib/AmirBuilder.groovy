package ir.amv.os.tools.jenkins.lib

import com.cloudbees.groovy.cps.NonCPS
import ir.amv.os.tools.jenkins.lib.my.GroovyClass

def execute(Map pipelineParams) {
//    def slave = detectSlaveType(pipelineParams)
    node('') {
        stage('amir') {
            detectSlaveType(pipelineParams)
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