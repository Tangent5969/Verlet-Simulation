package com.tangent.verlet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Particle {

    private float x;
    private float y;
    private float prevX;
    private float prevY;
    private float accX;
    private float accY;
    private int radius;
    private Color colour;

    public Particle(float x, float y, int r, Color colour, float accX, float accY) {
        this.x = x;
        this.y = y;
        this.prevX = x;
        this.prevY = y;
        this.radius = r;
        this.accX = accX;
        this.accY = accY;
        this.colour = colour;
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void changePos(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void update(float dt) {
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
