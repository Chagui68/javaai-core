package com.javaai.prediction;

import com.javaai.player.PlayerInputSnapshot;
import com.javaai.player.PlayerInputTracker;

import java.util.List;

public class PredictionEngine {

    public PlayerInputSnapshot predict(PlayerInputTracker tracker) {
        List<PlayerInputSnapshot> history = tracker.getRecent(50);
        if (history.size() < 5) return null;

        PlayerInputSnapshot last = history.get(history.size() - 1);

        double predictedX = predictCoordinate(history, true);
        double predictedZ = predictCoordinate(history, false);
        double predictedY = predictVertical(history, last);

        double velX = predictedX - last.getX();
        double velZ = predictedZ - last.getZ();
        double velY = predictedY - last.getY();

        float predictedYaw = predictYaw(history);
        float predictedPitch = predictPitch(history);

        boolean predictedSprint = predictSprint(history);
        boolean predictedSneak = predictSneak(history);
        boolean predictedJump = predictJump(history);

        boolean movingForward = Math.abs(velX) > 0.01 || Math.abs(velZ) > 0.01;
        boolean movingBackward = false;
        boolean movingLeft = false;
        boolean movingRight = false;

        if (movingForward) {
            double yawRad = Math.toRadians(predictedYaw);
            double forwardX = -Math.sin(yawRad);
            double forwardZ = Math.cos(yawRad);
            double dot = velX * forwardX + velZ * forwardZ;
            double cross = velX * forwardZ - velZ * forwardX;

            if (dot < -0.01) {
                movingForward = false;
                movingBackward = true;
            }
            if (cross > 0.01) {
                movingLeft = true;
            }
            if (cross < -0.01) {
                movingRight = true;
            }
        }

        return new PlayerInputSnapshot(
            predictedX, predictedY, predictedZ,
            velX, velY, velZ,
            predictedYaw, predictedPitch,
            movingForward, movingBackward, movingLeft, movingRight,
            predictedSprint, predictedSneak, predictedJump,
            last.isOnGround()
        );
    }

    private double predictCoordinate(List<PlayerInputSnapshot> history, boolean isX) {
        int size = history.size();
        if (size < 3) return isX ? history.get(size - 1).getX() : history.get(size - 1).getZ();

        double[] values = new double[size];
        for (int i = 0; i < size; i++) {
            values[i] = isX ? history.get(i).getX() : history.get(i).getZ();
        }

        double[] velocities = new double[size - 1];
        double[] accelerations = new double[size - 2];

        for (int i = 0; i < size - 1; i++) {
            velocities[i] = values[i + 1] - values[i];
        }
        for (int i = 0; i < size - 2; i++) {
            accelerations[i] = velocities[i + 1] - velocities[i];
        }

        double avgVelocity = 0;
        for (double v : velocities) avgVelocity += v;
        avgVelocity /= velocities.length;

        double avgAcceleration = 0;
        for (double a : accelerations) avgAcceleration += a;
        avgAcceleration /= accelerations.length;

        return values[size - 1] + avgVelocity + avgAcceleration * 0.5;
    }

    private double predictVertical(List<PlayerInputSnapshot> history, PlayerInputSnapshot last) {
        if (!last.isOnGround()) {
            double gravity = -0.08;
            double currentVelY = last.getVelocityY();
            return last.getY() + currentVelY + gravity;
        }

        if (last.isJump()) {
            return last.getY() + 0.42;
        }

        return last.getY();
    }

    private float predictYaw(List<PlayerInputSnapshot> history) {
        if (history.size() < 3) return history.get(history.size() - 1).getYaw();

        float sum = 0;
        float weight = 0;
        for (int i = 0; i < history.size(); i++) {
            float w = (float) (i + 1) / history.size();
            sum += history.get(i).getYaw() * w;
            weight += w;
        }
        return sum / weight;
    }

    private float predictPitch(List<PlayerInputSnapshot> history) {
        if (history.size() < 3) return history.get(history.size() - 1).getPitch();

        float sum = 0;
        float weight = 0;
        for (int i = 0; i < history.size(); i++) {
            float w = (float) (i + 1) / history.size();
            sum += history.get(i).getPitch() * w;
            weight += w;
        }
        return sum / weight;
    }

    private boolean predictSprint(List<PlayerInputSnapshot> history) {
        int recentSize = Math.min(10, history.size());
        int sprintCount = 0;
        for (int i = history.size() - recentSize; i < history.size(); i++) {
            if (history.get(i).isSprint()) sprintCount++;
        }
        return (double) sprintCount / recentSize > 0.5;
    }

    private boolean predictSneak(List<PlayerInputSnapshot> history) {
        int recentSize = Math.min(10, history.size());
        int sneakCount = 0;
        for (int i = history.size() - recentSize; i < history.size(); i++) {
            if (history.get(i).isSneak()) sneakCount++;
        }
        return (double) sneakCount / recentSize > 0.5;
    }

    private boolean predictJump(List<PlayerInputSnapshot> history) {
        if (history.size() < 5) return false;
        PlayerInputSnapshot last = history.get(history.size() - 1);

        if (!last.isOnGround()) return false;

        boolean wasMoving = false;
        for (int i = Math.max(0, history.size() - 5); i < history.size(); i++) {
            if (history.get(i).getHorizontalSpeed() > 0.1) {
                wasMoving = true;
                break;
            }
        }

        if (wasMoving && Math.random() < 0.1) return true;

        int jumpCount = 0;
        for (int i = 1; i < history.size(); i++) {
            if (history.get(i).isJump() && !history.get(i - 1).isJump()) {
                jumpCount++;
            }
        }
        double jumpRate = (double) jumpCount / history.size();
        return Math.random() < jumpRate * 2;
    }
}
