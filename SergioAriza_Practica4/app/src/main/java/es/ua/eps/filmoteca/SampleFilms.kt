package es.ua.eps.filmoteca

fun sampleFilms() = listOf(
    Film(
        title = "Star Wars: Episodio IV - Una nueva esperanza",
        director = "George Lucas",
        year = 1977,
        genre = Film.Genre.SciFi,
        format = Film.Format.BluRay,
        imdbUrl = "https://www.imdb.com/title/tt0076759/",
        comments = "Edición coleccionista",
        imageResId = R.drawable.nueva_esperanza
    ),
    Film(
        title = "El Señor de los Anillos: La Comunidad del Anillo",
        director = "Peter Jackson",
        year = 2001,
        genre = Film.Genre.SciFi,
        format = Film.Format.BluRay,
        imdbUrl = "https://www.imdb.com/title/tt0120737/",
        comments = "Edición 25 aniversario",
        imageResId = R.drawable.anillos
    )
)
