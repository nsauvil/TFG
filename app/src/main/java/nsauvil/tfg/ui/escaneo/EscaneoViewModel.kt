package nsauvil.tfg.ui.escaneo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EscaneoViewModel : ViewModel() {
    private var _buttonQRPressed = MutableLiveData<Boolean>(false)
    val buttonQRPressed: LiveData<Boolean>
        get() = _buttonQRPressed

    fun pressScanButton() {
        _buttonQRPressed.value = true
    }
}