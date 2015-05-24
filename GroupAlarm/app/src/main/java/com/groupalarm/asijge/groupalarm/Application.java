package com.groupalarm.asijge.groupalarm;

import com.parse.Parse;

/**
 * Created by sehlstedt on 24/05/15.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "FId7HlOrHpQ3O8UCZkZtUP6vdxCR0T0DfFgbRitf", "6jgsndZW6hvsVHenBLpuQ7egH6HPjGPIBPqn89A1");
    }
}
