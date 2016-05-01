package com.example.fileexplorer.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.fileexplorer.R;

public class Title extends LinearLayout{
	
	
	public Title(Context context,AttributeSet attrs){
		super(context,attrs);
		LayoutInflater.from(context).inflate(R.layout.title,this);
		TextView catalogFile = (TextView)findViewById(R.id.catalog_file);
		TextView classifyFile = (TextView)findViewById(R.id.classify_file);
		Button hideFunction = (Button)findViewById(R.id.more_function);
		catalogFile.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Toast.makeText(getContext()," message", Toast.LENGTH_SHORT).show();
			}
		});

}
}
