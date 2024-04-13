package com.armorhud;

public class Rotation {
    private float yaw;
    private float pitch;
    private boolean ignoreYaw;
    private boolean ignorePitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.ignoreYaw = false;
        this.ignorePitch = false;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isIgnoreYaw() {
        return ignoreYaw;
    }

    public void setIgnoreYaw(boolean ignoreYaw) {
        this.ignoreYaw = ignoreYaw;
    }

    public boolean isIgnorePitch() {
        return ignorePitch;
    }

    public void setIgnorePitch(boolean ignorePitch) {
        this.ignorePitch = ignorePitch;
    }
}
