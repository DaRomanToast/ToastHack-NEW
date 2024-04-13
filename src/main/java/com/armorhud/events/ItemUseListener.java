package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;

import java.util.ArrayList;

public interface ItemUseListener {
    void onItemUse(ItemUseEvent event);

    class ItemUseEvent extends CancellableEvent<ItemUseListener> {
        public static final int ID = 12;
        @Override
        public void call(ItemUseListener listener) {
            listener.onItemUse(this);
        }
    }
}
