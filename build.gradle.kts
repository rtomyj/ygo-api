import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.rtomyj.ygo"
version = "1.2.4"
java.sourceCompatibility = JavaVersion.VERSION_11
val archivesBaseName = "ygo-api"

val baseName = "ygo-api"
val springVersion = "2.4.2"
val swagger2Version = "3.0.0"
val javadocVersion = "3.1.1"
val cache2kVersion = "1.2.4.Final"
val lombokVersion = "1.18.12"
val mysqlVersion = "8.0.21"
val jacksonVersion = "2.11.2"
val cucumberVersion = "6.7.0"
val gatlingVersion = "3.5.0"
val restAssuredVersion = "4.3.3"
val groovyVersion = "3.0.7"


plugins {
	id("org.springframework.boot") version "2.4.4"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.30"
	kotlin("plugin.spring") version "1.4.30"
	id ("jacoco")
	id("java")
	id("scala")
}


repositories {
	mavenCentral()
}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = JavaVersion.VERSION_11.toString()
	}
}


sourceSets.create("integTest") {
	java.srcDir("src/integTest/java")
	resources.srcDir("src/integTest/resources")
}

val integTestImplementation by configurations.getting {
//	extendsFrom(configurations.implementation.get())
}


sourceSets.create("perfTest") {
	java.srcDir("src/perfTest/java")
	resources.srcDir("src/perfTest/resources")
}


dependencies {
	implementation("javax.validation:validation-api:2.0.1.Final")

	implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
	implementation("org.springframework.boot:spring-boot-starter-hateoas:$springVersion")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springVersion")
	implementation("org.springframework.boot:spring-boot-starter-log4j2:$springVersion")
	implementation("org.springframework.boot:spring-boot-starter-actuator:$springVersion")

	implementation("org.springframework.boot:spring-boot-starter-jetty:$springVersion")
	implementation("org.eclipse.jetty:jetty-alpn-conscrypt-server")
	implementation("org.eclipse.jetty.http2:http2-server")

	implementation("org.apache.maven.plugins:maven-javadoc-plugin:$javadocVersion")

	implementation("io.springfox:springfox-boot-starter:$swagger2Version")

	runtimeOnly("mysql:mysql-connector-java:$mysqlVersion")

	implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

	implementation("org.cache2k:cache2k-api:$cache2kVersion")
	implementation("org.cache2k:cache2k-core:$cache2kVersion")

	implementation("com.google.guava:guava:30.1-jre")

	annotationProcessor("org.projectlombok:lombok:$lombokVersion")	// needed to compile via gradle CLI
	implementation("org.projectlombok:lombok:$lombokVersion")	// plug in required to work in VSCode, might be the same for other IDE"s

}


configurations {

	implementation {
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
		exclude(module = "spring-boot-starter-tomcat")
		exclude(group = "org.apache.tomcat")
		exclude(group = "junit")
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}

	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}

}


tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
	group = "Build"
	description = "Creates a JAR file that can be executed to launch YGO service"

	manifest.attributes.apply {
		put("Implementation-Title", archivesBaseName)
	}
}


tasks.create("bootJarPath") {
	group = "Util"

	doFirst {
		println("${buildDir}/libs/${archivesBaseName}-${project.version}.jar")
	}
}


tasks.register("createDockerJar", Copy::class) {
	from("${buildDir}/libs/${archivesBaseName}-${project.version}.jar")
	into("${buildDir}/libs")

	rename ("${archivesBaseName}-${project.version}.jar", "${archivesBaseName}.jar")
}


tasks.withType<Javadoc> {

	options.memberLevel = JavadocMemberLevel.PRIVATE
	source = sourceSets["main"].allJava

}


apply(from = "gradle/unitTest.gradle.kts")
apply(from = "gradle/integTest.gradle.kts")
apply(from = "gradle/perfTest.gradle.kts")


tasks.create("runIntegrationTests") {
	dependsOn(tasks.assemble, tasks["compileIntegTestJava"])
	doLast {
		javaexec {
			main = "io.cucumber.core.cli.Main"
			classpath = sourceSets["integTest"].runtimeClasspath
			args = listOf("--plugin", "pretty", "--glue", "cucumber", "src/integTest/resources")
		}
	}
}


tasks.register("perfTest", JavaExec::class) {
	description = "Performance test executed using Gatling"
	group = "Test"
	classpath = sourceSets["perfTest"].runtimeClasspath


	main = "io.gatling.app.Gatling"
	args = listOf(
			"-s", "simulations.BrowseSimulation",
			"-rf", "${buildDir}/gatling-results",
			"--binaries-folder", sourceSets["perfTest"].output.classesDirs.toString() // ignored because of above bug
	)
}