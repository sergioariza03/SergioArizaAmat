package es.ua.eps.filmoteca

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ua.eps.filmoteca.databinding.ItemFilmBinding

class FilmRecyclerAdapter(
    private val films: List<Film>,
    private val onClick: (position: Int) -> Unit
) : RecyclerView.Adapter<FilmRecyclerAdapter.ViewHolder>() {

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

        holder.itemView.setOnClickListener {
            val adapterPosition = holder.bindingAdapterPosition

            if (adapterPosition != RecyclerView.NO_POSITION) {
                onClick(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int = films.size
}
