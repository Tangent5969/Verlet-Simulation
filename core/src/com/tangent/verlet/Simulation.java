package com.tangent.verlet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.Random;

public class Simulation {

    private enum SpawnObjectType {
        Ball, Chain
    }

    private final float maxWidth;
    private final float maxHeight;

    private final Random rand;
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

    private int boundRadius;
    private int boundX;
    private int boundY;
    private int subSteps;
    public boolean circle;

    private int spawnerX;
    private int spawnerY;

    public float[] forceStrength;
    public float[] forceX;
    public float[] forceY;
    public float[] restitution;
    public int[] spawnDelay;
    public float[] spawnSpeed;
    public float[] spawnAngle;
    public float[] anglePeriod;
    public int[] minSize;
    public int[] maxSize;
    public int[] ballRadius;
    public boolean randomSize;
    public boolean spawner;
    public boolean rainbow;
    public float[] colour;

    public boolean mouseForce;
    public int[] mouseRadius;
    public float[] mouseStrength;
    private SpawnObjectType spawnObject;
    public boolean spawnLocked;
    public boolean spawnLocked2;
    public int[] chainRadius;
    private float[] chainStart;
    private float[][] tempChain;

    private ArrayList<Particle> balls;
    private ArrayList<Link> links;
    private float time;
    private long prevTime;

    public Simulation(SimulationConfig config) {
        this.maxWidth = config.getWorldWidth();
        this.maxHeight = config.getWorldHeight();
        this.maxStrength = config.getMaxStrength();
        this.maxSpeed = config.getMaxSpeed();
        this.upperSize = config.getUpperSize();
        this.rand = new Random();


        this.subSteps = 8;
        this.boundX = (int) (maxWidth / 2);
        this.boundY = (int) (maxHeight / 2);
        this.boundRadius = (int) ((Math.min(maxWidth, maxHeight) / 2) - 50);
        this.circle = false;

        this.mouseForce = false;
        this.mouseRadius = new int[]{(int) (boundRadius * 0.25)};
        this.mouseStrength = new float[]{config.getForceStrengthDefault()};
        this.spawnObject = SpawnObjectType.Ball;
        this.spawnLocked = false;
        this.spawnLocked2 = false;
        this.chainRadius = new int[]{config.getChainRadiusDefault()};

        this.forceStrengthDefault = config.getForceStrengthDefault();
        this.forceXDefault = config.getForceXDefault();
        this.forceYDefault = config.getForceYDefault();
        this.restitutionDefault = config.getRestitutionDefault();
        this.spawnDelayDefault = config.getSpawnDelayDefault();
        this.spawnSpeedDefault = config.getSpawnSpeedDefault();
        this.spawnAngleDefault = config.getSpawnAngleDefault();
        this.anglePeriodDefault = config.getAnglePeriodDefault();
        this.minSizeDefault = config.getMinSizeDefault();
        this.maxSizeDefault = config.getMaxSizeDefault();
        this.colourDefault = config.getColourDefault();

        resetForces();
        resetSpawner();
        this.minSize = new int[]{minSizeDefault};
        this.maxSize = new int[]{maxSizeDefault};
        this.ballRadius = new int[]{(minSize[0] + maxSize[0]) / 2};
        this.randomSize = false;

        this.rainbow = true;
        this.colour = colourDefault;
        this.balls = new ArrayList<>();
        this.links = new ArrayList<>();
        this.time = 0;
        this.prevTime = 0;
    }

    public void addObject(float x, float y) {
        if (mouseForce || !inBounds(x, y)) return;
        if (spawnObject == SpawnObjectType.Ball)
            balls.add(new Particle(x, y, getBallSize(), spawnLocked, getColour(time)));
        else addChain(x, y, 0);
    }

    public boolean inBounds(float x, float y) {
        if (circle) {
            float dx = boundX - x;
            float dy = boundY - y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            return (dist > boundRadius);
        } else {
            return (x > boundX - boundRadius) && (x < boundX + boundRadius) && (y > boundY - boundRadius) && (y < boundY + boundRadius);
        }
    }

