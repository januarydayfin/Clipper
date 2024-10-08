plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("com.google.devtools.ksp") version "1.9.21-1.0.15"
}

android {
	namespace = "com.krayapp.buffercompanion"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.krayapp.buffercompanion"
		minSdk = 28
		targetSdk = 34
		versionCode = 105
		versionName = "1.05"

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
	implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
	implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
	val room_version = "2.6.1"
	implementation("androidx.activity:activity-ktx:1.8.2")
	implementation("androidx.room:room-runtime:$room_version")
	annotationProcessor("androidx.room:room-compiler:$room_version")
	implementation("androidx.room:room-ktx:$room_version")

	ksp("androidx.room:room-compiler:$room_version")
	implementation("androidx.core:core-ktx:1.12.0")
	implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
	implementation("androidx.appcompat:appcompat:1.6.1")
	implementation("androidx.recyclerview:recyclerview:1.3.2")
	implementation("com.google.android.material:material:1.11.0")
	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.5")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}