package com.example.fileexplorer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.fileexplorer.R;
import com.example.fileexplorer.file.FileAdapter;

public class SearchActivity extends Activity{
	private EditText searchMessage;
	private Button search;
	private ListView searchResult;
	public SimpleAdapter adapter;
	public List<Map<String, String>> searchList;
	public String searchRange;
	public ProgressDialog progressDialog ;
	public String keyword;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_result);
		
		
		
		
		
		searchMessage = (EditText)findViewById(R.id.search_message);
		Button search = (Button)findViewById(R.id.search);
		searchResult = (ListView)findViewById(R.id.search_result);
		searchList = new ArrayList<Map<String, String>>(); 
		progressDialog = new ProgressDialog(SearchActivity.this);
		keyword = searchMessage.getText().toString(); 
		
	
		
	
		adapter = new SimpleAdapter(this,searchList,R.layout.search_list,new String[]{"name","path"},new int[]{R.id.search_file_name,R.id.search_file_path});
		search.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				searchList.clear();
				keyword = searchMessage.getText().toString(); 
			/*	new Thread(new Runnable(){
					public void run(){
						search(new File(MainActivity.rootPath),keyword);
					}
				}).start();*/
				
				
				
				
			/*	Iterator iterator = MainActivity.totalFileList.iterator();
				while(iterator.hasNext()){
					Map map = (Map)iterator.next();
					String name = (String)map.get("name");
					Log.d("name",name);
					String searchRange = name.substring(0,name.indexOf("."));
					if(searchRange.indexOf(keyword) > -1||searchRange.indexOf(keyword.toUpperCase()) > -1){
						 searchList.add(map);		
					}
									
				}
				/*for(Map map : MainActivity.totalFileList){
					
					String name = (String)map.get("name");
					Log.d("file",name);
					String searchRange = name.substring(0,name.indexOf("."));
					if(searchRange.indexOf(keyword) > -1||searchRange.indexOf(keyword.toUpperCase()) > -1){
						 searchList.add(map);		
					}
				}*/
				
				
				
				
				if(MainActivity.progress == 1){
					Cursor cursor = MainActivity.db.query("File",null,null,null,null,null,null);
					if(cursor.moveToFirst()){
						do{
							String name = cursor.getString(cursor.getColumnIndex("name"));
							String path = cursor.getString(cursor.getColumnIndex("path"));
							if(name.indexOf(keyword) > -1||name.indexOf(keyword.toUpperCase()) > -1){
								 Map<String, String> map = new HashMap<String, String>(); 
								 map.put("name", name);  
			    				 map.put("path", path); 
								 searchList.add(map);
							}
						}while(cursor.moveToNext());					
					}
					cursor.close();
					searchResult.setAdapter(adapter);
				}else{
					progressDialog.setTitle("正在加载中....");
					progressDialog.setMessage("请稍等");
					progressDialog.setCancelable(true);
					progressDialog.show();
							
					}
				
			}
		});
		
		
		
	
		searchResult.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map map = searchList.get(position);
				File file = new File((String) map.get("path")); 
				Log.d("filePath",(String) map.get("path"));
				FileAdapter.openFile(file);
			}
		});
	

	
	}
	
	private void search(File file,String keyword) {
        if (file != null) {
            if (file.isDirectory()) {
                File[] listFile = file.listFiles();
                if (listFile != null) {
                    for (int i = 0; i < listFile.length; i++) {
                        search(listFile[i],keyword);
                    }
                }
            } else {
            	String searchRange = file.getName();
				if(searchRange.indexOf(keyword) > -1||searchRange.indexOf(keyword.toUpperCase()) > -1){
					 Map<String, String> map = new HashMap<String, String>(); 
					 map.put("name", file.getName());  
    				 map.put("path", file.getAbsolutePath()); 
					 searchList.add(map);		
				}
           }
        }
    }
	
	
	
	
	
	
	
	
	
	
	
	
}
