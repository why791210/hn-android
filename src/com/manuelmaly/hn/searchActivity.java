package com.manuelmaly.hn;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

import com.manuelmaly.hn.model.HNFeed;
import com.manuelmaly.hn.task.HNFeedTaskSearch;
import com.manuelmaly.hn.task.ITaskFinishedHandler;


@EActivity(R.layout.search)
public class searchActivity extends Activity implements ITaskFinishedHandler<HNFeed> {

    @ViewById(R.id.actionbar_back)
    ImageView mActionbarBack;
    
    @ViewById(R.id.searchbutton)
    Button mSearchButton;
    
    @ViewById(R.id.searchname)
    EditText mSearchName;
    
    private static final int TASKCODE_SEARCH = 200;
    
    @AfterViews
    public void init() {
        //Typeface tf = FontHelper.getComfortaa(this, true);
       
    }

    @Click(R.id.actionbar_back)
    void backClicked() {
        finish();
    }
    
    @Click(R.id.searchbutton)
    void searchButtonClicked() {
    	String searchString = mSearchName.getText().toString();
    	
    	// check empty.
    	if(TextUtils.isEmpty(searchString))
    	{
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder
        	.setTitle("Info")
        	.setMessage("Please text the search string.")
        	.setIcon(android.R.drawable.ic_dialog_info)
        	.setNegativeButton("OK", null)						//Do nothing on no
        	.show();
    		return ;
    	}
    	
    	HNFeedTaskSearch.start(this, this, TASKCODE_SEARCH, searchString);
    	
    }
    
    // finish handler # Calvin Chang
    @Override
    public void onTaskFinished(int taskCode, TaskResultCode code, HNFeed result, Object tag){
    	 
    	//Bundle bundle = this.getIntent().getExtras();

        //HNFeed feed = (HNFeed) bundle.getSerializable("HNFeed");
        //boolean isSearchResult = (boolean) bundle.getBoolean("IsSearchResult");
        
    	if(result.getPosts().size() >0)
    	{
    		//feed.clearPost();
    		//feed.addPosts(result.getPosts());
    		//isSearchResult = true;
    		
    		Intent resultIntent = new Intent();
    		resultIntent.putExtra("HNFeed", result);
    		resultIntent.putExtra("IsSearchResult", true);
    		setResult(Activity.RESULT_OK, resultIntent); 
    		
    		finish();
    	}
    	else{
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder
        	.setTitle("Info")
        	.setMessage("Can not find related articles.")
        	.setIcon(android.R.drawable.ic_dialog_info)
        	.setNegativeButton("OK", null)						//Do nothing on no
        	.show();		
    	}
    }
}