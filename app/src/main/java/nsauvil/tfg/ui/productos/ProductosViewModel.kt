package nsauvil.tfg.ui.productos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import nsauvil.tfg.R
import nsauvil.tfg.ui.domain.model.Producto

class ProductosViewModel : ViewModel() {

    private fun getAllProducts(): List<Producto>? {  //generar todos los alimentos del supermercado
        val res = mutableListOf<Producto>()
        res.add(Producto("aceite","Aceite" , 0.3f, 0.7f, 0.35f, 0.3f))
        res.add(Producto("arroz","Arroz" , 0.74f, 0.75f, 0.31f, 0.67f))
        res.add(Producto("atun","Atún" , 0.69f, 0.27f, 0.7f, 0.64f))
        res.add(Producto("bacon","Bacon" , 0.73f, 0.58f, 0.47f, 0.66f))
        res.add(Producto("bizcocho","Bizcocho" , 0.38f, 0.32f, 0.65f, 0.35f))
        res.add(Producto("carne","Carne" , 0.81f, 0.4f, 0.58f, 0.75f))
        res.add(Producto("cebolla","Cebolla" , 0.1f, 0.7f, 0.35f, 0.1f))
        res.add(Producto("chorizo","Chorizo" , 0.73f, 0.58f, 0.47f, 0.66f))
        res.add(Producto("desinfectante","Desinfectante" , 0.68f, 0.65f, 0.4f, 0.64f))
        res.add(Producto("detergente","Detergente" , 0.68f, 0.75f, 0.31f, 0.64f))
        res.add(Producto("especias","Especias" , 0.3f, 0.63f, 0.42f, 0.3f))
        res.add(Producto("fideos","Fideos" , 0.83f, 0.6f, 0.43f, 0.77f))
        res.add(Producto("fresa","Fresa" , 0.18f, 0.6f, 0.42f, 0.2f))
        res.add(Producto("fruta","Fruta" , 0.18f, 0.7f, 0.35f, 0.2f))
        res.add(Producto("gel","Gel" , 0.1f, 0.4f, 0.58f, 0.1f))
        res.add(Producto("helado","Helado" , 0.46f, 0.35f, 0.63f, 0.45f))
        res.add(Producto("judias","Judías" , 0.74f, 0.73f, 0.33f, 0.67f))
        res.add(Producto("leche","Leche" , 0.2f, 0.23f, 0.73f, 0.22f))
        res.add(Producto("lechuga","Lechuga" , 0.1f, 0.6f, 0.42f, 0.1f))
        res.add(Producto("manzana","Manzana" , 0.18f, 0.6f, 0.42f, 0.2f))
        res.add(Producto("maquillaje","Maquillaje" , 0.1f, 0.25f, 0.7f, 0.1f))
        res.add(Producto("mayonesa","Mayonesa" , 0.45f, 0.64f, 0.39f, 0.43f))
        res.add(Producto("merluza","Merluza" , 0.53f, 0.45f, 0.55f, 0.5f))
        res.add(Producto("naranja","Naranja" , 0.24f, 0.7f, 0.35f, 0.26f))
        res.add(Producto("papel","Papel" , 0.58f, 0.65f, 0.4f, 0.55f))
        res.add(Producto("pasta","Pasta" , 0.82f, 0.26f, 0.69f, 0.76f))
        res.add(Producto("pan","Pan" , 0.41f, 0.14f, 0.77f, 0.4f))
        res.add(Producto("patata","Patata" , 0.1f, 0.7f, 0.35f, 0.1f))
        res.add(Producto("pavo","Pavo" , 0.7f, 0.3f, 0.65f, 0.62f))
        res.add(Producto("pescado","Pescado" , 0.68f, 0.14f, 0.78f, 0.63f))
        res.add(Producto("pimiento","Pimiento" , 0.1f, 0.7f, 0.35f, 0.1f))
        res.add(Producto("pollo","Pollo" , 0.81f, 0.4f, 0.58f, 0.75f))
        res.add(Producto("platano","Plátano" , 0.24f, 0.7f, 0.35f, 0.26f))
        res.add(Producto("queso","Queso" , 0.85f, 0.76f, 0.26f, 0.78f))
        res.add(Producto("repelente","Repelente" , 0.52f, 0.65f, 0.4f, 0.51f))
        res.add(Producto("salmon","Salmón" , 0.53f, 0.25f, 0.7f, 0.5f))
        res.add(Producto("salchicha","Salchicha" , 0.7f, 0.3f, 0.65f, 0.62f))
        res.add(Producto("seta","Seta" , 0.25f, 0.6f, 0.43f, 0.25f))
        res.add(Producto("tarta","Tarta" , 0.38f, 0.44f, 0.57f, 0.35f))
        res.add(Producto("tomate","Tomate" , 0.1f, 0.7f, 0.35f, 0.1f))
        res.add(Producto("verdura","Verdura" , 0.1f, 0.7f, 0.35f, 0.1f))
        res.add(Producto("vino","Vino" , 0.4f, 0.64f, 0.39f, 0.39f))
        res.add(Producto("yogur","Yogur" , 0.3f, 0.35f, 0.62f, 0.3f))
        res.add(Producto("zanahoria","Zanahoria" , 0.1f, 0.7f, 0.35f, 0.1f))
        res.add(Producto("zumo","Zumo" , 0.19f, 0.4f, 0.58f, 0.21f))

        return res
    }
    private var _productos = MutableLiveData<List<Producto>>(getAllProducts())
    val productos : LiveData<List<Producto>>
        get() = _productos

    private val _selectedProduct = MutableLiveData<Producto>()
    val selectedProduct: LiveData<Producto>
        get() = _selectedProduct

    fun searchProducto(prod: Producto) {  //el Dialog te pasa el producto seleccionado
        _selectedProduct.value = prod
    }

    fun searchProductoPosition(position:Int) {
        val prod = _productos.value?.toMutableList()
        val prodToSearch = prod?.get(position)

    }
}