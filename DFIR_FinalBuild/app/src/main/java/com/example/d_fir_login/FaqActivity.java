package com.example.d_fir_login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListView;

public class FaqActivity extends AppCompatActivity {

    ExpandableListView expandableTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        expandableTextView = findViewById(R.id.expandable_View);
        ExpandableTextViewAdapter adapter = new ExpandableTextViewAdapter(FaqActivity.this);
        expandableTextView.setAdapter(adapter);
    }
}