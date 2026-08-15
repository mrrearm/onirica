# Onirica — Diario dei Sogni (Kotlin, 100% offline)

App Android nativa scritta in Kotlin (Jetpack Compose) per scrivere i propri
sogni, ottenere un'interpretazione e consultarne lo storico.

## Indipendenza totale

Questo progetto **non** contiene:
- chiamate a modelli di intelligenza artificiale o LLM;
- riferimenti a Manus o ad altri servizi/template esterni;
- richieste di rete (l'`AndroidManifest.xml` non chiede `INTERNET`);
- account, login o backend remoto.

Tutto avviene sul dispositivo:
- **Interpretazione**: `interpreter/DreamInterpreter.kt` è un motore
  deterministico basato su un dizionario di simboli onirici (ispirazione
  junghiana) e un piccolo lessico emotivo. Nessun modello, solo codice
  ispezionabile.
- **Database**: `data/DreamRepository.kt` salva tutti i sogni in un file
  JSON locale (`dreams.json`) nella storage privata dell'app, tramite
  `kotlinx.serialization`. È un database "integrato" leggero, senza
  server né librerie SQL: semplice da leggere, esportare o fare il backup.

## Struttura del progetto

```
app/src/main/java/com/onirica/dreamjournal/
├── MainActivity.kt              punto di ingresso, navigazione Home/Storico
├── data/
│   ├── Dream.kt                 modello dati del sogno
│   └── DreamRepository.kt       lettura/scrittura del file JSON locale
├── interpreter/
│   └── DreamInterpreter.kt      motore di interpretazione a regole
└── ui/
    ├── theme/Theme.kt           tema scuro "cosmico"
    └── screens/
        ├── HomeScreen.kt        scrittura del sogno + interpretazione
        └── HistoryScreen.kt     storico dei sogni interpretati
```

## Come compilare l'APK

Serve un JDK 17 e l'Android SDK (o Android Studio).

```bash
./gradlew assembleDebug
# APK generato in: app/build/outputs/apk/debug/app-debug.apk
```

Compilare direttamente in Termux richiede il pacchetto `android-sdk`/NDK
configurato via Termux (setup più laborioso); l'opzione più semplice resta
aprire il progetto in Android Studio, oppure compilarlo in una CI (es.
GitHub Actions) dopo aver pubblicato il repo.

## Pubblicare il progetto su GitHub da Termux

Nella cartella del progetto (dopo averla estratta sul telefono, es. in
`~/storage/downloads/onirica-android` o dove preferisci):

```bash
pkg install git -y
cd onirica-android
git init
git add .
git commit -m "Onirica: app Kotlin offline per l'interpretazione dei sogni"
git branch -M main

# crea prima il repository vuoto su github.com (senza README),
# poi collega il remote con il tuo username:
git remote add origin https://github.com/mrrearm/onirica-android.git
git push -u origin main
```

Se usi l'autenticazione via token invece della password:

```bash
git remote set-url origin https://<TUO_TOKEN>@github.com/mrrearm/onirica-android.git
git push -u origin main
```

## Note

- `minSdk 24`, `targetSdk 34`, Compose + Material 3.
- Nessuna dipendenza da librerie di terze parti oltre ad AndroidX,
  Jetpack Compose e `kotlinx-serialization-json`.
- Il dizionario simbolico in `DreamInterpreter.kt` è facilmente estendibile:
  basta aggiungere nuove voci alla lista `symbolDictionary`.
