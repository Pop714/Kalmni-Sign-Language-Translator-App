package net.alhrairyalbraa.kalmani.ui.educational_section

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.alhrairyalbraa.kalmani.databinding.CategoryLayoutBinding
import net.alhrairyalbraa.kalmani.utils.ClickListener

class RvAdapter(
    private var sentencesList: List<String>,
    private var category: String,
    private var listener: ClickListener
)
    : RecyclerView.Adapter<RvAdapter.ViewHolder>() {

    inner class ViewHolder(binding: CategoryLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        val sentenceText = binding.word
        val watchBtn = binding.watch
        val testBtn = binding.test
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CategoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sentence = sentencesList[position]
        holder.sentenceText.text = sentence
        holder.watchBtn.setOnClickListener {
            listener.show(sentence)
        }
        holder.testBtn.setOnClickListener {
            listener.test(sentence, category)
        }
    }

    override fun getItemCount(): Int = sentencesList.size
}