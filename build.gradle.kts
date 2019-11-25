import com.gradle.scan.plugin.BuildScanPlugin
import com.mkobit.jenkins.pipelines.http.AnonymousAuthentication
import org.gradle.kotlin.dsl.version
import java.io.ByteArrayOutputStream

plugins {
  id("com.gradle.build-scan") version "1.16"
  id("com.mkobit.jenkins.pipelines.shared-library") version "0.7.0"
  id("com.github.ben-manes.versions") version "0.20.0"
}

val commitSha: String by lazy {
  ByteArrayOutputStream().use {
    project.exec {
      commandLine("git", "rev-parse", "HEAD")
      standardOutput = it
    }
    it.toString(Charsets.UTF_8.name()).trim()
  }
}

buildScan {
  setTermsOfServiceAgree("yes")
  setTermsOfServiceUrl("https://gradle.com/terms-of-service")
  link("GitLab", "https://gitlab.com/amirmv2006/laas-jenkins-lib.git")
  value("Revision", commitSha)
}

tasks {
  register("wrapper", Wrapper::class) {
    gradleVersion = "4.10"
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  val spock = "org.spockframework:spock-core:1.1-groovy-2.4"
  testImplementation(spock)
  testImplementation("org.assertj:assertj-core:3.11.1")
  integrationTestImplementation(spock)
}

jenkinsIntegration {
  baseUrl.set(uri("http://localhost:5050").toURL())
  authentication.set(providers.provider { AnonymousAuthentication })
  downloadDirectory.set(layout.projectDirectory.dir("jenkinsResources"))
}

sharedLibrary {
  // TODO: this will need to be altered when auto-mapping functionality is complete
  coreVersion.set(jenkinsIntegration.downloadDirectory.file("core-version.txt").map { it.asFile.readText().trim() })
  // TODO: retrieve downloaded plugin resource
  pluginDependencies {
    dependency("org.jenkins-ci.plugins", "pipeline-build-step", "2.7")
    dependency("org.6wind.jenkins", "lockable-resources", "2.2")
    dependency("org.jenkinsci.plugins", "pipeline-model-api", "1.2.5")
    dependency("org.jenkinsci.plugins", "pipeline-model-declarative-agent", "1.1.1")
    dependency("org.jenkinsci.plugins", "pipeline-model-definition", "1.2.5")
    dependency("org.jenkinsci.plugins", "pipeline-model-extensions", "1.2.5")
  }
}
