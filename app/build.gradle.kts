plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id ("kotlin-kapt")
	id("androidx.navigation.safeargs.kotlin")
}

android {
	namespace = "com.uz.maztepis"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.uz.maztepis"
		minSdk = 21
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}

	viewBinding {
		enable = true
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
	}
}

buildscript {
	dependencies {
		classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.6")
	}
}

	dependencies {

		implementation("androidx.core:core-ktx:1.10.1")
		implementation("androidx.appcompat:appcompat:1.6.1")
		implementation("com.google.android.material:material:1.9.0")
		implementation("androidx.constraintlayout:constraintlayout:2.1.4")

		//Jetpack libraries
		implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
		implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
		implementation("androidx.navigation:navigation-fragment-ktx:2.7.1")
		implementation("androidx.navigation:navigation-ui-ktx:2.7.1")
		implementation("androidx.room:room-runtime:2.5.2")
		kapt("androidx.room:room-compiler:2.5.2")
		implementation("androidx.room:room-ktx:2.5.2")
		implementation("androidx.work:work-runtime-ktx:2.8.1")

		// Hilt for Dependency Injection
		implementation("com.google.dagger:hilt-android:2.47")
		kapt("com.google.dagger:hilt-compiler:2.47")
		implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
		kapt("androidx.hilt:hilt-compiler:1.0.0")

		// Coroutines
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

		// Timber for Logging
		implementation("com.jakewharton.timber:timber:5.0.1")

		// Testing Libraries
		testImplementation("junit:junit:4.13.2")
		testImplementation("org.mockito:mockito-core:5.4.0")
		androidTestImplementation("androidx.test.ext:junit:1.1.5")
		androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

		//animation
		implementation("com.github.gayanvoice:android-animations-kotlin:1.0.1")
	}