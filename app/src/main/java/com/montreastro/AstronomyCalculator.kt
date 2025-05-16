package com.montreastro

import java.util.*
import kotlin.math.*

/**
 * Classe pour les calculs astronomiques basée sur les algorithmes de Paul Schlyter
 * http://www.stjarnhimlen.se/comp/ppcomp.html
 */
class AstronomyCalculator {

    companion object {
        private const val DEG_TO_RAD = PI / 180.0
        private const val RAD_TO_DEG = 180.0 / PI
    }

    /**
     * Calcule le "jour" à partir de la date
     * Jour 0.0 correspond au 2000 Jan 0.0 UT (ou 1999 Dec 31, 0:00 UT)
     */
    fun getDayNumber(date: Date): Double {
        val calendar = Calendar.getInstance().apply {
            time = date
        }
        
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH est 0-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        
        val ut = hour + minute / 60.0 + second / 3600.0
        
        // Formule valide pour le calendrier grégorien
        var d = 367 * year - 7 * (year + (month + 9) / 12) / 4 - 
                3 * ((year + (month - 9) / 7) / 100 + 1) / 4 + 
                275 * month / 9 + day - 730515
        
        d += ut / 24.0
        
        return d
    }

    /**
     * Calcule la position de la Lune
     * @return Triple(longitude écliptique, latitude écliptique, distance en rayons terrestres)
     */
    fun calculateMoonPosition(dayNumber: Double): Triple<Double, Double, Double> {
        // Éléments orbitaux de la Lune
        val N = normalizeAngle(125.1228 - 0.0529538083 * dayNumber) // Longitude du nœud ascendant
        val i = 5.1454 // Inclinaison
        val w = normalizeAngle(318.0634 + 0.1643573223 * dayNumber) // Argument du périhélie
        val a = 60.2666 // Demi-grand axe (rayons terrestres)
        val e = 0.054900 // Excentricité
        val M = normalizeAngle(115.3654 + 13.0649929509 * dayNumber) // Anomalie moyenne
        
        // Calcul de l'anomalie excentrique E
        val E = calculateEccentricAnomaly(M, e)
        
        // Calcul de la distance et de l'anomalie vraie
        val xv = a * (cos(E * DEG_TO_RAD) - e)
        val yv = a * (sqrt(1.0 - e * e) * sin(E * DEG_TO_RAD))
        
        val v = atan2(yv, xv) * RAD_TO_DEG
        val r = sqrt(xv * xv + yv * yv)
        
        // Position dans l'espace 3D
        val lonEcl = normalizeAngle(v + w)
        val latEcl = asin(sin((lonEcl - N) * DEG_TO_RAD) * sin(i * DEG_TO_RAD)) * RAD_TO_DEG
        
        // Perturbations de la Lune
        val perturbations = calculateMoonPerturbations(dayNumber)
        val perturbedLonEcl = normalizeAngle(lonEcl + perturbations.first)
        val perturbedLatEcl = latEcl + perturbations.second
        val perturbedR = r + perturbations.third
        
        return Triple(perturbedLonEcl, perturbedLatEcl, perturbedR)
    }
    
