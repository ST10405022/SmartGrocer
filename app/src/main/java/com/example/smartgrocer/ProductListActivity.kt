package com.example.smartgrocer

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.Spinner
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocer.data.Product
import com.google.firebase.database.*

class ProductListActivity : AppCompatActivity() {

    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var spinnerCategoryFilter: Spinner
    private lateinit var productAdapter: ProductAdapter
    private val productList = mutableListOf<Product>()

    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        recyclerViewProducts = findViewById(R.id.recyclerViewProducts)
        spinnerCategoryFilter = findViewById(R.id.spinnerCategory)

        recyclerViewProducts.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter(emptyList())
        recyclerViewProducts.adapter = productAdapter

        databaseRef = FirebaseDatabase.getInstance().getReference("products")

        setupCategorySpinner()
        loadProductsFromFirebase()
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.product_categories)

        val spinnerAdapter = object : ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, categories
        ) {
            override fun isEnabled(position: Int): Boolean {
                // Disable the first item (e.g. "Select Category")
                return position != 0
            }

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

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoryFilter.adapter = spinnerAdapter

        spinnerCategoryFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedCategory = categories[position]
                when {
                    selectedCategory == "All" -> loadProductsFromFirebase()
                    selectedCategory != "Select Category" -> {
                        filterByCategory(selectedCategory)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun loadProductsFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (childSnapshot in snapshot.children) {
                    val product = childSnapshot.getValue(Product::class.java)
                    Log.d("FIREBASE", "Snapshot value: ${childSnapshot.value}")
                    Log.d("FIREBASE", "Parsed product: $product")
                    product?.let { productList.add(it) }
                }
                productAdapter.updateList(productList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
                error.toException().printStackTrace()
            }
        })
    }

    private fun filterByCategory(category: String) {
        val filteredList = if (category == "All" ||
            category.isEmpty() || category == "Select Category") {
            productList
        } else {
            productList.filter { it.category.equals(category, ignoreCase = true) }
        }
        productAdapter.updateList(filteredList)
    }
}
