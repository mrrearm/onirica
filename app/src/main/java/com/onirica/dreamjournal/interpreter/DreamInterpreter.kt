package com.onirica.dreamjournal.interpreter

/**
 * Motore di interpretazione dei sogni completamente locale e deterministico.
 *
 * Non contatta nessun servizio, non usa modelli linguistici e non dipende
 * da nessuna libreria di intelligenza artificiale: analizza il testo del
 * sogno confrontandolo con un dizionario di simboli onirici (ispirato alla
 * tradizione junghiana) e con un piccolo lessico emotivo, poi compone
 * un'interpretazione leggibile a partire da template testuali.
 *
 * È volutamente semplice e ispezionabile: chiunque può leggere il codice
 * e capire esattamente perché è stata generata una certa interpretazione,
 * cosa impossibile con un modello black-box.
 */
object DreamInterpreter {

    data class Result(
        val title: String,
        val interpretation: String,
        val symbols: List<String>,
        val mood: String
    )

    private data class Symbol(val keywords: List<String>, val label: String, val meaning: String)

    // Dizionario simbolico: parola chiave -> significato archetipico.
    // Facilmente estendibile aggiungendo nuove voci a questa lista.
    private val symbolDictionary = listOf(
        Symbol(listOf("acqua", "mare", "oceano", "onde", "nuotare", "fiume"), "Acqua",
            "l'acqua rimanda al mondo emotivo e all'inconscio: la sua calma o il suo tumulto riflettono lo stato interiore di chi sogna."),
        Symbol(listOf("volare", "volo", "ali", "cielo"), "Volo",
            "volare in sogno esprime spesso un desiderio di libertà, di superare un limite o un ostacolo percepito nella vita da svegli."),
        Symbol(listOf("cadere", "caduta", "precipizio", "precipitare"), "Caduta",
            "la caduta è tra i simboli più antichi del sogno: segnala insicurezza, perdita di controllo o paura di un fallimento imminente."),
        Symbol(listOf("casa", "stanza", "porta", "corridoio", "finestra"), "Casa",
            "la casa rappresenta la struttura del sé: le sue stanze sono spesso lette come diverse parti della personalità da esplorare."),
        Symbol(listOf("morte", "morire", "funerale", "tomba"), "Morte",
            "la morte onirica raramente è letterale: più spesso annuncia la fine di una fase e l'inizio di una trasformazione."),
        Symbol(listOf("inseguito", "inseguimento", "fuggire", "scappare", "correre via"), "Inseguimento",
            "essere inseguiti riflette un evitamento: qualcosa - un'emozione, una responsabilità - che si preferisce non affrontare direttamente."),
        Symbol(listOf("nudo", "nuda", "spogliato", "vestiti"), "Nudità",
            "la nudità in sogno tocca il tema della vulnerabilità e del timore di essere giudicati per ciò che si è realmente."),
        Symbol(listOf("denti", "dente", "perdere i denti"), "Denti",
            "la perdita dei denti è un classico simbolo di ansia legata all'immagine di sé, al tempo che passa o alla paura di perdere potere personale."),
        Symbol(listOf("serpente", "serpenti", "vipera"), "Serpente",
            "il serpente è un simbolo ambivalente: trasformazione e rinnovamento, ma anche minaccia latente o tradimento."),
        Symbol(listOf("bambino", "bambina", "neonato"), "Bambino",
            "il bambino onirico rappresenta spesso una parte nuova, fragile e ancora in crescita di sé, o un progetto appena nato."),
        Symbol(listOf("buio", "oscurità", "notte", "ombra"), "Ombra",
            "l'oscurità richiama l'archetipo dell'Ombra junghiana: gli aspetti di sé non ancora riconosciuti o accettati."),
        Symbol(listOf("specchio", "riflesso"), "Specchio",
            "lo specchio invita a un confronto diretto con l'immagine che si ha di sé, a volte rivelandone una versione distorta o inattesa."),
        Symbol(listOf("scuola", "esame", "interrogazione", "compito"), "Esame",
            "sognare un esame segnala il timore di essere valutati o di non essere all'altezza di un'aspettativa, propria o altrui."),
        Symbol(listOf("treno", "perdere il treno", "aereo", "perdere l'aereo", "auto", "macchina", "strada", "viaggio"), "Viaggio",
            "i mezzi di trasporto rappresentano il percorso di vita: perderli o guidarli male esprime il senso di controllo (o la sua assenza) sulla propria direzione."),
        Symbol(listOf("fuoco", "incendio", "fiamme", "brucia"), "Fuoco",
            "il fuoco è energia trasformativa: può indicare passione intensa oppure una situazione emotiva che rischia di sfuggire di mano."),
        Symbol(listOf("animale", "animali", "cane", "gatto", "lupo", "leone", "uccello"), "Animale",
            "le figure animali incarnano istinti e pulsioni non del tutto integrati nella vita cosciente."),
        Symbol(listOf("labirinto", "perso", "persa", "smarrito", "smarrita"), "Smarrimento",
            "sentirsi persi in un labirinto riflette un momento di incertezza rispetto a una scelta o a una direzione da prendere."),
        Symbol(listOf("mare in tempesta", "tempesta", "temporale", "uragano"), "Tempesta",
            "la tempesta è emozione che preme per esprimersi: un conflitto interiore vicino a raggiungere il culmine."),
        Symbol(listOf("volto", "faccia", "maschera"), "Maschera",
            "una maschera o un volto irriconoscibile suggeriscono la distanza tra il sé pubblico e quello autentico."),
        Symbol(listOf("gravidanza", "incinta", "partorire"), "Gravidanza",
            "la gravidanza onirica, al di là del significato letterale, è spesso metafora di un progetto o un'idea in gestazione.")
    )

