package com.manuelmaly.hn;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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
    	HashMap<String, String> param = new HashMap<String, String>();
    	
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
    	
    	// set search parameters.
		param.put("q", searchString);

		param.put("limit", "30");
		param.put("sortby", "points desc");
		param.put("weights[title]", "10.0");	
		param.put("weights[url]", "10.0");

		param.put("weights[text]", "0.7");
		param.put("weights[domain]", "2.0");
		param.put("weights[username]", "0.1");
		param.put("boosts[fields][points]", "0.15");
	    param.put("boosts[fields][num_comments]", "0.15");
		param.put("boosts[functions][pow(2,div(div(ms(create_ts,NOW),3600000),72))]", "200.00");
		param.put("pretty_print", "true");
		
    	HNFeedTaskSearch.start(this, this, TASKCODE_SEARCH, param);
    	
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