package com.montreastro

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var zodiacView: ZodiacView
    private lateinit var moonPositionTextView: TextView
    private lateinit var moonPositionDetailsTextView: TextView
    private lateinit var calculateButton: Button
    
    private val astronomyCalculator = AstronomyCalculator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialiser les vues
        zodiacView = findViewById(R.id.zodiacView)
        moonPositionTextView = findViewById(R.id.moonPositionTextView)
        moonPositionDetailsTextView = findViewById(R.id.moonPositionDetailsTextView)
        calculateButton = findViewById(R.id.calculateButton)
        
        // Configurer le bouton de calcul
        calculateButton.setOnClickListener {
            calculateMoonPosition()
        }
        
        // Calculer la position initiale
        calculateMoonPosition()
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
        updateUI(dms)
    }
    
    private fun updateUI(dms: AstronomyCalculator.Quadruple<Int, Int, Int, Double>) {
        // Formater la position de la Lune
        val formattedPosition = "${dms.first}° ${dms.second}' ${dms.third}\" (${String.format("%.2f", dms.fourth)}\")"
        
        // Mettre à jour le texte
        moonPositionDetailsTextView.text = formattedPosition
        
        // Mettre à jour la vue du zodiaque
        zodiacView.setMoonPosition(dms.fourth)
    }
}
