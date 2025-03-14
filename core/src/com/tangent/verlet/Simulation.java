package com.tangent.verlet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.Random;

public class Simulation {
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
    public boolean spawner;
    public boolean rainbow;
    public float[] colour;
    public boolean mouseForce;
    public int[] mouseRadius;
    public float[] mouseStrength;

    private ArrayList<Particle> balls;
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

        this.rainbow = true;
        this.colour = colourDefault;
        this.balls = new ArrayList<>();
        this.time = 0;
        this.prevTime = 0;
    }

    public void addBall(float x, float y) {
        if (mouseForce) return;
        if (circle) {
            float dx = boundX - x;
            float dy = boundY - y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist > boundRadius) {
                return;
            }
        } else {
            if ((x < boundX - boundRadius) || (x > boundX + boundRadius) || (y < boundY - boundRadius) || (y > boundY + boundRadius)) {
                return;
            }
        }
        balls.add(new Particle(x, y, (minSize[0] == maxSize[0]) ? minSize[0] : rand.nextInt(minSize[0], maxSize[0]), getColour(time)));
    }

    public void simulate(float mouseX, float mouseY, float dt) {
        time += dt;
        for (int i = 0; i < subSteps; i++) {
            update(mouseX, mouseY, dt / subSteps);
            collisions();
            bounds();

        }
        if (spawner) spawnBall(dt / subSteps);
    }

    private void update(float mouseX, float mouseY, float dt) {
        if (mouseForce) {
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
    }

    private Color getColour(float t) {
        if (!rainbow) return new Color(colour[0], colour[1], colour[2], 1);
        float r = (float) Math.sin(t);
        float g = (float) Math.sin(t + 0.66f * Math.PI);
        float b = (float) Math.sin(t + 1.32f * Math.PI);
        return new Color(r * r, g * g, b * b, 1);
    }

    public void reset() {
        balls.clear();
    }

    private void spawnBall(float dt) {
        if (System.nanoTime() < prevTime + spawnDelay[0] * 1000000L) return;
        prevTime = System.nanoTime();

        float angle = (float) (spawnAngle[0] * Math.sin(anglePeriod[0] * time) + 0.5 * Math.PI);
        float vx = (float) (Math.cos(angle) * spawnSpeed[0] * dt);
        float vy = (float) (Math.sin(angle) * spawnSpeed[0] * dt);
        balls.add(new Particle(boundX, boundY + boundRadius * 2f / 3, vx, vy, (minSize[0] == maxSize[0]) ? minSize[0] : rand.nextInt(minSize[0], maxSize[0]), getColour(time)));
    }

    public void resetForces() {
        this.forceStrength = new float[]{forceStrengthDefault};
        this.forceX = new float[]{forceXDefault};
        this.forceY = new float[]{forceYDefault};
        this.restitution = new float[]{restitutionDefault};
    }

    public void resetSpawner() {
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
        if (boundY + boundRadius > maxHeight || boundY - boundRadius < 0) {
            this.boundY = (int) (maxHeight / 2);
        } else {
            this.boundY = boundY;
        }
    }

    public int getBoundX() {
        return boundX;
    }

    public void setBoundX(int boundX) {
        boundX = Math.abs(boundX);
        if (boundX + boundRadius > maxWidth || boundX - boundRadius < 0) {
            this.boundX = (int) (maxWidth / 2);
        } else {
            this.boundX = boundX;
        }
    }

    public int getBoundRadius() {
        return boundRadius;
    }

    public void setBoundRadius(int boundRadius) {
        this.boundRadius = (int) Math.min(Math.abs(boundRadius), Math.min(maxWidth / 2, maxHeight / 2));
    }
}
