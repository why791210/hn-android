// Modify by CalvinChang 
// Login Id : CalvinChang 
// Student Id : 101552030
// Tag : #CalvinChang + number

package com.manuelmaly.hn.task;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import android.util.Log;

import com.manuelmaly.hn.App;
import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.model.HNPost;
import com.manuelmaly.hn.parser.HNFeedParser;
import com.manuelmaly.hn.reuse.CancelableRunnable;
import com.manuelmaly.hn.server.HNCredentials;
import com.manuelmaly.hn.server.IAPICommand;
import com.manuelmaly.hn.server.IAPICommand.RequestType;
import com.manuelmaly.hn.server.JsonStringDownloadCommand;
import com.manuelmaly.hn.server.StringDownloadCommand;
import com.manuelmaly.hn.util.Const;
import com.manuelmaly.hn.util.ExceptionUtil;
import com.manuelmaly.hn.util.FileUtil;
import com.manuelmaly.hn.util.Run;

public abstract class HNFeedTaskBase extends BaseTask<HNFeed> {

    public HNFeedTaskBase(String notificationBroadcastIntentID, int taskCode) {
        super(notificationBroadcastIntentID, taskCode);
    }
    
    // header type, default is HTML #CalvinChang01
    protected IAPICommand.HeaderType mHeaderType = IAPICommand.HeaderType.HTML;
    
    protected IAPICommand.HeaderType getHeaderType(){
 
    	return this.mHeaderType;
    }
    
    @Override
    public CancelableRunnable getTask() {
    	if(getHeaderType() == IAPICommand.HeaderType.JSON)
    		return new HNFeedSearchTaskRunnable();
    	
    	return new HNFeedTaskRunnable();
    }
    
    protected abstract String getFeedURL();
    
    protected HashMap<String, String> getFeedParam(){
    	
    	return new HashMap<String, String>();
    	
    }
    class HNFeedTaskRunnable extends CancelableRunnable {

        StringDownloadCommand mFeedDownload;

        @Override
        public void run() {
            mFeedDownload = new StringDownloadCommand(getFeedURL(), getFeedParam(), RequestType.GET, false, null,
                App.getInstance(), HNCredentials.getCookieStore(App.getInstance()));
            
            mFeedDownload.run();

            if (mCancelled)
                mErrorCode = IAPICommand.ERROR_CANCELLED_BY_USER;
            else
                mErrorCode = mFeedDownload.getErrorCode();

            if (!mCancelled && mErrorCode == IAPICommand.ERROR_NONE) {
                HNFeedParser feedParser = new HNFeedParser();
                try {
                    mResult = feedParser.parse(mFeedDownload.getResponseContent());
                    Run.inBackground(new Runnable() {
                        public void run() {
                            FileUtil.setLastHNFeed(mResult);
                        }
                    });
                } catch (Exception e) {
                    mResult = null;
                    ExceptionUtil.sendToGoogleAnalytics(e, Const.GAN_ACTION_PARSING);
                    Log.e("HNFeedTask", "HNFeed Parser Error :(", e);
                }
            }

            if (mResult == null)
                mResult = new HNFeed();
        }

        @Override
        public void onCancelled() {
            mFeedDownload.cancel();
        }

    }

    // Create TaskRunnable for Search #CalvinChang02
    class HNFeedSearchTaskRunnable extends CancelableRunnable {

    	JsonStringDownloadCommand mFeedDownload;

        @Override
        public void run() {
            mFeedDownload = new JsonStringDownloadCommand(getFeedURL(), getFeedParam(), RequestType.GET, false, null,
                App.getInstance(), HNCredentials.getCookieStore(App.getInstance()));
            
            mFeedDownload.run();

            if (mCancelled)
                mErrorCode = IAPICommand.ERROR_CANCELLED_BY_USER;
            else
                mErrorCode = mFeedDownload.getErrorCode();

            if (!mCancelled && mErrorCode == IAPICommand.ERROR_NONE) {
            	// parse json datas
            	//String response = mFeedDownload.getResponseContent();
            	
            	try {
					String results = new JSONObject(mFeedDownload.getResponseContent()).getString("results");
					int resultLength = new JSONArray(results).length();
					
					mResult = new HNFeed();
					
					for(int i=0; i< resultLength ; i++){
						String item = new JSONArray(results).getJSONObject(i).getString("item");
						// HNPost(String url, String title, String urlDomain, String author, String postID, int commentsCount, int points, String upvoteURL)
						JSONObject obj = new JSONObject(item);
						mResult.addPost(new HNPost(
											obj.getString("url"),
											obj.getString("title"),
											obj.getString("domain"),
											obj.getString("username"),
											obj.getString("id"),
											obj.getInt("num_comments"),
											obj.getInt("points"),
											obj.getString("url")
								));
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	//String NAME = new JSONObject(mJSONText).getString("name");
            	//String str = new JSONArray()
            	//JSONObject josnoObject = new JSONObject(mFeedDownload.getResponseContent());
            	//JSONArray itemArray = josnoObject.getJSONArray("results");
            	
            	//itemResults.getJSONArray(name)
            }

            if (mResult == null)
                mResult = new HNFeed();
        }

        @Override
        public void onCancelled() {
            mFeedDownload.cancel();
        }

    }    
}
