package com.montreastro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var zodiacView: ZodiacView
    private lateinit var moonPositionTextView: TextView
    private lateinit var moonPositionDetailsTextView: TextView
    private lateinit var updateTimeTextView: TextView
    private lateinit var natalPositionsTextView: TextView

    private val astronomyCalculator = AstronomyCalculator()
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 1000L // Mise à jour toutes les secondes
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    // Date de naissance pour les positions natales (17/12/1991 7h27)
    private val birthYear = 1991
    private val birthMonth = 12
    private val birthDay = 17
    private val birthHour = 7
    private val birthMinute = 27

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser les vues
        zodiacView = findViewById(R.id.zodiacView)
        moonPositionTextView = findViewById(R.id.moonPositionTextView)
        moonPositionDetailsTextView = findViewById(R.id.moonPositionDetailsTextView)
        updateTimeTextView = findViewById(R.id.updateTimeTextView)
        natalPositionsTextView = findViewById(R.id.natalPositionsTextView)

        // Calculer les positions natales
        calculateNatalPositions()

        // Démarrer les mises à jour en temps réel
        startRealTimeUpdates()
    }

    private fun calculateNatalPositions() {
        // Positions exactes des astres nataux en degrés absolus (0-360°)
        // Converties à partir des positions dans les signes du zodiaque
        val sunPosition = 241.06  // Sagittaire 1° 03' 30.31"
        val moonPosition = 5.28   // Bélier 5° 17' 00.71"
        val mercuryPosition = 224.28  // Scorpion 14° 16' 53.09"
        val venusPosition = 199.09    // Balance 19° 05' 09.00"
        val marsPosition = 229.29     // Scorpion 19° 17' 13.46"
        val jupiterPosition = 140.58  // Lion 20° 34' 56.21"
        val saturnPosition = 280.54   // Capricorne 10° 32' 05.62"

        // Créer un objet CelestialBodyPositions avec les positions exactes
        val natalPositions = AstronomyCalculator.CelestialBodyPositions(
            sunPosition,
            moonPosition,
            mercuryPosition,
            venusPosition,
            marsPosition,
            jupiterPosition,
            saturnPosition
        )

        // Mettre à jour la vue du zodiaque avec les positions natales
        zodiacView.setNatalPositions(natalPositions)

        // Afficher les positions natales dans l'interface utilisateur
        val positionsText = """
            Soleil: Sgr 1° 03' 30.31" (${sunPosition.format(2)}°)
            Lune: Ari 5° 17' 00.71" (${moonPosition.format(2)}°)
            Mercure: Sco 14° 16' 53.09" (${mercuryPosition.format(2)}°)
            Vénus: Lib 19° 05' 09.00" (${venusPosition.format(2)}°)
            Mars: Sco 19° 17' 13.46" (${marsPosition.format(2)}°)
            Jupiter: Leo 20° 34' 56.21" (${jupiterPosition.format(2)}°)
            Saturne: Cap 10° 32' 05.62" (${saturnPosition.format(2)}°)
        """.trimIndent()

        natalPositionsTextView.text = positionsText
    }

    // Extension pour formater les nombres décimaux
    private fun Double.format(digits: Int) = String.format("%.${digits}f", this)

    private fun startRealTimeUpdates() {
        // Mise à jour immédiate
        calculateMoonPosition()

        // Planifier des mises à jour régulières
        handler.postDelayed(object : Runnable {
            override fun run() {
                calculateMoonPosition()
                handler.postDelayed(this, updateInterval)
            }
        }, updateInterval)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Arrêter les mises à jour lorsque l'activité est détruite
        handler.removeCallbacksAndMessages(null)
    }

    private fun calculateMoonPosition() {
        // Obtenir la date et l'heure actuelles
        val currentDate = Date()

        // Calculer le jour
        val dayNumber = astronomyCalculator.getDayNumber(currentDate)

        // Calculer la position de la Lune
        val moonPosition = astronomyCalculator.calculateMoonPosition(dayNumber)

        // Extraire la longitude écliptique
        val moonLongitude = moonPosition.first

        // Convertir en degrés, minutes, secondes et secondes d'arc
        val dms = astronomyCalculator.decimalDegreesToDMS(moonLongitude)

        // Mettre à jour l'interface utilisateur
        updateUI(dms, currentDate)
    }

    private fun updateUI(dms: AstronomyCalculator.Quadruple<Int, Int, Int, Double>, currentDate: Date) {
        // Formater la position de la Lune
        val formattedPosition = "${dms.first}° ${dms.second}' ${dms.third}\""

        // Mettre à jour le texte de position
        moonPositionDetailsTextView.text = formattedPosition

        // Mettre à jour l'heure de mise à jour
        updateTimeTextView.text = getString(R.string.update_time, timeFormat.format(currentDate))

        // Convertir la position en degrés décimaux pour le zodiaque
        val decimalDegrees = dms.first + (dms.second / 60.0) + (dms.third / 3600.0)

        // Mettre à jour la vue du zodiaque
        zodiacView.setMoonPosition(decimalDegrees)

        // Ajouter une animation de fondu pour le texte de position
        moonPositionDetailsTextView.alpha = 0.7f
        moonPositionDetailsTextView.animate().alpha(1.0f).setDuration(300).start()
    }
}
