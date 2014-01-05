package com.manuelmaly.hn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.LineGraphView;
import com.manuelmaly.hn.CommentsActivity.GetLastHNPostCommentsTask;
import com.manuelmaly.hn.model.HNComment;
import com.manuelmaly.hn.model.HNPost;
import com.manuelmaly.hn.model.HNPostComments;
import com.manuelmaly.hn.task.HNPostCommentsTask;
import com.manuelmaly.hn.task.ITaskFinishedHandler;
import com.manuelmaly.hn.task.ITaskFinishedHandler.TaskResultCode;
import com.manuelmaly.hn.util.FileUtil;
import com.manuelmaly.hn.util.FontHelper;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.util.Dictionary;

@EActivity(R.layout.newstrend)
public class NewsTrendActivity extends BaseListActivity implements ITaskFinishedHandler<HNPostComments> {

	@ViewById(R.id.actionbar)
	FrameLayout mActionbarContainer;

	@ViewById(R.id.actionbar_title_button)
	Button mActionbarTitle;

	@ViewById(R.id.actionbar_refresh)
	ImageView mActionbarRefresh;

	@ViewById(R.id.actionbar_refresh_container)
	LinearLayout mActionbarRefreshContainer;

	@ViewById(R.id.actionbar_refresh_progress)
	ProgressBar mActionbarRefreshProgress;

	@ViewById(R.id.actionbar_share)
	ImageView mActionbarShare;

	@ViewById(R.id.actionbar_back)
	ImageView mActionbarBack;

	@ViewById(R.id.graph)
	LinearLayout gragh;
	
	//@ViewById(R.id.newstrend_list)
	//ListView newstrend_list;

	public static final String EXTRA_HNPOST = "HNPOST";
	private final int MINUTE_MAP_SIZE = 60;
	private final int HOUR_MAP_SIZE = 24;
	ArrayList<String> list = new ArrayList<String>();
	Map<Integer, Integer> minuteAgo = new Hashtable<Integer, Integer>(MINUTE_MAP_SIZE);
	Map<Integer, Integer> hourAgo = new Hashtable<Integer, Integer>(HOUR_MAP_SIZE);
	Map<Integer, Integer> dayAgo = new Hashtable<Integer, Integer>();
	String[] timeAgo;
   
    HNPost mPost;
    HNPostComments mComments;
    List<HNComment> mCommentsCache;
    boolean mHaveLoadedPosts = false;
    
