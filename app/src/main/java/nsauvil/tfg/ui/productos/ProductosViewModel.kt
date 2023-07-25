package nsauvil.tfg.ui.productos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import nsauvil.tfg.ui.domain.model.Producto

class ProductosViewModel : ViewModel() {


    private var _productosEs = MutableLiveData<List<Producto>>()  //los productos que se verán en la lista
    val productosEs : LiveData<List<Producto>>
        get() = _productosEs

    private var _productosEn = MutableLiveData<List<Producto>>()  //los productos que se verán en la lista
    val productosEn : LiveData<List<Producto>>
        get() = _productosEn

    private val _selectedProduct = MutableLiveData<Producto>()  //el producto que se desea localizar
    val selectedProduct: LiveData<Producto>
        get() = _selectedProduct

    fun searchProducto(prod: Producto) {  //el Dialog te pasa el producto seleccionado
        _selectedProduct.value = prod
    }


    fun setProductosEs(nuevosProductos: List<Producto>) {//para que al escanear el QR se sustituyan los productos
        _productosEs.value = nuevosProductos
    }

    fun setProductosEn(nuevosProductos: List<Producto>) {//para que al escanear el QR se sustituyan los productos
        _productosEn.value = nuevosProductos
    }
}