    public void addChain(float x, float y, int state) {
        if (spawnObject == SpawnObjectType.Ball || mouseForce || !inBounds(x, y)) {
            chainStart = null;
            tempChain = null;
            return;
        }
        if (state == 0) {
            chainStart = new float[]{x, y};
        } else if (state == 1) {
            if (chainStart == null) return;
            float dx = x - chainStart[0];
            float dy = y - chainStart[1];
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            int count = (int) (dist / (chainRadius[0] * 2f));
            if (count == 0) return;

            dx = dx / dist * chainRadius[0] * 2;
            dy = dy / dist * chainRadius[0] * 2;
            tempChain = new float[count][2];
            tempChain[0] = chainStart;
            for (int i = 1; i < count; i++) {
                tempChain[i] = new float[]{chainStart[0] + dx * i, chainStart[1] + dy * i};
            }

        } else {
            if (tempChain == null || tempChain.length < 2) return;
            Particle[] tempBalls = new Particle[tempChain.length];
            tempBalls[0] = new Particle(chainStart[0], chainStart[1], chainRadius[0], spawnLocked, getColour(time));
            for (int i = 1; i < tempChain.length - 1; i++) {
                tempBalls[i] = new Particle(tempChain[i][0], tempChain[i][1], chainRadius[0], false, getColour(time));
            }
            tempBalls[tempChain.length - 1] = new Particle(tempChain[tempChain.length - 1][0], tempChain[tempChain.length - 1][1], chainRadius[0], spawnLocked2, getColour(time));

            for (int i = 0; i < tempChain.length - 1; i++) {
                balls.add(tempBalls[i]);
                links.add(new Link(tempBalls[i], tempBalls[i + 1], chainRadius[0] * 2f));
            }
            balls.add(tempBalls[tempChain.length - 1]);
            tempChain = null;
        }

    }

    public void simulate(float mouseX, float mouseY, float dt) {
        time += dt;
        for (int i = 0; i < subSteps; i++) {
            update(mouseX, mouseY, dt / subSteps);
            updateLinks();
            collisions();
            bounds();
        }
        if (spawner) spawnBall(dt / subSteps);
    }

