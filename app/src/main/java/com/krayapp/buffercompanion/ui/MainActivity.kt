package com.krayapp.buffercompanion.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.krayapp.buffercompanion.ClipperApp
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.widget.MainWidgetProvider


class MainActivity : AppCompatActivity() {
	private lateinit var navController: NavController
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)

		navController = Navigation.findNavController(this, R.id.fragHost)
	}

	override fun onStop() {
		super.onStop()
		updateWidget()
	}

	fun navigateTo(actionId: Int) {
		navController.navigate(actionId)
	}


	fun popBackStack() {
		navController.popBackStack()
	}
	fun updateWidget() {
		val ids: IntArray = AppWidgetManager.getInstance(ClipperApp.getApplication())
			.getAppWidgetIds(
				ComponentName(
					ClipperApp.getApplication(),
					MainWidgetProvider::class.java
				)
			)
		val intent = Intent(this, MainWidgetProvider::class.java).apply {
			action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
			putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
		}
		sendBroadcast(intent)


		Handler(Looper.myLooper()!!).postDelayed({
			sendBroadcast(intent)
		}, 3000)
	}
}