package com.tangent.verlet;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends ApplicationAdapter {
    private ShapeRenderer sr;
    private Camera camera;
    private FitViewport viewport;
    Simulation sim;


    @Override
    public void create() {
        sr = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(2000, 2000, camera);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        sim = new Simulation(-1000, 0.25f, 8, 1000, 1000, 950, true, 50, 2000, 1.2f);
    }

    @Override
    public void render() {
        if (Gdx.input.isButtonJustPressed(0)) {
            Vector2 coords = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            sim.addBall(coords.x, coords.y);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            sim.toggleSpawner();
        }

        ScreenUtils.clear(Color.DARK_GRAY);
        viewport.apply();
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sim.simulate(Gdx.graphics.getDeltaTime());
        sim.render(sr);
        sr.end();
    }

    @Override
    public void dispose() {
        sr.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.setScreenPosition(viewport.getScreenX(), viewport.getScreenY());
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }
}
