package ir.amv.os.tools.jenkins.lib

trait ScriptAware {
    Map pipelineParams
    def script

    def setScript(def script) {
        this.script = script
    }

    def setPipelineParams(Map pipelineParams) {
        this.pipelineParams = pipelineParams
    }

    String copyGlobalLibraryScript(String srcPath, String destPath = null) {
        destPath = destPath ?: createTempLocation(srcPath)
        script.writeFile file: destPath, text: script.libraryResource(srcPath)
        script.sh 'ls -la ' + destPath
        script.echo "copyGlobalLibraryScript: copied ${srcPath} to ${destPath}"
        return destPath
    }

    /**
     * Generates a path to a temporary file location, ending with {@code path} parameter.
     *
     * @param path path suffix
     * @return path to file inside a temp directory
     */
    String createTempLocation(String path) {
        String tmpDir = script.pwd tmp: true
        return tmpDir + File.separator + new File(path).getName()
    }
}