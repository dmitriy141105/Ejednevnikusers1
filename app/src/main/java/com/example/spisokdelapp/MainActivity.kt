package com.example.spisokdelapp

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.SharedPreferences
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log

import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import android.widget.Spinner

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import kotlin.random.Random
private lateinit var constraintLayout: ConstraintLayout
private lateinit var button: Button
private const val prefsName = "SpisokDelPrefs"
private const val todosKey = "TodosList"
private const val accountIdKey = "AccountId"

class MainActivity : AppCompatActivity() {
    private val todos = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var sharedPreferences: SharedPreferences
    private var accountId: String? = null

    private  val CHANNEL_ID = "my_channel_id"
    private val notificationReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            "com.yourapp.CUSTOM_ACTION_ENABLE_NOTIFICATIONS" -> {
                // Enable notifications
                val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                createNotificationChannel(notificationManager)
                sendNotification(notificationManager, "Notification", "Notifications successfully enabled")
            }
            "com.yourapp.CUSTOM_ACTION_DISABLE_NOTIFICATIONS" -> {
                // Disable notifications
                val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }
        }
    } }



    fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My App Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Channel for My App notifications"
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(notificationManager: NotificationManager, title: String, message: String) {
        val notificationId = Random.nextInt() // Генерируем уникальный ID уведомления
        val contentIntent = Intent(this, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val serviceIntent = Intent(this, NotificationService::class.java)
        startService(serviceIntent)

        val filter = IntentFilter().apply {
            addAction("com.yourapp.CUSTOM_ACTION_ENABLE_NOTIFICATIONS")
            addAction("com.yourapp.CUSTOM_ACTION_DISABLE_NOTIFICATIONS")
        }
        registerReceiver(notificationReceiver, filter)

        sharedPreferences = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        accountId = sharedPreferences.getString(accountIdKey, null)
        constraintLayout = findViewById(R.id.yourConstraintLayoutId)
        button = findViewById(R.id.button)
        val ButtonHelp: Button = findViewById(R.id.buttonHelp)
        val listView: ListView = findViewById(R.id.listView)
        val buttonSort: Button = findViewById(R.id.buttonSort)
        val addButton: Button = findViewById(R.id.button)
        val buttonexit: Button = findViewById(R.id.buttonexit)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, todos)
        listView.adapter = adapter

        loadTodos()

        // Sort the list alphabetically after loading the todos
        todos.sort()
        adapter.notifyDataSetChanged()


        ButtonHelp.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }



        val defaultUEH: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        // Function to display a toast
        fun showToast(message: String, context: Context) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // Handle the exception
            Log.e("Непредвиденная ошибка", "Ошибка: ${throwable.message}", throwable)
            val message = "Произошла непредвиденная ошибка: ${throwable.message}"
            // Replace with the appropriate way of displaying the error message to the user
            showToast(message, applicationContext)
            // Call the default exception handler
            defaultUEH.uncaughtException(thread, throwable)
        }


        val sortDirections = booleanArrayOf(true, true, true, true, true, true) // Массив для отслеживания направления сортировки

        buttonSort.setOnClickListener {
            if (todos.isEmpty()) {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.apply {
                    setTitle("Ошибка")
                    setMessage("Список дел пуст.Сортировка невозможна.")
                    setPositiveButton("OK") { dialog, which -> }
                    show()
                }
            } else {
                val options = arrayOf("По названию", "По началу", "По завершению", "По категории", "По приоритету", "По времени добавления")
                val checkedItems = booleanArrayOf(false, false, false, false, false, false) // Массив для отслеживания выбранных элементов

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Выберите параметры сортировки")
                builder.setMultiChoiceItems(options, checkedItems) { dialog, which, isChecked ->
                    checkedItems[which] = isChecked
                }

                builder.setPositiveButton("Сортировать") { dialog, which ->
                    todos.sortWith(compareBy(
                        { if (checkedItems[0]) it.substringAfter("Название дела: ").substringBefore("\n") else "" },
                        { if (checkedItems[1]) it.substringAfter("Начало: ").substringBefore("\n") else "" },
                        { if (checkedItems[2]) it.substringAfter("Завершение: ").substringBefore("\n") else "" },
                        { if (checkedItems[3]) it.substringAfter("Категория: ").substringBefore("\n") else "" },
                        { if (checkedItems[4]) it.substringAfter("Приоритетность: ").substringBefore("\n") else "" },
                        { if (checkedItems[5]) it.substringAfter("Добавлено: ").substringBefore("\n") else "" }
                    ))
                    adapter.notifyDataSetChanged()
                }

                builder.setNeutralButton("Сброс") { dialog, which ->
                    todos.sortBy { it } // Сбрасываем сортировку
                    adapter.notifyDataSetChanged()
                }
                builder.setNegativeButton("Отмена", null)

                builder.show()
            }
        }

        fun removeTodo(position: Int) {
            val text = adapter.getItem(position)
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setTitle("Подтверждение удаления")
                setMessage("Вы действительно хотите удалить дело?")
                setPositiveButton("Да") { dialog, which ->
                    adapter.remove(text)
                    Toast.makeText(this@MainActivity, "Мы удалили: $text", Toast.LENGTH_LONG).show()
                    sortTodos()
                    saveTodos()
                }
                setNegativeButton("Отмена") { dialog, which ->
                    dialog.dismiss()
                }
            }
            val dialog = builder.create()
            dialog.show()
        }


        listView.setOnItemLongClickListener { _, view, position, _ ->
           removeTodo(position)
            true // указывает, что событие обработано
        }

        buttonexit.setOnClickListener{
            val intent = Intent(this, GlavnoeMenu::class.java)
            startActivity(intent)
            finish()
        }


        createNotificationChannel(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        val startDate =
        addButton.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_todo, null)

            val titleEditText = dialogView.findViewById<EditText>(R.id.editTextTitle)
            val descriptionEditText = dialogView.findViewById<EditText>(R.id.editTextDescription)
            val startDateEditText = dialogView.findViewById<EditText>(R.id.editTextStartDate)
            val endDateEditText = dialogView.findViewById<EditText>(R.id.editTextEndDate)
            val prioritySpinner = dialogView.findViewById<Spinner>(R.id.spinnerPriority)
            val priorities = arrayOf("Не выбрано","Низкая", "Средняя", "Высокая")
            val categorySpinner = dialogView.findViewById<Spinner>(R.id.categorySpinner)
            val categories = arrayOf("Не выбрано","Работа", "Учеба", "Семья", "Домашние дела", "Здоровье", "Личное развитие", "Отдых", "Финансы", "Другое")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            prioritySpinner.adapter = adapter

