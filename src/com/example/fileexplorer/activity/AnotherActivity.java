package com.example.fileexplorer.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.fileexplorer.R;
import com.example.fileexplorer.file.FileItem;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AnotherActivity extends Activity implements OnClickListener{
	
	
	public static List<FileItem> sameTypeList = new ArrayList<FileItem>();
	public  Cursor cursor;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.another_view);
		TextView catalogFile = (TextView)findViewById(R.id.catalog_file);
		catalogFile.setTextColor(0xFF000000);
		TextView classifyFile = (TextView)findViewById(R.id.classify_file);
		classifyFile.setTextColor(0xFFFF8C00);
		
		
		ImageView word = (ImageView)findViewById(R.id.type_word);
		ImageView ppt = (ImageView)findViewById(R.id.type_ppt);
		ImageView excel = (ImageView)findViewById(R.id.type_excel);
		ImageView pdf = (ImageView)findViewById(R.id.type_pdf);
		ImageView zip = (ImageView)findViewById(R.id.type_zip);
		ImageView file = (ImageView)findViewById(R.id.type_file);
		ImageView apk = (ImageView)findViewById(R.id.type_apk);
		ImageView gallery = (ImageView)findViewById(R.id.type_gallery);
		ImageView music = (ImageView)findViewById(R.id.type_music);
		ImageView video = (ImageView)findViewById(R.id.type_video);
		ImageView other = (ImageView)findViewById(R.id.type_other);
		
		word.setOnClickListener(this);
		ppt.setOnClickListener(this);
		excel.setOnClickListener(this);
		pdf.setOnClickListener(this);
		zip.setOnClickListener(this);
		file.setOnClickListener(this);
		apk.setOnClickListener(this);
		gallery.setOnClickListener(this);
		music.setOnClickListener(this);
		video.setOnClickListener(this);
		other.setOnClickListener(this);
		
		
		
		
		//显示存储情况
		TextView memoryAvailable = (TextView)findViewById(R.id.memory_available);
		TextView memoryAll = (TextView)findViewById(R.id.memory_all);
		ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		MainActivity.memoryCaculation.MemorySizeCaculation();
		memoryAvailable.setText("可用：" + MainActivity.memoryCaculation.availableMemorySize);
		memoryAll.setText("总共：" + MainActivity.memoryCaculation.totalMemorySize);
		progressBar.setProgress(MainActivity.memoryCaculation.memoryProprtion);
		
	
		//点击“分类”，进入到文件分类界面
		catalogFile.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(AnotherActivity.this,MainActivity.class);
				startActivity(intent);
			}
		});
				
		
	}
	
	 public void onClick(View v) {
		 cursor = MainActivity.db.query("File",null,null,null,null,null,null);
		 sameTypeList.clear();
		 switch (v.getId()) {
		 case R.id.type_word:
			 if(cursor.moveToFirst()){
					do{
						String type = cursor.getString(cursor.getColumnIndex("type"));
						if(type.equals("doc")||type.equals("docx")){
							FileItem fileItem = new FileItem();
							fileItem.setName(cursor.getString(cursor.getColumnIndex("name")));
							fileItem.setfilePath(cursor.getString(cursor.getColumnIndex("path")));
							fileItem.setImageId(R.drawable.word);
							sameTypeList.add(fileItem);
						}
					}while(cursor.moveToNext());					
				}
				cursor.close();
				Intent intent = new Intent(AnotherActivity.this,SameTypeActivity.class);
				intent.putExtra("type", "Word");
				startActivity(intent);
				break;
				
		 case R.id.type_ppt:
			 if(cursor.moveToFirst()){
					do{
						String type = cursor.getString(cursor.getColumnIndex("type"));
						if(type.equals("ppt")||type.equals("pptx")){
							FileItem fileItem = new FileItem();
							fileItem.setName(cursor.getString(cursor.getColumnIndex("name")));
							fileItem.setfilePath(cursor.getString(cursor.getColumnIndex("path")));
							fileItem.setImageId(R.drawable.ppt);
							sameTypeList.add(fileItem);
						}
					}while(cursor.moveToNext());					
				}
				cursor.close();
				Intent intent1 = new Intent(AnotherActivity.this,SameTypeActivity.class);
				intent1.putExtra("type", "Ppt");
				startActivity(intent1);
				break;		
		
			 
			 
		 }
		 
	 }
	
	 
	 
	 
	
	
	
	
}
