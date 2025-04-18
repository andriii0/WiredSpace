plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	id ("org.sonarqube") version "6.0.1.5171"
	id("jacoco")

}

group = "org.main"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

sonar {
	properties {
		property("sonar.projectKey", "WiredSpace")
		property("sonar.projectName", "WiredSpace")
		property("sonar.host.url", "http://localhost:9000")

		property("sonar.token", project.findProperty("sonar.token") as String)
		//property("sonar.junit.reportPaths", "build/test-results/test")
		//property("sonar.java.coveragePlugin", "jacoco")
		//property("sonar.jacoco.reportPath", "build/reports/jacoco/test/jacocoTestReport.xml")
	}
}



dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-web-services")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("io.jsonwebtoken:jjwt:0.12.6")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	testImplementation("org.springframework.security:spring-security-test")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}


tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.named("sonar") {
	dependsOn("test", "jacocoTestReport")
}