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
 * Se nessun simbolo del dizionario compare nel racconto, il motore non si
 * arrende: individua le parole più significative e ricorrenti del testo
 * (escludendo articoli, congiunzioni, ecc.) e le propone come "simboli
 * personali", così l'interpretazione ha sempre qualcosa di concreto su cui
 * riflettere anche per sogni molto specifici o insoliti.
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
        Symbol(listOf("acqua", "mare", "oceano", "onde", "nuotare", "fiume", "lago", "piscina", "pioggia"), "Acqua",
            "l'acqua rimanda al mondo emotivo e all'inconscio: la sua calma o il suo tumulto riflettono lo stato interiore di chi sogna."),
        Symbol(listOf("volare", "volo", "ali", "cielo", "librarsi", "planare"), "Volo",
            "volare in sogno esprime spesso un desiderio di libertà, di superare un limite o un ostacolo percepito nella vita da svegli."),
        Symbol(listOf("cadere", "caduta", "precipizio", "precipitare", "sprofondare"), "Caduta",
            "la caduta è tra i simboli più antichi del sogno: segnala insicurezza, perdita di controllo o paura di un fallimento imminente."),
        Symbol(listOf("casa", "stanza", "porta", "corridoio", "finestra", "soffitta", "cantina", "scale"), "Casa",
            "la casa rappresenta la struttura del sé: le sue stanze sono spesso lette come diverse parti della personalità da esplorare."),
        Symbol(listOf("morte", "morire", "funerale", "tomba", "lapide", "bara"), "Morte",
            "la morte onirica raramente è letterale: più spesso annuncia la fine di una fase e l'inizio di una trasformazione."),
        Symbol(listOf("inseguito", "inseguimento", "fuggire", "scappare", "correre via", "nascondersi"), "Inseguimento",
            "essere inseguiti riflette un evitamento: qualcosa - un'emozione, una responsabilità - che si preferisce non affrontare direttamente."),
        Symbol(listOf("nudo", "nuda", "spogliato", "spogliata", "vestiti"), "Nudità",
            "la nudità in sogno tocca il tema della vulnerabilità e del timore di essere giudicati per ciò che si è realmente."),
        Symbol(listOf("denti", "dente", "perdere i denti"), "Denti",
            "la perdita dei denti è un classico simbolo di ansia legata all'immagine di sé, al tempo che passa o alla paura di perdere potere personale."),
        Symbol(listOf("serpente", "serpenti", "vipera"), "Serpente",
            "il serpente è un simbolo ambivalente: trasformazione e rinnovamento, ma anche minaccia latente o tradimento."),
        Symbol(listOf("bambino", "bambina", "neonato", "bimbo", "bimba"), "Bambino",
            "il bambino onirico rappresenta spesso una parte nuova, fragile e ancora in crescita di sé, o un progetto appena nato."),
        Symbol(listOf("buio", "oscurità", "notte", "ombra", "tenebre"), "Ombra",
            "l'oscurità richiama l'archetipo dell'Ombra junghiana: gli aspetti di sé non ancora riconosciuti o accettati."),
        Symbol(listOf("specchio", "riflesso"), "Specchio",
            "lo specchio invita a un confronto diretto con l'immagine che si ha di sé, a volte rivelandone una versione distorta o inattesa."),
        Symbol(listOf("scuola", "esame", "interrogazione", "compito", "professore", "professoressa"), "Esame",
            "sognare un esame segnala il timore di essere valutati o di non essere all'altezza di un'aspettativa, propria o altrui."),
        Symbol(listOf("treno", "aereo", "auto", "macchina", "strada", "viaggio", "autobus", "bicicletta", "nave", "barca"), "Viaggio",
            "i mezzi di trasporto rappresentano il percorso di vita: perderli o guidarli male esprime il senso di controllo (o la sua assenza) sulla propria direzione."),
        Symbol(listOf("fuoco", "incendio", "fiamme", "brucia", "bruciare"), "Fuoco",
            "il fuoco è energia trasformativa: può indicare passione intensa oppure una situazione emotiva che rischia di sfuggire di mano."),
        Symbol(listOf("animale", "animali", "cane", "gatto", "lupo", "leone", "uccello", "cavallo", "orso", "pesce", "insetto", "ragno", "farfalla"), "Animale",
            "le figure animali incarnano istinti e pulsioni non del tutto integrati nella vita cosciente."),
        Symbol(listOf("labirinto", "perso", "persa", "smarrito", "smarrita", "disorientato", "disorientata"), "Smarrimento",
            "sentirsi persi in un labirinto riflette un momento di incertezza rispetto a una scelta o a una direzione da prendere."),
        Symbol(listOf("tempesta", "temporale", "uragano", "fulmine", "fulmini"), "Tempesta",
            "la tempesta è emozione che preme per esprimersi: un conflitto interiore vicino a raggiungere il culmine."),
        Symbol(listOf("volto", "faccia", "maschera", "irriconoscibile"), "Maschera",
            "una maschera o un volto irriconoscibile suggeriscono la distanza tra il sé pubblico e quello autentico."),
        Symbol(listOf("gravidanza", "incinta", "partorire", "parto"), "Gravidanza",
            "la gravidanza onirica, al di là del significato letterale, è spesso metafora di un progetto o un'idea in gestazione."),
        Symbol(listOf("lavoro", "ufficio", "capo", "collega", "riunione", "licenziato", "licenziata"), "Lavoro",
            "gli scenari lavorativi nei sogni parlano spesso di riconoscimento, prestazione e del peso delle responsabilità quotidiane."),
        Symbol(listOf("matrimonio", "sposa", "sposo", "sposarsi", "anello"), "Unione",
            "il matrimonio onirico simboleggia un'unione, non necessariamente romantica: può indicare l'integrazione di parti diverse di sé."),
        Symbol(listOf("soldi", "denaro", "monete", "banconote", "portafoglio", "ricco", "ricca", "povero", "povera"), "Denaro",
            "il denaro nei sogni è spesso metafora di valore personale, energia vitale o sicurezza, più che una questione puramente economica."),
        Symbol(listOf("montagna", "vetta", "salita", "scalare", "arrampicarsi"), "Montagna",
            "salire una montagna rappresenta uno sforzo verso un obiettivo importante e il desiderio di guadagnare una prospettiva più ampia."),
        Symbol(listOf("chiave", "chiavi", "serratura", "bloccato", "bloccata", "intrappolato", "intrappolata"), "Soglia",
            "chiavi, serrature e porte chiuse parlano di accessi negati o desiderati: qualcosa che si vuole raggiungere ma non si riesce ancora ad aprire."),
        Symbol(listOf("fantasma", "fantasmi", "spirito", "presenza"), "Presenza invisibile",
            "presenze non del tutto definite nel sogno spesso incarnano emozioni o ricordi non ancora del tutto elaborati."),
        Symbol(listOf("esplosione", "esplodere", "bomba", "guerra", "battaglia", "combattimento"), "Conflitto",
            "scene di conflitto o distruzione riflettono tensioni interiori che cercano una via di scarico o di risoluzione."),
        Symbol(listOf("spiaggia", "sole", "tramonto", "alba"), "Luce naturale",
            "luce, albe e tramonti accompagnano spesso momenti di passaggio, chiusura o apertura di un ciclo emotivo.")
    )

    private val positiveWords = listOf(
        "felice", "felicità", "gioia", "sereno", "serena", "pace", "amore", "libertà", "libero", "libera",
        "leggero", "leggera", "luce", "sorriso", "calma", "abbraccio", "volare", "festa", "ridere", "risata"
    )

    private val negativeWords = listOf(
        "paura", "ansia", "angoscia", "terrore", "urlo", "urlare", "piangere", "pianto", "morte", "morire",
        "buio", "solo", "sola", "perso", "persa", "smarrito", "smarrita", "cadere", "inseguito", "inseguita",
        "soffocare", "intrappolato", "intrappolata", "rabbia", "tradimento", "panico", "incubo"
    )

    // Parole troppo comuni per essere considerate "simboli personali" quando
    // si ricorre all'estrazione di fallback (articoli, preposizioni, verbi
    // ausiliari, connettivi, ecc.).
    private val stopWords = setOf(
        "il", "lo", "la", "i", "gli", "le", "un", "uno", "una", "di", "a", "da", "in", "con", "su", "per",
        "tra", "fra", "e", "o", "ma", "che", "non", "come", "poi", "quando", "mentre", "anche", "più", "molto",
        "questo", "questa", "quello", "quella", "mi", "ti", "si", "ci", "vi", "lo", "la", "li", "le", "ne",
        "sono", "sei", "è", "siamo", "siete", "ero", "eri", "era", "eravamo", "eravate", "erano",
        "ho", "hai", "ha", "abbiamo", "avete", "hanno", "avevo", "aveva", "stavo", "stava", "stavamo",
        "mio", "mia", "miei", "mie", "tuo", "tua", "suo", "sua", "loro", "nostro", "nostra",
        "del", "della", "dello", "dei", "degli", "delle", "al", "allo", "alla", "ai", "agli", "alle",
        "nel", "nella", "nello", "nei", "negli", "nelle", "dal", "dallo", "dalla", "dai", "dagli", "dalle",
        "sul", "sulla", "sullo", "sui", "sugli", "sulle", "quindi", "però", "cioè", "cosa", "cose",
        "dove", "chi", "cui", "se", "già", "ancora", "sempre", "mai", "così", "tutto", "tutti", "tutta", "tutte"
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

        // Se il dizionario non ha trovato nulla, non ci arrendiamo: estraiamo
        // le parole più ricorrenti e significative del racconto come simboli
        // "personali" del sogno, per dare comunque uno spunto concreto.
        val personalKeywords = if (matchedSymbols.isEmpty()) extractPersonalKeywords(lower) else emptyList()

        val symbolLabels = if (matchedSymbols.isNotEmpty()) matchedSymbols.map { it.label } else personalKeywords

        val title = buildTitle(symbolLabels, mood)
        val interpretation = buildInterpretation(matchedSymbols, personalKeywords, mood)

        return Result(
            title = title,
            interpretation = interpretation,
            symbols = symbolLabels,
            mood = mood
        )
    }

    /**
     * Estrae fino a 3 parole "di contenuto" (lunghezza >= 4, non stopword)
     * che compaiono più spesso nel testo, come indizio di ciò su cui il
     * racconto insiste maggiormente. Semplice conteggio di frequenza, senza
     * alcuna libreria linguistica esterna.
     */
    private fun extractPersonalKeywords(lower: String): List<String> {
        val words = Regex("[a-zàèéìòù']+").findAll(lower)
            .map { it.value }
            .filter { it.length >= 4 && it !in stopWords }
            .toList()

        if (words.isEmpty()) return emptyList()

        return words.groupingBy { it }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { words.indexOf(it.key) })
            .take(3)
            .map { it.key.replaceFirstChar { c -> c.uppercase() } }
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

    private fun buildInterpretation(symbols: List<Symbol>, personalKeywords: List<String>, mood: String): String {
        val sb = StringBuilder()

        sb.appendLine("SIMBOLI RICORRENTI")
        when {
            symbols.isNotEmpty() -> {
                symbols.forEach { symbol ->
                    sb.appendLine("• ${symbol.label}: ${symbol.meaning}")
                }
            }
            personalKeywords.isNotEmpty() -> {
                sb.appendLine(
                    "Nel tuo racconto non compaiono simboli tra quelli più classici della tradizione onirica, " +
                        "ma alcune parole ricorrono più delle altre e probabilmente portano il senso più personale " +
                        "di questo sogno:"
                )
                personalKeywords.forEach { keyword ->
                    sb.appendLine("• $keyword: prova a chiederti cosa rappresenta per te nella vita da sveglio, e quale emozione porta con sé quando ci pensi.")
                }
            }
            else -> {
                sb.appendLine(
                    "Il racconto è troppo breve o generico per individuare simboli specifici. Prova ad aggiungere " +
                        "qualche dettaglio in più - luoghi, persone, oggetti, cosa provavi - per un'interpretazione più ricca."
                )
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
