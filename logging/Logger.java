package com.endlessonline.client.logging;

import com.endlessonline.client.events.Event;

public interface Logger {

    void log(Event event);
    void log(Type type, String text);

    enum Type { INFO, ERROR }
}
