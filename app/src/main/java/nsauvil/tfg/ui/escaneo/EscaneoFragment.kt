package nsauvil.tfg.ui.escaneo

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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


class EscaneoFragment: Fragment(R.layout.fragment_escaneo) {
    private var _binding : FragmentEscaneoBinding? = null //mantiene la referencia al binding y se inicializa a null
    private val binding get() = _binding!!  //da acceso a la referencia anterior
    private val viewModel: ProductosViewModel by activityViewModels()
    private val viewModel2: EscaneoViewModel by activityViewModels()
    private val viewModel3: MapaViewModel by activityViewModels()

    lateinit var listaProductos: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEscaneoBinding.bind(view)

        binding.buttonQR.setOnClickListener {
            startQRScanner()
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
        integrator.setPrompt("Escanea un código QR")
        integrator.setCameraId(0) // Usar la cámara trasera del dispositivo
        integrator.setBeepEnabled(false) // Desactiva el sonido de escaneo
        integrator.initiateScan()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        viewModel2.pressScanButton() //cambiar el mensaje del botón
        if (result != null && result.contents != null) {
            val qrCodeResult = result.contents
            if (qrCodeResult != null) { //por si hay fallos al escanear
               // Toast.makeText(requireContext(), "Código QR: $qrCodeResult", Toast.LENGTH_SHORT).show()
                val firestore = FirebaseFirestore.getInstance()
                //Primero, recuperar los productos
                val colRef = firestore.collection(qrCodeResult)  // el QR contiene el nombre del supermercado
                val colRef2 = colRef.document("productos").collection("productos")
                colRef2.get().addOnSuccessListener { querySnapshot ->
                    val productosList = mutableListOf<Producto>()
                    for (document in querySnapshot.documents) {
                        val producto = document.toObject(Producto::class.java)
                        producto?.let { productosList.add(it) }
                    }
                    val productosListOrd = productosList.sortedBy { it.nom_producto }
                    viewModel.setProductos(productosListOrd)

                    binding.buttonQR.text = getString(R.string.buttonQR2)
                }.addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
                //ahora los mapas
                val storageRef = FirebaseStorage.getInstance().reference
                val refMap1 = storageRef.child("$qrCodeResult/planos/mapa_vertical.png")
                val refMap2 = storageRef.child("$qrCodeResult/planos/mapa_horizontal.png")
                // Obtener las URLs de descarga de las imágenes
                refMap1.downloadUrl.addOnSuccessListener { uri ->
                    val UrlPlano1 = uri.toString()
                    // cargar la imagen en un ImageView
                    viewModel3.cambiarMapaV(UrlPlano1)
                }.addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
                refMap2.downloadUrl.addOnSuccessListener { uri2 ->
                    val UrlPlano2 = uri2.toString()
                    // cargar la imagen en un ImageView
                    viewModel3.cambiarMapaH(UrlPlano2)
                }.addOnFailureListener { exception ->
                    exception.printStackTrace()
                }

                //y la lista
                val refList = storageRef.child("$qrCodeResult/lista/lista_productos.txt")
                refList.getBytes(Long.MAX_VALUE)
                    .addOnSuccessListener { bytes ->
                        listaProductos = String(bytes, Charsets.UTF_8)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Error al leer el archivo: ${exception.message}")
                    }

            }
        } else {
            // El código del QR es nulo
            Toast.makeText(requireContext(), "Error: Código QR nulo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   //libera los recursos asociados al binding

    }


}