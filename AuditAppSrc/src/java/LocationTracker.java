package com.machadalo.audit;
import android.location.Location;

/**
 * Created by root on 6/1/17.
 */

public interface LocationTracker {
    public interface LocationUpdateListener{

        public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime);
    }

    public void start();
    public void start(LocationUpdateListener update);

    public void stop();

    public boolean hasLocation();

    public boolean hasPossiblyStaleLocation();

    public Location getLocation();

    public Location getPossiblyStaleLocation();
}

