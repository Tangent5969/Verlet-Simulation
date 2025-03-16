package com.tangent.verlet;

public class Link {
    private final Particle p1;
    private final Particle p2;
    private final float distance;

    public Link(Particle p1, Particle p2, Float dist) {
        this.p1 = p1;
        this.p2 = p2;
        this.distance = dist;
    }

    public void update() {
        float dx = p1.getX() - p2.getX();
        float dy = p1.getY() - p2.getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        float diff = (dist - distance) / 2;
        dx /= dist;
        dy /= dist;
        p1.changePos(-dx * diff, -dy * diff);
        p2.changePos(dx * diff, dy * diff);
    }
}
