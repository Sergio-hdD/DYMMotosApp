package com.dymmotosapp

data class Entrega(
    val id: String,
    val comercio: String,
    val direccion: String,
    val estado: String
)

object EntregaParser {

    fun parse(text: String): List<Entrega> {
        val lines = text
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val entregas = mutableListOf<Entrega>()
        var i = 0

        val headerRegex = Regex("""#(\d+)\s*-\s*(.+)""")

        while (i + 2 < lines.size) {
            val header = lines[i]
            val direccion = lines[i + 1]
            val estado = lines[i + 2]

            val match = headerRegex.find(header)

            if (match != null) {
                val id = match.groupValues[1]
                val comercio = match.groupValues[2]

                entregas.add(
                    Entrega(
                        id = id,
                        comercio = comercio,
                        direccion = direccion,
                        estado = estado
                    )
                )
                i += 3
            } else {
                // Si el OCR falló en una línea, avanzamos una
                i++
            }
        }

        return entregas
    }
}