	@AfterViews
	public void init() {
		// init map
		for(int i=1;i<=MINUTE_MAP_SIZE;i++){
			minuteAgo.put(Integer.valueOf(i), Integer.valueOf(0));
		}
		for(int i=1;i<=HOUR_MAP_SIZE;i++){
			hourAgo.put(Integer.valueOf(i), Integer.valueOf(0));
		}		
		//get mPost from MainActivity
		mPost = (HNPost) getIntent().getSerializableExtra(EXTRA_HNPOST);
		
		mComments = new HNPostComments();
		mCommentsCache = new ArrayList<HNComment>();

		mActionbarContainer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			}
		});

		mActionbarTitle.setTypeface(FontHelper.getComfortaa(this, true));
		//set text of title
		mActionbarTitle.setText(getString(R.string.newstrend));
        //back to previous page
		mActionbarBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		mActionbarShare.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			}
		});

		mActionbarRefresh.setImageDrawable(getResources().getDrawable(
				R.drawable.refresh));
		
		mActionbarRefreshContainer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			}
		});

		mActionbarRefreshProgress.setVisibility(View.GONE);
		
        //loadIntermediateCommentsFromStore();
        startFeedLoading();
        
	}
	
	@Override
    protected void onResume() {
        super.onResume();
    }
	
    @Override
    public void onTaskFinished(int taskCode, TaskResultCode code, HNPostComments result, Object tag) {
        if (code.equals(TaskResultCode.Success) && list != null)
        	showNewstrend(result);
        else if (!code.equals(TaskResultCode.Success))
            Toast.makeText(this, getString(R.string.
                    error_unable_to_retrieve_comments), Toast.LENGTH_SHORT).show();
        updateEmptyView();
    	updateStatusIndicatorOnLoadingFinished(code);
    }
	
    private void showNewstrend(HNPostComments comments) {
        mComments = comments;
        drawGraphView();
    }
    
    private void loadIntermediateCommentsFromStore() {
        new GetLastHNPostCommentsTask().execute(mPost.getPostID());
    }

    class GetLastHNPostCommentsTask extends FileUtil.GetLastHNPostCommentsTask {
        protected void onPostExecute(HNPostComments result) {
            if (result != null && result.getUserAcquiredFor().equals(Settings
                    .getUserName(NewsTrendActivity.this)))
            	showNewstrend(result);
            else {
                updateEmptyView();
            }
        }
    }
    
    private void updateStatusIndicatorOnLoadingStarted() {
        mActionbarRefreshProgress.setVisibility(View.VISIBLE);
        mActionbarRefresh.setVisibility(View.GONE);
    }
    
    private void updateStatusIndicatorOnLoadingFinished(TaskResultCode code) {
    	mActionbarRefreshProgress.setVisibility(View.GONE);
    	mActionbarRefresh.setVisibility(View.VISIBLE);
    }
    
    private void startFeedLoading() {
    	mHaveLoadedPosts = false;
        HNPostCommentsTask.startOrReattach(this, this, mPost.getPostID(), 0);
        updateStatusIndicatorOnLoadingStarted();
    }
    
    private void updateEmptyView() {
        if (mHaveLoadedPosts)
            //mEmptyView.setText(getString(R.string.no_comments));
        	System.out.println();

        mHaveLoadedPosts = true;
    }
    
    public void drawGraphView() {
    	
    	mCommentsCache = mComments.getComments();
    	timeAgo = new String[mCommentsCache.size()];
    	
    	// XX minute(s)/hour(s)/day(s) ago
		if (mCommentsCache != null) {
			for (int i = 0; i < mCommentsCache.size(); i++)
			{
				String timeAgostr = mCommentsCache.get(i).getTimeAgo();
				Integer key;
				Integer value;
				//timeAgo[i] = timeAgostr;
				if(timeAgostr.contains("minute"))
				{
					int endofnum = timeAgostr.indexOf("minute");
					//add number
					key = Integer.valueOf(timeAgostr.substring(1, endofnum-1));
					value = minuteAgo.get(key);
					// process minute
					minuteAgo.put(key, Integer.valueOf(value.intValue() + 1));
					
					// process hour
					key = Integer.valueOf(1);
					value = hourAgo.get(key);
					hourAgo.put(key, Integer.valueOf(value.intValue() + 1));
					
					// process day
					//value = dayAgo.get(key);
					//value = Integer.valueOf(value.intValue() + 1);					
				}
				else if(timeAgostr.contains("hour"))
				{
					int endofnum = timeAgostr.indexOf("hour");
					//add number
					//hourAgo.add(Integer.valueOf(timeAgostr.substring(1, endofnum-1)));
					// process hour
					key = Integer.valueOf(timeAgostr.substring(1, endofnum-1));
					value = hourAgo.get(key);
					hourAgo.put(key, Integer.valueOf(value.intValue() + 1));
					
					// process day
					//key = Integer.valueOf(1);
					//value = dayAgo.get(key);
					//value = Integer.valueOf(value.intValue() + 1);						
				}
				else
				{
					int endofnum = timeAgostr.indexOf("day");
					//add number
					//dayAgo.add(Integer.valueOf(timeAgostr.substring(1, endofnum-1)));
					// process day
					key = Integer.valueOf(timeAgostr.substring(1, endofnum-1));
					value = dayAgo.get(key);
					if(value == null)
						dayAgo.put(key, 0) ;
					else
						value = Integer.valueOf(value.intValue() + 1);
				}
			}
		}
		/*
		Collections.sort(hourAgo,new Comparator<Integer>(){
			public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
		});*/
		// (Integer [])((Hashtable)hourAgo).values().toArray()
		/*newstrend_list.setAdapter(new ArrayAdapter<Integer>(this,
				android.R.layout.simple_list_item_1, new ArrayList<Integer>(
						hourAgo.values())));

		newstrend_list.setTextFilterEnabled(true);*/

		GraphViewData[] data = new GraphViewData[24];

		for(int i = 0; i < 24; i++)
		{
			data[i] = new GraphViewData(i + 1, hourAgo.get(i + 1));
		}
		//data[2] = new GraphViewData(2, (float)hourAgo.get(2));
		//data[3] = new GraphViewData(3, (float)hourAgo.get(3));


		GraphView graphView = new LineGraphView(this // context
				, "News Trend" // heading
		);

		graphView.addSeries(new GraphViewSeries(data)); // data
		graphView.getGraphViewStyle().setNumHorizontalLabels(4);
		graphView.getGraphViewStyle().setNumVerticalLabels(5);
		//graphView.setViewPort(5, 10);
		graphView.setScrollable(true);
		//graphView.setScalable(true);  
		//graphView.setDrawBackground(true);

		gragh.addView(graphView);
    	
    }
    
	public NewsTrendActivity() {	
	}
}
