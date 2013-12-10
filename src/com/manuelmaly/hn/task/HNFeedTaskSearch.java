package com.manuelmaly.hn.task;
import java.util.HashMap;

import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.server.IAPICommand;

import android.app.Activity;
import android.content.Context;

public class HNFeedTaskSearch extends HNFeedTaskBase {

	//private HNFeed mFeedToAttachResultsTo;
	private static HNFeedTaskSearch instance;
	private static HashMap<String, String> mParam = null;
	public static final String BROADCAST_INTENT_ID = "HNFeedSearch";
    private static HNFeedTaskSearch getInstance(int taskCode) {
        synchronized (HNFeedTaskBase.class) {
            if (instance == null)
                instance = new HNFeedTaskSearch(taskCode);
        }
        return instance;
    }
	
    private HNFeedTaskSearch(int taskCode) {
        super(BROADCAST_INTENT_ID, taskCode);
        mHeaderType = IAPICommand.HeaderType.JSON;
    }
    
    public static void start(Activity activity, ITaskFinishedHandler<HNFeed> finishedHandler, int taskCode, HashMap<String, String> param) {
    	
    	HNFeedTaskSearch task = getInstance(taskCode);
    	mParam = param;
        task.setOnFinishedHandler(activity, finishedHandler, HNFeed.class);
        if (!task.isRunning())
            task.startInBackground();
    	
    }
    
	@Override
	protected String getFeedURL() {
		// test url
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
   /* 
    public void setFeedToAttachResultsTo(HNFeed feedToAttachResultsTo) {
        this.mFeedToAttachResultsTo = feedToAttachResultsTo;
    }*/
}
