package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;

public interface GameRenderListener {
    void onGameRender(final GameRenderEvent event);

    class GameRenderEvent extends CancellableEvent<GameRenderListener> {
        private @Getter @Setter MatrixStack stack;
        public static final int ID = 7;
        public GameRenderEvent(MatrixStack stack) {
            this.stack = stack;
        }


        @Override
        public void call(GameRenderListener listener) {
            listener.onGameRender(this);
        }
    }
}