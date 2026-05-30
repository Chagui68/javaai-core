package com.javaai.pattern;

import com.javaai.player.PlayerInputSnapshot;
import com.javaai.player.PlayerInputTracker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputPattern {

    public Map<String, Double> analyzeStyle(PlayerInputTracker tracker) {
        Map<String, Double> style = new HashMap<>();
        List<PlayerInputSnapshot> history = tracker.getHistory();

        if (history.size() < 10) return style;

        style.put("aggression", calculateAggression(history));
        style.put("caution", calculateCaution(history));
        style.put("predictability", calculatePredictability(history));
        style.put("movement_variance", calculateMovementVariance(history));
        style.put("direction_changes", calculateDirectionChangeRate(history));
        style.put("vertical_usage", calculateVerticalUsage(history));
        style.put("sprint_tendency", tracker.getSprintPercentage());
        style.put("sneak_tendency", tracker.getSneakPercentage());
        style.put("jump_frequency", tracker.getJumpFrequency());
        style.put("avg_speed", tracker.getAverageHorizontalSpeed());

        return style;
    }

    private double calculateAggression(List<PlayerInputSnapshot> history) {
        if (history.size() < 5) return 0.5;
        int aggressiveFrames = 0;
        for (int i = 1; i < history.size(); i++) {
            if (history.get(i).isSprint() && history.get(i).getPitch() < 0) {
                aggressiveFrames++;
            }
        }
        return Math.min(1.0, (double) aggressiveFrames / history.size() * 2);
    }

    private double calculateCaution(List<PlayerInputSnapshot> history) {
        if (history.size() < 5) return 0.5;
        int cautiousFrames = 0;
        for (PlayerInputSnapshot snap : history) {
            if (snap.isSneak() || snap.getSpeed() < 0.05) {
                cautiousFrames++;
            }
        }
        return Math.min(1.0, (double) cautiousFrames / history.size());
    }

    private double calculatePredictability(List<PlayerInputSnapshot> history) {
        if (history.size() < 20) return 0.5;
        int consistentDirection = 0;
        for (int i = 5; i < history.size(); i++) {
            PlayerInputSnapshot current = history.get(i);
            PlayerInputSnapshot prev = history.get(i - 1);
            double dx = current.getX() - prev.getX();
            double dz = current.getZ() - prev.getZ();
            if (Math.abs(dx) > 0.01 || Math.abs(dz) > 0.01) {
                double prevDx = prev.getX() - history.get(i - 2).getX();
                double prevDz = prev.getZ() - history.get(i - 2).getZ();
                if (dx * prevDx + dz * prevDz > 0) {
                    consistentDirection++;
                }
            }
        }
        int movingFrames = history.size() - 5;
        return movingFrames > 0 ? Math.min(1.0, (double) consistentDirection / movingFrames) : 0.5;
    }

    private double calculateMovementVariance(List<PlayerInputSnapshot> history) {
        if (history.size() < 10) return 0.5;
        double meanSpeed = 0;
        for (PlayerInputSnapshot snap : history) {
            meanSpeed += snap.getHorizontalSpeed();
        }
        meanSpeed /= history.size();

        double variance = 0;
        for (PlayerInputSnapshot snap : history) {
            double diff = snap.getHorizontalSpeed() - meanSpeed;
            variance += diff * diff;
        }
        variance /= history.size();

        return Math.min(1.0, Math.sqrt(variance) * 10);
    }

    private double calculateDirectionChangeRate(List<PlayerInputSnapshot> history) {
        if (history.size() < 10) return 0.5;
        int changes = 0;
        for (int i = 2; i < history.size(); i++) {
            double dx1 = history.get(i).getX() - history.get(i - 1).getX();
            double dz1 = history.get(i).getZ() - history.get(i - 1).getZ();
            double dx2 = history.get(i - 1).getX() - history.get(i - 2).getX();
            double dz2 = history.get(i - 1).getZ() - history.get(i - 2).getZ();
            double cross = dx1 * dz2 - dz1 * dx2;
            if (Math.abs(cross) > 0.01) {
                changes++;
            }
        }
        return Math.min(1.0, (double) changes / (history.size() - 2));
    }

    private double calculateVerticalUsage(List<PlayerInputSnapshot> history) {
        if (history.isEmpty()) return 0;
        int verticalFrames = 0;
        for (PlayerInputSnapshot snap : history) {
            if (snap.isJump() || Math.abs(snap.getVelocityY()) > 0.1) {
                verticalFrames++;
            }
        }
        return Math.min(1.0, (double) verticalFrames / history.size());
    }
}
