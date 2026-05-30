package com.javaai.core;

import com.javaai.player.PlayerInputSnapshot;
import com.javaai.player.PlayerInputTracker;
import com.javaai.pattern.InputPattern;
import com.javaai.prediction.PredictionEngine;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JavaAI {
    private static JavaAI instance;
    private final Map<UUID, PlayerInputTracker> trackers;
    private final PredictionEngine predictionEngine;
    private final InputPattern patternAnalyzer;

    private JavaAI() {
        this.trackers = new ConcurrentHashMap<>();
        this.predictionEngine = new PredictionEngine();
        this.patternAnalyzer = new InputPattern();
    }

    public static synchronized JavaAI getInstance() {
        if (instance == null) {
            instance = new JavaAI();
        }
        return instance;
    }

    public PlayerInputTracker getTracker(UUID playerUUID) {
        return trackers.computeIfAbsent(playerUUID, k -> new PlayerInputTracker(k));
    }

    public void recordInput(UUID playerUUID, PlayerInputSnapshot snapshot) {
        getTracker(playerUUID).record(snapshot);
    }

    public PlayerInputSnapshot predictNext(UUID playerUUID) {
        PlayerInputTracker tracker = getTracker(playerUUID);
        if (tracker.getHistorySize() < 10) {
            return null;
        }
        return predictionEngine.predict(tracker);
    }

    public Map<String, Double> getPlayerStyle(UUID playerUUID) {
        PlayerInputTracker tracker = getTracker(playerUUID);
        if (tracker.getHistorySize() < 20) {
            return Map.of();
        }
        return patternAnalyzer.analyzeStyle(tracker);
    }

    public void clearTracker(UUID playerUUID) {
        trackers.remove(playerUUID);
    }

    public void clearAll() {
        trackers.clear();
    }

    public int getTrackedPlayerCount() {
        return trackers.size();
    }

    public long getHistorySize(UUID playerUUID) {
        PlayerInputTracker tracker = trackers.get(playerUUID);
        return tracker != null ? tracker.getHistorySize() : 0;
    }
}
