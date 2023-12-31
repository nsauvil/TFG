package nsauvil.tfg.ui.productos

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import nsauvil.tfg.R
import nsauvil.tfg.databinding.FragmentProductosBinding
import java.util.*

class ProductosFragment: Fragment(R.layout.fragment_productos){
    private var _binding : FragmentProductosBinding? = null //Mantiene la referencia al binding y se inicializa a null
    private val binding get() = _binding!!  //Da acceso a la referencia anterior
    private val viewModel: ProductosViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductosBinding.bind(view)
        val adapter = ProductoListAdapter()
        binding.recyclerView.adapter = adapter

        val idioma = Locale.getDefault().language //Muestra los productos en función del idioma predeterminado del dispositivo
        val prodMostrar = if (idioma == "en") {
            viewModel.productosEn
        } else {
            viewModel.productosEs
        }
        prodMostrar.observe(viewLifecycleOwner) {al->  //Cualquiera de los dos (en/es) vale
            adapter.submitList(al)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   //Libera los recursos asociados al binding
    }


}