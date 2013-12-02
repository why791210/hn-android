package com.manuelmaly.hn;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.manuelmaly.hn.util.FontHelper;

@EActivity(R.layout.search)
public class searchActivity extends Activity {

    @ViewById(R.id.actionbar_back)
    ImageView mActionbarBack;
    
    @ViewById(R.id.searchbutton)
    Button mSearchButton;
    
    @ViewById(R.id.searchname)
    EditText mSearchName;

    @AfterViews
    public void init() {
        Typeface tf = FontHelper.getComfortaa(this, true);
       
    }

    @Click(R.id.actionbar_back)
    void backClicked() {
        finish();
    }

}