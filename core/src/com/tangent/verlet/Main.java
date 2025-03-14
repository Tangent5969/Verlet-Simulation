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
    private SimulationConfig config;
    private Simulation sim;
    private Gui gui;

    @Override
    public void create() {
        config = new SimulationConfig("config.txt");
        sr = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(config.getWorldWidth(), config.getWorldHeight(), camera);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        sim = new Simulation(config);
        gui = new Gui(sim);
    }

    @Override
    public void render() {
        Vector2 coords = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        if (Gdx.input.isButtonJustPressed(0)) {
            sim.addBall(coords.x, coords.y);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            sim.spawner = !sim.spawner;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            sim.reset();
        }

        ScreenUtils.clear(Color.DARK_GRAY);
        viewport.apply();
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sim.simulate(coords.x, coords.y, Gdx.graphics.getDeltaTime());
        sim.render(sr);
        sr.end();

        gui.gui();
    }

    @Override
    public void dispose() {
        sr.dispose();
        gui.disposeImGui();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.setScreenPosition(viewport.getScreenX(), viewport.getScreenY());
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
    }
}
