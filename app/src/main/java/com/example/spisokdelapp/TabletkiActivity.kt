package com.example.spisokdelapp

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TabletkiActivity : AppCompatActivity() {

    private lateinit var buttonexit: Button
    private lateinit var buttonsort: Button
    private lateinit var buttonhelp: Button
    private lateinit var btnAddMedication: Button
    private lateinit var listViewMedications: ListView
    private lateinit var medications: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>







    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_tablets)
            buttonsort = findViewById(R.id.buttonSortMedications)
            buttonexit = findViewById(R.id.buttonexit1112)
            buttonhelp = findViewById(R.id.buttonHelp111112)
            btnAddMedication = findViewById(R.id.btnAddMedication)
            listViewMedications = findViewById(R.id.listViewMedications)

            medications = ArrayList()
            adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, medications)
            listViewMedications.adapter = adapter

            loadMedicationsFromSharedPreferences()

            buttonexit.setOnClickListener {
                val intent = Intent(this, GlavnoeMenu::class.java)
                startActivity(intent)
                finish()
            }

            btnAddMedication.setOnClickListener {
                showAddMedicationDialog()
            }

            buttonhelp.setOnClickListener {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
            }


            buttonsort.setOnClickListener {
             
            }


            listViewMedications.setOnItemClickListener { parent, view, position, id ->


            }




            listViewMedications.setOnItemLongClickListener { parent, view, position, id ->
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Подтверждение удаления")
                alertDialogBuilder.setMessage("Вы уверены, что хотите удалить этот элемент?")

                alertDialogBuilder.setPositiveButton("Да") { dialog, which ->
                    medications.removeAt(position)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Лекарство успешно удалено!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }

                alertDialogBuilder.setNegativeButton("Нет") { dialog, which ->
                    dialog.dismiss()
                }

                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()

                true  // верните true, чтобы показать, что событие обработано
            }

        }catch (e: Exception) {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.apply {
                    setTitle("Ошибка")
                    setMessage("Произошла ошибка: ${e.message}")
                    setPositiveButton("OK") { dialog, which -> }
                    show()
                }
                e.printStackTrace()
            }
        }


    override fun onPause() {
        super.onPause()
        saveMedicationsToSharedPreferences()
    }

    private fun saveMedicationsToSharedPreferences() {
        val sharedPreferences = getSharedPreferences("Medications", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("medications", medications.toSet())
        editor.apply()
    }





    private fun loadMedicationsFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences("Medications", Context.MODE_PRIVATE)
        val savedMedications = sharedPreferences.getStringSet("medications", null)
        if (savedMedications != null) {
            medications.clear()
            medications.addAll(savedMedications)
            adapter.notifyDataSetChanged()
        }
    }



    private fun showDatePickerDialog(editText: EditText) {
        val datePickerDialog = DatePickerDialog(this)
        datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = dateFormat.format(calendar.time)

            editText.setText(selectedDate)
        }
        datePickerDialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showAddMedicationDialog() {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_medication, null)
        val etMedicationName = dialogView.findViewById<EditText>(R.id.etMedicationName)
        val etDosage = dialogView.findViewById<EditText>(R.id.etDosage)
        val etStartDate = dialogView.findViewById<EditText>(R.id.etStartDate)
        val etEndDate = dialogView.findViewById<EditText>(R.id.etEndDate)
        val etFrequencyPerDay = dialogView.findViewById<EditText>(R.id.etFrequencyPerDay)
        etDosage.inputType = InputType.TYPE_CLASS_NUMBER
        etFrequencyPerDay.inputType = InputType.TYPE_CLASS_NUMBER

        val spinnerMedicationType = dialogView.findViewById<Spinner>(R.id.spinnerMedicationType)

        val spinnerDosageUnit = dialogView.findViewById<Spinner>(R.id.spinnerDosageUnit)
        val spinnerTimeOfIntake = dialogView.findViewById<Spinner>(R.id.spinnerTimeOfIntake)

        val dosageUnitOptions = arrayOf("Не выбрано", "мг", "г", "мл", "л", "шт", "уп", "доз", "капель", "таблеток", "капсул", "ампул", "млн. МЕ", "грамм-эквивалентов",  "ед.", "мг/кг", "мг/мл")
        val timeOfIntakeOptions = arrayOf("Не выбрано","С утра", "Днем", "Вечером", "По расписанию")

        val adapterDosageUnit = ArrayAdapter(this, android.R.layout.simple_spinner_item, dosageUnitOptions)
        adapterDosageUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDosageUnit.adapter = adapterDosageUnit

        val adapterTimeOfIntake = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeOfIntakeOptions)
        adapterTimeOfIntake.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTimeOfIntake.adapter = adapterTimeOfIntake
        // Создание адаптера для спиннера
        val medicationTypeOptions = arrayOf("Не выбрано", "1. Таблетки\n", "2. Капсулы\n" , "3. Сиропы\n" , "4. Растворы\n" , "5. Ампулы\n" , "6. Суппозитории\n" , "7. Кремы\n" , "8. Гели\n" , "9. Пластыри\n" , "10. Спреи\n", "11. Порошки\n" , "12. Глазные капли\n" , "13. Ушные капли\n" , "14. Ингаляторы\n" , "15. Драже\n" , "16. Крем-гель\n", "17. Мази\n", "18. Линименты\n" , "19. Эмульсии\n" , "20. Гранулы")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, medicationTypeOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMedicationType.adapter = adapter

        etStartDate.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showDatePickerDialog(v as EditText)
            }
        }

        etEndDate.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showDatePickerDialog(v as EditText)
            }
        }
        etStartDate.keyListener = null
        etEndDate.keyListener = null

        dialogBuilder.setView(dialogView)
            .setPositiveButton("Добавить") { dialog, which ->
                val medicationName = etMedicationName.text.toString()
                val dosage = etDosage.text.toString()
                val medicationType = spinnerMedicationType.selectedItem.toString()
                val startDate = etStartDate.text.toString()
                val endDate = etEndDate.text.toString()
                val frequencyPerDay = etFrequencyPerDay.text.toString()
                val dosageUnit = spinnerDosageUnit.selectedItem.toString()
                val timeOfIntake = spinnerTimeOfIntake.selectedItem.toString()

                if (medicationName.isEmpty() || dosage.isEmpty() || medicationType.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || frequencyPerDay.isEmpty() || dosageUnit.isEmpty() || timeOfIntake.isEmpty()) {
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.apply {
                        setTitle("Ошибка")
                        setMessage("Пожалуйста, заполните все поля")
                        setPositiveButton("OK") { dialog, which -> }
                        show()
                    }
                } else {
                    // Проверка на корректность даты
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val startDateDate = sdf.parse(startDate)
                    val endDateDate = sdf.parse(endDate)

                    if (startDateDate != null && endDateDate != null && (startDateDate.after(endDateDate) || endDateDate.before(startDateDate))) {
                        val alertDialogBuilder = AlertDialog.Builder(this)
                        alertDialogBuilder.apply {
                            setTitle("Ошибка в датах")
                            setMessage("Проверьте правильность указанных дат")
                            setPositiveButton("OK") { dialog, which -> }
                            show()
                        }
                    } else {
                        val medicationInfo = "Название: $medicationName\nДозировка: $dosage $dosageUnit\nТип: $medicationType\nНачало: $startDate\nКонец: $endDate\nЧастота приема: $frequencyPerDay раз(а) в день\nВремя приема: $timeOfIntake"

                        medications.add(medicationInfo)
                        adapter.notifyDataSetChanged()
                        showToast("Лекарство добавлено успешно!")
                        adapter.notifyDataSetChanged()

                    }

                }

                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }

        val dialog = dialogBuilder.create()
        dialog.show()
    }





}


