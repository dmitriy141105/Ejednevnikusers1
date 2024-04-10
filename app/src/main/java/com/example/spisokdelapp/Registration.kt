package com.example.spisokdelapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Registration : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val userLogin: EditText = findViewById(R.id.user_login)
        val userEmail: EditText = findViewById(R.id.user_email)
        val userPass: EditText = findViewById(R.id.user_pass)
        val buttonRegister: Button = findViewById(R.id.button_reg)
        val linkToAuth: TextView = findViewById(R.id.link_to_auth)

        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
        val sharedPreferences = getSharedPreferences("AuthPrefs", MODE_PRIVATE) // Получаем доступ к SharedPreferences
        fun isEmailValid(email: String): Pair<Boolean, String> {
            if (email.isEmpty()) {
                return Pair(false, "Email не может быть пустым.")
            }

            val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(com|ru|org|net|gov|edu|int|mil|biz|info|name|museum|coop)$"
            val emailRegex = Regex(emailPattern)

            if (!emailRegex.matches(email)) {
                return Pair(false, "Email имеет недопустимый формат.")
            }

            val atIndex = email.indexOf('@')
            if (atIndex == -1 || atIndex < 3) {
                return Pair(false, "Email должен содержать минимум 3 символа до символа '@'.")
            }

            val dotIndex = email.lastIndexOf('.')
            if (dotIndex == -1 || dotIndex <= atIndex) {
                return Pair(false, "Email должен содержать домен после символа '@'.")
            }



            return Pair(true, "Email валиден.")
        }


        fun isPasswordValid(password: String): Boolean {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
            return password.matches(passwordPattern.toRegex())
        }
        buttonRegister.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val pass = userPass.text.toString().trim()

            val (isEmailValid, emailValidationMsg) = isEmailValid(email) // Поправленный код здесь
            val isPassValid = isPasswordValid(pass)
            val editor = sharedPreferences.edit()
            editor.putString("email1", email)
            editor.apply()
            when {
                login.isEmpty() -> Toast.makeText(this, "Пожалуйста, введите логин", Toast.LENGTH_LONG).show()
                email.isEmpty() -> Toast.makeText(this, "Пожалуйста, введите электронную почту", Toast.LENGTH_LONG).show()
                pass.isEmpty() -> Toast.makeText(this, "Пожалуйста, введите пароль", Toast.LENGTH_LONG).show()
                !isEmailValid -> Toast.makeText(this, emailValidationMsg, Toast.LENGTH_LONG).show()
                !isPassValid -> Toast.makeText(this, "Пароль должен содержать не менее 8 символов, включая заглавные буквы и специальные символы", Toast.LENGTH_LONG).show()
                else -> {
                    // Сохраняем данные в SharedPreferences.
                    val sharedPreferences = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("login", login)
                    editor.putString("email", email)
                    editor.putString("password", pass)
                    editor.apply()


                    val user = User(login, email, pass)
                    val db = DbHelper(this, null)
                    db.adduser(user)
                    Toast.makeText(this,"Пользователь $login добавлен", Toast.LENGTH_LONG).show()

                    // Переходим на страницу авторизации с заполненными данными.
                    val intent = Intent(this, AuthActivity::class.java).apply {
                        putExtra("login", login)
                        putExtra("password", pass)
                    }
                    startActivity(intent)
                    finish() // Закрываем текущую активность.
                }
            }
        } } }



