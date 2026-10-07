package es.ua.eps.filmoteca

enum class Mode {
    Bindings,
    Compose,
}

val GlobalMode = Mode.Bindings


enum class ListMode {
    ListView,
    RecyclerView,
}

val GlobalListMode = ListMode.RecyclerView
