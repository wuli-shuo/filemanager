package com.example.fileexplorer.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.example.fileexplorer.R;
import com.example.fileexplorer.activity.MainActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileAdapter extends ArrayAdapter<FileItem>{
	
	private int resourceId;
	private static Context context;
	private List<FileItem> fileList;    //文件列表
	public static List<FileItem> fileSelected = new ArrayList<FileItem>();   //被选择的文件名
	private int tag = 0;  //标志符，用于判断文件是否已经解锁
	
	
	
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
		final FileItem file = getItem(position);  //获取当前项的FileItem实例
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
		       return true;
	       }
		
	 });
		
		//点击事件，选中或取消选择框选择
		view.setOnClickListener(new OnClickListener() {   
			public void onClick(View v) {
				if(MainActivity.isMulChoice){
					if(checkBox.isChecked()){
						checkBox.setChecked(false);
						fileSelected.remove(fileList.get(position));
					}else{
						SharedPreferences preferences = context.getSharedPreferences(file.getName(),context.MODE_PRIVATE);
						String path = preferences.getString("file","");
						if(path.equals(file.getfilePath())){
							Toast.makeText(context,"加密文件，不可修改",Toast.LENGTH_SHORT).show();
						}else{
							checkBox.setChecked(true);
							fileSelected.add(fileList.get(position));
						}
						
					}
				MainActivity.fileselectedNumber.setText("已选中"+fileSelected.size()+"项");
				}
				else{
					final SharedPreferences pref = context.getSharedPreferences(file.getName(),context.MODE_PRIVATE);
					String filePath = pref.getString("file","");
					if(filePath.equals(file.getfilePath()) && tag == 0){
						AlertDialog.Builder dialog = new AlertDialog.Builder(context);
				 		dialog.setTitle("打开私密文件");
				 		final View view = LayoutInflater.from(context).inflate(R.layout.defined_dialog, null); 
				 		TextView editTip = (TextView)view.findViewById(R.id.edit_tip);
				 		editTip.setText("请输入密码");
				 		final EditText editContent2 = (EditText)view.findViewById(R.id.edit_content);
				 		editContent2.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
				 		dialog.setView(view);
				 		dialog.setCancelable(false);
				 		dialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							    String input = editContent2.getText().toString();
							    if (!input.equals("")){			    	
									String password = pref.getString("password", "");
									if(password.equals(input)){
										tag = 1;
										Toast.makeText(context,"密码正确!",Toast.LENGTH_SHORT).show();
									}					
							    	else
							    		Toast.makeText(context,"密码错误!",Toast.LENGTH_SHORT).show();
								}
							    else
							    	Toast.makeText(context,"请输入密码!",Toast.LENGTH_SHORT).show();					
							}	
							} );
				 		 dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
							
							}
						});
				 		dialog.show();
					}
					else if(!filePath.equals(file.getfilePath()) || tag == 1){
						tag = 0;
						MainActivity.currentPath = MainActivity.currentPath  + fileList.get(position).getName() ;
						File file = new File(MainActivity.currentPath); 
						//判断是否为文件夹
						if (file.canRead() && file.canExecute() && file.isDirectory()){
							MainActivity.currentPath =  MainActivity.currentPath + File.separator;
							MainActivity.initView(MainActivity.currentPath);
							FileAdapter adapter = new FileAdapter(context,R.layout.file_item,MainActivity.catalogIndex.get(MainActivity.catalogIndex.size()-1));
							MainActivity.listView.setAdapter(adapter);
						}	
						else
							openFile(file);
					}
				}
			}
		});

		return view;
	}
	

	public static void openFile(File file){
		 Intent intent = new Intent(); 
		 Log.e("path",file.getAbsolutePath());
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
	     intent.setAction(android.content.Intent.ACTION_VIEW); 
	      // 获取文件媒体类型    
	     String type = getMIMEType(file);
	      if(type == null)  
		        return;  
	      intent.setDataAndType(Uri.fromFile(file), type);  
	      ((Activity) context).startActivityForResult(intent,1); 
	      MainActivity.currentPath = new File( MainActivity.currentPath).getParent() + File.separator;
		}  
	
	 //获取文件类型
	 public static String getMIMEType(File file) {    
	     String type = "";    
	     String fileName = file.getName();    
	     String end = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();    
          // 判断文件类型    
	     if(end.equals("wma")||end.equals("mp3")||end.equals("midi")||end.equals("ape")     
		   || end.equals("amr")||end.equals("ogg")||end.equals("wav")||end.equals("acc")) {    
	    	  type = "audio/*";     
		 } else if (end.equals("3gp")||end.equals("mp4")||end.equals("rmvb")||end.equals("flv")  
       ||end.equals("avi")||end.equals("wmv")||end.equals("f4v")) {    
	         type = "video/*";    
	    } else if (end.equals("jpg")||end.equals("gif")||end.equals("png")||end.equals("jpeg")||end.equals("bmp")) {    
	    	 type = "image/*";    
	    } else if(end.equals("txt")||end.equals("c")||end.equals("cpp")||end.equals("h")||end.equals("java")||end.equals("log")){
	    	 type = "text/plain";
	    }else if(end.equals("apk")){
	    	 type = "application/vnd.android.package-archive";	
	    }else if(end.equals("doc")||end.equals("docx")){
	    	 type = "application/msword";
	    }else if(end.equals("pdf")){
	    	 type = "application/pdf";
	    }else if(end.equals("ppt")||end.equals("pptx")){
	    	 type = "application/vnd.ms-powerpoint";
	    }else if(end.equals("xls")||end.equals("xlsx")){
	    	 type = "application/vnd.ms-excel";
	    }else if(end.equals("zip")){
	    	 type = "application/zip";
	    }else if(end.equals("htm")||end.equals("html")||end.equals("dhtml")){
	    	 type = "text/html";
	    }else{
	    	type = "*/*";
	    }
	     
	   // MIME Type格式:"文件类型/文件扩展名"       
	    return type;    
	 }  
	
	
	
	

}
