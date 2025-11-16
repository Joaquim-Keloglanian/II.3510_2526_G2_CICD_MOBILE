package com.example.deviseapp.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.deviseapp.R

class CurrencyAdapter(
    context: Context,
    private val items: List<CurrencyDisplay>
) : ArrayAdapter<CurrencyDisplay>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_currency, parent, false)
        val item = getItem(position)
        val textView = view.findViewById<TextView>(R.id.currencyText)
        textView.text = item?.label() ?: ""
        return view
    }

    fun findPositionByCode(code: String): Int =
        items.indexOfFirst { it.code == code }.takeIf { it >= 0 } ?: 0
}


