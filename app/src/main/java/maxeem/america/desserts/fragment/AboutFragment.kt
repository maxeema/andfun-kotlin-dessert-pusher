package maxeem.america.desserts.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import maxeem.america.desserts.databinding.FragmentAboutBinding
import maxeem.america.desserts.model.AboutModel
import org.jetbrains.anko.AnkoLogger

class AboutFragment : Fragment(), AnkoLogger {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentAboutBinding.inflate(inflater, container, false).apply {
            model = viewModels<AboutModel>().value
        }.root

}
