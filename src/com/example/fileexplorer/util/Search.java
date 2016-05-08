package com.example.fileexplorer.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.example.fileexplorer.R;
import com.example.fileexplorer.activity.MainActivity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class Search extends LinearLayout{
	public static SearchView searchView;
	private SimpleAdapter adapter; 
	private Context context;
	
	public Search(final Context context,AttributeSet attrs){
		super(context,attrs);
		View view = LayoutInflater.from(context).inflate(R.layout.search_widget,this);
		/*View view1 = LayoutInflater.from(context).inflate(R.layout.search_result,this);
		adapter = new SimpleAdapter(context,MainActivity.totalFileList,R.layout.search_list,new String[]{"name"},new int[]{R.id.search_file_name});
		final ListView searchResult = (ListView)view1.findViewById(R.id.search_result);
		searchResult.setAdapter(adapter); 
		searchResult.setTextFilterEnabled(true);   //设置searchResult可以被过虑   
		searchView = (SearchView) view.findViewById(R.id.search_view); 
		searchView.setIconifiedByDefault(false);   // 设置搜索框默认是否自动缩小为图标
		searchView.setSubmitButtonEnabled(true);   // 设置该SearchView显示搜索按钮
		searchView.setQueryHint("查询");
	   
		// 为搜索框设置事件监听器
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			// 用户输入字符时激发该方法
           public boolean onQueryTextChange(String str) {
           	if (TextUtils.isEmpty(str)) {
            		searchResult.clearTextFilter();     // 清除ListView的过滤
            		 } else {
            		 // 使用用户输入的内容对ListView的列表项进行过滤
            		 searchResult.setFilterText(str);
            		 }
            		 return true;
        	  

            }
            
         // 单击搜索按钮时激发该方法
            public boolean onQueryTextSubmit(String str) {
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

		//返回true,截取关闭事件,不让搜索框收起来
        searchView.setOnCloseListener(new OnCloseListener() {        
                public boolean onClose() {
                       return true;
                }
        });*/

		
		
		
		
		
		

		EditText searchMessage = (EditText)findViewById(R.id.search_message);
		Button search = (Button)findViewById(R.id.search);
		final String keyword = searchMessage.getText().toString();
		search.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Iterator iterator = MainActivity.totalFileList.iterator();
				while(iterator.hasNext()){
					Map map = (Map)iterator.next();
					String name = (String)map.get("name");
					String searchRange = name.substring(0,name.indexOf("."));
					if(searchRange.indexOf(keyword) > -1||searchRange.indexOf(keyword.toUpperCase()) > -1){
						 List<Map<String, String>> searchList = new ArrayList<Map<String, String>>(); 
						 searchList.add(map);
					}
				}
				Toast.makeText(getContext()," message", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	

}
