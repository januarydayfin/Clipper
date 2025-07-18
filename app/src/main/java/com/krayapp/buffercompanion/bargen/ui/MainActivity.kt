package com.krayapp.buffercompanion.bargen.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.databinding.MainActivityBinding


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var vb: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        vb = MainActivityBinding.inflate(layoutInflater)
        setContentView(vb.root)

        navController = findNavController(R.id.fragHost)
    }

    fun calledFromShortcut() = intent.action == "bargen.create.qr.buffer"
}