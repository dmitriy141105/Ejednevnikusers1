package com.example.spisokdelapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment



class SpisokPokypok : AppCompatActivity() {
    private lateinit var buttonhelp: Button
    private lateinit var buttonsort: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var shoppingListView: ListView
    private lateinit var addItemButton: Button
    private lateinit var buttonexit: Button
    private lateinit var adapter: ArrayAdapter<String>
    private val shoppingItems = ArrayList<String>()

    private fun saveShoppingItemsToSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.putStringSet("shoppingItems", HashSet(shoppingItems))
        editor.apply()
    }

    private fun loadShoppingItemsFromSharedPreferences() {
        val savedItems = sharedPreferences.getStringSet("shoppingItems", HashSet())
        if (savedItems != null) {
            shoppingItems.addAll(savedItems)
            adapter.notifyDataSetChanged()
        }
    }
    fun sort_by_store_name(item: String): String {
        return item.substringAfter("Название магазина: ").substringBefore("\n")
    }

    fun sort_by_item_name(item: String): String {
        return item.substringAfter("Название товара: ").substringBefore("\n")
    }

    fun sort_by_quantity(item: String): String {
        return item.substringAfter("Количество: ").substringBefore(" ")
    }

    fun sort_by_price(item: String): String {
        return item.substringAfter("Цена за единицу товара: ").substringBefore(" ")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_notepad)
            buttonsort = findViewById(R.id.sort_button)
            buttonhelp = findViewById(R.id.button2)
            shoppingListView = findViewById(R.id.shopping_list_view)
            addItemButton = findViewById(R.id.add_item_button)
            buttonexit = findViewById(R.id.buttonexit2323)
            adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, shoppingItems)
            shoppingListView.adapter = adapter

            addItemButton.setOnClickListener {
                showAddItemDialog()
            }
            buttonhelp.setOnClickListener {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
            }
            sharedPreferences = getSharedPreferences("ShoppingList", Context.MODE_PRIVATE)
            loadShoppingItemsFromSharedPreferences()

            buttonexit.setOnClickListener{
                val intent = Intent(this, GlavnoeMenu::class.java)
                startActivity(intent)
                finish()
            }
            shoppingListView.setOnItemLongClickListener { parent, view, position, id ->
                showDeleteConfirmationDialog(position)
                true // указывает, что событие обработано
            }
            shoppingListView.setOnItemClickListener { parent, view, position, id ->
                showEditItemDialog(position)
                true // указывает, что событие обработано
            }



        buttonsort.setOnClickListener {
            if (shoppingItems.isEmpty()) {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.apply {
                    setTitle("Ошибка")
                    setMessage("Список покупок пуст. Сортировка невозможна.")
                    setPositiveButton("OK") { dialog, which -> }
                    show()
                } }else {
                val builder = AlertDialog.Builder(this)

                val sortOptions = arrayOf("По названию магазина", "По названию товара", "По количеству товара", "По цене")
                val checkedItems = booleanArrayOf(false, false, false, false)

                builder.setTitle("Выберите параметры сортировку: ")
                builder.setMultiChoiceItems(sortOptions, checkedItems) { _, which, isChecked ->
                    checkedItems[which] = isChecked
                }

                builder.setPositiveButton("Сортировать") { dialog, _ ->
                    shoppingItems.sortWith(compareBy {
                        when {
                            checkedItems[0] -> sort_by_store_name(it)
                            checkedItems[1] -> sort_by_item_name(it)
                            checkedItems[2] -> sort_by_quantity(it)
                            checkedItems[3] -> sort_by_price(it)
                            else -> 0
                        }
                    })

                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                    showToast("Список отсортирован успешно!")
                }

                builder.setNeutralButton("Сброс") { _, _ ->
                    checkedItems.fill(false)
                }

                builder.setNegativeButton("Отмена") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()
            }
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Подтверждение удаления")
        builder.setMessage("Вы действительно хотите удалить эту покупку?")
            .setPositiveButton("Да") { dialog, _ ->
                shoppingItems.removeAt(position)
                adapter.notifyDataSetChanged()
                saveShoppingItemsToSharedPreferences()
                showToast("Покупка удалена!")
            }
            .setNegativeButton("Нет") { dialog, _ -> }
        builder.create().show()
    }

    private fun showEditItemDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.popup_edit_item, null)
        val etEditItem = dialogView.findViewById<EditText>(R.id.et_edit_item)
        etEditItem.setText(shoppingItems[position])

        builder.setView(dialogView)

        builder.setPositiveButton("Отредактировать") { dialog, _ ->
            shoppingItems[position] = etEditItem.text.toString()
            adapter.notifyDataSetChanged()
            saveShoppingItemsToSharedPreferences()
            showToast("Редактирование прошло успешно!")
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showAddItemDialog() {
        val builder = AlertDialog.Builder(this)


        val dialogView = layoutInflater.inflate(R.layout.popup_add_item, null)
        val etStoreName = dialogView.findViewById<EditText>(R.id.et_store_name)
        val etItemName = dialogView.findViewById<EditText>(R.id.et_item_name)
        val etQuantity = dialogView.findViewById<EditText>(R.id.et_quantity)

        // Ваш код для инициализации Spinner
        val spinnerUnit: Spinner = dialogView.findViewById(R.id.spinner_unit)
        val unitAdapter = ArrayAdapter.createFromResource(this@SpisokPokypok, R.array.units_array, android.R.layout.simple_spinner_item)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnit.adapter = unitAdapter

        val etPrice = dialogView.findViewById<EditText>(R.id.et_price)


        builder.setView(dialogView)

        etPrice.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

        etQuantity.inputType = InputType.TYPE_CLASS_NUMBER  or InputType.TYPE_NUMBER_FLAG_DECIMAL
        val spinnerPrice: Spinner = dialogView.findViewById(R.id.spinner_currency)
        var priceAdapter = ArrayAdapter.createFromResource(this@SpisokPokypok, R.array.price_array, android.R.layout.simple_spinner_item)
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPrice.adapter = priceAdapter



        builder.setPositiveButton("Добавить") { dialog, which ->
            val storeName = etStoreName.text.toString()
            val itemName = etItemName.text.toString()
            val quantity = etQuantity.text.toString()
            val unit = spinnerUnit.selectedItem.toString()
            val price = etPrice.text.toString()
            val valuta = spinnerPrice.selectedItem.toString()

            if (storeName.isNotEmpty() && itemName.isNotEmpty() && quantity.isNotEmpty() && unit.isNotEmpty() && price.isNotEmpty()) {


                var newItem = "Название магазина: $storeName\n" + "Название товара: $itemName\n" + "Количество: $quantity $unit\n" +  "Цена за единицу товара: $price $valuta"

                shoppingItems.add(newItem)
                adapter.notifyDataSetChanged()
                saveShoppingItemsToSharedPreferences()
            } else {
                showToast("Заполните все поля")
            }
        }
        builder.setNegativeButton("Отмена") { dialog, which -> }

        builder.show()
    }

}
