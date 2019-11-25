package ir.amv.os.tools.jenkins.lib

def execute(Map pipelineParams) {
    properties([
            parameters([
                    string(name: 'submodule', defaultValue: ''),
                    string(name: 'submodule_branch', defaultValue: ''),
                    string(name: 'commit_sha', defaultValue: ''),
            ])
    ])
//    def slave = detectSlaveType(pipelineParams)
}

//@NonCPS
//def detectSlaveType(Map pipelineParams) {
//    return new AnySlaveWithDockerBuild(pipelineParams, this)
//}

def getParamOrEnvVariable(Map pipelineParams, String paramName) {
    def param = pipelineParams.getOrDefault(paramName, System.getenv(paramName))
    pipelineParams.putIfAbsent(paramName, param)
    param
}