    /**
     * Calcule les perturbations de la Lune
     * @return Triple(perturbation en longitude, perturbation en latitude, perturbation en distance)
     */
    private fun calculateMoonPerturbations(dayNumber: Double): Triple<Double, Double, Double> {
        // Éléments orbitaux du Soleil
        val Ms = normalizeAngle(356.0470 + 0.9856002585 * dayNumber)
        val ws = normalizeAngle(282.9404 + 4.70935E-5 * dayNumber)
        val Ls = normalizeAngle(Ms + ws)
        
        // Éléments orbitaux de la Lune
        val Mm = normalizeAngle(115.3654 + 13.0649929509 * dayNumber)
        val Nm = normalizeAngle(125.1228 - 0.0529538083 * dayNumber)
        val wm = normalizeAngle(318.0634 + 0.1643573223 * dayNumber)
        val Lm = normalizeAngle(Mm + wm + Nm)
        
        // Paramètres pour les perturbations
        val D = normalizeAngle(Lm - Ls) // Élongation moyenne de la Lune
        val F = normalizeAngle(Lm - Nm) // Argument de latitude pour la Lune
        
        // Perturbations en longitude
        var dLon = -1.274 * sin((Mm - 2 * D) * DEG_TO_RAD) // Évection
        dLon += 0.658 * sin(2 * D * DEG_TO_RAD) // Variation
        dLon -= 0.186 * sin(Ms * DEG_TO_RAD) // Équation annuelle
        dLon -= 0.059 * sin((2 * Mm - 2 * D) * DEG_TO_RAD)
        dLon -= 0.057 * sin((Mm - 2 * D + Ms) * DEG_TO_RAD)
        dLon += 0.053 * sin((Mm + 2 * D) * DEG_TO_RAD)
        dLon += 0.046 * sin((2 * D - Ms) * DEG_TO_RAD)
        dLon += 0.041 * sin((Mm - Ms) * DEG_TO_RAD)
        dLon -= 0.035 * sin(D * DEG_TO_RAD) // Équation parallactique
        dLon -= 0.031 * sin((Mm + Ms) * DEG_TO_RAD)
        dLon -= 0.015 * sin((2 * F - 2 * D) * DEG_TO_RAD)
        dLon += 0.011 * sin((Mm - 4 * D) * DEG_TO_RAD)
        
        // Perturbations en latitude
        var dLat = -0.173 * sin((F - 2 * D) * DEG_TO_RAD)
        dLat -= 0.055 * sin((Mm - F - 2 * D) * DEG_TO_RAD)
        dLat -= 0.046 * sin((Mm + F - 2 * D) * DEG_TO_RAD)
        dLat += 0.033 * sin((F + 2 * D) * DEG_TO_RAD)
        dLat += 0.017 * sin((2 * Mm + F) * DEG_TO_RAD)
        
        // Perturbations en distance
        var dR = -0.58 * cos((Mm - 2 * D) * DEG_TO_RAD)
        dR -= 0.46 * cos(2 * D * DEG_TO_RAD)
        
        return Triple(dLon, dLat, dR)
    }
    
    /**
     * Calcule l'anomalie excentrique à partir de l'anomalie moyenne et de l'excentricité
     */
    private fun calculateEccentricAnomaly(M: Double, e: Double): Double {
        // Première approximation de E
        var E = M + e * RAD_TO_DEG * sin(M * DEG_TO_RAD) * (1.0 + e * cos(M * DEG_TO_RAD))
        
        // Si l'excentricité est faible, cette approximation est suffisante
        if (e < 0.05) return E
        
        // Sinon, itérer pour une meilleure précision
        var E0: Double
        var E1 = E
        do {
            E0 = E1
            E1 = E0 - (E0 - e * RAD_TO_DEG * sin(E0 * DEG_TO_RAD) - M) / 
                 (1.0 - e * cos(E0 * DEG_TO_RAD))
        } while (abs(E1 - E0) > 0.001)
        
        return E1
    }
    
    /**
     * Normalise un angle pour qu'il soit entre 0 et 360 degrés
     */
    private fun normalizeAngle(angle: Double): Double {
        var result = angle % 360.0
        if (result < 0) result += 360.0
        return result
    }
    
    /**
     * Convertit une position en degrés décimaux en degrés, minutes, secondes et secondes d'arc
     * @return Quadruple(degrés, minutes, secondes, secondes d'arc)
     */
    fun decimalDegreesToDMS(decimalDegrees: Double): Quadruple<Int, Int, Int, Double> {
        val totalSeconds = decimalDegrees * 3600.0
        val degrees = totalSeconds / 3600.0
        val degreesInt = degrees.toInt()
        
        val minutesDecimal = (degrees - degreesInt) * 60.0
        val minutesInt = minutesDecimal.toInt()
        
        val secondsDecimal = (minutesDecimal - minutesInt) * 60.0
        val secondsInt = secondsDecimal.toInt()
        
        val arcSeconds = totalSeconds % 60.0
        
        return Quadruple(degreesInt, minutesInt, secondsInt, arcSeconds)
    }
    
    /**
     * Classe pour représenter un quadruple de valeurs
     */
    data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
