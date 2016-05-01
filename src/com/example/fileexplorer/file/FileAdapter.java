package com.example.fileexplorer.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.example.fileexplorer.R;
import com.example.fileexplorer.activity.MainActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileAdapter extends ArrayAdapter<FileItem>{
	
	private int resourceId;
	private Context context;
	private List<FileItem> fileList;    //文件列表
	public static List<FileItem> fileSelected = new ArrayList<FileItem>();   //被选择的文件名
	
	
	public FileAdapter(Context context,int textViewResourceId,List<FileItem> objects){
		super(context,textViewResourceId,objects);
		resourceId = textViewResourceId;
		fileList = objects;
		this.context = context;
		//根据前面操作（长按、点击“确定”“取消”按钮）来设置再次加载列表
		if(MainActivity.isMulChoice){
			for(int i=0;i<fileList.size();i++){
				fileList.get(i).setboxChecked(false);
			    fileList.get(i).setboxVisible(CheckBox.VISIBLE);
           }
	   }else{
		   for(int i=0;i<fileList.size();i++){
			   	fileList.get(i).setboxChecked(false);	
			    fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
	       }
	  }

	}
	
	@Override //每个子项被滚到屏幕内的时候调用此方法
	public View getView(final int position,View convertView,ViewGroup parent){
		FileItem file = getItem(position);  //获取当前项的FileItem实例
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);  //为子项加载布局
		ImageView fileImage = (ImageView)view.findViewById(R.id.file_image);
		TextView fileName = (TextView)view.findViewById(R.id.file_name);
		final CheckBox checkBox = (CheckBox)view.findViewById(R.id.check_box);

		fileImage.setImageResource(file.getImageId());
		fileName.setText(file.getName());
		checkBox.setChecked(file.getboxChecked());
		checkBox.setVisibility(file.getboxVisible());
		
		//长按点击事件，显示隐藏功能选项和选择框
		view.setOnLongClickListener(new OnLongClickListener(){
	       public boolean onLongClick(View v) {
	    	   MainActivity.isMulChoice = true;
	    	   fileSelected.clear();
	    	   MainActivity.optionLayout.setVisibility(View.VISIBLE);
	    	   MainActivity.fileselectedNumber.setVisibility(View.VISIBLE);
		       for(int i=0;i<fileList.size();i++){
		    	   fileList.get(i).setboxVisible(CheckBox.VISIBLE);
		        }
		       FileAdapter adapter = new FileAdapter(context,R.layout.file_item,fileList);
		       MainActivity.listView.setAdapter(adapter);
		       return true;
	       }
		
	 });
		
		//点击事件，选中或取消选择框选择
		view.setOnClickListener(new OnClickListener() {   
			public void onClick(View v) {
				if(MainActivity.isMulChoice){
					if(checkBox.isChecked()){
						checkBox.setChecked(false);
						fileSelected.remove(fileList.get(position).getName());
					}else{
						checkBox.setChecked(true);
						fileSelected.add(fileList.get(position));
					}
				MainActivity.fileselectedNumber.setText("已选中"+fileSelected.size()+"项");
				}
				else{
					MainActivity.currentPath = MainActivity.currentPath + File.separator + fileList.get(position).getName() + File.separator;
					File file = new File(MainActivity.currentPath); 
					//判断是否为文件夹
					if (file.canRead() && file.canExecute() && file.isDirectory()){
						MainActivity.initView(MainActivity.currentPath);
						FileAdapter adapter = new FileAdapter(context,R.layout.file_item,MainActivity.catalogIndex.get(MainActivity.catalogIndex.size()-1));
						MainActivity.listView.setAdapter(adapter);
					}	
					else
						openFile(file);
					
					
					
				}
			}
		});

		return view;
	}
	

	private void openFile(File file){
		 Intent intent = new Intent();    
	     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
	     intent.setAction(android.content.Intent.ACTION_VIEW);               
	      // 获取文件媒体类型    
	     String type = getMIMEType(file);    
	      if(type==null)  
		        return;  
	      intent.setDataAndType(Uri.fromFile(file), type);  
	      context.startActivity(intent);    
		}  
	
	 //获取文件类型
	 private String getMIMEType(File file) {    
	     String type = "";    
	     String fileName = file.getName();    
	     String end = fileName.substring(fileName.indexOf(".") + 1).toLowerCase();    
          // 判断文件类型    
	     if(end.equals("wma") || end.equals("mp3") || end.equals("midi")||end.equals("ape")     
		   || end.equals("amr") || end.equals("ogg") || end.equals("wav")||end.equals("acc")) {    
	    	  type = "audio/*";     
		 } else if (end.equals("3gp") || end.equals("mp4")||end.equals("rmvb")||end.equals("flv")  
       ||end.equals("avi")||end.equals("wmv")||end.equals("f4v")) {    
	         type = "video/*";    
	    } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {    
	    	 type = "image/*";    
	    } else if(end.equals("txt")){
	    	 type = "text/plain";
	    }else{
	    	 Toast.makeText(context, "not media file", Toast.LENGTH_LONG).show();  
	    	 return null;  
	    }    
	   // MIME Type格式:"文件类型/文件扩展名"       
	    return type;    
	 }  
	
	
	
	

}
