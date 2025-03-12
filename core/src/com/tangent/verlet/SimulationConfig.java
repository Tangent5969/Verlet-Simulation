package com.tangent.verlet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SimulationConfig {
    private final float worldWidth;
    private final float worldHeight;
    private final float boundRadius;
    private final float boundX;
    private final float boundY;
    private final float gravity;
    private final float restitution;
    private final int subSteps;
    private final boolean circle;
    private final int spawnDelay;
    private final float spawnSpeed;
    private final float spawnAngle;
    private final float anglePeriod;
    private final int minSize;
    private final int maxSize;

    public SimulationConfig(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                Gdx.files.internal("Default.txt").copyTo(new FileHandle(path));
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));

            reader.readLine();
            reader.readLine();
            this.worldWidth = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.worldHeight = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.subSteps = Integer.parseInt(reader.readLine());
            reader.readLine();
            this.circle = Boolean.parseBoolean(reader.readLine());
            reader.readLine();
            this.boundX = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.boundY = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.boundRadius = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.gravity = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.restitution = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.minSize = Integer.parseInt(reader.readLine());
            reader.readLine();
            this.maxSize = Integer.parseInt(reader.readLine());
            reader.readLine();
            this.spawnDelay = Integer.parseInt(reader.readLine());
            reader.readLine();
            this.spawnSpeed = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.spawnAngle = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.anglePeriod = Float.parseFloat(reader.readLine());
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public float getBoundRadius() {
        return boundRadius;
    }

    public float getBoundX() {
        return boundX;
    }

    public float getBoundY() {
        return boundY;
    }

    public float getGravity() {
        return gravity;
    }

    public float getRestitution() {
        return restitution;
    }

    public int getSubSteps() {
        return subSteps;
    }

    public boolean isCircle() {
        return circle;
    }

    public int getSpawnDelay() {
        return spawnDelay;
    }

    public float getSpawnSpeed() {
        return spawnSpeed;
    }

    public float getSpawnAngle() {
        return spawnAngle;
    }

    public float getAnglePeriod() {
        return anglePeriod;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }


}
