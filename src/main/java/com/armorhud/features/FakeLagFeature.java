package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.DecimalSetting;
import com.armorhud.setting.IntegerSetting;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.Vec3d;
import com.armorhud.utils.RenderUtils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FakeLagFeature extends Feature implements UpdateListener {
    private Vec3d pos;
    private Queue<Packet<?>> packets;
    private boolean bruh;
    public IntegerSetting choke = new IntegerSetting("Choke", 100, this);
    public BooleanSetting holdOnKB = new BooleanSetting("Hold On KB", false,this);
    public DecimalSetting kbHoldTime = new DecimalSetting("KB Hold Time", 100f, this);
    public BooleanSetting render = new BooleanSetting("Render", true,this);
    public FakeLagFeature() {
        super("Fake Lag", "Movement");
        packets = new ConcurrentLinkedQueue<>();
        pos = Vec3d.ZERO;
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
        pos = MinecraftClient.getInstance().player.getPos();
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
        flushPackets();
    }

    @Override
    public void onUpdate() {
        if (MinecraftClient.getInstance().currentScreen != null) {
            return;
        }
        handlePacketQueue();
        if (render.getValue()) {
            MatrixStack matrixStack = new MatrixStack();
            Vec3d position = new Vec3d(100, 75, 100);
            int color = 0xFFFF0000;
   //         RenderUtils.renderCircle(position, 5.0, color, 36, matrixStack);
        }
    }

    private void handlePacketQueue() {
        if (!packets.isEmpty() && shouldSendPackets()) {
            flushPackets();
        }
    }

    private boolean shouldSendPackets() {
        return true;
    }

    private void flushPackets() {
        while (!packets.isEmpty()) {
 //           PacketUtil.sendPacket(packets.poll());
        }
    }
}