// Создаем адаптеры для prioritySpinner и categorySpinner
            val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
            priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            prioritySpinner.adapter = priorityAdapter

            val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = categoryAdapter



            fun isEndDateValid(year: Int, month: Int, day: Int, showErrorToast: Boolean = true): Boolean {
                val startDate = startDateEditText.text.toString()
                val endDate = "$day/${month + 1}/$year"

                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val startDateDate = formatter.parse(startDate)
                val endDateDate = formatter.parse(endDate)

                val startDateAfterEndDate = startDateDate.after(endDateDate)
                val endDateBeforeToday = Calendar.getInstance().apply { set(year, month, day) }.before(Calendar.getInstance())

                if (startDateAfterEndDate && showErrorToast) {


                    // Очистка поля ввода начальной даты
                    startDateEditText.text.clear()
                    endDateEditText.text.clear()
                    // Блокировка поля ввода начальной даты
                    startDateEditText.isEnabled = false
                    endDateEditText.isEnabled = false
                    AlertDialog.Builder(this)
                        .setTitle("Ошибка")
                        .setMessage("Начальная дата не может быть позже конечной.")
                        .setPositiveButton("OK") { _, _ -> }
                        .setOnDismissListener {
                            startDateEditText.isEnabled = true // Разблокировка поля при закрытии диалога
                            endDateEditText.isEnabled = true
                        }

                        .show()
                }


                return !startDateAfterEndDate && !endDateBeforeToday
            }

             fun isEndDateAfterStartDate(year: Int, month: Int, day: Int): Boolean {
                val startDate =startDateEditText.text.toString()// Получите начальную дату из поля начальной даты
                val startCalendar = Calendar.getInstance()
                // Установите начальную дату в календарь startCalendar

                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, day)

                return selectedCalendar >= startCalendar
            }

            startDateEditText.setOnClickListener {
                val calendar = Calendar.getInstance()

                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, monthOfYear, dayOfMonth)

                    if (selectedCalendar.after(calendar) || DateUtils.isToday(selectedCalendar.timeInMillis)) {
                        val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                        startDateEditText.setText(selectedDate)
                    } else {
                        if (DateUtils.isToday(selectedCalendar.timeInMillis)) {
                            // Handle today's date (optional, can be left empty if no specific action is required)
                        } else {
                            val alertDialogBuilder = AlertDialog.Builder(this)
                            alertDialogBuilder.apply {
                                setMessage("Выберите дату в будущем")
                                setPositiveButton("OK") { dialog, _ ->
                                    // Optionally, you can clear the input here
                                    startDateEditText.setText("")
                                    dialog.dismiss()
                                }
                                show()
                            }
                        }
                    }
                }, year, month, day)


                datePickerDialog.show()
            }



// Обработчик нажатия на поле конечной даты
            endDateEditText.setOnClickListener {

                if (startDateEditText.text.toString().isEmpty()) {
                    Toast.makeText(this, "Необходимо сначала ввести начальную дату", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"

                    if (isEndDateValid(year, monthOfYear, dayOfMonth) && isEndDateAfterStartDate(year, monthOfYear, dayOfMonth)) {
                        endDateEditText.setText(selectedDate)
                    }
                }, year, month, day)

                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000 // устанавливаем минимальную дату равной текущей дате
                datePickerDialog.show()
            }






