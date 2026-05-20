package com.sakuga.app.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sakuga.app.databinding.ItemTagSuggestionBinding
import com.sakuga.app.domain.model.Tag

class TagAutocompletePopup(
    private val context: Context,
    private val anchor: View,
    private val onTagSelected: (Tag) -> Unit
) {
    // Unused but kept for API compat — actual fetch is driven externally via showSuggestions()
    var fetchTags: (suspend (String) -> List<Tag>)? = null

    private val tagAdapter = TagSuggestionAdapter { tag ->
        popup.dismiss()
        onTagSelected(tag)
    }

    private val recycler = RecyclerView(context).apply {
        layoutManager = LinearLayoutManager(context)
        adapter = tagAdapter
        setBackgroundColor(0xFFFFFFFF.toInt())
    }

    private val popup = PopupWindow(context).apply {
        contentView = recycler
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = false
        elevation = 16f
    }

    fun showSuggestions(tags: List<Tag>) {
        tagAdapter.submit(tags)
        recycler.measure(
            View.MeasureSpec.makeMeasureSpec(anchor.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(
                minOf(tags.size * 160, 480),  // cap at ~3 rows
                View.MeasureSpec.AT_MOST
            )
        )
        popup.width  = anchor.width
        popup.height = recycler.measuredHeight.coerceAtLeast(80)

        if (popup.isShowing) {
            popup.update()
        } else {
            popup.showAsDropDown(anchor)
        }
    }

    fun dismiss() {
        if (popup.isShowing) popup.dismiss()
    }
}

class TagSuggestionAdapter(
    private val onClick: (Tag) -> Unit
) : RecyclerView.Adapter<TagSuggestionAdapter.VH>() {

    private val items = mutableListOf<Tag>()

    fun submit(tags: List<Tag>) {
        items.clear()
        items.addAll(tags.take(10))
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemTagSuggestionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    inner class VH(private val b: ItemTagSuggestionBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(tag: Tag) {
            b.tvTagName.text  = tag.name
            b.tvTagCount.text = formatCount(tag.count)
            b.root.setOnClickListener { onClick(tag) }
        }

        private fun formatCount(count: Int): String = when {
            count >= 1000 -> "${count / 1000}k"
            else          -> count.toString()
        }
    }
}
