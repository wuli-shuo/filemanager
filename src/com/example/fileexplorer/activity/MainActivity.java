package com.example.fileexplorer.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.fileexplorer.R;
import com.example.fileexplorer.file.FileAdapter;
import com.example.fileexplorer.file.FileItem;
import com.example.fileexplorer.util.MemoryCaculation;
import com.example.fileexplorer.util.ZipUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener{
	
	
	public static List<FileItem> fileList ;
	public static List<List> catalogIndex = new ArrayList<List>();
	private String rootPath = "/mnt/sdcard/";
	public MemoryCaculation memoryCaculation = new MemoryCaculation();
	public static boolean isMulChoice = false;  //是否多选
	public static TableLayout optionLayout;  //文件操作功能框架
	public static TextView fileselectedNumber;
	public static ListView listView;     //文件列表
	public static String currentPath  = "/mnt/sdcard/";
	public static File[] files;
	public static List< Map<String, String> > totalFileList = new ArrayList< Map<String, String> >();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		new Thread(new Runnable(){
			public void run(){
				getTotalFile(rootPath);
				for(int i = 0 ; i < totalFileList.size()-1 ; i++ ) {  //去除重复项
					for(int j = totalFileList.size()-1 ; j > i; j-- ) {
						if ( (totalFileList.get(j).get("name")).equals((totalFileList.get(i)).get("name")) ){
							totalFileList.remove(j);
				       }
				     }
				 }
			}
		}).start();	
		
		
		
		
		//显示文件列表
		initView(currentPath);
		FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
		listView = (ListView)findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		/*listView.setOnItemClickListener(this);  */
		
		
		//功能按钮注册监听事件
		Button renameFile = (Button)findViewById(R.id.rename_file);
		renameFile.setOnClickListener(this);
		Button deleteFile = (Button)findViewById(R.id.delete_file);
		deleteFile.setOnClickListener(this);
		Button encryptFile = (Button)findViewById(R.id.encrypt_file);
		encryptFile.setOnClickListener(this);
		Button zipFile = (Button)findViewById(R.id.zip_file);
		zipFile.setOnClickListener(this);
		
		
		
		//搜索界面跳换
		EditText searchMessage = (EditText)findViewById(R.id.search_message);
		searchMessage.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(MainActivity.this,SearchActivity.class);
				startActivity(intent);
			}
		});
		
		
		//文件分类界面
		TextView classifyFile = (TextView)findViewById(R.id.classify_file);
		classifyFile.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(MainActivity.this,AnotherActivity.class);
				startActivity(intent);
			}
		});
		
		
		
		Button moreFunction = (Button)findViewById(R.id.more_function);
		moreFunction.setOnClickListener(this);
		
		//文件操作功能选择框架
		optionLayout = (TableLayout)findViewById(R.id.function_option);
		fileselectedNumber = (TextView)findViewById(R.id.fileselected_number);

		//显示存储情况
		TextView memoryAvailable = (TextView)findViewById(R.id.memory_available);
		TextView memoryAll = (TextView)findViewById(R.id.memory_all);
		ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		memoryCaculation.MemorySizeCaculation();
		memoryAvailable.setText("可用：" + memoryCaculation.availableMemorySize);
		memoryAll.setText("总共：" + memoryCaculation.totalMemorySize);
		progressBar.setProgress(memoryCaculation.memoryProprtion);
						
	
	}
	
	
	public void getTotalFile(String path){
		files = new File(path).listFiles();
		if(files == null)
			return ;
		for(File file : files){
			Map<String, String> map = new HashMap<String, String>(); 
			if(file.isFile()){
				 map.put("name", file.getName());  
				 map.put("path", file.getPath());  
				 totalFileList.add(map); 
			}
			if(file.isDirectory()){     //如果是文件夹，递归查找
				getTotalFile(file.getPath());
			}
			
		}
	}
	
	
	public static void initView(String filePath){
			fileList = new ArrayList<FileItem>();
			File folder =new File(filePath);
			files = folder.listFiles();
			for(File file : files){
				FileItem fileItem = new FileItem();
				fileItem.setImageId(R.drawable.word);
				fileItem.setName(file.getName());
				fileItem.setfilePath(file.getPath());
				fileList.add(fileItem);
				}
			catalogIndex.add(fileList);
	}
	
	//功能点击事件，对选中的文件进行操作
	 public void onClick(View v) {
		 switch (v.getId()) {
		/* case R.id.more_function:
		 		showPopupWindow(v);
		 		break; 		*/
		 case R.id.rename_file:
		 		isMulChoice = false;
		 		AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
		 		dialog1.setTitle("重命名");
		 		final View view1 = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
		 		TextView editTip1 = (TextView)view1.findViewById(R.id.edit_tip);
		 		editTip1.setText("请输入新文件名");
		 		dialog1.setView(view1);
		 		dialog1.setCancelable(false);
		 		dialog1.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.d("function","delete file");
						EditText editContent = (EditText)view1.findViewById(R.id.edit_content);
					    String fileName = editContent.getText().toString();
					    File newFile = new File(currentPath + File.separator + fileName);
					    for(FileItem fileSelected: FileAdapter.fileSelected){
					  		for(File file : files){
					  			if(file.getName().equals(fileSelected.getName()))
					  				file.renameTo(newFile);
					  				int index = fileList.indexOf(fileSelected);
					  				fileList.get(index).setName(fileName);
							}
						}
						FileAdapter.fileSelected.clear();
						FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
					    listView.setAdapter(adapter);
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
					}
				} );
		 		dialog1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						/*FileAdapter.fileSelected.clear();
						FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
					    listView.setAdapter(adapter);*/
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
					}
				});
		 		dialog1.show();
		 		break;
		 case R.id.delete_file:
		 		isMulChoice = false;
		 		AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		 		dialog.setTitle("删除");
		 		dialog.setMessage("确定删除此文件");
		 		dialog.setCancelable(false);
		 		dialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					/*	if (file.isFile()) {  
							file.delete();    
						}  
					    if(file.isDirectory()){  
						     File[] childFiles = file.listFiles();  
					         if (childFiles == null || childFiles.length == 0) {  
					               file.delete();  
					         }  
					         for (int i = 0; i < childFiles.length; i++) {  
						           delete(childFiles[i]);  
					        }  
					       file.delete();  
					   }  */
						
						for(FileItem fileSelected: FileAdapter.fileSelected){
					  		for(File file : files){
					  			if(file.getName().equals(fileSelected.getName()))
					  				file.delete();	
							}
					  		fileList.remove(fileSelected);
						}
						FileAdapter.fileSelected.clear();
						FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
					    listView.setAdapter(adapter);
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
					}
				} );
		 		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						FileAdapter.fileSelected.clear();
						FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
					    listView.setAdapter(adapter);
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
					}
				});
		 		dialog.show();
		 		/*isMulChoice =false;
		 		for(int i=0;i<selectid.size();i++){
		 			for(int j=0;j<array.size();j++){
		 				if(selectid.get(i).equals(array.get(j))){
		 					array.remove(j);
		 				}
		 			}
		 		}
		 		
		 		*/
		 	break;
		case  R.id.copy_file:
			break;
		case R.id.paste_file:
			break;
		case R.id.new_file:
	 		isMulChoice = false;
	 		AlertDialog.Builder dialog0 = new AlertDialog.Builder(MainActivity.this);
	 		dialog0.setTitle("新建文件夹");
	 		final View view = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
	 		TextView editTip = (TextView)view.findViewById(R.id.edit_tip);
	 		editTip.setText("请输入新建文件名");
	 		dialog0.setView(view);
	 		dialog0.setCancelable(false);
	 		dialog0.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.d("function","delete file");
					EditText editContent = (EditText)view.findViewById(R.id.edit_content);
				    String fileName = editContent.getText().toString();
				    File newFile = new File(currentPath + File.separator + fileName);  //实例化File
				    if (!newFile.exists()) {
				    	newFile.mkdir();    //创建文件夹
				    	FileItem fileItem = new FileItem();
						fileItem.setImageId(R.drawable.file);
						fileItem.setName(fileName);
						fileItem.setfilePath(newFile.getPath());
						fileList.add(fileItem);
	                }
					FileAdapter.fileSelected.clear();
					FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
				    listView.setAdapter(adapter);
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				}
			} );
	 		dialog0.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					/*FileAdapter.fileSelected.clear();
					FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
				    listView.setAdapter(adapter);*/
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				}
			});
	 		dialog0.show();
	 		break; 	
		 		
		case R.id.sortup_file:	
	 		//排序 
	        Collections.sort(fileList,new Comparator<FileItem>(){ 
	            public int compare(FileItem arg0, FileItem arg1) { 
	                return arg0.getName().compareTo(arg1.getName());
	                } 
	        });
	        Log.d("function","new file");
	        FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
		    listView.setAdapter(adapter);
	 		break; 		
		 	
		case R.id.sortdown_file:
	 		Collections.reverse(fileList);
	 		FileAdapter adapter1 = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
			listView.setAdapter(adapter1);
	 		break; 	
	 		
	/*	case R.id.encrypt_file:
			isMulChoice = false;
			FileAdapter adapter3 = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
		    listView.setAdapter(adapter3);
			optionLayout.setVisibility(View.GONE);
		    fileselectedNumber.setVisibility(View.GONE);
		    
			break;  */
		
		case R.id.crop_file:
			break;
			
	/*	case R.id.more_function:
			
			for(FileItem fileSelected: FileAdapter.fileSelected){
				if( (new File(fileSelected.getfilePath())).isDirectory())
					copyFolder(fileSelected.getfilePath(),currentPath + File.separator +  fileSelected.getName(),fileSelected.getName());
				if((new File(fileSelected.getfilePath())).isFile()){
					copyFile(fileSelected.getfilePath(),currentPath + File.separator +  fileSelected.getName());
					FileItem fileItem = new FileItem();
					fileItem.setImageId(R.drawable.file);
					fileItem.setName(fileSelected.getName());
					fileList.add(fileItem);	
				}		
			}
			Toast.makeText(this,"OK  ", Toast.LENGTH_SHORT).show();
			FileAdapter adapter4 = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
		    listView.setAdapter(adapter4);
		    FileAdapter.fileSelected.clear();
			break;   */
		case R.id.more_function:
			isMulChoice = false;
	 		AlertDialog.Builder dialog4 = new AlertDialog.Builder(MainActivity.this);
	 		dialog4.setTitle("压缩");
	 		final View view4 = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
	 		TextView editTip4 = (TextView)view4.findViewById(R.id.edit_tip);
	 		editTip4.setText("压缩到");
	 		dialog4.setView(view4);
	 		dialog4.setCancelable(false);
	 		dialog4.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.d("function","delete file");
					EditText editContent = (EditText)view4.findViewById(R.id.edit_content);
				    String fileName = editContent.getText().toString();
				    FileItem selectedFileItem = FileAdapter.fileSelected.get(0);
				    try{
				    	ZipUtil.zipFile(selectedFileItem.getfilePath(),
				    			selectedFileItem.getfilePath().replace(selectedFileItem.getName(), fileName));
				    	}catch(Exception e){
				    		 e.printStackTrace(); 
				    	}
				    FileItem fileItem = new FileItem();
					fileItem.setImageId(R.drawable.file);
					fileItem.setName(fileName);
					fileItem.setfilePath(FileAdapter.fileSelected.get(0).getfilePath());
					fileList.add(fileItem);
					FileAdapter.fileSelected.clear();
					FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
				    listView.setAdapter(adapter);
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				}
			} );
	 		dialog4.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					/*FileAdapter.fileSelected.clear();
					FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
				    listView.setAdapter(adapter);*/
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				}
			});
	 		dialog4.show();
	 		break; 	
		case R.id.zip_file:
			isMulChoice = false;
	 		AlertDialog.Builder dialog5 = new AlertDialog.Builder(MainActivity.this);
	 		dialog5.setTitle("解压");
	 		final View view5 = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
	 		TextView editTip5 = (TextView)view5.findViewById(R.id.edit_tip);
	 		editTip5.setText("解压到");
	 		dialog5.setView(view5);
	 		dialog5.setCancelable(false);
	 		dialog5.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.d("function","delete file");
					EditText editContent = (EditText)view5.findViewById(R.id.edit_content);
				    String fileName = editContent.getText().toString();
				    FileItem selectedFileItem = FileAdapter.fileSelected.get(0);
				    try{
				    	ZipUtil.unZipFile(selectedFileItem.getfilePath(),
				    			selectedFileItem.getfilePath().replace(selectedFileItem.getName(), fileName));
				    	}catch(Exception e){
				    		 e.printStackTrace(); 
				    	}
				    FileItem fileItem = new FileItem();
					fileItem.setImageId(R.drawable.file);
					fileItem.setName(fileName);
					fileItem.setfilePath(FileAdapter.fileSelected.get(0).getfilePath());
					fileList.add(fileItem);
					FileAdapter.fileSelected.clear();
					FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
				    listView.setAdapter(adapter);
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				}
			} );
	 		dialog5.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					/*FileAdapter.fileSelected.clear();
					FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
				    listView.setAdapter(adapter);*/
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				}
			});
	 		dialog5.show();
	 		break; 	
			
		case R.id.encrypt_file:
			isMulChoice = false;
	 		AlertDialog.Builder dialog6 = new AlertDialog.Builder(MainActivity.this);
	 		dialog6.setTitle("设置为私密文件");
	 		final View view6 = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
	 		TextView editTip6 = (TextView)view6.findViewById(R.id.edit_tip);
	 		editTip6.setText("请输入密码");
	 		dialog6.setView(view6);
	 		dialog6.setCancelable(false);
	 		dialog6.setPositiveButton("确定",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.d("function","delete file");
					EditText editContent = (EditText)view6.findViewById(R.id.edit_content);
				    String password = editContent.getText().toString();
				    if (!password.equals("")){
				    	SharedPreferences.Editor editor = getSharedPreferences(FileAdapter.fileSelected.get(0).getName(),MODE_PRIVATE).edit();
						editor.putString("password", password);
						editor.commit();
						FileAdapter.fileSelected.get(0).setpasswordNeeded(true);
						Toast.makeText(MainActivity.this,"设置成功!",Toast.LENGTH_SHORT).show();
				    }else{
				    	Toast.makeText(MainActivity.this,"密码不能为空!",Toast.LENGTH_SHORT).show();
				    }
					FileAdapter.fileSelected.clear();
					FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
				    listView.setAdapter(adapter);
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				}
			} );
	 		dialog6.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					/*FileAdapter.fileSelected.clear();
					FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
				    listView.setAdapter(adapter);*/
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				}
			});
	 		dialog6.show();
	 		break; 	
	 		
	 		
	 		
		default:
        	break; 		
	        
	        }   
	    }

	
	/*  public void onItemClick(AdapterView<?> parent, View view, int position, long id) { 
		 	Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
		 	Log.d("intofolder","successful");
		 	String path = currentPath + File.separator + fileList.get(position).getName() + File.separator;
		 	currentPath = path;
			File file = new File(currentPath);  
			
			initView(currentPath);
			catalogIndex.add(fileList); 
			FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
			listView = (ListView)findViewById(R.id.list_view);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this); 
	  }
			 /*if (file.canRead()&&file.canExecute()&&file.isDirectory()){  
            	
            }  
		   else{  
		    	openFile(file);  
	       }  */
	  
		 
		 

	 private void showPopupWindow(View view) {

	        // 一个自定义的布局，作为显示的内容
	        View contentView = LayoutInflater.from(this).inflate(
	                R.layout.pop_window, null);
	        final PopupWindow popupWindow = new PopupWindow(contentView,
	                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
	        
	        
	        
	        
	        popupWindow.setTouchable(true);
	        // 使其聚集  
	        popupWindow.setFocusable(true);  
	        // 设置允许在外点击消失  
	        popupWindow.setOutsideTouchable(true);  
	        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
	        popupWindow.setBackgroundDrawable(new BitmapDrawable());  


	     /*   popupWindow.setTouchInterceptor(new OnTouchListener() {

	            @Override
	            public boolean onTouch(View v, MotionEvent event) {

	                Log.i("mengdd", "onTouch : ");

	                return false;
	                // 这里如果返回true的话，touch事件将被拦截
	                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
	            }
	        });

	        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
	        // 我觉得这里是API的一个bug
	        popupWindow.setBackgroundDrawable(getResources().getDrawable(
	                R.drawable.selectmenu_bg_downward));*/

	        // 设置好参数之后再show
	        popupWindow.showAsDropDown(view);
	        TextView newFile = (TextView)findViewById(R.id.new_file);
			newFile.setOnClickListener(this);
			TextView sortupFile = (TextView)findViewById(R.id.sortup_file);
			sortupFile.setOnClickListener(this);
			TextView sortdownFile = (TextView)findViewById(R.id.sortdown_file);
			sortdownFile.setOnClickListener(this);
			TextView copyFile = (TextView)findViewById(R.id.copy_file);
			copyFile.setOnClickListener(this);
			TextView cropFile = (TextView)findViewById(R.id.crop_file);
			cropFile.setOnClickListener(this);
			TextView pasteFile = (TextView)findViewById(R.id.paste_file);
			pasteFile.setOnClickListener(this);

	    }

	 
	 
	 
	 public void copyFile(String oldPath, String newPath) {   
		 try {   
	         int byteSum = 0;   
	         int byteRead = 0;   
	         File oldfile = new File(oldPath);   
	         if (oldfile.exists()) { //文件存在时   
	        	 InputStream input = new FileInputStream(oldPath); //读入原文件   
	             FileOutputStream output = new FileOutputStream(newPath);   
	             byte[] buffer = new byte[1444];   
                 int length;   
                 while ( (byteRead = input.read(buffer)) != -1) {      
                     output.write(buffer, 0, byteRead);   
                 }   
                 input.close(); 
                 output.close();
	         }   
	       }   
	      catch (Exception e) {   
	    	  Toast.makeText(this, "复制文件操作出错", Toast.LENGTH_SHORT).show();
		      e.printStackTrace();   
	     }   
   }   

	 
	 public void copyFolder(String oldPath, String newPath,String folderName) {   
		 try {  
			 (new File(newPath)).mkdirs();    //建立新文件夹目录   
			  FileItem fileItem = new FileItem();
			  fileItem.setImageId(R.drawable.file);
			  fileItem.setName(folderName);
			  fileList.add(fileItem);
			  File[] file = (new File(oldPath)).listFiles(); 
	          for (int i = 0; i < file.length; i++) {  
	                if (file[i].isFile()) {        
	                	String sourceFile=file[i].getPath();           
	                	String targetFile = newPath + File.separator + file[i].getName();  
	                    copyFile(sourceFile,targetFile);  
	                 }  
	                if (file[i].isDirectory()) {    
	                   String path1=oldPath + File.separator + file[i].getName();  // 准备复制的源文件夹 
	                   String path2=newPath + File.separator + file[i].getName();   // 准备复制的目标文件夹   
	                   copyFolder(path1, path2,file[i].getName());  
	                }  
	               
	          } 
	   } catch (Exception e) {   
		   	Toast.makeText(this, "复制文件夹操作出错", Toast.LENGTH_SHORT).show();   
	        e.printStackTrace();   
	      }   
    }  


	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 public void onBackPressed(){
		 isMulChoice = false;
		 optionLayout.setVisibility(View.GONE);
		 fileselectedNumber.setVisibility(View.GONE);
		 catalogIndex.remove(catalogIndex.get(catalogIndex.size()-1));
		 FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
		 listView = (ListView)findViewById(R.id.list_view);
		 listView.setAdapter(adapter);
		 currentPath = new File(currentPath).getParent();
	 }
	 
	 
	

}