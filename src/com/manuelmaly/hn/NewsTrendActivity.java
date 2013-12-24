package com.manuelmaly.hn;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.BarGraphView;
import com.manuelmaly.hn.CommentsActivity.GetLastHNPostCommentsTask;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

	@ViewById(R.id.graph)
	LinearLayout gragh;

	public static final String EXTRA_HNPOST = "HNPOST";
   
    HNPost mPost;
    HNPostComments mComments;
    
	@AfterViews
	public void init() {
		//get mPost from MainActivity
		mPost = (HNPost) getIntent().getSerializableExtra(EXTRA_HNPOST);
		
		mComments = new HNPostComments();

		mActionbarContainer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			}
		});

		mActionbarTitle.setTypeface(FontHelper.getComfortaa(this, true));
		mActionbarTitle.setText(getString(R.string.newstrend));

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
		
        startFeedLoading();
		
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
				new GraphViewData(1, 2.0d)
				, new GraphViewData(2, 1.5d)
				, new GraphViewData(3, 2.5d)
				, new GraphViewData(4, 1.0d)
		});

		GraphView graphView = new BarGraphView(
				this // context
				, "GraphViewDemo" // heading
				);
		graphView.addSeries(exampleSeries); // data

		//LinearLayout layout = (LinearLayout) findViewById(R.id.graph);
		gragh.addView(graphView);
	}
	
	@Override
    protected void onResume() {
        super.onResume();
    }
	
    @Override
    public void onTaskFinished(int taskCode, TaskResultCode code, HNPostComments result, Object tag) {
    	showNewstrend(result);
    	updateStatusIndicatorOnLoadingFinished(code);
    }
	
    private void showNewstrend(HNPostComments comments) {
        mComments = comments;
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
        HNPostCommentsTask.startOrReattach(this, this, mPost.getPostID(), 0);
        updateStatusIndicatorOnLoadingStarted();
    }
    
	public NewsTrendActivity() {	
	}
}
