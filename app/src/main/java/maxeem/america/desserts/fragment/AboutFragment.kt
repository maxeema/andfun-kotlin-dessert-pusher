package maxeem.america.desserts.fragment

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import maxeem.america.desserts.databinding.FragmentAboutBinding
import maxeem.america.desserts.model.AboutModel
import org.jetbrains.anko.AnkoLogger

class AboutFragment : Fragment(), AnkoLogger {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding =  FragmentAboutBinding.inflate(inflater, container, false)
        binding.model = viewModels<AboutModel>().value
        binding.description.movementMethod = LinkMovementMethod.getInstance()
        return binding.root
    }

}
