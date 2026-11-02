
package es.ua.eps.filmoteca

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ua.eps.filmoteca.databinding.ItemFilmBinding

class FilmRecyclerAdapter(
    private val films: List<Film>,
    private val onClick: (position: Int) -> Unit,
    private val onLongClick: (position: Int) -> Unit
) : RecyclerView.Adapter<FilmRecyclerAdapter.ViewHolder>() {

    private val selectedPositions = mutableSetOf<Int>()

    class ViewHolder(
        val binding: ItemFilmBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemFilmBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.binding.bind(films[position])

        holder.itemView.isActivated = position in selectedPositions

        holder.itemView.setOnClickListener {
            val adapterPosition = holder.bindingAdapterPosition

            if (adapterPosition != RecyclerView.NO_POSITION) {
                onClick(adapterPosition)
            }
        }

        holder.itemView.setOnLongClickListener {
            val adapterPosition = holder.bindingAdapterPosition

            if (adapterPosition != RecyclerView.NO_POSITION) {
                onLongClick(adapterPosition)
                true
            } else {
                false
            }
        }
    }

    fun toggleSelection(position: Int) {
        if (!selectedPositions.add(position)) {
            selectedPositions.remove(position)
        }

        notifyItemChanged(position)
    }

    fun getSelectedPositions(): Set<Int> {
        return selectedPositions.toSet()
    }

    fun clearSelection() {
        val previousSelection = selectedPositions.toList()
        selectedPositions.clear()

        previousSelection.forEach { position ->
            if (position in films.indices) {
                notifyItemChanged(position)
            }
        }
    }

    fun restoreSelection(positions: Set<Int>) {
        selectedPositions.clear()
        selectedPositions.addAll(positions.filter { it in films.indices })
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = films.size
}
