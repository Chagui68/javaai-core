package com.javaai.util

import com.javaai.player.PlayerInputSnapshot
import kotlin.math.sqrt

object MathUtils {
    fun lerp(start: Double, end: Double, t: Double): Double {
        return start + (end - start) * t
    }

    fun lerp(start: Float, end: Float, t: Float): Float {
        return start + (end - start) * t
    }

    fun clamp(value: Double, min: Double, max: Double): Double {
        return Math.max(min, Math.min(max, value))
    }

    fun clamp(value: Float, min: Float, max: Float): Float {
        return Math.max(min, Math.min(max, value))
    }

    fun distance(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
        val dx = x2 - x1
        val dy = y2 - y1
        val dz = z2 - z1
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    fun smoothstep(edge0: Double, edge1: Double, x: Double): Double {
        val t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0)
        return t * t * (3 - 2 * t)
    }

    fun normalizeAngle(angle: Float): Float {
        var a = angle
        while (a > 180) a -= 360
        while (a < -180) a += 360
        return a
    }

    fun angleDifference(a: Float, b: Float): Float {
        return normalizeAngle(a - b)
    }

    fun interpolateYaw(current: Float, target: Float, speed: Float): Float {
        val diff = angleDifference(target, current)
        return current + diff * speed
    }

    fun calculateVelocity(from: PlayerInputSnapshot, to: PlayerInputSnapshot): Triple<Double, Double, Double> {
        val timeDiff = (to.timestamp - from.timestamp).toDouble() / 1000.0
        if (timeDiff <= 0) return Triple(0.0, 0.0, 0.0)

        val vx = (to.x - from.x) / timeDiff
        val vy = (to.y - from.y) / timeDiff
        val vz = (to.z - from.z) / timeDiff
        return Triple(vx, vy, vz)
    }

    fun movingAverage(values: List<Double>, windowSize: Int): List<Double> {
        if (values.size < windowSize) return values.toList()

        val result = mutableListOf<Double>()
        for (i in values.indices) {
            val start = maxOf(0, i - windowSize + 1)
            val window = values.subList(start, i + 1)
            result.add(window.average())
        }
        return result
    }
}
