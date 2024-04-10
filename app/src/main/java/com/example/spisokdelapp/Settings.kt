package com.example.spisokdelapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.spisokdelapp.AuthActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        sharedPreferences = getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
        val login = sharedPreferences.getString("login", "")
        val pass = sharedPreferences.getString("pass", "")
        val email12 = sharedPreferences.getString("email1", "")

        // Отображаем данные о пользователе на экране
        val loginTextView = findViewById<TextView>(R.id.emailTextView)
        val passTextView = findViewById<TextView>(R.id.passwordTextView)
        val emailtextview1=findViewById<TextView>(R.id.userEmailTextView)

        loginTextView.text = "Логин: $login"

        passTextView.text = "Пароль: $pass"

        val email = sharedPreferences.getString("email", "") ?: ""
        val email1 = sharedPreferences.getString("email1", "") ?: ""
        val password = sharedPreferences.getString("password", "") ?: ""
        val buttonExit = findViewById<Button>(R.id.buttonexit11)
        // Отображаем данные о пользователе на экране
        val emailTextView = findViewById<TextView>(R.id.emailTextView)
        val passwordTextView = findViewById<TextView>(R.id.passwordTextView)
        val notificationsSwitch = findViewById<Switch>(R.id.notificationsSwitch)

        emailtextview1.text= "Ваша почта: $email12"
        emailTextView.text = "Ваш логин: $email"
        passwordTextView.text = "Ваш пароль: $password"

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logoutAndNavigateToAuthActivity()
        }
        buttonExit.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Закрывает текущую активность
        }


        notificationsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val intent = Intent("com.yourapp.CUSTOM_ACTION_ENABLE_NOTIFICATIONS")
                sendBroadcast(intent)
            } else {
                val intent = Intent("com.yourapp.CUSTOM_ACTION_DISABLE_NOTIFICATIONS")
                sendBroadcast(intent)
            }
        }
    }

    private fun logoutAndNavigateToAuthActivity() {
        with(sharedPreferences.edit()) {
            remove("email")
            remove("password")
            apply()
        }

        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}
