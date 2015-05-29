package com.groupalarm.asijge.groupalarm;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Application.java is a singleton running over the whole lifecycle of the GroupAlarm app.
 * Can be used to create any variables that need global access.
 *
 * @author asijge
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Initialise Parse.com app Id and Client key
        // ****************************************************************************
        // Uncomment and fill in with your Parse credentials:
        // Parse.initialize(this, "your_application_id", "your_client_key");
        // ****************************************************************************

        // Save details of the current app installation to the Parse cloud
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
