plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("com.google.devtools.ksp") version "1.9.21-1.0.15"
	id("com.google.gms.google-services")
	id("com.google.firebase.crashlytics")
}

android {
	namespace = "com.krayapp.buffercompanion"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.krayapp.buffercompanion"
		minSdk = 28
		targetSdk = 34
		versionCode = 2
		versionName = "0.2a"

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

	buildFeatures {
		viewBinding = true
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}
	kotlinOptions {
		jvmTarget = "1.8"
	}
}

dependencies {

	val room_version = "2.6.1"

	implementation("androidx.room:room-runtime:$room_version")
	annotationProcessor("androidx.room:room-compiler:$room_version")
	implementation("androidx.room:room-ktx:$room_version")

	ksp("androidx.room:room-compiler:$room_version")
	implementation("androidx.core:core-ktx:1.12.0")
	implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
	implementation("androidx.appcompat:appcompat:1.6.1")
	implementation("com.google.android.material:material:1.10.0")
	implementation("com.google.firebase:firebase-crashlytics")
	implementation("com.google.firebase:firebase-analytics")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}