package com.manuelmaly.hn.task;
import com.manuelmaly.hn.model.HNFeed;

import android.app.Activity;
import android.content.Context;

public class HNFeedTaskSearch extends HNFeedTaskBase {

	private HNFeed mFeedToAttachResultsTo;
	private static HNFeedTaskSearch instance;
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
    }
    
    public static void start(Activity activity, ITaskFinishedHandler<HNFeed> finishedHandler,
            				 HNFeed feedToAttachResultsTo, int taskCode) {
    	
    		HNFeedTaskSearch task = getInstance(taskCode);
            task.setOnFinishedHandler(activity, finishedHandler, HNFeed.class);
            task.setFeedToAttachResultsTo(feedToAttachResultsTo);
            if (task.isRunning()){
                task.cancel();
            }
            
            task.startInBackground();
        }
    
	@Override
	protected String getFeedURL() {
		// test url
		return "http://api.thriftdb.com/api.hnsearch.com/items/_search?" +
			    "q=facebook&weights[title]=1.1&weights[text]=0.7&weights[domain]=2.0&" +
			    "weights[username]=0.1&weights[type]=0.0&boosts[fields][points]=0.15&" +
			    "boosts[fields][num_comments]=0.15&" +
			    "boosts[functions][pow(2,div(div(ms(create_ts,NOW),3600000),72))]=200.0&" +
			    "pretty_print=true";
	}
	
    public static void stopCurrent(Context applicationContext) {
        getInstance(0).cancel();
    }

    public static boolean isRunning(Context applicationContext) {
        return getInstance(0).isRunning();
    }
    
    public void setFeedToAttachResultsTo(HNFeed feedToAttachResultsTo) {
        this.mFeedToAttachResultsTo = feedToAttachResultsTo;
    }
}
