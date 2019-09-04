package maxeem.america.desserts.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import maxeem.america.desserts.R
import maxeem.america.desserts.app
import maxeem.america.desserts.databinding.FragmentAboutBinding
import maxeem.america.desserts.model.AboutModel
import maxeem.america.desserts.util.asString
import maxeem.america.desserts.util.onClick
import org.jetbrains.anko.AnkoLogger

class AboutFragment : Fragment(), AnkoLogger {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentAboutBinding.inflate(inflater, container, false).apply {
            model = viewModels<AboutModel>().value
            author.apply {
                val mail = Intent(Intent.ACTION_SENDTO)
                        .setData(Uri.parse("mailto:${R.string.author_email.asString()}"))
                isClickable = mail.resolveActivity(app.packageManager) != null
                if (isClickable) onClick {
                    startActivity(mail.apply {
                        putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject.asString(R.string.app_name.asString()))
                    })
                }
            }
            googlePlay.onClick {
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(
                            "https://play.google.com/store/apps/details?id=${app.packageName}")
                    `package` = "com.android.vending"
                    if (resolveActivity(app.packageManager) == null)
                        `package` = null
                    startActivity(this)
                }
            }
        }.root

}
