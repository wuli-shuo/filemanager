package com.example.fileexplorer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_result);
		
		searchMessage = (EditText)findViewById(R.id.search_message);
		Button search = (Button)findViewById(R.id.search);
		searchResult = (ListView)findViewById(R.id.search_result);
		searchList = new ArrayList<Map<String, String>>(); 
		
	
		adapter = new SimpleAdapter(this,searchList,R.layout.search_list,new String[]{"name","path"},new int[]{R.id.search_file_name,R.id.search_file_path});
		search.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				searchList.clear();
				String keyword = searchMessage.getText().toString();
				Iterator iterator = MainActivity.totalFileList.iterator();
				while(iterator.hasNext()){
					Map map = (Map)iterator.next();
					String name = (String)map.get("name");
					String searchRange = name.substring(0,name.indexOf("."));
					if(searchRange.indexOf(keyword) > -1||searchRange.indexOf(keyword.toUpperCase()) > -1){
						 searchList.add(map);
						
					}
				}
				searchResult.setAdapter(adapter);
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
}
