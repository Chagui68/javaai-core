package com.javaai.player;

public class PlayerInputSnapshot {
    private final double x, y, z;
    private final double velocityX, velocityY, velocityZ;
    private final float yaw, pitch;
    private final boolean forward, backward, left, right;
    private final boolean sprint, sneak, jump;
    private final boolean onGround;
    private final long timestamp;

    public PlayerInputSnapshot(double x, double y, double z,
                               double velocityX, double velocityY, double velocityZ,
                               float yaw, float pitch,
                               boolean forward, boolean backward, boolean left, boolean right,
                               boolean sprint, boolean sneak, boolean jump,
                               boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.sprint = sprint;
        this.sneak = sneak;
        this.jump = jump;
        this.onGround = onGround;
        this.timestamp = System.currentTimeMillis();
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
    public double getVelocityZ() { return velocityZ; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
    public boolean isForward() { return forward; }
    public boolean isBackward() { return backward; }
    public boolean isLeft() { return left; }
    public boolean isRight() { return right; }
    public boolean isSprint() { return sprint; }
    public boolean isSneak() { return sneak; }
    public boolean isJump() { return jump; }
    public boolean isOnGround() { return onGround; }
    public long getTimestamp() { return timestamp; }

    public double getSpeed() {
        return Math.sqrt(velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ);
    }

    public double getHorizontalSpeed() {
        return Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
    }

    public boolean isMoving() {
        return getHorizontalSpeed() > 0.01;
    }

    public double distanceTo(PlayerInputSnapshot other) {
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
