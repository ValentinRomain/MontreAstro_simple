package com.montreastro

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

class ZodiacView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Peintures pour les cercles et textes
    private val zodiacPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 80
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val markerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 150
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 24f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val smallTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = 200
        textSize = 16f
        textAlign = Paint.Align.CENTER
    }

    // Icônes des astres
    private val sunDrawable = ContextCompat.getDrawable(context, R.drawable.ic_sun)
    private val moonDrawable = ContextCompat.getDrawable(context, R.drawable.ic_moon)
    private val mercuryDrawable = ContextCompat.getDrawable(context, R.drawable.ic_mercury)
    private val venusDrawable = ContextCompat.getDrawable(context, R.drawable.ic_venus)
    private val marsDrawable = ContextCompat.getDrawable(context, R.drawable.ic_mars)
    private val jupiterDrawable = ContextCompat.getDrawable(context, R.drawable.ic_jupiter)
    private val saturnDrawable = ContextCompat.getDrawable(context, R.drawable.ic_saturn)

    // Fond étoilé
    private val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.starry_background)

    private val zodiacRect = RectF()
    private var moonPositionDegrees = 0.0

    // Positions des astres nataux (calculées à partir de la date de naissance)
    private var natalPositions: AstronomyCalculator.CelestialBodyPositions? = null

    // Effet de brillance pour les astres
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
        style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = 50f
        zodiacRect.set(
            padding,
            padding,
            width.toFloat() - padding,
            height.toFloat() - padding
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = zodiacRect.centerX()
        val centerY = zodiacRect.centerY()
        val radius = (zodiacRect.width() / 2) - 20f

        // Dessiner le fond étoilé
        backgroundDrawable?.let {
            it.setBounds(
                zodiacRect.left.toInt(),
                zodiacRect.top.toInt(),
                zodiacRect.right.toInt(),
                zodiacRect.bottom.toInt()
            )
            it.draw(canvas)
        }

        // Dessiner les cercles concentriques
        canvas.drawCircle(centerX, centerY, radius, circlePaint)
        canvas.drawCircle(centerX, centerY, radius * 0.8f, circlePaint)

        // Dessiner les marqueurs de degrés (tous les 30 degrés)
        for (i in 0 until 12) {
            val angle = i * 30f + 90 // +90 pour commencer en haut
            val startRadius = radius * 0.75f
            val endRadius = radius

            val startX = centerX + startRadius * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val startY = centerY + startRadius * Math.sin(Math.toRadians(angle.toDouble())).toFloat()
            val endX = centerX + endRadius * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val endY = centerY + endRadius * Math.sin(Math.toRadians(angle.toDouble())).toFloat()

            markerPaint.alpha = 255
            markerPaint.strokeWidth = 2.5f

            canvas.drawLine(startX, startY, endX, endY, markerPaint)

            // Ajouter des étiquettes pour les positions principales
            val textRadius = radius * 1.1f
            val textX = centerX + textRadius * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val textY = centerY + textRadius * Math.sin(Math.toRadians(angle.toDouble())).toFloat() + 8f
            canvas.drawText("${(i * 30) % 360}°", textX, textY, textPaint)
        }

        // Dessiner les marqueurs de degrés mineurs (tous les 10 degrés)
        for (i in 0 until 36) {
            if (i % 3 != 0) { // Éviter de redessiner les marqueurs principaux
                val angle = i * 10f + 90 // +90 pour commencer en haut
                val startRadius = radius * 0.85f
                val endRadius = radius

                val startX = centerX + startRadius * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
                val startY = centerY + startRadius * Math.sin(Math.toRadians(angle.toDouble())).toFloat()
                val endX = centerX + endRadius * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
                val endY = centerY + endRadius * Math.sin(Math.toRadians(angle.toDouble())).toFloat()

                markerPaint.alpha = 180
                markerPaint.strokeWidth = 1.5f

                canvas.drawLine(startX, startY, endX, endY, markerPaint)
            }
        }

        // Dessiner les astres natals avec leurs icônes
        natalPositions?.let { positions ->
            drawCelestialBody(canvas, centerX, centerY, radius * 0.7f, positions.sun, sunDrawable, ContextCompat.getColor(context, R.color.sun_color))
            drawCelestialBody(canvas, centerX, centerY, radius * 0.7f, positions.moon, moonDrawable, ContextCompat.getColor(context, R.color.moon_color), true)
            drawCelestialBody(canvas, centerX, centerY, radius * 0.7f, positions.mercury, mercuryDrawable, ContextCompat.getColor(context, R.color.mercury_color))
            drawCelestialBody(canvas, centerX, centerY, radius * 0.7f, positions.venus, venusDrawable, ContextCompat.getColor(context, R.color.venus_color))
            drawCelestialBody(canvas, centerX, centerY, radius * 0.7f, positions.mars, marsDrawable, ContextCompat.getColor(context, R.color.mars_color))
            drawCelestialBody(canvas, centerX, centerY, radius * 0.7f, positions.jupiter, jupiterDrawable, ContextCompat.getColor(context, R.color.jupiter_color))
            drawCelestialBody(canvas, centerX, centerY, radius * 0.7f, positions.saturn, saturnDrawable, ContextCompat.getColor(context, R.color.saturn_color))
        }

        // Dessiner la position actuelle de la Lune avec un effet de brillance
        val moonAngle = moonPositionDegrees.toFloat() + 90 // +90 pour commencer en haut
        val moonRadians = Math.toRadians(moonAngle.toDouble())
        val moonX = centerX + radius * 0.9f * Math.cos(moonRadians).toFloat()
        val moonY = centerY + radius * 0.9f * Math.sin(moonRadians).toFloat()

        // Effet de brillance pour la Lune
        glowPaint.color = ContextCompat.getColor(context, R.color.moon_color)
        glowPaint.alpha = 100
        canvas.drawCircle(moonX, moonY, 40f, glowPaint)

        // Dessiner l'icône de la Lune
        moonDrawable?.let {
            val iconSize = 60
            it.setBounds(
                (moonX - iconSize / 2).toInt(),
                (moonY - iconSize / 2).toInt(),
                (moonX + iconSize / 2).toInt(),
                (moonY + iconSize / 2).toInt()
            )
            it.draw(canvas)
        }

        // Ajouter une étiquette avec la position en degrés
        val labelY = moonY + 40f
        smallTextPaint.color = ContextCompat.getColor(context, R.color.moon_color)
        smallTextPaint.textSize = 18f
        canvas.drawText(String.format("%.1f°", moonPositionDegrees), moonX, labelY, smallTextPaint)
    }

    fun setMoonPosition(degrees: Double) {
        moonPositionDegrees = degrees
        invalidate()
    }

    fun setNatalPositions(positions: AstronomyCalculator.CelestialBodyPositions) {
        natalPositions = positions
        invalidate()
    }

    private fun drawCelestialBody(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        degrees: Double,
        drawable: android.graphics.drawable.Drawable?,
        color: Int,
        isNatal: Boolean = false
    ) {
        // Angle en degrés (ajouter 90° pour commencer en haut)
        val angle = degrees.toFloat() + 90 // +90 pour commencer en haut

        // Calculer la position en coordonnées cartésiennes
        val radians = Math.toRadians(angle.toDouble())
        val x = centerX + radius * Math.cos(radians).toFloat()
        val y = centerY + radius * Math.sin(radians).toFloat()

        // Effet de brillance pour le corps céleste
        glowPaint.color = color
        glowPaint.alpha = 60
        canvas.drawCircle(x, y, 25f, glowPaint)

        // Dessiner l'icône du corps céleste
        drawable?.let {
            val iconSize = 40
            it.setBounds(
                (x - iconSize / 2).toInt(),
                (y - iconSize / 2).toInt(),
                (x + iconSize / 2).toInt(),
                (y + iconSize / 2).toInt()
            )
            it.draw(canvas)
        }

        // Si c'est la lune natale, ajouter un contour pour la distinguer
        if (isNatal) {
            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            strokePaint.style = Paint.Style.STROKE
            strokePaint.strokeWidth = 2f
            strokePaint.color = Color.WHITE
            canvas.drawCircle(x, y, 22f, strokePaint)
        }

        // Ajouter une étiquette avec la position en degrés
        val labelY = y + 30f
        smallTextPaint.color = color
        canvas.drawText(String.format("%.1f°", degrees), x, labelY, smallTextPaint)
    }
}
