package com.example.fileexplorer.activity;

import com.example.fileexplorer.R;
import com.example.fileexplorer.file.FileTypeAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SameTypeActivity extends Activity{
	
	private FileTypeAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.same_type_list);
		
		
		adapter = new FileTypeAdapter(SameTypeActivity.this,R.layout.file_item,AnotherActivity.sameTypeList);
		ListView typeList = (ListView)findViewById(R.id.type_list);
		typeList.setAdapter(adapter);
		
		
		TextView fileName = (TextView)findViewById(R.id.file_type);
		Intent intent = getIntent();
		String typeName = intent.getStringExtra("type");
		fileName.setText(typeName);
		
		
	}
	
	
	
	
	
	
}
