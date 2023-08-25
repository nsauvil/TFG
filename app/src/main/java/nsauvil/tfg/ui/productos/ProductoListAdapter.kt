package nsauvil.tfg.ui.productos


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import nsauvil.tfg.R
import nsauvil.tfg.databinding.ProductoItemBinding
import nsauvil.tfg.ui.domain.model.Producto

class ProductoListAdapter() : ListAdapter<Producto, ProductoListAdapter.viewHolder>(ProductoDiff){

    object ProductoDiff : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem
        }
    }
    class viewHolder(  //Optimizar actualización info en las vistas del RecyclerView
        private val binding: ProductoItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val texto : TextView = itemView.findViewById<TextView>(R.id.alimento)
        private val button: MaterialButton = itemView.findViewById<MaterialButton>(R.id.button)
        fun bind (prod:Producto) {
            texto.text = prod.nom_producto
            button.setOnClickListener {
               val dialogFragment = SelectDialogFragment()
                val position = adapterPosition
               if (position != RecyclerView.NO_POSITION) {
                   // Pasar el producto al DialogFragment
                   dialogFragment.setSelectedProduct(prod)
               }
               // Mostrar el DialogFragment
               val fragmentManager = itemView.context as AppCompatActivity
               dialogFragment.show(fragmentManager.supportFragmentManager, "mi_dialog_fragment")
           }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(
            (ProductoItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
            ))
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}