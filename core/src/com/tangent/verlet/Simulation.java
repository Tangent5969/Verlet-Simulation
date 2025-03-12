package com.tangent.verlet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.Random;

public class Simulation {
    private final float boundRadius;
    private final float boundX;
    private final float boundY;
    private final float gravity;
    private final float restitution;
    private final int subSteps;
    private final boolean circle;
    private final long spawnDelay;
    private final float spawnSpeed;
    private final float spawnAngle;
    private final float anglePeriod;
    private final int minSize;
    private final int maxSize;
    private final Random rand;

    private ArrayList<Particle> balls;
    private boolean spawner;
    private float time;
    private long prevTime;

    public Simulation(SimulationConfig config) {
        this.gravity = config.getGravity();
        this.restitution = config.getRestitution();
        this.subSteps = config.getSubSteps();
        this.boundX = config.getBoundX();
        this.boundY = config.getBoundY();
        this.boundRadius = config.getBoundRadius();
        this.circle = config.isCircle();
        this.spawnDelay = config.getSpawnDelay() * 1000000L;
        this.spawnSpeed = config.getSpawnSpeed();
        this.spawnAngle = config.getSpawnAngle();
        this.anglePeriod = config.getAnglePeriod();
        this.minSize = config.getMinSize();
        this.maxSize = config.getMaxSize();
        this.rand = new Random();
        this.balls = new ArrayList<>();
        this.spawner = false;
        this.time = 0;
        this.prevTime = 0;
    }

    public void addBall(float x, float y) {
        if (circle) {
            float dx = boundX - x;
            float dy = boundY - y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist > boundRadius) {
                return;
            }
        } else {
            if (!(x > boundX - boundRadius) || !(x < boundX + boundRadius) || !(y > boundY - boundRadius) || !(y < boundY + boundRadius)) {
                return;
            }
        }
        balls.add(new Particle(x, y, rand.nextInt(minSize, maxSize), getColour(time)));
    }

    public void simulate(float dt) {
        time += dt;
        for (int i = 0; i < subSteps; i++) {
            update(gravity, dt / subSteps);
            collisions();
            bounds();

        }
        if (spawner) spawnBall(dt / subSteps);
    }

    private void update(float gravity, float dt) {
        for (Particle ball : balls) {
            ball.update(gravity, dt);
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
                    float diff = restitution * (dist - minDist);
                    balls.get(i).changePos(-(dx / dist) * diff, -(dy / dist) * diff);
                    balls.get(j).changePos((dx / dist) * diff, (dy / dist) * diff);
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

    public static Color getColour(float t) {
        float r = (float) Math.sin(t);
        float g = (float) Math.sin(t + 0.66f * Math.PI);
        float b = (float) Math.sin(t + 1.32f * Math.PI);
        return new Color(r * r, g * g, b * b, 1);
    }

    public void toggleSpawner() {
        spawner = !spawner;
    }

    public void reset() {
        balls.clear();
    }

    private void spawnBall(float dt) {
        if (System.nanoTime() < prevTime + spawnDelay) return;
        prevTime = System.nanoTime();

        float angle = (float) (spawnAngle * Math.sin(anglePeriod * time) + 0.5 * Math.PI);
        float vx = (float) (Math.cos(angle) * spawnSpeed * dt);
        float vy = (float) (Math.sin(angle) * spawnSpeed * dt);
        balls.add(new Particle(boundX, boundY + boundRadius * 2 / 3, vx, vy, rand.nextInt(minSize, maxSize), getColour(time)));
    }
}
