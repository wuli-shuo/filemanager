package com.example.fileexplorer.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
	private EditText searchMessage;   //搜索框
	private Button search;
	private ListView searchResult;   //界面显示的列表
	public SimpleAdapter adapter;
	public List<Map<String, String>> searchList;   //列表内容
	public String searchRange;
	public ProgressDialog progressDialog;    //用于提示正在加载搜索数据
	public String keyword;    //搜索关键字
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_result);
		
		/*
		 * 获取界面控件
		 */
		searchMessage = (EditText)findViewById(R.id.search_message);
		search = (Button)findViewById(R.id.search);
		searchResult = (ListView)findViewById(R.id.search_result);
		searchList = new ArrayList<Map<String, String>>(); 
		progressDialog = new ProgressDialog(SearchActivity.this);
		adapter = new SimpleAdapter(this,searchList,R.layout.search_list,new String[]{"name","path"},new int[]{R.id.search_file_name,R.id.search_file_path});
		
		/*
		 * 对搜索按钮设置监听事件
		 */
		search.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				searchList.clear();
				keyword = searchMessage.getText().toString(); 
					
				
				if(MainActivity.progress == 1){  //progress作为标识符，判断MainActivity中关于文件数据库是否加载完成
					if(TextUtils.isEmpty(keyword)){
						Toast.makeText(SearchActivity.this,"请输入搜索关键字",Toast.LENGTH_SHORT).show();
					}else{
						Cursor cursor = MainActivity.db.query("File",null,null,null,null,null,null);
						/*
						 * 从数据库中读取数据，并与搜索关键字进行匹配，将符合要求的数据加入到searchList中
						 */
						if(cursor.moveToFirst()){   
							do{
								String name = cursor.getString(cursor.getColumnIndex("name"));  //文件名
								String path = cursor.getString(cursor.getColumnIndex("path"));  //文件路径
								if(name.indexOf(keyword) > -1||name.indexOf(keyword.toUpperCase()) > -1){
									 Map<String, String> map = new HashMap<String, String>(); 
									 map.put("name", name);  
				    				 map.put("path", path); 
									 searchList.add(map);
								}
							}while(cursor.moveToNext());					
						}
						cursor.close();
						if(searchList.size() == 0){   //如果没有符合要求的数据，提示“没有找到文件”
							 Map<String, String> map = new HashMap<String, String>(); 
							 map.put("name","     没有找到相关文件...");  
		    				 map.put("path", ""); 
							 searchList.add(map);
						}
						searchResult.setAdapter(adapter);
					}
					
				}else{   //提示数据库还没加载完成
					progressDialog.setTitle("正在加载中....");
					progressDialog.setMessage("请稍等");
					progressDialog.setCancelable(true);
					progressDialog.show();
							
					}
				
			}
		});
		
		
		/*
		 * 为列表设置点击监听事件
		 */
		searchResult.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map map = searchList.get(position);
				File file = new File((String) map.get("path")); 
				openTargetFile(file);
			}
		});
	

	
	}
	
	
	/*
	 * 打开文件
	 */
	public void openTargetFile(File file){
		 Intent intent = new Intent(); 
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
	     intent.setAction(android.content.Intent.ACTION_VIEW); 
	      // 获取文件媒体类型    
	     String type = FileAdapter.getMIMEType(file);
	      if(type == null)  
		        return;  
	      intent.setDataAndType(Uri.fromFile(file), type);  
	      startActivity(intent); 
		}  	
	
}
