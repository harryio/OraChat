package com.harryio.orainteractive.ui.chat;

import java.util.List;

public class MessageList {
    private boolean success;
    private List<Message> data;

    public boolean isSuccess() {
        return success;
    }

    public List<Message> getMessages() {
        return data;
    }
}
