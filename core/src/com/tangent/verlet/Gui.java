package com.tangent.verlet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImInt;
import org.lwjgl.glfw.GLFW;

public class Gui {
    private ImGuiImplGlfw imGuiGlfw;
    private ImGuiImplGl3 imGuiGl3;
    private InputProcessor tempProcessor;
    private final Simulation sim;

    public Gui(Simulation sim) {
        this.sim = sim;
        initImGui();
    }

    public void initImGui() {
        imGuiGlfw = new ImGuiImplGlfw();
        imGuiGl3 = new ImGuiImplGl3();
        long windowHandle = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setIniFilename(null);
        io.getFonts().addFontDefault();
        io.getFonts().build();
        imGuiGlfw.init(windowHandle, true);
        imGuiGl3.init("#version 150");
    }

    public void gui() {
        // start imGui
        if (tempProcessor != null) {
            Gdx.input.setInputProcessor(tempProcessor);
            tempProcessor = null;
        }

        imGuiGl3.newFrame();
        imGuiGlfw.newFrame();
        ImGui.newFrame();

        // do stuff
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX(), ImGui.getMainViewport().getPosY(), ImGuiCond.Once);
        ImGui.setNextWindowCollapsed(true, ImGuiCond.Once);
        ImGui.begin("Settings");

        if (ImGui.collapsingHeader("Debug")) {
            ImGui.text(Gdx.graphics.getFramesPerSecond() + " fps");
            ImGui.text(String.format("%.2f", Gdx.graphics.getDeltaTime() * 1000) + " ms");
            ImGui.text(sim.getSize() + " balls");
            if (sim.isFixedSize()) ImGui.text("Fixed Radius : " + sim.ballRadius[0]);
            ImInt steps = new ImInt(sim.getSubSteps());
            if (ImGui.inputInt("Sub Steps", steps)) sim.setSubSteps(steps.get());
            if (ImGui.smallButton("Reset Balls##1")) sim.resetBalls();
        }


        if (ImGui.collapsingHeader("World")) {
            ImInt boundX = new ImInt(sim.getBoundX());
            ImInt boundY = new ImInt(sim.getBoundY());
            ImInt radius = new ImInt(sim.getBoundRadius());
            if (ImGui.inputInt("Centre X", boundX)) sim.setBoundX(boundX.get());
            if (ImGui.inputInt("Centre Y", boundY)) sim.setBoundY(boundY.get());
            if (ImGui.inputInt("Radius", radius)) sim.setBoundRadius(radius.get());
            if (ImGui.smallButton(sim.circle ? "Circle" : "Square")) sim.circle = !sim.circle;
            ImGui.sameLine();
            ImGui.text("World Shape : Circle / Square");

        }

        if (ImGui.collapsingHeader("Forces")) {
            ImGui.sliderFloat("Force Strength", sim.forceStrength, 0, sim.getMaxStrength());
            ImGui.sliderFloat("Force X", sim.forceX, -1, 1);
            ImGui.sliderFloat("Force Y", sim.forceY, -1, 1);
            ImGui.sliderFloat("Restitution", sim.restitution, 0, 1);
            if (ImGui.smallButton("Reset##2")) sim.resetForces();
        }

        if (ImGui.collapsingHeader("Spawner")) {
            ImInt spawnX = new ImInt(sim.getSpawnerX());
            ImInt spawnY = new ImInt(sim.getSpawnerY());
            if (ImGui.checkbox("Active", sim.spawner)) sim.spawner = !sim.spawner;
            if (ImGui.inputInt("Spawner X", spawnX)) sim.setSpawnerX(spawnX.get());
            if (ImGui.inputInt("Spawner Y", spawnY)) sim.setSpawnerY(spawnY.get());
            ImGui.sliderInt("Spawn Delay", sim.spawnDelay, 10, 1000);
            ImGui.sliderFloat("Spawn Speed", sim.spawnSpeed, 0, sim.getMaxSpeed());
            ImGui.sliderFloat("Spawn Angle", sim.spawnAngle, 0.1f, 3.142f);
            ImGui.sliderFloat("Angle Period", sim.anglePeriod, 0.1f, 5);
            if (ImGui.smallButton("Reset##3")) sim.resetSpawner();

        }

        if (ImGui.collapsingHeader("Balls")) {
            if (ImGui.checkbox("Rainbow", sim.rainbow)) sim.rainbow = !sim.rainbow;
            if (sim.isFixedSize()) {
                ImGui.text("Fixed Radius : " + sim.ballRadius[0]);
            } else {
                ImGui.sameLine();
                if (ImGui.checkbox("Random Size", sim.randomSize)) sim.randomSize = !sim.randomSize;
                ImGui.colorEdit3("colour", sim.colour);
                if (sim.randomSize) {
                    ImGui.sliderInt("Min Size", sim.minSize, 1, sim.maxSize[0], 1);
                    if (ImGui.sliderInt("Max Size", sim.maxSize, 2, sim.getUpperSize()))
                        if (sim.minSize[0] > sim.maxSize[0]) sim.minSize[0] = sim.maxSize[0] - 1;
                } else ImGui.sliderInt("Ball Size", sim.ballRadius, 1, sim.getUpperSize());
            }
        }

        if (ImGui.collapsingHeader("Mouse")) {
            if (ImGui.smallButton(sim.mouseForce ? "Force" : "Spawn")) sim.mouseForce = !sim.mouseForce;
            ImGui.sameLine();
            ImGui.text("Mouse Mode : Spawn / Force");
            if (sim.mouseForce) {
                ImGui.sliderInt("Force Radius", sim.mouseRadius, 0, sim.getBoundRadius());
                ImGui.sliderFloat("Mouse Force", sim.mouseStrength, -sim.getMaxStrength(), sim.getMaxStrength());
            } else {
                if (ImGui.smallButton(sim.getSpawnObject())) sim.cycleMouseSpawn();
                ImGui.sameLine();
                ImGui.text("Spawn Object : Ball / Chain");
                if (ImGui.checkbox((sim.getSpawnObject().equals("Ball")) ? "Stationary" : "Point 1 Stationary", sim.spawnLocked))
                    sim.spawnLocked = !sim.spawnLocked;
                if (sim.getSpawnObject().equals("Chain")) {
                    ImGui.sameLine();
                    if (ImGui.checkbox("Point 2 Stationary", sim.spawnLocked2)) sim.spawnLocked2 = !sim.spawnLocked2;
                    if (!sim.isFixedSize()) {
                        ImGui.sliderInt("Chain Radius", sim.chainRadius, 1, sim.getUpperSize());
                    }
                }
            }
        }

        ImGui.end();

        // end imGUI
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
        if (ImGui.getIO().getWantCaptureKeyboard() || ImGui.getIO().getWantCaptureMouse()) {
            tempProcessor = Gdx.input.getInputProcessor();
            Gdx.input.setInputProcessor(null);
        }

    }

    public void disposeImGui() {
        imGuiGl3 = null;
        imGuiGlfw = null;
        ImGui.destroyContext();
    }
}
