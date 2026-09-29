package com.stocksync.error404

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class LeftFragment : Fragment(R.layout.fragment_left) {

    interface OnOptionSelectedListener {
        fun onOptionSelected(option: String)
    }

    private var listener: OnOptionSelectedListener? = null
    private var selectedPosition: Int = 1 // Por defecto "Inicio" (posición 1)

    data class SidebarItem(
        val title: String,
        val iconResId: Int,
        val actionName: String
    )

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

        val items = listOf(
            SidebarItem("Categorías", R.drawable.ic_sidebar_categories, "Categorías"),
            SidebarItem("Inicio", R.drawable.ic_sidebar_home, "Inicio"),
            SidebarItem("Catálogo", R.drawable.ic_sidebar_catalog, "Catálogo"),
            SidebarItem("Videos", R.drawable.ic_sidebar_videos, "Videos"),
            SidebarItem("Mi Cuenta", R.drawable.ic_sidebar_account, "Mi Cuenta")
        )

        val listView = view.findViewById<ListView>(R.id.optionsListView)

        val adapter = object : ArrayAdapter<SidebarItem>(
            requireContext(),
            R.layout.item_sidebar_button,
            items
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val rowView = convertView ?: layoutInflater.inflate(R.layout.item_sidebar_button, parent, false)
                val item = items[position]

                val container = rowView.findViewById<View>(R.id.itemContainer)
                val iconView = rowView.findViewById<ImageView>(R.id.itemIcon)
                val textView = rowView.findViewById<TextView>(R.id.itemText)

                iconView.setImageResource(item.iconResId)
                textView.text = item.title

                if (position == selectedPosition) {
                    container.setBackgroundResource(R.drawable.bg_sidebar_item_selected)
                    iconView.setColorFilter(Color.parseColor("#FFFFFF"))
                    textView.setTextColor(Color.parseColor("#FFFFFF"))
                } else {
                    container.background = null
                    iconView.setColorFilter(Color.parseColor("#F4F5F8"))
                    textView.setTextColor(Color.parseColor("#F4F5F8"))
                }

                return rowView
            }
        }

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedPosition = position
            adapter.notifyDataSetChanged()
            val selected = items[position]
            listener?.onOptionSelected(selected.actionName)
        }
    }
}
