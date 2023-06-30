package nsauvil.tfg.ui.ajustes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import nsauvil.tfg.R
import nsauvil.tfg.databinding.FragmentAjustesBinding

class AjustesFragment: Fragment(R.layout.fragment_ajustes){
    private var _binding : FragmentAjustesBinding? = null //mantiene la referencia al binding y se inicializa a null
    private val binding get() = _binding!!  //da acceso a la referencia anterior

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAjustesBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   //libera los recursos asociados al binding
    }
}