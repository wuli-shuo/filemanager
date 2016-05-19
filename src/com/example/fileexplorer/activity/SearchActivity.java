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
	private EditText searchMessage;   //������
	private Button search;
	private ListView searchResult;   //������ʾ���б�
	public SimpleAdapter adapter;
	public List<Map<String, String>> searchList;   //�б�����
	public String searchRange;
	public ProgressDialog progressDialog;    //������ʾ���ڼ�����������
	public String keyword;    //�����ؼ���
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_result);
		
		/*
		 * ��ȡ����ؼ�
		 */
		searchMessage = (EditText)findViewById(R.id.search_message);
		search = (Button)findViewById(R.id.search);
		searchResult = (ListView)findViewById(R.id.search_result);
		searchList = new ArrayList<Map<String, String>>(); 
		progressDialog = new ProgressDialog(SearchActivity.this);
		adapter = new SimpleAdapter(this,searchList,R.layout.search_list,new String[]{"name","path"},new int[]{R.id.search_file_name,R.id.search_file_path});
		
		/*
		 * ��������ť���ü����¼�
		 */
		search.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				searchList.clear();
				keyword = searchMessage.getText().toString(); 
					
				
				if(MainActivity.progress == 1){  //progress��Ϊ��ʶ�����ж�MainActivity�й����ļ����ݿ��Ƿ�������
					if(TextUtils.isEmpty(keyword)){
						Toast.makeText(SearchActivity.this,"�����������ؼ���",Toast.LENGTH_SHORT).show();
					}else{
						Cursor cursor = MainActivity.db.query("File",null,null,null,null,null,null);
						/*
						 * �����ݿ��ж�ȡ���ݣ����������ؼ��ֽ���ƥ�䣬������Ҫ������ݼ��뵽searchList��
						 */
						if(cursor.moveToFirst()){   
							do{
								String name = cursor.getString(cursor.getColumnIndex("name"));  //�ļ���
								String path = cursor.getString(cursor.getColumnIndex("path"));  //�ļ�·��
								if(name.indexOf(keyword) > -1||name.indexOf(keyword.toUpperCase()) > -1){
									 Map<String, String> map = new HashMap<String, String>(); 
									 map.put("name", name);  
				    				 map.put("path", path); 
									 searchList.add(map);
								}
							}while(cursor.moveToNext());					
						}
						cursor.close();
						if(searchList.size() == 0){   //���û�з���Ҫ������ݣ���ʾ��û���ҵ��ļ���
							 Map<String, String> map = new HashMap<String, String>(); 
							 map.put("name","     û���ҵ�����ļ�...");  
		    				 map.put("path", ""); 
							 searchList.add(map);
						}
						searchResult.setAdapter(adapter);
					}
					
				}else{   //��ʾ���ݿ⻹û�������
					progressDialog.setTitle("���ڼ�����....");
					progressDialog.setMessage("���Ե�");
					progressDialog.setCancelable(true);
					progressDialog.show();
							
					}
				
			}
		});
		
		
		/*
		 * Ϊ�б����õ�������¼�
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
	 * ���ļ�
	 */
	public void openTargetFile(File file){
		 Intent intent = new Intent(); 
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
	     intent.setAction(android.content.Intent.ACTION_VIEW); 
	      // ��ȡ�ļ�ý������    
	     String type = FileAdapter.getMIMEType(file);
	      if(type == null)  
		        return;  
	      intent.setDataAndType(Uri.fromFile(file), type);  
	      startActivity(intent); 
		}  	
	
}
