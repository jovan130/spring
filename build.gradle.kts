import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
	kotlin("plugin.allopen") version "1.9.22"
	kotlin("plugin.jpa") version "1.9.22"
	kotlin("kapt") version "1.9.22"
	jacoco
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.springframework.boot:spring-boot-devtools")
	kapt("org.springframework.boot:spring-boot-configuration-processor")

	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.1")
	testImplementation("org.mockito:mockito-core:4.5.1")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "17"
		freeCompilerArgs += "-Xjsr305=strict"
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.Embeddable")
	annotation("jakarta.persistence.MappedSuperclass")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
// Configure JaCoCo
jacoco {
	toolVersion = "0.8.8" // Use the latest version
	reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests are required to run before generating the report

	reports {
		xml.required.set(true)
		csv.required.set(false)
		html.required.set(true)
		html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
	}

	// Define what classes should be included/excluded from coverage
	classDirectories.setFrom(
			files(classDirectories.files.map {
				fileTree(it) {
					exclude(
							"**/config/**",
							"**/entity/**",
							"**/dto/**",
							"**/exception/**",
							"**/Application*"
					)
				}
			})
	)
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = "0.80".toBigDecimal() // Minimum 80% coverage
			}
		}

		rule {
			enabled = true
			element = "CLASS"
			includes = listOf("com.cota.mapping.controller.*", "com.cota.mapping.service.*")

			limit {
				counter = "LINE"
				value = "COVEREDRATIO"
				minimum = "0.80".toBigDecimal()
			}

			limit {
				counter = "BRANCH"
				value = "COVEREDRATIO"
				minimum = "0.60".toBigDecimal()
			}
		}
	}
}

// Ensure coverage verification happens during build
tasks.check {
	dependsOn(tasks.jacocoTestCoverageVerification)
	dependsOn(tasks.jacocoTestReport)
}
