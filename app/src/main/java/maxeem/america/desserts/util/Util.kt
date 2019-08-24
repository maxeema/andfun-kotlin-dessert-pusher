package maxeem.america.desserts.util

import androidx.core.text.HtmlCompat

/**
 * Utilities object
 */

object Util {

    fun fromHtml(s: String) = HtmlCompat.fromHtml(s, HtmlCompat.FROM_HTML_MODE_COMPACT)

}