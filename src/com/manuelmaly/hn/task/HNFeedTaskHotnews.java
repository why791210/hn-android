package com.manuelmaly.hn.task;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;

import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.server.IAPICommand;

public class HNFeedTaskHotnews extends HNFeedTaskBase {

	private static HNFeedTaskHotnews instance;
	private static HashMap<String, String> mParam = null;
	public static final String BROADCAST_INTENT_ID = "HNFeedHotnews";
    private static HNFeedTaskHotnews getInstance(int taskCode) {
        synchronized (HNFeedTaskBase.class) {
            if (instance == null)
                instance = new HNFeedTaskHotnews(taskCode);
        }
        return instance;
    }
	
    private HNFeedTaskHotnews(int taskCode) {
        super(BROADCAST_INTENT_ID, taskCode);
        mHeaderType = IAPICommand.HeaderType.JSON;
    }
    
    public static void start(Activity activity, ITaskFinishedHandler<HNFeed> finishedHandler, int taskCode, HashMap<String, String> param) {
    	
    	HNFeedTaskHotnews task = getInstance(taskCode);
    	mParam = param;
        task.setOnFinishedHandler(activity, finishedHandler, HNFeed.class);
        if (!task.isRunning())
            task.startInBackground();
    	
    }
    
	@Override
	protected String getFeedURL() {
		return "http://api.thriftdb.com/api.hnsearch.com/items/_search";
	}
	
	@Override
    protected HashMap<String, String> getFeedParam(){
    	return mParam;
    }
	
    public static void stopCurrent(Context applicationContext) {
        getInstance(0).cancel();
    }

    public static boolean isRunning(Context applicationContext) {
        return getInstance(0).isRunning();
    }

}
