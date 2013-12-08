package com.manuelmaly.hn.task;
import java.util.HashMap;

import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.server.IAPICommand;

import android.app.Activity;
import android.content.Context;

public class HNFeedTaskSearch extends HNFeedTaskBase {

	//private HNFeed mFeedToAttachResultsTo;
	private static HNFeedTaskSearch instance;
	private static String mSearchString = null;
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
    
    public static void start(Activity activity, ITaskFinishedHandler<HNFeed> finishedHandler, int taskCode, String searchString) {
    	
    	HNFeedTaskSearch task = getInstance(taskCode);
    	mSearchString = searchString;
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
    	
		HashMap<String, String> param =  new HashMap<String, String>();
		
		param.put("q", mSearchString);
		param.put("weights[title]", "1.1");
		param.put("weights[text]", "0.7");
		param.put("weights[domain]", "2.0");
		param.put("weights[username]", "0.1");
		param.put("weights[type]", "0.0");
		param.put("boosts[fields][points]", "0.15");
		param.put("boosts[fields][num_comments]", "0.15");
		param.put("boosts[functions][pow(2,div(div(ms(create_ts,NOW),3600000),72))]", "200.00");
		param.put("pretty_print", "true");
		
    	return param;
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
