package com.armorhud.setting;

import com.armorhud.feature.Feature;
import com.armorhud.keybind.Keybind;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import static org.lwjgl.glfw.GLFW.*;

public class KeybindSetting extends Setting<Keybind> {

    private Keybind value;

    public KeybindSetting(String name, Feature feature, Keybind defaultValue) {
        super(name, feature);
        this.value = defaultValue;
    }

    @Override
    public Keybind getValue() {
        return value;
    }

    public void setValue(Keybind value) {
        this.value = value;
        this.getFeature().onChangeSetting(this);
    }

    @Override
    protected void loadFromStringInternal(String string) {
        String[] parts = string.split(",");

        String name = parts[0];
        int key = Integer.parseInt(parts[1]);
        boolean activeOnPress = Boolean.parseBoolean(parts[2]);
        boolean activeOnRelease = Boolean.parseBoolean(parts[3]);
        String command = parts[4];


        this.value = new Keybind(name, key, activeOnPress, activeOnRelease,command);
    }



    @Override
    public String storeAsString() {

        return Integer.toString(value.getKey());
    }

    public String getKeyName() {
        return glfwGetKeyName(value.getKey(), 0);
    }


}
