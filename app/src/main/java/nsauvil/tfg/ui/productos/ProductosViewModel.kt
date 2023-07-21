package nsauvil.tfg.ui.productos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nsauvil.tfg.ui.domain.model.Producto

class ProductosViewModel : ViewModel() {


    private var _productos = MutableLiveData<List<Producto>>()  //los productos que se verán en la lista
    val productos : LiveData<List<Producto>>
        get() = _productos

    private val _selectedProduct = MutableLiveData<Producto>()  //el producto que se desea localizar
    val selectedProduct: LiveData<Producto>
        get() = _selectedProduct

    fun searchProducto(prod: Producto) {  //el Dialog te pasa el producto seleccionado
        _selectedProduct.value = prod
    }


    fun setProductos(nuevosProductos: List<Producto>) {//para que al escanear el QR se sustituyan los productos
        _productos.value = nuevosProductos
    }
}