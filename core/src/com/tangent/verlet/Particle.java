package com.tangent.verlet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Particle {

    private float x;
    private float y;
    private float prevX;
    private float prevY;
    private final int radius;
    private final boolean locked;
    private final Color colour;

    public Particle(float x, float y, int r, boolean locked, Color colour) {
        this.x = x;
        this.y = y;
        this.prevX = x;
        this.prevY = y;
        this.radius = r;
        this.locked = locked;
        this.colour = colour;
    }

    public Particle(float x, float y, float vx, float vy, int r, boolean locked, Color colour) {
        this.x = x;
        this.y = y;
        this.locked = locked;
        this.prevX = x - vx;
        this.prevY = y - vy;
        this.radius = r;
        this.colour = colour;
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void changePos(float x, float y) {
        if (locked) return;
        this.x += x;
        this.y += y;
    }

    public void update(float accX, float accY, float dt) {
        if (locked) return;
        float tempX = x;
        float tempY = y;
        x = 2 * x - prevX + accX * dt * dt;
        y = 2 * y - prevY + accY * dt * dt;
        prevX = tempX;
        prevY = tempY;
    }

    public void render(ShapeRenderer sr) {
        sr.setColor(colour);
        sr.circle(x, y, radius);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

}
