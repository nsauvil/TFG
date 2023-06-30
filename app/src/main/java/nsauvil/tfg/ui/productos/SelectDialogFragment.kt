package nsauvil.tfg.ui.productos

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import nsauvil.tfg.R
import nsauvil.tfg.ui.domain.model.Producto

open class SelectDialogFragment(): DialogFragment() {
    private val viewModel: ProductosViewModel by activityViewModels()
    private lateinit var selectedProduct: Producto //almacena el producto que se quiere buscar

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.search1))
        builder.setMessage(getString(R.string.search2))
        builder.setPositiveButton(getString(R.string.search3)) { _, _ ->
            dismiss()
            viewModel.searchProducto(selectedProduct)
            findNavController().navigate(R.id.action_productosFragment_to_mapaFragment)
             }
        builder.setNegativeButton(getString(R.string.search4)) { _, _ -> dismiss() }
        return builder.create()
    }
    fun setSelectedProduct(producto: Producto) {
        selectedProduct = producto
    }
}