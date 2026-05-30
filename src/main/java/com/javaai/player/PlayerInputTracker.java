package com.javaai.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerInputTracker {
    private final UUID playerUUID;
    private final List<PlayerInputSnapshot> history;
    private static final int MAX_HISTORY = 500;
    private int patternIndex;

    public PlayerInputTracker(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.history = new ArrayList<>();
        this.patternIndex = 0;
    }

    public void record(PlayerInputSnapshot snapshot) {
        history.add(snapshot);
        if (history.size() > MAX_HISTORY) {
            history.remove(0);
        }
    }

    public PlayerInputSnapshot getLatest() {
        return history.isEmpty() ? null : history.get(history.size() - 1);
    }

    public PlayerInputSnapshot get(int index) {
        if (index < 0 || index >= history.size()) return null;
        return history.get(index);
    }

    public List<PlayerInputSnapshot> getRecent(int count) {
        int start = Math.max(0, history.size() - count);
        return new ArrayList<>(history.subList(start, history.size()));
    }

    public List<PlayerInputSnapshot> getHistory() {
        return new ArrayList<>(history);
    }

    public int getHistorySize() {
        return history.size();
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public double getAverageSpeed() {
        if (history.isEmpty()) return 0;
        return history.stream()
            .mapToDouble(PlayerInputSnapshot::getSpeed)
            .average()
            .orElse(0);
    }

    public double getAverageHorizontalSpeed() {
        if (history.isEmpty()) return 0;
        return history.stream()
            .mapToDouble(PlayerInputSnapshot::getHorizontalSpeed)
            .average()
            .orElse(0);
    }

    public double getSprintPercentage() {
        if (history.isEmpty()) return 0;
        long sprintCount = history.stream().filter(PlayerInputSnapshot::isSprint).count();
        return (double) sprintCount / history.size();
    }

    public double getSneakPercentage() {
        if (history.isEmpty()) return 0;
        long sneakCount = history.stream().filter(PlayerInputSnapshot::isSneak).count();
        return (double) sneakCount / history.size();
    }

    public double getJumpFrequency() {
        if (history.size() < 2) return 0;
        long jumpCount = 0;
        for (int i = 1; i < history.size(); i++) {
            if (history.get(i).isJump() && !history.get(i - 1).isJump()) {
                jumpCount++;
            }
        }
        return (double) jumpCount / (history.size() - 1);
    }

    public int getNextPatternIndex() {
        return patternIndex++ % Math.max(1, history.size());
    }

    public void clear() {
        history.clear();
        patternIndex = 0;
    }
}
