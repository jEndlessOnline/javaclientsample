package com.endlessonline.client.logging;

import com.endlessonline.client.events.Event;

public class SystemLogger implements Logger {

    @Override
    public void log(Event event) {
        System.out.println(event);
    }

    @Override
    public void log(Type type, String text) {
        switch (type) {
            case INFO:
                System.out.println(text);
                break;
            case ERROR:
                System.err.println(text);
                break;
        }
    }
}
