package nsauvil.tfg.ui.mapa


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapaViewModel: ViewModel() {
    private var _imagenV = MutableLiveData<String>()
    val imagenV : LiveData<String>
        get() = _imagenV
    private var _imagenH = MutableLiveData<String>()
    val imagenH : LiveData<String>
        get() = _imagenH

    fun cambiarMapaV (imagen: String) {  //mapa vertical
        _imagenV.value = imagen
    }

    fun cambiarMapaH (imagen: String) {  //mapa horizontal
        _imagenH.value = imagen
    }
}