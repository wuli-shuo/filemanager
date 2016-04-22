package com.example.fileexplorer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.example.fileexplorer.R;
import com.example.fileexplorer.file.FileAdapter;
import com.example.fileexplorer.file.FileItem;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends Activity {
	
	private List<FileItem> fileList = new ArrayList<FileItem>();
	private String rootPath = "/mnt/sdcard/";
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*new Thread(new Runnable(){
			public void run(){
				initView(rootPath);
			}
		}).start();	*/
		initView(rootPath);
		FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
		ListView listView = (ListView)findViewById(R.id.list_view);
		listView.setAdapter(adapter);
	}
	
	private void initView(String filePath){
			File folder =new File(filePath);
			File[] files = folder.listFiles();
			for(File file : files){
				FileItem fileItem = new FileItem();
				fileItem.setImageId(R.drawable.file);
				fileItem.setName(file.getName());
				fileList.add(fileItem);
				}
			
	}

}