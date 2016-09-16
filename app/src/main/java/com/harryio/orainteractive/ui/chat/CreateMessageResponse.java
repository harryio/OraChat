package com.harryio.orainteractive.ui.chat;

public class CreateMessageResponse {
    boolean success;
    Message data;

    public boolean isSuccess() {
        return success;
    }

    public Message getData() {
        return data;
    }
}
