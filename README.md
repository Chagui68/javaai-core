# JavaAI Core

A lightweight Java/Kotlin library for tracking, analyzing, and predicting Minecraft player inputs in real time.

**Group:** `com.javaai` · **Artifact:** `javaai-core` · **Version:** `1.0.0`

---

## Features

- **Input Tracking** – Record per-frame player snapshots (position, velocity, yaw/pitch, movement keys, sprint/sneak/jump, on-ground state) for any number of players.
- **Play Style Analysis** – Extract behavioral metrics from a player's history: aggression, caution, predictability, movement variance, direction-change rate, vertical usage, sprint/sneak tendencies, and average speed.
- **Prediction** – Estimate the player's next position, velocity, orientation, and action state based on recent movement patterns using kinematic extrapolation and weighted averaging.
- **Lightweight & Thread‑Safe** – No external dependencies. Designed for server-side plugin integration.

---

## Usage

### 1. Add the dependency

Since the library is not yet published to Maven Central, clone the repo and publish it to your local Maven repository:

```bash
git clone https://github.com/Chagui68/JavaAI.git
cd JavaAI
./gradlew publishToMavenLocal
```

Then add it to your `build.gradle.kts`:

```kotlin
repositories {
    mavenLocal()
}

dependencies {
    implementation("com.javaai:javaai-core:1.0.0")
}
```

Or for Maven `pom.xml`:

```xml
<dependency>
    <groupId>com.javaai</groupId>
    <artifactId>javaai-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Record player input

Create a `PlayerInputSnapshot` each tick/game-frame and feed it to the `JavaAI` singleton:

```java
import com.javaai.core.JavaAI;
import com.javaai.player.PlayerInputSnapshot;
import java.util.UUID;

UUID playerId = player.getUniqueId();

PlayerInputSnapshot snapshot = new PlayerInputSnapshot(
    player.getX(), player.getY(), player.getZ(),
    player.getVelocity().getX(), player.getVelocity().getY(), player.getVelocity().getZ(),
    player.getYaw(), player.getPitch(),
    player.isForward(), player.isBackward(), player.isLeft(), player.isRight(),
    player.isSprinting(), player.isSneaking(), player.isJumping(),
    player.isOnGround()
);

JavaAI.getInstance().recordInput(playerId, snapshot);
```

### 3. Predict next state

```java
PlayerInputSnapshot predicted = JavaAI.getInstance().predictNext(playerId);
if (predicted != null) {
    System.out.println("Predicted position: " + predicted.getX() + ", " + predicted.getY() + ", " + predicted.getZ());
}
```

The predictor requires at least 10 snapshots before returning a result.

### 4. Analyze play style

```java
Map<String, Double> style = JavaAI.getInstance().getPlayerStyle(playerId);
System.out.println("Aggression: " + style.get("aggression"));
System.out.println("Caution: " + style.get("caution"));
System.out.println("Predictability: " + style.get("predictability"));
```

Requires at least 20 snapshots.

### 5. Manage trackers

```java
// Remove a specific player's data
JavaAI.getInstance().clearTracker(playerId);

// Clear all tracked data
JavaAI.getInstance().clearAll();

// Check tracked player count
int count = JavaAI.getInstance().getTrackedPlayerCount();

// Get history size for a player
long size = JavaAI.getInstance().getHistorySize(playerId);
```

---

## API Overview

| Class | Purpose |
|---|---|
| `JavaAI` | Singleton entry point; manages per-player trackers |
| `PlayerInputSnapshot` | Immutable snapshot of a player's state at one moment |
| `PlayerInputTracker` | Stores and analyzes a player's input history (max 500 entries) |
| `InputPattern` | Analyzes history and produces behavioral style metrics |
| `PredictionEngine` | Predicts the next snapshot using kinematic + weighted models |
| `MathUtils` | Utility helpers: lerp, clamp, smoothstep, angle math, moving average |

---

## License

MIT