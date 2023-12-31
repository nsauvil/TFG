package nsauvil.tfg.ui.mapa

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.Coil
import coil.request.ImageRequest
import com.google.android.material.floatingactionbutton.FloatingActionButton
import nsauvil.tfg.R
import nsauvil.tfg.databinding.FragmentMapaBinding
import nsauvil.tfg.ui.domain.model.Producto
import nsauvil.tfg.ui.productos.ProductosViewModel
import nsauvil.tfg.ui.productos.SelectDialogFragment2
import okhttp3.*
import java.io.*
import java.text.Normalizer
import java.util.*
import kotlin.collections.ArrayList

private const val RECOGNIZER_CODE = 1
class MapaFragment: Fragment(R.layout.fragment_mapa){
    private var _binding : FragmentMapaBinding? = null //Mantiene la referencia al binding y se inicializa a null
    private val binding get() = _binding!!  //Da acceso a la referencia anterior
    private val viewModel: ProductosViewModel by activityViewModels() //Obtiene el ViewModel de los productos
    private val viewModel2: MapaViewModel by activityViewModels()
    private lateinit var recordButton: FloatingActionButton //Botón de grabación



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {  //se ejecuta al crearse la vista del objeto
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapaBinding.bind(view)


        viewModel2.imagenV.observe(viewLifecycleOwner) { //Imagen obtenida mediante el escaneo de QR (vertical)
            if (!it.isNullOrEmpty() && binding.map1 != null) {
                val request = ImageRequest.Builder(requireContext())
                    .data(it)
                    .target(binding.map1!!)
                    .build()
                val disposable = Coil.imageLoader(requireContext()).enqueue(request) //En este caso, uso la biblioteca Coil para cargar las imágenes almacenadas en la base de datos
                binding.map1?.visibility = View.VISIBLE
                binding.floatingActionButton.visibility = View.VISIBLE
            }
        }

        viewModel2.imagenH.observe(viewLifecycleOwner) {//Imagen obtenida mediante el escaneo de QR (apaisada)
            if (!it.isNullOrEmpty() && binding.map2 != null) {
                val request = ImageRequest.Builder(requireContext())
                    .data(it)
                    .target(binding.map2!!)
                    .build()
                val disposable = Coil.imageLoader(requireContext()).enqueue(request)
                binding.map2?.visibility = View.VISIBLE
                binding.floatingActionButton.visibility = View.VISIBLE
            }
        }

        binding.map1?.viewTreeObserver?.addOnGlobalLayoutListener (object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {  //Para asegurarnos de que la imagen no da null (viewTreeObserver)
                viewModel.selectedProduct.observe(viewLifecycleOwner) { prod ->
                    moveImageToLocation(prod)  //Si cambia el objeto seleccionado, se localiza en el mapa
                }
                // Quitar el listener
                binding.map1?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
        })
        binding.map2?.viewTreeObserver?.addOnGlobalLayoutListener (object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {  //Para asegurarnos de que la imagen no da null
                viewModel.selectedProduct.observe(viewLifecycleOwner) { prod ->
                    moveImageToLocation2(prod)  //Lo mismo que en el método anterior, pero para la pantalla apaisada
                }
                // Quitar el listener
                binding.map2?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
        })

        recordButton = view.findViewById(R.id.floatingActionButton)
        recordButton.setOnClickListener{//Para el reconocimiento de voz se emplea RecognizerIntent
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH) //Iniciar la actividad de reconocimiento de voz (muestra la pantalla y transcribe lo que dices)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)  //El reconocimiento de voz es libre, no hay restricciones específicas
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.audioRecord))
            startActivityForResult(intent, RECOGNIZER_CODE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { //Este método gestiona los resultados obtenidos con el audio
        if(requestCode == RECOGNIZER_CODE && resultCode == RESULT_OK && data != null) {
            val taskText: ArrayList<String> = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>
            val pal = taskText.get(0)
            val pal1 = quitarTildes(pal)//Para que coincida con el id
            val pal2 = pal1.lowercase()
            val idioma = Locale.getDefault().language
            var selectedProd : Producto ?= null
            if (idioma == "en") {
                selectedProd = viewModel.productosEn.value?.find {it.id == pal2}
            } else {
                selectedProd = viewModel.productosEs.value?.find {it.id == pal2}
            }
            if (selectedProd != null) {
                val dialogFragment = SelectDialogFragment2()
                dialogFragment.setSelectedProduct(selectedProd)

                // Mostrar el DialogFragment
                val fragmentManager = requireActivity() as AppCompatActivity
                dialogFragment.show(fragmentManager.supportFragmentManager, "mi_dialog_fragment")
            } else {
                Toast.makeText(requireContext(), getString(R.string.errorProd), Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.errorAudio), Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun quitarTildes(text: String): String {
        val normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD)
        val pattern = "\\p{InCombiningDiacriticalMarks}+".toRegex()
        return pattern.replace(normalizedText, "")
    }

    //Para asegurarnos de que, independientemente del dispositivo, los productos se localizan bien:
    private fun calculateCoordinatesV(prod:Producto): Pair<Float, Float> { //Coordenadas orientación vertical
        binding.map1?.let { mapView ->
            if (mapView.width > 0 && mapView.height > 0) {
                val mapWidth = mapView.width.toFloat()
                val mapHeight = mapView.height.toFloat()
                val mapStartX = mapView.left.toFloat()
                val mapStartY = mapView.top.toFloat()
                val locationXPercentage = prod.cord1
                val locationYPercentage = prod.cord2
                val locationX = mapStartX + locationXPercentage * mapWidth
                val locationY = mapStartY + locationYPercentage * mapHeight
                return Pair(locationX, locationY)
            }
        }
        return Pair(prod.cord1, prod.cord2)
    }
    private fun calculateCoordinatesH(prod:Producto): Pair<Float, Float> { //Coordenadas orientación apaisada
        binding.map2?.let { mapView ->
            if (mapView.width > 0 && mapView.height > 0) {
                val mapWidth = mapView.width.toFloat()
                val mapHeight = mapView.height.toFloat()
                val mapStartX = mapView.left.toFloat()
                val mapStartY = mapView.top.toFloat()
                val locationXPercentage = prod.cord11
                val locationYPercentage = prod.cord22
                val locationX = mapStartX + locationXPercentage * mapWidth
                val locationY = mapStartY + locationYPercentage * mapHeight
                return Pair(locationX, locationY)
            }
        }
        return Pair(prod.cord11, prod.cord22)
    }
    private fun moveImageToLocation(producto: Producto) {
        val coordenadasV = calculateCoordinatesV(producto)
        binding.marca1?.let { marca1 ->  //Orientación vertical mapa
            marca1.translationX = 0.0f   //Para asegurarnos de que se coloca desde el comienzo
            marca1.translationY = 0.0f
            marca1.translationX = coordenadasV.first
            marca1.translationY = coordenadasV.second
            marca1.visibility = View.VISIBLE
        }  //comprueba que no haya null
    }
    private fun moveImageToLocation2(producto: Producto) {
        val coordenadasH = calculateCoordinatesH(producto)
        binding.marca2?.let { marca2 ->   //Orientación apaisada mapa
            marca2.translationX = 0.0f
            marca2.translationY = 0.0f
            marca2.translationX = coordenadasH.first
            marca2.translationY = coordenadasH.second
            marca2.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   //Libera los recursos asociados al binding
    }


}