    private val positiveWords = listOf(
        "felice", "felicità", "gioia", "sereno", "serena", "pace", "amore", "libertà", "libero", "libera",
        "leggero", "leggera", "luce", "sorriso", "calma", "abbraccio", "volare", "festa"
    )

    private val negativeWords = listOf(
        "paura", "ansia", "angoscia", "terrore", "urlo", "urlare", "piangere", "pianto", "morte", "morire",
        "buio", "solo", "sola", "perso", "persa", "smarrito", "smarrita", "cadere", "inseguito", "inseguita",
        "soffocare", "intrappolato", "intrappolata", "rabbia", "tradimento"
    )

    fun interpret(rawContent: String): Result {
        val content = rawContent.trim()
        val lower = content.lowercase()

        val matchedSymbols = symbolDictionary.filter { symbol ->
            symbol.keywords.any { keyword -> lower.contains(keyword) }
        }

        val positiveHits = positiveWords.count { lower.contains(it) }
        val negativeHits = negativeWords.count { lower.contains(it) }
        val mood = when {
            positiveHits > negativeHits -> "luminoso"
            negativeHits > positiveHits -> "inquieto"
            else -> "ambivalente"
        }

        val title = buildTitle(matchedSymbols.map { it.label }, mood)
        val interpretation = buildInterpretation(content, matchedSymbols, mood)

        return Result(
            title = title,
            interpretation = interpretation,
            symbols = matchedSymbols.map { it.label },
            mood = mood
        )
    }

    private fun buildTitle(symbolLabels: List<String>, mood: String): String {
        if (symbolLabels.isEmpty()) {
            return when (mood) {
                "luminoso" -> "Un sogno di quiete"
                "inquieto" -> "Un sogno inquieto"
                else -> "Un sogno sospeso"
            }
        }
        val main = symbolLabels.first()
        return "Il sogno di $main".let { if (symbolLabels.size > 1) "$it e ${symbolLabels[1].lowercase()}" else it }
    }

    private fun buildInterpretation(content: String, symbols: List<Symbol>, mood: String): String {
        val sb = StringBuilder()

        sb.appendLine("SIMBOLI RICORRENTI")
        if (symbols.isEmpty()) {
            sb.appendLine(
                "Non ho riconosciuto simboli ricorrenti tra quelli catalogati, ma questo non rende il sogno " +
                    "meno significativo: a volte sono i dettagli più personali, non i grandi archetipi, a portare " +
                    "il senso più profondo. Prova a soffermarti su cosa provavi mentre il sogno accadeva."
            )
        } else {
            symbols.forEach { symbol ->
                sb.appendLine("• ${symbol.label}: ${symbol.meaning}")
            }
        }

        sb.appendLine()
        sb.appendLine("DINAMICHE EMOTIVE")
        sb.appendLine(
            when (mood) {
                "luminoso" -> "Il tono emotivo che emerge dal racconto è prevalentemente disteso: sembra che " +
                    "questo sogno rifletta un momento di equilibrio o un desiderio di leggerezza che sta " +
                    "trovando spazio nella tua vita da sveglio."
                "inquieto" -> "Il tono emotivo che emerge dal racconto è teso: il sogno sembra dare voce a una " +
                    "preoccupazione, una tensione irrisolta o una paura che chiede di essere riconosciuta, non " +
                    "necessariamente risolta subito."
                else -> "Il racconto non pende chiaramente né verso la leggerezza né verso il disagio: è un " +
                    "sogno più contemplativo, che forse riflette un momento di passaggio o di attesa nella tua vita."
            }
        )

        sb.appendLine()
        sb.appendLine("RIFLESSIONE FINALE")
        sb.appendLine(
            "Questa lettura nasce dal confronto tra le parole del tuo racconto e un dizionario simbolico " +
                "generale: non è una previsione né una diagnosi, ma uno spunto per osservarti con più attenzione. " +
                "Il significato più vero di un sogno lo conosce solo chi lo ha vissuto: lascia che questa " +
                "interpretazione sia un punto di partenza, non una risposta definitiva."
        )

        return sb.toString().trim()
    }
}
