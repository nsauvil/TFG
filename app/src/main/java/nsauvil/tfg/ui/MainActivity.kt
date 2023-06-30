package nsauvil.tfg.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import nsauvil.tfg.R
import nsauvil.tfg.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
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
                R.id.productosFragment, R.id.mapaFragment, R.id.ajustesFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}