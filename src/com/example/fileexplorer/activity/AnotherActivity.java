package com.example.fileexplorer.activity;

import com.example.fileexplorer.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AnotherActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.another_view);
		TextView catalogFile = (TextView)findViewById(R.id.catalog_file);
		catalogFile.setTextColor(0xFF000000);
		TextView classifyFile = (TextView)findViewById(R.id.classify_file);
		classifyFile.setTextColor(0xFFFF8C00);
		
		//显示存储情况
		TextView memoryAvailable = (TextView)findViewById(R.id.memory_available);
		TextView memoryAll = (TextView)findViewById(R.id.memory_all);
		ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		MainActivity.memoryCaculation.MemorySizeCaculation();
		memoryAvailable.setText("可用：" + MainActivity.memoryCaculation.availableMemorySize);
		memoryAll.setText("总共：" + MainActivity.memoryCaculation.totalMemorySize);
		progressBar.setProgress(MainActivity.memoryCaculation.memoryProprtion);
		
	
		//点击“分类”，进入到文件分类界面
		classifyFile.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(AnotherActivity.this,MainActivity.class);
				startActivity(intent);
			}
		});
		
		
		
		
	}
}
