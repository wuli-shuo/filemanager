package com.example.fileexplorer.util;

import com.example.fileexplorer.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Search extends LinearLayout{
	
	
	public Search(Context context,AttributeSet attrs){
		super(context,attrs);
		LayoutInflater.from(context).inflate(R.layout.search_widget,this);
		EditText searchMessage = (EditText)findViewById(R.id.search_message);
		Button search = (Button)findViewById(R.id.search);
		String message = searchMessage.getText().toString();
		search.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Toast.makeText(getContext()," message", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	

}
