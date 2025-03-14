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
    private final int subSteps;
    private final boolean circle;
    private final float maxStrength;
    private final float maxSpeed;
    private final int upperSize;

    private final float forceStrengthDefault;
    private final float forceXDefault;
    private final float forceYDefault;
    private final float restitutionDefault;
    private final int spawnDelayDefault;
    private final float spawnSpeedDefault;
    private final float spawnAngleDefault;
    private final float anglePeriodDefault;
    private final int minSizeDefault;
    private final int maxSizeDefault;
    private final float[] colourDefault;

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
            this.maxStrength = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.maxSpeed = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.upperSize = Integer.parseInt(reader.readLine());
            reader.readLine();
            this.forceStrengthDefault = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.forceXDefault = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.forceYDefault = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.restitutionDefault = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.spawnDelayDefault = Integer.parseInt(reader.readLine());
            reader.readLine();
            this.spawnSpeedDefault = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.spawnAngleDefault = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.anglePeriodDefault = Float.parseFloat(reader.readLine());
            reader.readLine();
            this.minSizeDefault = Integer.parseInt(reader.readLine());
            reader.readLine();
            this.maxSizeDefault = Integer.parseInt(reader.readLine());
            reader.readLine();
            String[] temp = reader.readLine().split(",");
            this.colourDefault = new float[]{Float.parseFloat(temp[0]) / 255, Float.parseFloat(temp[1]) / 255, Float.parseFloat(temp[2]) / 255};
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

    public int getSubSteps() {
        return subSteps;
    }

    public boolean isCircle() {
        return circle;
    }

    public float getMaxStrength() {
        return maxStrength;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public int getUpperSize() {
        return upperSize;
    }

    public float getForceStrengthDefault() {
        return forceStrengthDefault;
    }

    public float getForceXDefault() {
        return forceXDefault;
    }

    public float getForceYDefault() {
        return forceYDefault;
    }

    public float getRestitutionDefault() {
        return restitutionDefault;
    }

    public int getSpawnDelayDefault() {
        return spawnDelayDefault;
    }

    public float getSpawnSpeedDefault() {
        return spawnSpeedDefault;
    }

    public float getSpawnAngleDefault() {
        return spawnAngleDefault;
    }

    public float getAnglePeriodDefault() {
        return anglePeriodDefault;
    }

    public int getMinSizeDefault() {
        return minSizeDefault;
    }

    public int getMaxSizeDefault() {
        return maxSizeDefault;
    }

    public float[] getColourDefault() {
        return colourDefault;
    }
}
