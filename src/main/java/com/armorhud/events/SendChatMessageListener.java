package com.armorhud.events;

import de.florianmichael.dietrichevents2.CancellableEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public interface SendChatMessageListener {
     void sendChatMessage(final SendChatMessageEvent event);

     class SendChatMessageEvent extends CancellableEvent<SendChatMessageListener> {
         public static final int ID = 25;
         private String message;
        private @Getter @Setter boolean modified;

        public SendChatMessageEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
            this.modified = true;
        }


         @Override
         public void call(SendChatMessageListener listener) {
             listener.sendChatMessage(this);
         }
    }
}