package com.dymmotosapp

data class DatosDeViaje(
    val idMercadoLibre: String,
    val razonSocial: String,
    val direccion: String,
    val localidad: String
)

object EntregaParser {

    fun extraerIds(text: String): List<String> {
        val ids = mutableListOf<String>()

        val firstHashIndex = Regex("(?=#\\d+)").find(text)?.range?.first
            ?: return ids

        val textoUtil = text.substring(firstHashIndex)

        val matcher = Regex("#(\\d+)").findAll(textoUtil)
        matcher.forEach {
            ids.add(it.groupValues[1])
        }

        return ids
    }

    fun parseBloques(
        textoCompleto: String,
        ids: List<String>
    ): List<DatosDeViaje> {

        val resultados = mutableListOf<DatosDeViaje>()

        ids.forEachIndexed { index, id ->

            val inicio = textoCompleto.indexOf(id)
            if (inicio == -1) return@forEachIndexed

            val bloque = if (index + 1 < ids.size) {
                val siguienteId = ids[index + 1]
                val fin = textoCompleto.indexOf(siguienteId)
                textoCompleto.substring(inicio, fin)
            } else {
                textoCompleto.substring(inicio)
            }

            if (!bloque.contains("Entrega exitosa")) return@forEachIndexed

            val bloqueLimpio = bloque
                .replace(Regex("Entrega exitosa[\\s\\S]*$"), "")
                .trim()

            if (bloqueLimpio.isBlank()) return@forEachIndexed

            // Divide donde hay salto de línea seguido de coma
            val subbloques = bloqueLimpio.split(Regex("\n(?=[^\\n]*,)"))
            if (subbloques.size < 2) return@forEachIndexed

            // Línea ID + Razón social
            var lineaHeader = subbloques[0]
                .replace("\n", " ")
                .replace(Regex("(#\\d+)-"), "$1 -")
                .trim()

            val partesHeader = lineaHeader.split(" - ")
            if (partesHeader.size < 2) return@forEachIndexed

            val idML = partesHeader[0].substring(1)
            val razonSocial = partesHeader[1].replace(Regex("\\s+"), " ")

            // Línea dirección + localidad (última coma)
            val lineaDireccion = subbloques[1].replace("\n", " ").trim()
            val partesDireccion = lineaDireccion.split(Regex(", (?=[^,]*$)"))
            if (partesDireccion.size < 2) return@forEachIndexed

            val direccion = partesDireccion[0]
            val localidad = partesDireccion[1]

            resultados.add(
                DatosDeViaje(
                    idMercadoLibre = idML,
                    razonSocial = razonSocial,
                    direccion = direccion,
                    localidad = localidad
                )
            )
        }

        return resultados
    }
}