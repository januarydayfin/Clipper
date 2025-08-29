package com.krayapp.buffercompanion.bargen.ui

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.play.core.review.ReviewManagerFactory
import com.krayapp.buffercompanion.bargen.ClipperApp
import com.krayapp.buffercompanion.bargen.GlobalPrefs
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.databinding.MainActivityBinding


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var vb: MainActivityBinding
    private var onVolumeButtonHandler: () -> Unit = { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        vb = MainActivityBinding.inflate(layoutInflater)
        setContentView(vb.root)

        navController = findNavController(R.id.fragHost)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val scanOnVolume = ClipperApp.getPrefs().scanOnVolume
        Log.d("FATA", String.format("%s", scanOnVolume))

        if (!scanOnVolume || event.action == MotionEvent.ACTION_UP)
            return super.dispatchKeyEvent(event)

        return when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> {
                onVolumeButtonHandler()
                true
            }

            else -> super.dispatchKeyEvent(event)
        }
    }

    fun updateVolumeButtonHandler(block: () -> Unit) {
        onVolumeButtonHandler = block
    }

    fun calledFromShortcut() = intent.action == "bargen.create.qr.buffer"
}