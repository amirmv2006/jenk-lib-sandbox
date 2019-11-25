def call(Map pipelineParams) {
    pipeline {
        agent {
            label 'master'
        }
        environment {
            RUNNING_ON_OPENSHIFT = "${env.RUN_ON_OPENSHIFT}"
        }
        stages {
            stage('buildOnOpenshift') {
                when {
                    beforeAgent true
                    environment name: 'RUNNING_ON_OPENSHIFT', value: 'true'
                }
                agent {
                    node {
                        label 'maven'
                    }
                }
                environment {
                    MAVEN_DEPLOY_USERNAME = "admin"
                    MAVEN_DEPLOY_PASSWORD = "admin123"
                    MAVEN_REPO_BASE_URL = "nexus.tools.svc:8081/repository"
                    MAVEN_FETCH_REPO = "maven-public"
                    MAVEN_RELEASES_REPO = "maven-releases"
                    MAVEN_SNAPSHOTS_REPO = "maven-snapshots"
                }
                steps {
                    steps(pipelineParams)
                }
            }
            stage('buildOnLocal') {
                when {
                    beforeAgent true
                    not { environment name: 'RUNNING_ON_OPENSHIFT', value: 'true' }
                }
                agent {
                    docker {
                        image 'maven:3-alpine'
                        args '--network="container:nexus"'
                    }
                }
                environment {
                    MAVEN_DEPLOY_USERNAME = "admin"
                    MAVEN_DEPLOY_PASSWORD = "admin123"
                    MAVEN_REPO_BASE_URL = "nexus.tools.svc:8081/repository"
                    MAVEN_FETCH_REPO = "maven-public"
                    MAVEN_RELEASES_REPO = "maven-releases"
                    MAVEN_SNAPSHOTS_REPO = "maven-snapshots"
                }
                steps {
                    steps(pipelineParams)
                }
            }
        }
    }
}

private void steps(Map pipelineParams) {
    checkout scm
    copySettingsXml(pipelineParams)
    sh 'mvn clean deploy -P custom-repo -P deploy'
}

private void copySettingsXml(Map pipelineParams) {
    String settingsXml = libraryResource 'ir/amv/enterprise/laas/tools/jenkins/lib/maven-default-settings.xml'
    def createFileCommand = 'echo "'
    createFileCommand += settingsXml.replaceAll("\\n", "")
    createFileCommand += '" >> /tmp/settings.xml'
    echo createFileCommand
    sh createFileCommand
    sh 'cp /tmp/settings.xml ~/.m2'
    sh 'echo ~/.m2/settings.xml'
}
