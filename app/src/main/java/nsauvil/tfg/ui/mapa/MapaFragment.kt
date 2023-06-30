package nsauvil.tfg.ui.mapa

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton

import nsauvil.tfg.R
import nsauvil.tfg.databinding.FragmentMapaBinding
import nsauvil.tfg.ui.domain.model.Producto
import nsauvil.tfg.ui.productos.ProductosViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt

private const val SAMPLE_RATE = 44100
private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
private val RECORDING_DURATION = 3000L // Duración de grabación en milisegundos (5 segundos)

class MapaFragment: Fragment(R.layout.fragment_mapa){
    private var _binding : FragmentMapaBinding? = null //mantiene la referencia al binding y se inicializa a null
    private val binding get() = _binding!!  //da acceso a la referencia anterior
    private val viewModel: ProductosViewModel by activityViewModels()
    private lateinit var recordButton: FloatingActionButton

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false
    private val RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    private val PERMISSION_REQUEST_CODE = 200
    private var outputAudio: String = "" // Ruta de salida para el archivo de audio grabado
    private var outputStream: FileOutputStream?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapaBinding.bind(view)
        //viewModel.selectedProduct.observe(viewLifecycleOwner) {prod->
            //moveImageToLocation(prod)
        //}
        binding.map1?.viewTreeObserver?.addOnGlobalLayoutListener (object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {  //para asegurarnos de que la imagen no da null
                viewModel.selectedProduct.observe(viewLifecycleOwner) { prod ->
                    moveImageToLocation(prod)
                }
                // Remueve el listener una vez que se ha obtenido el ancho
                binding.map1?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }

        })
        binding.map2?.viewTreeObserver?.addOnGlobalLayoutListener (object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {  //para asegurarnos de que la imagen no da null
                viewModel.selectedProduct.observe(viewLifecycleOwner) { prod ->
                    moveImageToLocation2(prod)
                }
                // Remueve el listener una vez que se ha obtenido el ancho
                binding.map2?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            }

        })


        recordButton = view.findViewById(R.id.floatingActionButton)
        recordButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                if (checkPermission()) {
                    startRecording()
                } else {
                    requestPermission()
                }
            }
        }

    }
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            RECORD_AUDIO_PERMISSION
        )
        return result == PackageManager.PERMISSION_GRANTED
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(RECORD_AUDIO_PERMISSION),
            PERMISSION_REQUEST_CODE
        )
    }
    private fun startRecording() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar el permiso RECORD_AUDIO si no está concedido
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
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
            audioRecord =  AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )

            //val dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM) //tarjeta al almacenamiento externo
            val dir = File(Environment.getExternalStorageDirectory(), "Music")
            dir.mkdirs()

            //val outputFile = File(dir, "grabacion.wav")
            // Verificar si el archivo ya existe y eliminarlo si es necesario
            var counter = 0
            var outputFile: File

            do {
                counter++
                val fileName = "grabacion$counter.wav"
                outputFile = File(dir, fileName)
            } while (outputFile.exists())

            outputAudio = outputFile.absolutePath

            outputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(bufferSize)

            audioRecord?.startRecording()

            isRecording = true
            recordButton.isEnabled = false
            recordButton.isPressed = true
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Grabación iniciada", Toast.LENGTH_SHORT).show()
            }
            Log.d("iniciar","Grabación iniciada")

            recordingThread = Thread {
                val startTime = System.currentTimeMillis() // Tiempo de inicio de la grabación
                while (isRecording && System.currentTimeMillis() - startTime < RECORDING_DURATION) {
                    val bytesRead = audioRecord?.read(buffer, 0, bufferSize)

                    if (bytesRead != null && bytesRead != AudioRecord.ERROR_INVALID_OPERATION) {
                        outputStream!!.write(buffer, 0, bytesRead)
                    }
                }
                //requireActivity().runOnUiThread {
                    //Toast.makeText(requireContext(), "Grabando...", Toast.LENGTH_SHORT).show()
                //}
                //outputStream.close()
                stopRecording()
            }
            recordingThread?.start()

            // Actualizar la interfaz de usuario si es necesario
            //updateUI()
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
                    "Grabación finalizada. Archivo: $outputAudio",
                    Toast.LENGTH_SHORT
                ).show()
            }

            audioRecord?.stop()
            audioRecord?.release()
            isRecording = false
            recordingThread?.join()

            //cerrar el archivo de grabación
            outputStream?.close()

            // Eliminar el archivo de grabación anterior
            val previousRecording = File(outputAudio)
            if (previousRecording.exists()) {
                previousRecording.delete()
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
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