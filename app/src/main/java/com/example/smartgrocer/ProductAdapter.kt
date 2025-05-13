package com.example.smartgrocer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocer.data.Product

class ProductAdapter(private var productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var filteredList: List<Product> = productList

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textViewName)
        val category: TextView = itemView.findViewById(R.id.textViewCategory)
        val price: TextView = itemView.findViewById(R.id.textViewPrice)
        val availability: TextView = itemView.findViewById(R.id.textViewAvailability)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = filteredList[position]
        holder.name.text = product.name
        holder.category.text = product.category
        holder.price.text = "R %.2f".format(product.price)
        holder.availability.text = if (product.inStock) "In Stock" else "Out of Stock"
        holder.availability.setTextColor(
            if (product.inStock) 0xFF00796B.toInt() else 0xFFD32F2F.toInt()
        )
    }

    override fun getItemCount(): Int = filteredList.size

    fun updateList(newList: List<Product>) {
        productList = newList
        filteredList = newList
        notifyDataSetChanged()
    }
}
