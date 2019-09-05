package maxeem.america.desserts.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("goneIf")
fun View.goneIf(condition: Boolean) {
    visibility = if (condition) View.GONE else View.VISIBLE
}

@BindingAdapter("textHtml")
fun TextView.setTextHtml(str: String) {
    text = str.fromHtml()
}
