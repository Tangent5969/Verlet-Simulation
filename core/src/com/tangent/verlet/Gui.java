package com.tangent.verlet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
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
        ImGui.begin("Settings");

        if (ImGui.collapsingHeader("Debug")) {
            ImGui.text(Gdx.graphics.getFramesPerSecond() + " fps");
            ImGui.text(String.format("%.2f", Gdx.graphics.getDeltaTime() * 1000) + " ms");
            ImGui.text(sim.getSize() + " balls");
            if (ImGui.smallButton("Reset##1")) sim.reset();

        }

        if (ImGui.collapsingHeader("Forces")) {
            ImGui.sliderFloat("Force Strength", sim.forceStrength, 0, sim.getMaxStrength());
            ImGui.sliderFloat("Force X", sim.forceX, -1, 1);
            ImGui.sliderFloat("Force Y", sim.forceY, -1, 1);
            ImGui.sliderFloat("Restitution", sim.restitution, 0, 1);
            if (ImGui.smallButton("Reset##2")) sim.resetForces();
        }

        if (ImGui.collapsingHeader("Spawner")) {
            if (ImGui.checkbox("Active", sim.spawner)) sim.spawner = !sim.spawner;
            ImGui.sliderInt("Spawn Delay", sim.spawnDelay, 10, 1000);
            ImGui.sliderFloat("Spawn Speed", sim.spawnSpeed, 0, sim.getMaxSpeed());
            ImGui.sliderFloat("Spawn Angle", sim.spawnAngle, 0.1f, 2);
            ImGui.sliderFloat("Angle Period", sim.anglePeriod, 0.1f, 10);
            if (ImGui.smallButton("Reset##3")) sim.resetSpawner();

        }

        if (ImGui.collapsingHeader("Balls")) {
            if (ImGui.checkbox("Rainbow", sim.rainbow)) sim.rainbow = !sim.rainbow;
            ImGui.colorEdit3("colour", sim.colour);

            ImGui.sliderInt("Min Size", sim.minSize, 1, sim.maxSize[0], 1);
            if (ImGui.sliderInt("Max Size", sim.maxSize, 2, sim.getUpperSize()))
                if (sim.minSize[0] > sim.maxSize[0]) sim.minSize[0] = sim.maxSize[0] - 1;
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
