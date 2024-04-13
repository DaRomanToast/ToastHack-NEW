package com.armorhud.utils;


import com.armorhud.Client;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.io.File;


public class KeyInputHandler{

    private boolean ctrlZeroPressed = false;
    private boolean waitingForNumberKey = false;

    public KeyInputHandler(){
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client){
        long windowHandle = client.getWindow().getHandle();

        if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_CONTROL)== GLFW.GLFW_PRESS &&
            GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_0) == GLFW.GLFW_PRESS && !waitingForNumberKey){
            ctrlZeroPressed = true;
            waitingForNumberKey = true;
        }

        if (ctrlZeroPressed && waitingForNumberKey){
            for(int key = GLFW.GLFW_KEY_1; key <= GLFW.GLFW_KEY_9; key++){
                if (GLFW.glfwGetKey(windowHandle, key) == GLFW.GLFW_PRESS){
                    int configIndex = key - GLFW.GLFW_KEY_1;
                    loadConfig(configIndex);
                    ctrlZeroPressed = false;
                    waitingForNumberKey = false;
                    break;
                }
            }
        }

        if ((GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_RELEASE || GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_0) == GLFW.GLFW_RELEASE) && ctrlZeroPressed) {
            ctrlZeroPressed = false;
            waitingForNumberKey = false;
        }
    }
// coded fully by toast hahahahahahaha best coder
    private void loadConfig(int index) {
        File configDirectory = Client.TOASTHACK.getCwHackDirectory().resolve("config").toFile();
        File[] configFiles = configDirectory.listFiles();
        if (configFiles != null && index >= 0 && index < configFiles.length) {
            String configPath = configFiles[index].getAbsolutePath();
            Client.TOASTHACK.getFeatures().loadFromFile(configPath);
        }
    }
}