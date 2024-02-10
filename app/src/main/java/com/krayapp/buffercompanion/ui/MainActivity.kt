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
import com.krayapp.buffercompanion.ClipperApp
import com.krayapp.buffercompanion.Constants.Companion.MAIN_FRAG
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.databinding.MainActivityBinding
import com.krayapp.buffercompanion.ui.fragments.ToolbarAssist
import com.krayapp.buffercompanion.widget.MainWidgetProvider
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
	private lateinit var navController: NavController
	private lateinit var vb: MainActivityBinding
	private lateinit var toolbarAssist: ToolbarAssist
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		vb = MainActivityBinding.inflate(layoutInflater)
		setContentView(vb.root)
		toolbarAssist = ToolbarAssist(vb.toolbar.root)

		navController = Navigation.findNavController(this, R.id.fragHost)

		navController.addOnDestinationChangedListener { _, destination, _ ->
			if (destination.label == MAIN_FRAG)
				toolbarAssist.onMainScreen()
		}

		vb.toolbar.showSettings.setOnClickListener {
			navigateTo(R.id.toSettings)
		}
	}

	override fun onStop() {
		super.onStop()
		refreshWidget()
	}

	fun navigateTo(actionId: Int) {
		try {
			navController.navigate(actionId)
		} catch (ignored: Exception) { }
	}

	fun restartApp() {
		MainScope().launch {
			delay(500)
			startActivity(
				Intent(
					this@MainActivity,
					MainActivity::class.java
				).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })

			Runtime.getRuntime().exit(0)
		}
	}

	fun popBackStack() {
		navController.popBackStack()
	}

	fun toolbarAssistant() : ToolbarAssist {
		return toolbarAssist
	}

	fun refreshWidget() {
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
		}, 3000) //delay чтобы БД успела обновиться
	}
}