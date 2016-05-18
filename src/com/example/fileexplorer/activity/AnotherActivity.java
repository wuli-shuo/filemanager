package com.example.fileexplorer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.fileexplorer.R;
import com.example.fileexplorer.file.FileAdapter;
import com.example.fileexplorer.file.FileItem;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
		Button more = (Button)findViewById(R.id.more_function);
		more.setVisibility(View.GONE);
		
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
		
		
		
		//搜索界面跳换
		EditText searchMessage = (EditText)findViewById(R.id.search_message);
			searchMessage.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					Intent intent = new Intent(AnotherActivity.this,SearchActivity.class);
					startActivity(intent);
						
				}
			});
		
		
		
		//显示存储情况
		TextView memoryAvailable = (TextView)findViewById(R.id.memory_available);
		TextView memoryAll = (TextView)findViewById(R.id.memory_all);
		ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		MainActivity.memoryCaculation.MemorySizeCaculation();
		memoryAvailable.setText("可用：" + MainActivity.memoryCaculation.availableMemorySize);
		memoryAll.setText("总共：" + MainActivity.memoryCaculation.totalMemorySize);
		progressBar.setProgress(MainActivity.memoryCaculation.memoryProprtion);
		
	
		//点击“手机”，进入到手机文件目录界面
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
		
		 case R.id.type_excel:
			 if(cursor.moveToFirst()){
					do{
						String type = cursor.getString(cursor.getColumnIndex("type"));
						if(type.equals("xlsx")||type.equals("xls")){
							FileItem fileItem = new FileItem();
							fileItem.setName(cursor.getString(cursor.getColumnIndex("name")));
							fileItem.setfilePath(cursor.getString(cursor.getColumnIndex("path")));
							fileItem.setImageId(R.drawable.excel);
							sameTypeList.add(fileItem);
						}
					}while(cursor.moveToNext());					
				}
				cursor.close();
				Intent intent2 = new Intent(AnotherActivity.this,SameTypeActivity.class);
				intent2.putExtra("type", "Excel");
				startActivity(intent2);
				break;	
				
		 case R.id.type_pdf:
			 if(cursor.moveToFirst()){
					do{
						String type = cursor.getString(cursor.getColumnIndex("type"));
						if(type.equals("pdf")){
							FileItem fileItem = new FileItem();
							fileItem.setName(cursor.getString(cursor.getColumnIndex("name")));
							fileItem.setfilePath(cursor.getString(cursor.getColumnIndex("path")));
							fileItem.setImageId(R.drawable.pdf);
							sameTypeList.add(fileItem);
						}
					}while(cursor.moveToNext());					
				}
				cursor.close();
				Intent intent3 = new Intent(AnotherActivity.this,SameTypeActivity.class);
				intent3.putExtra("type", "Pdf");
				startActivity(intent3);
				break;			
			
		 case R.id.type_zip:
			 if(cursor.moveToFirst()){
					do{
						String type = cursor.getString(cursor.getColumnIndex("type"));
						if(type.equals("zip")){
							FileItem fileItem = new FileItem();
							fileItem.setName(cursor.getString(cursor.getColumnIndex("name")));
							fileItem.setfilePath(cursor.getString(cursor.getColumnIndex("path")));
							fileItem.setImageId(R.drawable.zip);
							sameTypeList.add(fileItem);
						}
					}while(cursor.moveToNext());					
				}
				cursor.close();
				Intent intent4 = new Intent(AnotherActivity.this,SameTypeActivity.class);
				intent4.putExtra("type", "Zip");
				startActivity(intent4);
				break;
				
		 case R.id.type_file:
			 if(cursor.moveToFirst()){
					do{
						String type = cursor.getString(cursor.getColumnIndex("type"));
						if(type.equals("txt")||type.equals("c")||type.equals("cpp")||type.equals("h")||type.equals("java")
						||type.equals("log")||type.equals("htm")||type.equals("html")||type.equals("dhtml")){
							FileItem fileItem = new FileItem();
							fileItem.setName(cursor.getString(cursor.getColumnIndex("name")));
							fileItem.setfilePath(cursor.getString(cursor.getColumnIndex("path")));
							fileItem.setImageId(R.drawable.text);
							sameTypeList.add(fileItem);
						}
					}while(cursor.moveToNext());					
				}
				cursor.close();
				Intent intent5 = new Intent(AnotherActivity.this,SameTypeActivity.class);
				intent5.putExtra("type", "Documentation");
				startActivity(intent5);
				break;	
				
		 case R.id.type_apk:
			 if(cursor.moveToFirst()){
					do{
						String type = cursor.getString(cursor.getColumnIndex("type"));
						if(type.equals("apk")){
							FileItem fileItem = new FileItem();
							fileItem.setName(cursor.getString(cursor.getColumnIndex("name")));
							fileItem.setfilePath(cursor.getString(cursor.getColumnIndex("path")));
							fileItem.setImageId(R.drawable.apk);
							sameTypeList.add(fileItem);
						}
					}while(cursor.moveToNext());					
				}
				cursor.close();
				Intent intent6 = new Intent(AnotherActivity.this,SameTypeActivity.class);
				intent6.putExtra("type", "Apk");
				startActivity(intent6);
				break;	
		
		 case R.id.type_gallery:
			 if(cursor.moveToFirst()){
					do{
						String type = cursor.getString(cursor.getColumnIndex("type"));
						if(type.equals("jpg")||type.equals("gif")||type.equals("png")||type.equals("jpeg")||type.equals("bmp")){
							FileItem fileItem = new FileItem();
							fileItem.setName(cursor.getString(cursor.getColumnIndex("name")));
							fileItem.setfilePath(cursor.getString(cursor.getColumnIndex("path")));
							fileItem.setImageId(R.drawable.gallery);
							sameTypeList.add(fileItem);
						}
					}while(cursor.moveToNext());					
				}
				cursor.close();
				Intent intent7 = new Intent(AnotherActivity.this,SameTypeActivity.class);
				intent7.putExtra("type", "Gallery");
				startActivity(intent7);
				break;		
				
		 case R.id.type_music:
			 if(cursor.moveToFirst()){
					do{
						String type = cursor.getString(cursor.getColumnIndex("type"));
						if(type.equals("wma")||type.equals("mp3")||type.equals("midi")||type.equals("ape")     
								   || type.equals("amr")||type.equals("ogg")||type.equals("wav")||type.equals("acc")){
							FileItem fileItem = new FileItem();
							fileItem.setName(cursor.getString(cursor.getColumnIndex("name")));
							fileItem.setfilePath(cursor.getString(cursor.getColumnIndex("path")));
							fileItem.setImageId(R.drawable.music);
							sameTypeList.add(fileItem);
						}
					}while(cursor.moveToNext());					
				}
				cursor.close();
				Intent intent8 = new Intent(AnotherActivity.this,SameTypeActivity.class);
				intent8.putExtra("type", "Music");
				startActivity(intent8);
				break;		
				
		 case R.id.type_video:
			 if(cursor.moveToFirst()){
					do{
						String type = cursor.getString(cursor.getColumnIndex("type"));
						if(type.equals("3gp")||type.equals("mp4")||type.equals("rmvb")||type.equals("flv")  
							       ||type.equals("avi")||type.equals("wmv")||type.equals("f4v")){
							FileItem fileItem = new FileItem();
							fileItem.setName(cursor.getString(cursor.getColumnIndex("name")));
							fileItem.setfilePath(cursor.getString(cursor.getColumnIndex("path")));
							fileItem.setImageId(R.drawable.video);
							sameTypeList.add(fileItem);
						}
					}while(cursor.moveToNext());					
				}
				cursor.close();
				Intent intent9 = new Intent(AnotherActivity.this,SameTypeActivity.class);
				intent9.putExtra("type", "Video");
				startActivity(intent9);
				break;	
				
		 case R.id.type_other:
			 if(cursor.moveToFirst()){
					do{
						String path = cursor.getString(cursor.getColumnIndex("path"));
						File file = new File(path);
						if(FileAdapter.getMIMEType(file).equals("*/*")){
							FileItem fileItem = new FileItem();
							fileItem.setName(cursor.getString(cursor.getColumnIndex("name")));
							fileItem.setfilePath(cursor.getString(cursor.getColumnIndex("path")));
							fileItem.setImageId(R.drawable.unknown);
							sameTypeList.add(fileItem);
						}
					}while(cursor.moveToNext());					
				}
				cursor.close();
				Intent intent11 = new Intent(AnotherActivity.this,SameTypeActivity.class);
				intent11.putExtra("type", "Other");
				startActivity(intent11);
				break;		
				
				
			 
		 }
		 
	 }
	
	 
	 
	 
	
	
	
	
}
