package com.example.smartgrocer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.smartgrocer.data.Product

class AddProductActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var switchAvailability: SwitchCompat
    private lateinit var buttonAddProduct: Button
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        editTextName = findViewById(R.id.editTextName)
        editTextPrice = findViewById(R.id.editTextPrice)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        switchAvailability = findViewById(R.id.switchAvailability)
        buttonAddProduct = findViewById(R.id.buttonAddProduct)

        databaseRef = FirebaseDatabase.getInstance().getReference("products")

        setupCategorySpinner() // Call this method to set up the category spinner

        // Set up the click listener for the "Add Product" button
        buttonAddProduct.setOnClickListener {
            Log.d("AddProduct", "Button clicked") // Log the button click
            addProductToFirebase()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.product_categories)
        //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        // Use a custom ArrayAdapter with disabled items for the dropdown
        val adapter = object : ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, categories
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item ("Select Category")
                return position != 0
            }

            // Override the getDropDownView method to set the disabled item color
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                // Grey out the disabled item in the dropdown
                if (position == 0) {
                    (view as TextView).setTextColor(Color.GRAY)
                } else {
                    (view as TextView).setTextColor(Color.BLACK)
                }
                return view
            }
        }

        // Set the dropdown resource for the custom adapter
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun addProductToFirebase() {
        val name = editTextName.text.toString().trim()
        val priceText = editTextPrice.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()
        val inStock = switchAvailability.isChecked

        // Validate inputs before adding to Firebase
        if (name.isEmpty()) {
            editTextName.error = "Enter product name"
            return
        }

        if (priceText.isEmpty()) {
            editTextPrice.error = "Enter product price"
            return
        }

        val price = priceText.toDoubleOrNull()
        if (price == null || price < 0) {
            editTextPrice.error = "Enter a valid positive price"
            return
        }
        val database = FirebaseDatabase.getInstance()
        val productRef = database.getReference("products")
        val product = Product(id = productRef.push().key ?: "", name, price, category, inStock) // Create a Product object

        // Add the product to Firebase
        Log.d("AddProduct", "Attempting to add: $name, $price, $category, $inStock")
        productRef.child(product.id).setValue(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
                clearInputs()
            }
            .addOnFailureListener { e ->
                Log.e("AddProduct", "Firebase failed: ${e.message}")
                Toast.makeText(this, "Failed to add: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun clearInputs() {
        editTextName.text.clear()
        editTextPrice.text.clear()
        spinnerCategory.setSelection(0)
        switchAvailability.isChecked = true
    }
}
