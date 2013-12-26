package com.manuelmaly.hn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
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

	//@ViewById(R.id.graph)
	//LinearLayout gragh;
	
	@ViewById(R.id.newstrend_list)
	ListView newstrend_list;

	public static final String EXTRA_HNPOST = "HNPOST";
	ArrayList<String> list = new ArrayList<String>();
	String[] timeAgo;
   
    HNPost mPost;
    HNPostComments mComments;
    List<HNComment> mCommentsCache;
    boolean mHaveLoadedPosts = false;
    
	@AfterViews
	public void init() {
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
    	
		if (mCommentsCache != null) {
			for (int i = 0; i < mCommentsCache.size(); i++)
				timeAgo[i] = mCommentsCache.get(i).getTimeAgo();
		}
		
    	newstrend_list.setAdapter(new ArrayAdapter<String>(this,
    			 android.R.layout.simple_list_item_1, timeAgo));
    	
    	newstrend_list.setTextFilterEnabled(true);
    	/*GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
    	    	for(int i = 0; i < mCommentsCache.size(); i++)
    	    	{
    	    		new GraphViewData(i, mCommentsCache.get(i).getTimeAgo().);
    	    	}
				//new GraphViewData(1, 2.0d)
				//, new GraphViewData(2, 1.5d)
		});

		GraphView graphView = new LineGraphView(
				this // context
				, "GraphViewDemo" // heading
				);
		graphView.addSeries(exampleSeries); // data
		graphView.getGraphViewStyle().setNumHorizontalLabels(10);
		graphView.getGraphViewStyle().setNumVerticalLabels(5);
		graphView.setViewPort(5, 10);
		graphView.setScrollable(true);
		//graphView.setScalable(true);  
		//graphView.setDrawBackground(true);

		gragh.addView(graphView);*/
    	
    }
    
	public NewsTrendActivity() {	
	}
}
