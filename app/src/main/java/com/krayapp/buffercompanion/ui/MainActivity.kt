package com.krayapp.buffercompanion.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.krayapp.buffercompanion.Constants.Companion.MAIN_FRAG
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.databinding.MainActivityBinding


class MainActivity : AppCompatActivity() {
	private lateinit var navController: NavController
	private lateinit var vb: MainActivityBinding
	private lateinit var toolbarAssist: ToolbarAssist
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		vb = MainActivityBinding.inflate(layoutInflater)
		setContentView(vb.root)
		toolbarAssist = ToolbarAssist(vb.toolbar.root)

		navController = findNavController(R.id.fragHost)

		navController.addOnDestinationChangedListener { _, destination, _ ->
			if (destination.label == MAIN_FRAG)
				toolbarAssist.onMainScreen()
		}
	}

	fun popBackStack() {
		navController.popBackStack()
	}

	fun toolbarAssistant() : ToolbarAssist {
		return toolbarAssist
	}
}