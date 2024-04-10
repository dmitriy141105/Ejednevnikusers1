package com.example.spisokdelapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class SobytieActivity : AppCompatActivity() {
    private lateinit var sortButton: Button
    private lateinit var addEventButton: Button
    private lateinit var buttonexit: Button
    private lateinit var eventsListView: ListView
    private lateinit var events: MutableList<Pair<String, Date>>
    private lateinit var adapter: ArrayAdapter<String>
    private val sharedPreferencesKey = "MY_EVENTS"


    private fun updateListView() {
        adapter.clear()
        adapter.addAll(events.map { buildEventString(it.first, it.second, "") })
        adapter.notifyDataSetChanged()
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort_by_date -> sortEventsByDate()
            R.id.action_sort_by_name -> sortEventsByName()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun sortEventsByDate() {
        events.sortBy { it.second } // Сортировка списка событий по дате
        updateListView()
    }
    private fun sortEventsByName() {
        events.sortBy { it.first } // Сортировка списка событий по имени
        updateListView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sobytie)
        buttonexit = findViewById(R.id.buttonexit11)
        addEventButton = findViewById(R.id.add_event_button)
        eventsListView = findViewById(R.id.events_list_view)
        events = mutableListOf()
        adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, events.map { "${it.second}" })
        eventsListView.adapter = adapter
        sortButton = findViewById(R.id.sort12)





        sortButton.setOnClickListener {
            if (events.isEmpty()) {
                Toast.makeText(
                    this,
                    "Список событий пуст. Сортировка невозможна.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showSortDialog()
            }
        }

        addEventButton.setOnClickListener {
            showAddEventDialog()
        }
        buttonexit.setOnClickListener {
            val intent = Intent(this, GlavnoeMenu::class.java)
            startActivity(intent)
            finish()
        }
        fun removeEvent(position: Int) {
            events.removeAt(position)
            updateListView()
        }

        eventsListView.setOnItemLongClickListener { parent, view, position, id ->
            val selectedEvent = events[position]
            val eventDate = selectedEvent.second

            // Удаление события при длительном нажатии
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Подтверждение удаления")
            alertDialogBuilder.setMessage("Вы уверены, что хотите удалить данное событие?")
            alertDialogBuilder.setPositiveButton("Да") { dialog, which ->
                removeEvent(position)
                showToast("Событие успешно удалено!")
            }
            alertDialogBuilder.setNegativeButton("Отмена", null)
            alertDialogBuilder.show()

            true // Возвращаем true, чтобы сообщить, что событие обработано
        }


    }

        private fun showSortDialog() {
        val checkedItems = booleanArrayOf(false, false)
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Выбери варианты сортировки")
            .setMultiChoiceItems(arrayOf("По дате", "По названию"), checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("OK") { _, _ ->
                if (checkedItems[0]) sortEventsByDate()
                if (checkedItems[1]) sortEventsByName()
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
        dialogBuilder.create().show()
    }
    private fun showDatePickerDialog(dateEditText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateString = sdf.format(selectedDate.time)

            dateEditText.setText(dateString)
        }, year, month, day)

        datePickerDialog.show()
    }
    private fun showAddEventDialog() {
        val dialogView = layoutInflater.inflate(R.layout.popup_add_event, null)
        val eventNameEditText = dialogView.findViewById<EditText>(R.id.event_name_edittext)
        val startDateEditText = dialogView.findViewById<EditText>(R.id.start_date_edittext)
        val endDateEditText = dialogView.findViewById<EditText>(R.id.end_date_edittext)

        // Добавление обработчика для открытия DatePickerDialog при нажатии на поле даты
        startDateEditText.setOnClickListener {
            showDatePickerDialog(startDateEditText)
        }
        endDateEditText.setOnClickListener {
            showDatePickerDialog(endDateEditText)
        }
        val prioritySpinner = dialogView.findViewById<Spinner>(R.id.priority_spinner)
        val priorities = arrayOf("Не выбрано", "Низкий", "Средний", "Высокий")
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        prioritySpinner.adapter = priorityAdapter

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setPositiveButton("Добавить") { _, _ ->
            val eventName = eventNameEditText.text.toString()
            val eventDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(startDateEditText.text.toString())
            val eventEndDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(endDateEditText.text.toString())
            val eventPriority = prioritySpinner.selectedItem.toString()
            addEvent(eventName, eventDate, eventEndDate, eventPriority)
        }

        builder.setNegativeButton("Отмена") { dialog, which -> }
        val dialog = builder.create()
        dialog.show()
    }



    private fun addEvent(eventName: String, eventDate: Date, eventEndDate: Date, eventPriority: String) {
        val formattedEvent = buildEventString(eventName, eventDate, eventPriority)
        events.add(Pair(formattedEvent, eventDate))

        adapter.clear()
        adapter.addAll(events.map { buildEventString(it.first, it.second, eventPriority) })
        adapter.notifyDataSetChanged()
    }


    private fun removeEvent(position: Int) {
        events.removeAt(position)
        showToast("Событие успешно удалено!")
        adapter.clear()
        adapter.addAll(events.map { buildEventString(it.first, it.second, "") })
        adapter.notifyDataSetChanged()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun editEvent(position: Int, eventName: String, eventDate: Date, eventPriority: String) {
        val formattedEvent = buildEventString(eventName, eventDate, eventPriority)
        events[position] = Pair(formattedEvent, eventDate)

        adapter.clear()
        adapter.addAll(events.map { buildEventString(it.first, it.second, "") })
        adapter.notifyDataSetChanged()

        // Show a toast message to indicate that the event has been edited
        showToast("Event edited successfully")
    }


    private fun buildEventString(eventName: String, eventDate: Date, eventPriority: String): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedStartDate = sdf.format(eventDate)

        // Форматирование строки, каждый элемент на новой строке
        return "Название события: $eventName\nПриоритет: $eventPriority\nДата начала: $formattedStartDate\nДата завершения: "
    }

}
