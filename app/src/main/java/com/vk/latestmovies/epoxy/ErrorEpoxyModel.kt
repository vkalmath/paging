package com.vk.latestmovies.epoxy

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.vk.latestmovies.R

@EpoxyModelClass(layout = R.layout.erro_layout)
abstract class ErrorEpoxyModel : EpoxyModelWithHolder<ErrorEpoxyModel.Holder>() {

    @EpoxyAttribute
    var errorStr: String? = null

    override fun bind(holder: Holder) {
        holder.errorTextView.text = errorStr
    }

    class Holder : EpoxyHolder() {
        lateinit var errorTextView: TextView
        override fun bindView(itemView: View) {
            errorTextView = itemView.findViewById(R.id.error_tv)
        }
    }
}
