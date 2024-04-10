package com.example.spisokdelapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast


// Класс для авторизации пользователя
class AuthActivity : AppCompatActivity() {

    // Объявление переменных
    private lateinit var userLogin: EditText
    private lateinit var userpass: EditText
    private lateinit var sharedPreferences: SharedPreferences

    // Метод onCreate, вызывается при создании Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("AuthPrefs", MODE_PRIVATE) // Initialize sharedPreferences

        val savedLogin1 = sharedPreferences.getString("email", null)
        val savedPass1 = sharedPreferences.getString("password", null)

        if (savedLogin1 != null && savedPass1 != null) {
            // Если данные о пользователе сохранены, переходим к GlavnoeMenu
            val mainActivityIntent = Intent(this, GlavnoeMenu::class.java)
            startActivity(mainActivityIntent)
            finish() // Закрываем текущую Activity, чтобы пользователь не мог вернуться на экран авторизации
        } else {
            setContentView(R.layout.activity_auth) // Устанавливаем макет для этой Activity
        }


            sharedPreferences = getSharedPreferences(
                "AuthPrefs",
                MODE_PRIVATE
            ) // Получаем доступ к SharedPreferences

            // Находим View элементы по их id
            userLogin = findViewById(R.id.user_login_auth)
            userpass = findViewById(R.id.user_pass_auth)
            val button: Button = findViewById(R.id.button_auth)
            val linkToReg: TextView = findViewById(R.id.link_to_reg)

            // Обработчик клика на текстовой ссылке для перехода на активити регистрации
            linkToReg.setOnClickListener() {
                val intent = Intent(this, Registration::class.java)
                startActivity(intent)
            }

            // Автозаполнение данных при выходе из аккаунта
            val savedLogin = sharedPreferences.getString("login", "")
            val savedPass = sharedPreferences.getString("pass", "")
            userLogin.setText(savedLogin)
            userpass.setText(savedPass)


            // Обработчик клика на кнопке "Войти"
            button.setOnClickListener {
                val login = userLogin.text.toString().trim()
                val pass = userpass.text.toString().trim()
                val editor = sharedPreferences.edit()
                editor.putString("email", login)
                editor.putString("password", pass)
                editor.apply()

                if (login == "" || pass == "") {
                    Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
                } else {
                    val db = DbHelper(this, null)
                    val isAuth = db.getUser(login, pass)

                    if (login == "admin" && pass == "admin") {
                        Toast.makeText(this, "C возвращением, администратор!", Toast.LENGTH_LONG)
                            .show()

                        // Очищаем поля ввода
                        userLogin.text.clear()
                        userpass.text.clear()

                        // Сохраняем логин и пароль в SharedPreferences для автозаполнения
                        val editor = sharedPreferences.edit()
                        editor.putString("login", login)
                        editor.putString("pass", pass)
                        editor.apply()

                        // Создаем Intent для перехода на MainActivity
                        val mainActivityIntent = Intent(this, GlavnoeMenu::class.java)

                        // Запускаем MainActivity
                        startActivity(mainActivityIntent)
                        finish() // Закрываем текущую Activity
                    } else if (isAuth) {
                        Toast.makeText(this, "Пользователь $login авторизован", Toast.LENGTH_LONG)
                            .show()

                        // Очищаем поля ввода
                        userLogin.text.clear()
                        userpass.text.clear()

                        // Сохраняем логин и пароль в SharedPreferences для автозаполнения
                        val editor = sharedPreferences.edit()
                        editor.putString("login", login)
                        editor.putString("pass", pass)
                        editor.apply()

                        // Создаем Intent для перехода на MainActivity
                        val mainActivityIntent = Intent(this, GlavnoeMenu::class.java)

                        // Запускаем MainActivity
                        startActivity(mainActivityIntent)
                        finish() // Закрываем текущую Activity
                    } else {
                        Toast.makeText(
                            this,
                            "Ошибка, неправильный логин или пароль",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