    private void update(float mouseX, float mouseY, float dt) {
        if (mouseForce && Gdx.input.isButtonPressed(0)) {
            for (Particle ball : balls) {
                float dx = mouseX - ball.getX();
                float dy = mouseY - ball.getY();
                if (dx * dx + dy * dy > mouseRadius[0] * mouseRadius[0])
                    ball.update(forceX[0] * forceStrength[0], forceY[0] * forceStrength[0], dt);
                else {
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);
                    ball.update(forceX[0] * forceStrength[0] - (dx / dist) * mouseStrength[0], forceX[0] * forceStrength[0] - (dy / dist) * mouseStrength[0], dt);
                }
            }
        } else {
            for (Particle ball : balls) {
                ball.update(forceX[0] * forceStrength[0], forceY[0] * forceStrength[0], dt);
            }
        }
    }

    private void updateLinks() {
        for (Link link : links)
            link.update();
    }

    private void collisions() {
        for (int i = 0; i < balls.size(); i++) {
            for (int j = i + 1; j < balls.size(); j++) {
                float dx = balls.get(i).getX() - balls.get(j).getX();
                float dy = balls.get(i).getY() - balls.get(j).getY();
                float dist = dx * dx + dy * dy;
                float minDist = balls.get(i).getRadius() + balls.get(j).getRadius();
                if (dist < minDist * minDist) {
                    dist = (float) Math.sqrt(dist);
                    float diff = restitution[0] * (dist - minDist);
                    float ratio1 = (float) balls.get(i).getRadius() / (balls.get(i).getRadius() + balls.get(j).getRadius());
                    float ratio2 = (float) balls.get(j).getRadius() / (balls.get(i).getRadius() + balls.get(j).getRadius());
                    dx /= dist;
                    dy /= dist;
                    balls.get(i).changePos(-dx * diff * ratio2, -dy * diff * ratio2);
                    balls.get(j).changePos(dx * diff * ratio1, dy * diff * ratio1);
                }
            }
        }
    }

    private void bounds() {
        if (circle) {
            for (Particle ball : balls) {
                float dx = boundX - ball.getX();
                float dy = boundY - ball.getY();
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist > boundRadius - ball.getRadius()) {
                    ball.setPos(boundX - (dx / dist) * (boundRadius - ball.getRadius()), boundY - (dy / dist) * (boundRadius - ball.getRadius()));
                }
            }
        } else {
            for (Particle ball : balls) {
                if (ball.getX() - ball.getRadius() < boundX - boundRadius)
                    ball.setPos(boundX - boundRadius + ball.getRadius(), ball.getY());
                else if (ball.getX() + ball.getRadius() > boundX + boundRadius)
                    ball.setPos(boundX + boundRadius - ball.getRadius(), ball.getY());
                if (ball.getY() - ball.getRadius() < boundY - boundRadius)
                    ball.setPos(ball.getX(), boundY - boundRadius + ball.getRadius());
                else if (ball.getY() + ball.getRadius() > boundY + boundRadius)
                    ball.setPos(ball.getX(), boundY + boundRadius - ball.getRadius());
            }
        }
    }

    public void render(ShapeRenderer sr) {
        sr.setColor(Color.BLACK);
        if (circle) sr.circle(boundX, boundY, boundRadius);
        else sr.rect(boundX - boundRadius, boundY - boundRadius, boundRadius * 2, boundRadius * 2);
        for (Particle ball : balls) ball.render(sr);
        sr.setColor(getColour(time));
        if (tempChain != null) for (float[] ball : tempChain) {
            sr.circle(ball[0], ball[1], chainRadius[0]);
        }
    }

    private Color getColour(float t) {
        if (!rainbow) return new Color(colour[0], colour[1], colour[2], 1);
        float r = (float) Math.sin(t);
        float g = (float) Math.sin(t + 0.66f * Math.PI);
        float b = (float) Math.sin(t + 1.32f * Math.PI);
        return new Color(r * r, g * g, b * b, 1);
    }

    private int getBallSize() {
        if (randomSize) {
            return (minSize[0] == maxSize[0]) ? minSize[0] : rand.nextInt(minSize[0], maxSize[0]);
        }
        return ballRadius[0];
    }

    public void resetBalls() {
        balls.clear();
        links.clear();
    }

    private void spawnBall(float dt) {
        if (System.nanoTime() < prevTime + spawnDelay[0] * 1000000L) return;
        prevTime = System.nanoTime();

        float angle = (float) (spawnAngle[0] * Math.sin(anglePeriod[0] * time) + 0.5 * Math.PI);
        float vx = (float) (Math.cos(angle) * spawnSpeed[0] * dt);
        float vy = (float) (-Math.sin(angle) * spawnSpeed[0] * dt);
        balls.add(new Particle(spawnerX, spawnerY, vx, vy, getBallSize(), false, getColour(time)));
    }

    public void resetForces() {
        this.forceStrength = new float[]{forceStrengthDefault};
        this.forceX = new float[]{forceXDefault};
        this.forceY = new float[]{forceYDefault};
        this.restitution = new float[]{restitutionDefault};
    }

    public void resetSpawner() {
        this.spawnerX = boundX;
        this.spawnerY = boundY + boundRadius * 2 / 3;
        this.spawnDelay = new int[]{spawnDelayDefault};
        this.spawnSpeed = new float[]{spawnSpeedDefault};
        this.spawnAngle = new float[]{spawnAngleDefault};
        this.anglePeriod = new float[]{anglePeriodDefault};
        this.spawner = false;
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

    public int getSize() {
        return balls.size();
    }

    public int getSubSteps() {
        return subSteps;
    }

    public void setSubSteps(int subSteps) {
        this.subSteps = Math.abs(subSteps);
    }

    public int getBoundY() {
        return boundY;
    }

    public void setBoundY(int boundY) {
        boundY = Math.abs(boundY);
        if (boundY + boundRadius > maxHeight || boundY - boundRadius < 0) return;
        this.boundY = boundY;
    }

    public int getBoundX() {
        return boundX;
    }

    public void setBoundX(int boundX) {
        boundX = Math.abs(boundX);
        if (boundX + boundRadius > maxWidth || boundX - boundRadius < 0) return;
        this.boundX = boundX;
    }

    public int getBoundRadius() {
        return boundRadius;
    }

    public void setBoundRadius(int boundRadius) {
        boundRadius = Math.abs(boundRadius);
        if (boundRadius > Math.min(maxWidth / 2, maxHeight / 2)) return;
        this.boundRadius = boundRadius;
    }

    public int getSpawnerX() {
        return spawnerX;
    }

    public void setSpawnerX(int spawnerX) {
        if (spawnerX < boundX - boundRadius || spawnerX > boundX + boundRadius) return;
        this.spawnerX = spawnerX;
    }

    public int getSpawnerY() {
        return spawnerY;
    }

    public void setSpawnerY(int spawnerY) {
        if (spawnerY < boundY - boundRadius || spawnerY > boundY + boundRadius) return;
        this.spawnerY = spawnerY;
    }

    public String getSpawnObject() {
        return spawnObject.name();
    }

    public void cycleMouseSpawn() {
        switch (spawnObject) {
            case Ball:
                spawnObject = SpawnObjectType.Chain;
                break;
            case Chain:
                spawnObject = SpawnObjectType.Ball;
                break;
        }
    }
}
