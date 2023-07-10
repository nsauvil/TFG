package nsauvil.tfg.ui.productos

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import nsauvil.tfg.R
import nsauvil.tfg.databinding.FragmentProductosBinding
class ProductosFragment: Fragment(R.layout.fragment_productos){
    private var _binding : FragmentProductosBinding? = null //mantiene la referencia al binding y se inicializa a null
    private val binding get() = _binding!!  //da acceso a la referencia anterior
    private val viewModel: ProductosViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductosBinding.bind(view)
        val adapter = ProductoListAdapter()
        binding.recyclerView.adapter = adapter
        viewModel.productos.observe(viewLifecycleOwner) {al->
            adapter.submitList(al)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   //libera los recursos asociados al binding
    }


}