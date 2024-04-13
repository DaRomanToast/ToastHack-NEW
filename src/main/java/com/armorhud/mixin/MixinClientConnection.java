package com.armorhud.mixin;

import com.armorhud.events.Type;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void handlePacket(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
    com.armorhud.events.PacketListener.PacketEvent packetEvent = new com.armorhud.events.PacketListener.PacketEvent(packet, Type.INCOMING);
        //@formatter:off
        DietrichEvents2.global().postInternal(com.armorhud.events.PacketListener.PacketEvent.ID, packetEvent);
        //@formatter:on
        if (packetEvent.isCancelled()) {
            ci.cancel();
        }
    }
}
