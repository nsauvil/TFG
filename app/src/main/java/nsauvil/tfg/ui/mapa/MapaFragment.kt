package nsauvil.tfg.ui.mapa

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.Coil
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import nsauvil.tfg.R
import nsauvil.tfg.databinding.FragmentMapaBinding
import nsauvil.tfg.ui.domain.model.Producto
import nsauvil.tfg.ui.productos.ProductosViewModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*

private const val SAMPLE_RATE = 44100  //frecuencia de muestreo para la grabación (muestras por segundo)
private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO //configuración del canal de audio
private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT //formato de codificación audio

class MapaFragment: Fragment(R.layout.fragment_mapa){
    private var _binding : FragmentMapaBinding? = null //mantiene la referencia al binding y se inicializa a null
    private val binding get() = _binding!!  //da acceso a la referencia anterior
    private val viewModel: ProductosViewModel by activityViewModels() //obtiene el ViewModel de los productos
    private val viewModel2: MapaViewModel by activityViewModels()
    private lateinit var recordButton: FloatingActionButton //botón de grabación
    private var audioRecord: AudioRecord? = null  //almacena referencia a la instancia de grabación de audio
    private var recordingThread: Thread? = null //almacena referencia al hilo responsable de la grabación de audio
    private var isRecording = false  //indica si la grabación está en curso
    private val RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO  //permiso grabar audio
    private val WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE  //permiso escribir
    private val PERMISSION_REQUEST_CODE = 200  //código de solicitud utilizado para solicitar permisos
    private var outputAudioDir: String = "" // Ruta de salida para el archivo de audio grabado
    private var outputStream: FileOutputStream?= null  //referencia al flujo de salida para el audio grabado
    private var outputFile: File?= null //archivo de audio

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {  //se ejecuta al crearse la vista del objeto
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapaBinding.bind(view)

        viewModel2.imagenV.observe(viewLifecycleOwner) { //imagen escaneada con el QR
            if (!it.isNullOrEmpty() && binding.map1 != null) {
                val request = ImageRequest.Builder(requireContext())
                    .data(it)
                    .target(binding.map1!!)
                    .build()
                val disposable = Coil.imageLoader(requireContext()).enqueue(request)
                binding.map1?.visibility = View.VISIBLE
                binding.floatingActionButton.visibility = View.VISIBLE
            }
        }

        viewModel2.imagenH.observe(viewLifecycleOwner) {
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
        /*
        viewModel2.imagenV.observe(viewLifecycleOwner) { //imagen escaneada con el QR
            if (!it.isNullOrEmpty() && binding.map1 != null) {
                Picasso.get()
                    .load(it)
                    .into(binding.map1!!)
                binding.map1?.visibility = View.VISIBLE
                binding.floatingActionButton.visibility = View.VISIBLE
            }
        }
        viewModel2.imagenH.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty() && binding.map2 != null) {
                Picasso.get()
                    .load(it)
                    .into(binding.map2!!)
                binding.map2?.visibility = View.VISIBLE
                binding.floatingActionButton.visibility = View.VISIBLE
            }
        } */

        binding.map1?.viewTreeObserver?.addOnGlobalLayoutListener (object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {  //para asegurarnos de que la imagen no da null (viewTreeObserver)
                viewModel.selectedProduct.observe(viewLifecycleOwner) { prod ->
                    moveImageToLocation(prod)  //si cambia el objeto seleccionado, se localiza en el mapa
                }
                // Quita el listener
                binding.map1?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
        })
        binding.map2?.viewTreeObserver?.addOnGlobalLayoutListener (object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {  //para asegurarnos de que la imagen no da null
                viewModel.selectedProduct.observe(viewLifecycleOwner) { prod ->
                    moveImageToLocation2(prod)  //lo mismo que en el método anterior, pero para la pantalla horizontal
                }
                // Quita el listener
                binding.map2?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }
        })
        recordButton = view.findViewById(R.id.floatingActionButton)
        recordButton.setOnClickListener {
            startRecording()  //si pulsas el botón de grabar, empieza la grabación
        }
    }

