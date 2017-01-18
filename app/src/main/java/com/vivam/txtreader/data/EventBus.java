package com.vivam.txtreader.data;

import com.squareup.otto.Bus;

/**
 * Created by kangweodai on 18/01/17.
 */

public class EventBus {

    private static Bus bus = new Bus();

    public static void register(Object event) {
        bus.register(event);
    }

    public static void unregister(Object event) {
        bus.unregister(event);
    }

    public static void post(Object event) {
        bus.post(event);
    }
}
