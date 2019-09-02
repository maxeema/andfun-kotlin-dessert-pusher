package maxeem.america.desserts

import android.app.Application
import android.os.Handler

val app = DessertsApp.instance

class DessertsApp : Application() {

    companion object {
        private lateinit var appRef : DessertsApp
        val instance get() = appRef
    }

    init { appRef = this }

    val handler by lazy { Handler() }

}

val DessertsApp.packageInfo
    get() = packageManager.getPackageInfo(packageName, 0)