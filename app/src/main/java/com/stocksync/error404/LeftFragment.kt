package com.stocksync.error404

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class LeftFragment : Fragment(R.layout.fragment_left) {

    interface OnOptionSelectedListener {
        fun onOptionSelected(option: String)
    }

    private var listener: OnOptionSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnOptionSelectedListener) {
            listener = context
        }
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = listOf("Perfil", "Fotos", "Video", "Web", "Botones")
        val listView = view.findViewById<ListView>(R.id.optionsListView)

        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            options
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val text = view.findViewById<TextView>(android.R.id.text1)
                text.setTextColor(android.graphics.Color.parseColor("#F4F5F8"))
                text.textSize = 22f
                text.typeface = android.graphics.Typeface.DEFAULT_BOLD
                text.setPadding(20, 26, 20, 26)
                text.setBackgroundColor(android.graphics.Color.parseColor("#113D4D"))
                text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                return view
            }
        }

        listView.setBackgroundColor(android.graphics.Color.parseColor("#0B1F2A"))
        listView.setCacheColorHint(android.graphics.Color.parseColor("#0B1F2A"))
        listView.setSelector(android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#D84F52")))
        listView.divider = null
        listView.setPadding(12, 12, 12, 12)
        listView.isVerticalScrollBarEnabled = true

        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            listener?.onOptionSelected(options[position])
        }
    }
}
