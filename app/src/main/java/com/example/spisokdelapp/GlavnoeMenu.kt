package com.example.spisokdelapp



import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class GlavnoeMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.glav_menu)

        val buttonDela = findViewById<Button>(R.id.buttonDela)
        val buttonspisok = findViewById<Button>(R.id.buttonpokypki)

        val buttontabl:Button = findViewById(R.id.buttontablet)
        val settingsbutton =  findViewById<Button>(R.id.settingsbutton)

        buttonDela.setOnClickListener {
            // Handle Список дел button click
            startActivity(Intent(this, MainActivity::class.java))
        }

        settingsbutton.setOnClickListener {
            // Handle Список дел button click
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        buttonspisok.setOnClickListener {
            // Handle Список дел button click
            startActivity(Intent(this,SpisokPokypok::class.java))
        }

        buttontabl.setOnClickListener {
            // Handle Список дел button click
            startActivity(Intent(this, TabletkiActivity::class.java))
        }


    }
}