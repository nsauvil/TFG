package nsauvil.tfg.ui.escaneo


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.integration.android.IntentIntegrator
import nsauvil.tfg.R
import nsauvil.tfg.databinding.FragmentEscaneoBinding
import nsauvil.tfg.ui.domain.model.Producto
import nsauvil.tfg.ui.mapa.MapaViewModel
import nsauvil.tfg.ui.productos.ProductosViewModel
import java.util.*


class EscaneoFragment: Fragment(R.layout.fragment_escaneo) {
    private var _binding : FragmentEscaneoBinding? = null //mantiene la referencia al binding y se inicializa a null
    private val binding get() = _binding!!  //da acceso a la referencia anterior
    private val viewModel: ProductosViewModel by activityViewModels()
    private val viewModel2: EscaneoViewModel by activityViewModels()
    private val viewModel3: MapaViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEscaneoBinding.bind(view)

        binding.buttonQR.setOnClickListener {
            startQRScanner()  //al pulsar el botón, llama a la cámara
        }

        viewModel2.buttonQRPressed.observe(viewLifecycleOwner) {
            if (viewModel2.buttonQRPressed.value == true) {
                binding.buttonQR.text = getString(R.string.buttonQR2) //cambia el texto del botón al pulsarlo una vez
            } else {
                binding.buttonQR.text = getString(R.string.buttonQR1)
            }
        }
    }

    private fun startQRScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt( getString(R.string.buttonQR1))
        integrator.setCameraId(0) // Usar la cámara trasera del dispositivo
        integrator.setBeepEnabled(false) // Desactiva el sonido de escaneo
        integrator.initiateScan()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        viewModel2.pressScanButton() //cambiar el mensaje del botón
        if (result != null && result.contents != null) {
            val qrCodeResult = result.contents
            val firestore = FirebaseFirestore.getInstance()
            //Primero, recuperar los productos
            val colRef = firestore.collection(qrCodeResult)  // el QR contiene el nombre del supermercado
            colRef.get().addOnSuccessListener {
                val colRef2Es = colRef.document("es").collection("productos")
                val colRef2En = colRef.document("en").collection("productos")
                colRef2Es.get().addOnSuccessListener { querySnapshot ->
                    val productosList = mutableListOf<Producto>()
                    for (document in querySnapshot.documents) {
                        val producto = document.toObject(Producto::class.java) //convertir los datos en Firebase en Productos
                        producto?.let { productosList.add(it) }
                    }
                    val productosListOrd = productosList.sortedBy { it.nom_producto } //ordenar alfabéticamente los productos, para facilitar su búsqueda
                    viewModel.setProductosEs(productosListOrd)
                }.addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), getString(R.string.errorQR2), Toast.LENGTH_SHORT).show()
                    exception.printStackTrace()
                }
                colRef2En.get().addOnSuccessListener { querySnapshot ->
                    val productosList = mutableListOf<Producto>()
                    for (document in querySnapshot.documents) {
                        val producto = document.toObject(Producto::class.java) //convertir los datos en Firebase en Productos
                        producto?.let { productosList.add(it) }
                    }
                    val productosListOrd = productosList.sortedBy { it.nom_producto } //ordenar alfabéticamente los productos, para facilitar su búsqueda
                    viewModel.setProductosEn(productosListOrd)
                }.addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), getString(R.string.errorQR2), Toast.LENGTH_SHORT).show()
                    exception.printStackTrace()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), getString(R.string.errorQR2), Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
            }
            //ahora los mapas. Los ponemos aquí para evitar excepciones al acceder a Storage
            val storageRef = FirebaseStorage.getInstance().reference
            val refMap1 = storageRef.child("$qrCodeResult/planos/mapa_vertical.png")
            val refMap2 = storageRef.child("$qrCodeResult/planos/mapa_horizontal.png")
            // Obtener las URLs de descarga de las imágenes
            refMap1.downloadUrl.addOnSuccessListener { uri ->
                val urlPlano1 = uri.toString()
                // cargar la imagen en un ImageView
                viewModel3.cambiarMapaV(urlPlano1)
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), getString(R.string.errorQR2), Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
            }
            refMap2.downloadUrl.addOnSuccessListener { uri2 ->
                val urlPlano2 = uri2.toString()
                // cargar la imagen en un ImageView
                viewModel3.cambiarMapaH(urlPlano2)
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), getString(R.string.errorQR2), Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
            }
        } else {
            // El código del QR es nulo
            Toast.makeText(requireContext(), getString(R.string.errorQR), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   //libera los recursos asociados al binding
    }


}