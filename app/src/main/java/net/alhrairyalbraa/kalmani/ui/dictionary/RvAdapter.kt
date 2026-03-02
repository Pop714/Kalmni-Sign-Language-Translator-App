package net.alhrairyalbraa.kalmani.ui.dictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.alhrairyalbraa.kalmani.databinding.DictionaryLayoutBinding
import net.alhrairyalbraa.kalmani.utils.ClickListener

class RvAdapter(
    private var sentencesList: List<String>,
    private var listener: ClickListener
)
    : RecyclerView.Adapter<RvAdapter.ViewHolder>() {

    inner class ViewHolder(binding: DictionaryLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val sentenceText = binding.sentence
        val clickableIcon = binding.clickableBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DictionaryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RvAdapter.ViewHolder, position: Int) {
        val sentence = sentencesList[position]
        holder.sentenceText.text = sentence
        holder.clickableIcon.setOnClickListener {
            listener.show(sentence)
        }
    }

    override fun getItemCount(): Int = sentencesList.size
}