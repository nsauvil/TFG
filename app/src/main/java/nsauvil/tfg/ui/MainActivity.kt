package nsauvil.tfg.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import nsauvil.tfg.R
import nsauvil.tfg.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val db = FirebaseFirestore.getInstance() //instancia conectada a la base de datos. almacena los productos
    //private val storageRef = FirebaseStorage.getInstance().reference //storage de las listas y los planos

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TFG)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater) //Obtiene referencia a la clase ActivityMainBinding, generada automáticamente
        setContentView(binding.root)  //establece la vista de la actividad
        val navController = binding.navHostFragment.getFragment<NavHostFragment>().navController  //referencia al controlador de navegación
        val navigationView = binding.bottomNavigationView as? NavigationBarView  //para tratar igual tanto al RailView como al BotttomNavigation
        navigationView?.setupWithNavController(navController)  //lo asociamos a la barra de navegación

        setSupportActionBar(binding.toolbar) //establece la nueva barra creada como barra de acción
        appBarConfiguration = AppBarConfiguration (  //nueva instancia de AppBarConfiguration
            setOf(
                R.id.productosFragment, R.id.mapaFragment, R.id.escaneoFragment, R.id.ajustesFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        //iniciar la base de datos del voiceMarket
        //PRODUCTOS
        /*
        val productosEs = getAllProductsEs()  //todos los productos del market en español
        val productosEn = getAllProductsEn()  //todos los productos del market en inglés
        for (producto in productosEs) {
            db.collection("market").document("es").collection("productos").add(producto)
        }
        for (producto in productosEn) {
            db.collection("market").document("en").collection("productos").add(producto)
        } */

    }
    /*
    private fun getAllProductsEs(): List<Map<String, Any>> {  //generar todos los alimentos del supermercado
        val res = mutableListOf<Map<String, Any>>()
        res.add(hashMapOf("id" to "aceite", "nom_producto" to "Aceite", "cord1" to 0.3f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.3f))
        res.add(hashMapOf("id" to "arroz", "nom_producto" to "Arroz", "cord1" to 0.74f,"cord2" to 0.75f, "cord11" to 0.31f,"cord22" to 0.67f))
        res.add(hashMapOf("id" to "atun", "nom_producto" to "Atún", "cord1" to 0.69f,"cord2" to 0.27f, "cord11" to 0.7f,"cord22" to 0.64f))
        res.add(hashMapOf("id" to "bacon", "nom_producto" to "Bacon", "cord1" to 0.73f,"cord2" to 0.58f, "cord11" to 0.47f,"cord22" to 0.66f))
        res.add(hashMapOf("id" to "bizcocho", "nom_producto" to "Bizcocho", "cord1" to 0.38f,"cord2" to 0.32f, "cord11" to 0.65f,"cord22" to 0.35f))
        res.add(hashMapOf("id" to "carne", "nom_producto" to "Carne", "cord1" to 0.81f,"cord2" to 0.4f, "cord11" to 0.58f,"cord22" to 0.75f))
        res.add(hashMapOf("id" to "cebolla", "nom_producto" to "Cebolla", "cord1" to 0.1f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "chorizo", "nom_producto" to "Chorizo", "cord1" to 0.73f,"cord2" to 0.58f, "cord11" to 0.47f,"cord22" to 0.66f))
        res.add(hashMapOf("id" to "desinfectante", "nom_producto" to "Desinfectante", "cord1" to 0.68f,"cord2" to 0.65f, "cord11" to 0.4f,"cord22" to 0.64f))
        res.add(hashMapOf("id" to "detergente", "nom_producto" to "Detergente", "cord1" to 0.68f,"cord2" to 0.75f, "cord11" to 0.31f,"cord22" to 0.64f))
        res.add(hashMapOf("id" to "especias", "nom_producto" to "Especias", "cord1" to 0.3f,"cord2" to 0.63f, "cord11" to 0.42f,"cord22" to 0.3f))
        res.add(hashMapOf("id" to "fideos", "nom_producto" to "Fideos", "cord1" to 0.83f,"cord2" to 0.6f, "cord11" to 0.43f,"cord22" to 0.77f))
        res.add(hashMapOf("id" to "fresa", "nom_producto" to "Fresa", "cord1" to 0.18f,"cord2" to 0.6f, "cord11" to 0.42f,"cord22" to 0.2f))
        res.add(hashMapOf("id" to "fruta", "nom_producto" to "Fruta", "cord1" to 0.18f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.2f))
        res.add(hashMapOf("id" to "gel", "nom_producto" to "Gel", "cord1" to 0.1f,"cord2" to 0.4f, "cord11" to 0.58f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "helado", "nom_producto" to "Helado", "cord1" to 0.46f,"cord2" to 0.35f, "cord11" to 0.63f,"cord22" to 0.45f))
        res.add(hashMapOf("id" to "judias", "nom_producto" to "Judías", "cord1" to 0.74f,"cord2" to 0.73f, "cord11" to 0.33f,"cord22" to 0.67f))
        res.add(hashMapOf("id" to "leche", "nom_producto" to "Leche", "cord1" to 0.2f,"cord2" to 0.23f, "cord11" to 0.73f,"cord22" to 0.22f))
        res.add(hashMapOf("id" to "lechuga", "nom_producto" to "Lechuga", "cord1" to 0.1f,"cord2" to 0.6f, "cord11" to 0.42f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "manzana", "nom_producto" to "Manzana", "cord1" to 0.18f,"cord2" to 0.6f, "cord11" to 0.42f,"cord22" to 0.2f))
        res.add(hashMapOf("id" to "maquillaje", "nom_producto" to "Maquillaje", "cord1" to 0.1f,"cord2" to 0.25f, "cord11" to 0.7f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "mayonesa", "nom_producto" to "Mayonesa", "cord1" to 0.45f,"cord2" to 0.64f, "cord11" to 0.39f,"cord22" to 0.43f))
        res.add(hashMapOf("id" to "merluza", "nom_producto" to "Merluza", "cord1" to 0.53f,"cord2" to 0.45f, "cord11" to 0.55f,"cord22" to 0.5f))
        res.add(hashMapOf("id" to "naranja", "nom_producto" to "Naranja", "cord1" to 0.24f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.26f))
        res.add(hashMapOf("id" to "papel", "nom_producto" to "Papel", "cord1" to 0.58f,"cord2" to 0.65f, "cord11" to 0.4f,"cord22" to 0.55f))
        res.add(hashMapOf("id" to "pasta", "nom_producto" to "Pasta", "cord1" to 0.82f,"cord2" to 0.26f, "cord11" to 0.69f,"cord22" to 0.76f))
        res.add(hashMapOf("id" to "pan", "nom_producto" to "Pan", "cord1" to 0.41f,"cord2" to 0.14f, "cord11" to 0.77f,"cord22" to 0.4f))
        res.add(hashMapOf("id" to "patata", "nom_producto" to "Patata", "cord1" to 0.1f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "pavo", "nom_producto" to "Pavo", "cord1" to 0.7f,"cord2" to 0.3f, "cord11" to 0.65f,"cord22" to 0.62f))
        res.add(hashMapOf("id" to "pescado", "nom_producto" to "Pescado", "cord1" to 0.68f,"cord2" to 0.14f, "cord11" to 0.78f,"cord22" to 0.63f))
        res.add(hashMapOf("id" to "pimiento", "nom_producto" to "Pimiento", "cord1" to 0.1f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "pollo", "nom_producto" to "Pollo", "cord1" to 0.81f,"cord2" to 0.4f, "cord11" to 0.58f,"cord22" to 0.75f))
        res.add(hashMapOf("id" to "platano", "nom_producto" to "Plátano", "cord1" to 0.24f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.26f))
        res.add(hashMapOf("id" to "queso", "nom_producto" to "Queso", "cord1" to 0.85f,"cord2" to 0.76f, "cord11" to 0.26f,"cord22" to 0.78f))
        res.add(hashMapOf("id" to "repelente", "nom_producto" to "Repelente", "cord1" to 0.52f,"cord2" to 0.65f, "cord11" to 0.4f,"cord22" to 0.51f))
        res.add(hashMapOf("id" to "salmon", "nom_producto" to "Salmón", "cord1" to 0.53f,"cord2" to 0.25f, "cord11" to 0.7f,"cord22" to 0.5f))
        res.add(hashMapOf("id" to "salchicha", "nom_producto" to "Salchicha", "cord1" to 0.7f,"cord2" to 0.3f, "cord11" to 0.65f,"cord22" to 0.62f))
        res.add(hashMapOf("id" to "seta", "nom_producto" to "Seta", "cord1" to 0.25f,"cord2" to 0.6f, "cord11" to 0.43f,"cord22" to 0.25f))
        res.add(hashMapOf("id" to "tarta", "nom_producto" to "Tarta", "cord1" to 0.38f,"cord2" to 0.44f, "cord11" to 0.57f,"cord22" to 0.35f))
        res.add(hashMapOf("id" to "tomate", "nom_producto" to "Tomate", "cord1" to 0.1f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "verdura", "nom_producto" to "Verdura", "cord1" to 0.1f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "vino", "nom_producto" to "Vino", "cord1" to 0.4f,"cord2" to 0.64f, "cord11" to 0.39f,"cord22" to 0.39f))
        res.add(hashMapOf("id" to "yogur", "nom_producto" to "Yogur", "cord1" to 0.3f,"cord2" to 0.35f, "cord11" to 0.62f,"cord22" to 0.3f))
        res.add(hashMapOf("id" to "zanahoria", "nom_producto" to "Zanahoria", "cord1" to 0.1f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "zumo", "nom_producto" to "Zumo", "cord1" to 0.19f,"cord2" to 0.4f, "cord11" to 0.58f,"cord22" to 0.21f))

        return res
    }
    private fun getAllProductsEn(): List<Map<String, Any>> {  //generar todos los alimentos del supermercado
        val res = mutableListOf<Map<String, Any>>()
        res.add(hashMapOf("id" to "oil", "nom_producto" to "Oil", "cord1" to 0.3f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.3f))
        res.add(hashMapOf("id" to "rice", "nom_producto" to "Rice", "cord1" to 0.74f,"cord2" to 0.75f, "cord11" to 0.31f,"cord22" to 0.67f))
        res.add(hashMapOf("id" to "tuna", "nom_producto" to "Tuna", "cord1" to 0.69f,"cord2" to 0.27f, "cord11" to 0.7f,"cord22" to 0.64f))
        res.add(hashMapOf("id" to "bacon", "nom_producto" to "Bacon", "cord1" to 0.73f,"cord2" to 0.58f, "cord11" to 0.47f,"cord22" to 0.66f))
        res.add(hashMapOf("id" to "sponge cake", "nom_producto" to "Sponge cake", "cord1" to 0.38f,"cord2" to 0.32f, "cord11" to 0.65f,"cord22" to 0.35f))
        res.add(hashMapOf("id" to "meat", "nom_producto" to "Meat", "cord1" to 0.81f,"cord2" to 0.4f, "cord11" to 0.58f,"cord22" to 0.75f))
        res.add(hashMapOf("id" to "onion", "nom_producto" to "Onion", "cord1" to 0.1f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "chorizo", "nom_producto" to "Chorizo", "cord1" to 0.73f,"cord2" to 0.58f, "cord11" to 0.47f,"cord22" to 0.66f))
        res.add(hashMapOf("id" to "disinfectant", "nom_producto" to "Disinfectant", "cord1" to 0.68f,"cord2" to 0.65f, "cord11" to 0.4f,"cord22" to 0.64f))
        res.add(hashMapOf("id" to "detergent", "nom_producto" to "Detergent", "cord1" to 0.68f,"cord2" to 0.75f, "cord11" to 0.31f,"cord22" to 0.64f))
        res.add(hashMapOf("id" to "spices", "nom_producto" to "Spices", "cord1" to 0.3f,"cord2" to 0.63f, "cord11" to 0.42f,"cord22" to 0.3f))
        res.add(hashMapOf("id" to "noodles", "nom_producto" to "Noodles", "cord1" to 0.83f,"cord2" to 0.6f, "cord11" to 0.43f,"cord22" to 0.77f))
        res.add(hashMapOf("id" to "strawberry", "nom_producto" to "Strawberry", "cord1" to 0.18f,"cord2" to 0.6f, "cord11" to 0.42f,"cord22" to 0.2f))
        res.add(hashMapOf("id" to "fruit", "nom_producto" to "Fruit", "cord1" to 0.18f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.2f))
        res.add(hashMapOf("id" to "gel", "nom_producto" to "Gel", "cord1" to 0.1f,"cord2" to 0.4f, "cord11" to 0.58f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "ice cream", "nom_producto" to "Ice cream", "cord1" to 0.46f,"cord2" to 0.35f, "cord11" to 0.63f,"cord22" to 0.45f))
        res.add(hashMapOf("id" to "beans", "nom_producto" to "Beans", "cord1" to 0.74f,"cord2" to 0.73f, "cord11" to 0.33f,"cord22" to 0.67f))
        res.add(hashMapOf("id" to "milk", "nom_producto" to "Milk", "cord1" to 0.2f,"cord2" to 0.23f, "cord11" to 0.73f,"cord22" to 0.22f))
        res.add(hashMapOf("id" to "lettuce", "nom_producto" to "Lettuce", "cord1" to 0.1f,"cord2" to 0.6f, "cord11" to 0.42f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "apple", "nom_producto" to "Apple", "cord1" to 0.18f,"cord2" to 0.6f, "cord11" to 0.42f,"cord22" to 0.2f))
        res.add(hashMapOf("id" to "make up", "nom_producto" to "Make-up", "cord1" to 0.1f,"cord2" to 0.25f, "cord11" to 0.7f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "mayonnaise", "nom_producto" to "Mayonnaise", "cord1" to 0.45f,"cord2" to 0.64f, "cord11" to 0.39f,"cord22" to 0.43f))
        res.add(hashMapOf("id" to "hake", "nom_producto" to "Hake", "cord1" to 0.53f,"cord2" to 0.45f, "cord11" to 0.55f,"cord22" to 0.5f))
        res.add(hashMapOf("id" to "orange", "nom_producto" to "Orange", "cord1" to 0.24f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.26f))
        res.add(hashMapOf("id" to "paper", "nom_producto" to "Paper", "cord1" to 0.58f,"cord2" to 0.65f, "cord11" to 0.4f,"cord22" to 0.55f))
        res.add(hashMapOf("id" to "pasta", "nom_producto" to "Pasta", "cord1" to 0.82f,"cord2" to 0.26f, "cord11" to 0.69f,"cord22" to 0.76f))
        res.add(hashMapOf("id" to "bread", "nom_producto" to "Bread", "cord1" to 0.41f,"cord2" to 0.14f, "cord11" to 0.77f,"cord22" to 0.4f))
        res.add(hashMapOf("id" to "potato", "nom_producto" to "Potato", "cord1" to 0.1f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "turkey", "nom_producto" to "Turkey", "cord1" to 0.7f,"cord2" to 0.3f, "cord11" to 0.65f,"cord22" to 0.62f))
        res.add(hashMapOf("id" to "fish", "nom_producto" to "Fish", "cord1" to 0.68f,"cord2" to 0.14f, "cord11" to 0.78f,"cord22" to 0.63f))
        res.add(hashMapOf("id" to "pepper", "nom_producto" to "Pepper", "cord1" to 0.1f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "chicken", "nom_producto" to "Chicken", "cord1" to 0.81f,"cord2" to 0.4f, "cord11" to 0.58f,"cord22" to 0.75f))
        res.add(hashMapOf("id" to "banana", "nom_producto" to "Banana", "cord1" to 0.24f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.26f))
        res.add(hashMapOf("id" to "cheese", "nom_producto" to "Cheese", "cord1" to 0.85f,"cord2" to 0.76f, "cord11" to 0.26f,"cord22" to 0.78f))
        res.add(hashMapOf("id" to "repellent", "nom_producto" to "Repellent", "cord1" to 0.52f,"cord2" to 0.65f, "cord11" to 0.4f,"cord22" to 0.51f))
        res.add(hashMapOf("id" to "salmon", "nom_producto" to "Salmon", "cord1" to 0.53f,"cord2" to 0.25f, "cord11" to 0.7f,"cord22" to 0.5f))
        res.add(hashMapOf("id" to "sausage", "nom_producto" to "Sausage", "cord1" to 0.7f,"cord2" to 0.3f, "cord11" to 0.65f,"cord22" to 0.62f))
        res.add(hashMapOf("id" to "mushroom", "nom_producto" to "Mushroom", "cord1" to 0.25f,"cord2" to 0.6f, "cord11" to 0.43f,"cord22" to 0.25f))
        res.add(hashMapOf("id" to "cake", "nom_producto" to "Cake", "cord1" to 0.38f,"cord2" to 0.44f, "cord11" to 0.57f,"cord22" to 0.35f))
        res.add(hashMapOf("id" to "tomato", "nom_producto" to "Tomato", "cord1" to 0.1f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "vegetable", "nom_producto" to "Vegetable", "cord1" to 0.1f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "wine", "nom_producto" to "Wine", "cord1" to 0.4f,"cord2" to 0.64f, "cord11" to 0.39f,"cord22" to 0.39f))
        res.add(hashMapOf("id" to "yoghurt", "nom_producto" to "Yoghurt", "cord1" to 0.3f,"cord2" to 0.35f, "cord11" to 0.62f,"cord22" to 0.3f))
        res.add(hashMapOf("id" to "carrot", "nom_producto" to "Carrot", "cord1" to 0.1f,"cord2" to 0.7f, "cord11" to 0.35f,"cord22" to 0.1f))
        res.add(hashMapOf("id" to "juice", "nom_producto" to "Juice", "cord1" to 0.19f,"cord2" to 0.4f, "cord11" to 0.58f,"cord22" to 0.21f))

        return res
    } */

}