    private fun startRecording() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                RECORD_AUDIO_PERMISSION
            ) != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(
                requireContext(),
                WRITE_EXTERNAL_STORAGE_PERMISSION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar el permiso RECORD_AUDIO o el WRITE_EXTERNAL_STORAGE si no están concedidos
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(RECORD_AUDIO_PERMISSION, WRITE_EXTERNAL_STORAGE_PERMISSION),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        try {
            val bufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT
            )
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )
            val dir = File(Environment.getExternalStorageDirectory(), "Music")  //referencia al directorio
            dir.mkdirs()  // crea la carpeta Music, por si no existe en el directorio de almacenamiento externo
            outputFile = File(dir, "audio.raw")  //crea un objeto file que representa el archivo de salida de audio dentro de "Music"
            outputAudioDir = outputFile!!.absolutePath //ruta absoluta archivo salida
            outputStream = FileOutputStream(outputFile) //para escribir los datos de audio en el archivo
            val buffer = ByteArray(bufferSize)

            audioRecord?.startRecording()
            isRecording = true
            recordButton.isEnabled = false   //desactiva el botón para evitar que se interrumpa la grabación
            recordButton.isPressed = true
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Grabación iniciada", Toast.LENGTH_SHORT)
                    .show()  //la interfaz mostrará este mensaje
            }
            Log.d("iniciar", "Grabación iniciada")
            recordingThread = Thread {
                val startTime = System.currentTimeMillis() // Tiempo de inicio de la grabación
                while (isRecording && System.currentTimeMillis() - startTime < 3000L) { //se ejecuta mientas isRecording sea true, durante unos 3 segundos
                    val bytesRead = audioRecord?.read(buffer, 0, bufferSize)
                    if (bytesRead != null && bytesRead != AudioRecord.ERROR_INVALID_OPERATION) {
                        outputStream!!.write(buffer, 0, bytesRead)
                    }
                }
                stopRecording()
            }
            recordingThread?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun stopRecording() {
        try {
            requireActivity().runOnUiThread {
                recordButton.isEnabled = true
                recordButton.isPressed = false
                Toast.makeText(
                    requireContext(),
                    "Grabación finalizada",
                    Toast.LENGTH_SHORT
                ).show()
            }
            audioRecord?.stop()
            audioRecord?.release()
            isRecording = false
            recordingThread?.join()
            //cerrar el archivo de grabación
            outputStream?.close()
            val listaProductosFile = File("C:\\Users\\noeli\\AndroidStudioProjects\\lista_productos.txt")
            // Enviar el archivo de audio al servidor
            sendAudioToServer(outputFile, listaProductosFile)

        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun sendAudioToServer(audioFile: File?, listaProductosFile: File) {
        val client = OkHttpClient()
        // Crea una solicitud HTTP POST para enviar el archivo al servidor
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "audio",
                "audio.raw",
                audioFile!!.asRequestBody("audio/raw".toMediaTypeOrNull()))
            .addFormDataPart(
                "lista_productos",
                "lista_productos.txt",
                listaProductosFile.asRequestBody("text/plain".toMediaTypeOrNull()))
            .build()

        val request = Request.Builder()
            .url("http://91.242.153.52:631/Escritorio/iatros/audio_files/upload.php")  // dirección IP y puerto servidor
            .post(requestBody)
            .build()

        // Envía la solicitud al servidor
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Error al enviar la solicitud
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) { //completar luego
                if (response.isSuccessful) {
                    //val responseBody = response.body?.string()
                    // Eliminar el archivo de audio después de recibir la respuesta
                    //audioFile.delete()
                } else {
                    // La solicitud no fue exitosa
                    // Puedes manejar el caso de error aquí
                }
            }
        })
    }

    //para asegurarnos que independientemente del dispositivo, los productos se localizan bien
    private fun calculateCoordinatesV(prod:Producto): Pair<Float, Float> {
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
    private fun calculateCoordinatesH(prod:Producto): Pair<Float, Float> {
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
        binding.marca1?.let { marca1 ->  //orientación vertical mapa
            marca1.translationX = 0.0f   //asegurarnos de que se coloca desde el comienzo
            marca1.translationY = 0.0f
            marca1.translationX = coordenadasV.first
            marca1.translationY = coordenadasV.second
            marca1.visibility = View.VISIBLE
        }  //comprueba que no haya null

    }
    private fun moveImageToLocation2(producto: Producto) {
        val coordenadasH = calculateCoordinatesH(producto)
        binding.marca2?.let { marca2 ->   //orientación horizontal mapa
            marca2.translationX = 0.0f
            marca2.translationY = 0.0f
            marca2.translationX = coordenadasH.first
            marca2.translationY = coordenadasH.second
            marca2.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   //libera los recursos asociados al binding
    }


}