// ...

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setView(dialogView)


            alertDialogBuilder.setPositiveButton("Добавить") { dialog, which ->
                val title = titleEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                val startDate = startDateEditText.text.toString().trim()
                val endDate = endDateEditText.text.toString().trim()
                val selectedCategory = categorySpinner.selectedItem.toString()

                val priority = prioritySpinner.selectedItem.toString()

                if (title.isNotEmpty()) {
                    val currentTime = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(
                        Date()
                    )
                    val newTodo = """
    |Название дела: $title
    |Описание: $description
    |Начало: $startDate
    |Завершение: $endDate
    |Категория: $selectedCategory
    |Приоритетность: $priority
    |Время добавления: $currentTime
    """.trimMargin() + "\n"

                    todos.add(0, newTodo)
                    sortTodos()
                    adapter.notifyDataSetChanged()
                    saveTodos()

                    // Проверяем разницу между текущей датой и конечной датой
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val currentDate = Calendar.getInstance()
                    val endDateCalendar = Calendar.getInstance()
                    endDateCalendar.time = sdf.parse(startDate)

                    val diffInMillies = endDateCalendar.timeInMillis - currentDate.timeInMillis
                    val diffInDays = ((diffInMillies / (1000 * 60 * 60 * 24))+1).toInt()
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    if (diffInDays in 10..30) {
                        // Разрешение на отправку уведомлений дважды в день
                        for (i in 1..2) {
                            val message = "У вас запланировано дело через $diffInDays дня(дней). Не забудьте подготовиться!"
                            sendNotification(notificationManager, "Дело: $title", message)
                        } }
                   if (diffInDays in 5..9) {
                        // Разрешение на отправку уведомлений трижды в день
                        for (i in 1..3) {
                            val message = "У вас запланировано дело через $diffInDays дня(дней). Не забудьте подготовиться!"
                            sendNotification(notificationManager, "Дело: $title", message)
                        }
                    }
                    if (diffInDays in 0..4) {
                       // Разрешение на отправку уведомлений пять раз в день
                       for (i in 1..4) {
                           val message = if (diffInDays == 0) {
                               "У вас на сегодня запланировано дело. Не забудьте подготовиться!"
                           } else {
                               "У вас запланировано дело через $diffInDays дня(дней). Не забудьте подготовиться!"
                           } }}
                } else {
                    Toast.makeText(this, "Пожалуйста, введите название дела", Toast.LENGTH_LONG).show()
                }

                adapter.notifyDataSetChanged()



                // Clear the input fields
                titleEditText.text.clear()
                descriptionEditText.text.clear()
                startDateEditText.text.clear()
                endDateEditText.text.clear()
                categorySpinner.setSelection(0)



                dialog.dismiss()
            }
            alertDialogBuilder.setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }

            alertDialogBuilder.create().show()
        }



        listView.setOnItemClickListener { _, _, position, _ ->
            editTodo(position)
            true
        }


    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationReceiver)
    }

    private fun addTodo(userData: EditText) {
        val text = userData.text.toString().trim()
        if (text.isNotEmpty()) {
            if (todos.contains(text)) {
                showAlreadyExistsDialog(text, userData)
            } else {
                todos.add(0, text) // Add to the list
                sortTodos()
                userData.text.clear()
                saveTodos()
            }
        } else {
            Toast.makeText(this, "Пожалуйста, введите название дела", Toast.LENGTH_LONG).show()
        }
    }



    private fun editTodo(position: Int) {
        val currentText = adapter.getItem(position)
        val editTextBox = EditText(this).apply {
            setText(currentText)
        }
        AlertDialog.Builder(this@MainActivity).apply {
            setTitle("Изменить дело")
            setMessage("Введите новое название для выбранного дела.")
            setView(editTextBox)
            setPositiveButton("Сохранить") { dialog, _ ->
                val newText = editTextBox.text.toString()
                if (newText.isNotEmpty()) {
                    todos[position] = newText
                    sortTodos()
                    saveTodos()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Необходимо ввести название дела!",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                }
            }
            setNegativeButton("Отмена", null)
            show()
        }
    }

    private fun showAlreadyExistsDialog(text: String, userData: EditText) {
        AlertDialog.Builder(this).apply {
            setTitle("Элемент уже существует")
            setMessage("Дело \"$text\" уже есть в списке. Добавить ещё одно?")
            setPositiveButton("Да") { dialog, _ ->
                todos.add(0, text)
                sortTodos()
                userData.text.clear()
                dialog.dismiss()
                saveTodos()
            }
            setNegativeButton("Нет") { dialog, _ ->
                userData.text.clear()
                dialog.dismiss()
            }
            show()
        }
    }

    private fun sortTodos() {
        todos.sort() // Sort the todos list
        adapter.notifyDataSetChanged() // Notify the adapter of the data change
    }



    private fun saveTodos() {
        val editor = sharedPreferences.edit()
        editor.putStringSet(todosKey, todos.toSet())
        editor.putString(accountIdKey, accountId)
        editor.apply()
    }

    private fun loadTodos() {
        val savedTodos = sharedPreferences.getStringSet(todosKey, null)
        savedTodos?.let {
            todos.addAll(it)
        }
    }



}