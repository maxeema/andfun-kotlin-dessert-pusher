package maxeem.america.desserts.model

import androidx.lifecycle.ViewModel
import maxeem.america.desserts.R
import maxeem.america.desserts.app
import maxeem.america.desserts.packageInfo
import maxeem.america.desserts.util.fromHtml

class AboutModel : ViewModel() {

    val author = app.getString(R.string.app_author)
    val version = app.packageInfo.versionName
    val description = app.getString(R.string.app_description).fromHtml()

}
