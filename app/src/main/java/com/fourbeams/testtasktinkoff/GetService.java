package com.fourbeams.testtasktinkoff;

import android.app.IntentService;
import android.content.Intent;

/**
 * IntentService for starting processor for getting data from server
 * <br/>{@link #onDestroy()} method indicates to ServiceFacade that service finished getting data from server
 *
 */
public class GetService extends IntentService {
    private String task;

    public GetService() {
        super("GetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        task = intent.getStringExtra("TASK");
        new Processor(getApplicationContext()).startGetProcessor();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ServiceFacade.getInstance()
                .removeServiceFromPool(task);
    }
}