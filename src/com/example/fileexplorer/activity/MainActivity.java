package com.example.fileexplorer.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.fileexplorer.R;
import com.example.fileexplorer.database.FileOpenHelper;
import com.example.fileexplorer.file.FileAdapter;
import com.example.fileexplorer.file.FileItem;
import com.example.fileexplorer.util.MemoryCaculation;
import com.example.fileexplorer.util.ZipUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener{
	
	
	public static List<FileItem> fileList ;  //用于储存当前目录下各文件的相关数据
	public static List<List> catalogIndex = new ArrayList<List>();   //用于储存当前以及之前的目录
	public static  String rootPath =Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;  //根目录
	public static MemoryCaculation memoryCaculation = new MemoryCaculation();   //关于计算储存空间的类
	public static boolean isMulChoice = false;  //是否多选（各项选择框）
	public static TableLayout optionLayout;  //文件操作功能框架
	public static TextView fileselectedNumber;    //显示选中的数量
	public static ListView listView;     //文件列表
	public static String currentPath ;   //当前路径
	public static File[] files;    //当前目录下的文件数组
	public  List< Map<String, String> > totalFileList = new ArrayList< Map<String, String> >();
	public Button moreFunction;       //更多功能的触发按钮
	public PopupWindow popupWindow;    //置顶功能框
	public int flag = 0;   //用于判断在粘贴前一操作是复制还是剪切
	public int mark = 0;   //用于在Back前判断关闭文件
	public  FileOpenHelper dbHelper = new FileOpenHelper(this,"File.db",null,1);
	public static SQLiteDatabase db;   //储存文件信息的数据库
	public static int progress = 0;    //标识符，用于标志数据是否完全导入到数据库里
	public TextView memoryAvailable;   //可用储存空间
	public TextView memoryAll;        //全部储存空间
	public ProgressBar progressBar;   //储存空间进度条

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		/*
		 * 将当前手机内存界面上面的标题中的“手机”的颜色设置为橙色
		 */
		TextView catalogFile = (TextView)findViewById(R.id.catalog_file);
		catalogFile.setTextColor(0xFFFF8C00);
		TextView classifyFile = (TextView)findViewById(R.id.classify_file);
		classifyFile.setTextColor(0xFF000000);
		
		currentPath= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		/*
		 * 将有关文件的数据导入到数据库里
		 */
		db = dbHelper.getWritableDatabase();
		Cursor myCursor = db.rawQuery("select * from File", null);
		if(myCursor.getCount() == 0){     //判断数据库是否为空
			new Thread(new Runnable(){
				public void run(){
					
					db.beginTransaction();  //开启事务，保证数据完全导入到数据库里
					try{
						saveFile(rootPath);
						db.setTransactionSuccessful();   //事务已经执行成功
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						progress = 1;     //数据完全导入，将progress置为1
						db.endTransaction();
					}
			}
		}).start();
	}else{
		progress = 1;   //数据早已导入，将progress置为1
	}
	
		
		
		
		/*
		 * 显示文件列表
		 */
		initView(currentPath);
		FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
		listView = (ListView)findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		
		
		/*
		 * 功能按钮注册监听事件
		 */
		Button renameFile = (Button)findViewById(R.id.rename_file);
		renameFile.setOnClickListener(this);
		Button deleteFile = (Button)findViewById(R.id.delete_file);
		deleteFile.setOnClickListener(this);
		Button copyFile = (Button)findViewById(R.id.copy_file);
		copyFile.setOnClickListener(this);
		Button cropFile = (Button)findViewById(R.id.crop_file);
		cropFile.setOnClickListener(this);
		Button encryptFile = (Button)findViewById(R.id.encrypt_file);
		encryptFile.setOnClickListener(this);
		
		
		/*
		 * 搜索界面跳换
		 */
		EditText searchMessage = (EditText)findViewById(R.id.search_message);
		searchMessage.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(MainActivity.this,SearchActivity.class);
				startActivity(intent);
				
			}
		});
		
		
		/*
		 * 点击“分类”，进入到文件分类界面
		 */
		classifyFile.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(progress == 0){    //数据还未完全加载到数据库中
					ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
					progressDialog.setTitle("正在加载数据....");
					progressDialog.setMessage("请稍后再");
					progressDialog.setCancelable(true);
					progressDialog.show();
				}else{
					Intent intent = new Intent(MainActivity.this,AnotherActivity.class);
					startActivity(intent);
				}	
			}
		});
		
		
		/*
		 * “更加功能”按钮注册监听事件
		 */
		Button moreFunction = (Button)findViewById(R.id.more_function);
		moreFunction.setOnClickListener(this);
		
		/*
		 * 文件操作功能选择框架
		 */
		optionLayout = (TableLayout)findViewById(R.id.function_option);
		fileselectedNumber = (TextView)findViewById(R.id.fileselected_number);

		/*
		 * 显示存储情况
		 */
		memoryAvailable = (TextView)findViewById(R.id.memory_available);
		memoryAll = (TextView)findViewById(R.id.memory_all);
		ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		memoryCaculation.MemorySizeCaculation();
		memoryAvailable.setText("可用：" + memoryCaculation.availableMemorySize);
		memoryAll.setText("总共：" + memoryCaculation.totalMemorySize);
		progressBar.setProgress(memoryCaculation.memoryProprtion);
							
	}
	
	
	/*
	 * 更新存储空间的数据（在删除、复制、压缩、解压等操作后）
	 */
	public void updateMemory(){
		memoryAvailable = (TextView)findViewById(R.id.memory_available);
		memoryAll = (TextView)findViewById(R.id.memory_all);
		ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		memoryCaculation.MemorySizeCaculation();
		memoryAvailable.setText("可用：" + memoryCaculation.availableMemorySize);
		memoryAll.setText("总共：" + memoryCaculation.totalMemorySize);
		progressBar.setProgress(memoryCaculation.memoryProprtion);
	}

	
	/*
	 * 将有关文件的数据存储到数据库中
	 */
	public void saveFile(String path){
		File[] totalFile = new File(path).listFiles();
		if(totalFile == null)
			return ;
		for(File file : totalFile){
			ContentValues values = new ContentValues();
			if(file.isFile()){
				values.put("name", file.getName());  
				values.put("path", file.getAbsolutePath());  
				values.put("type",file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase());
				db.insert("File", null, values);
			}
			if(file.isDirectory()){     //如果是文件夹，递归查找
				saveFile(file.getAbsolutePath());
			}
			
		}
	}
	
	/*
	 * 将某一路径下的文件移出数据库
	 */
	public void removelFileFromDB(String path){
			File[] totalFile = new File(path).listFiles();
			if(totalFile == null || totalFile.length == 0)
				return ;
			for(File file : totalFile){
				if(file.isFile()){
					db.delete("File", "path = ?",  new String[]{file.getAbsolutePath()});
				}
				if(file.isDirectory()){     //如果是文件夹，递归查找
					removelFileFromDB(file.getAbsolutePath());
				}	
			}
	}
	
	/*
	 *  将某一路径下的文件添加到数据库
	 */
	public void addlFileToDB(String path){
		File[] totalFile = new File(path).listFiles();
		if(totalFile == null || totalFile.length == 0)
			return ;
		for(File file : totalFile){
			if(file.isFile()){
				ContentValues values = new ContentValues();
                values.put("name", file.getName());
                values.put("path", file.getAbsolutePath());
                values.put("type",file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase());
                db.insert("File",null,values);
			}
			if(file.isDirectory()){     //如果是文件夹，递归查找
				addlFileToDB(file.getAbsolutePath());
			}	
		}
	}
	
	/*
	 * 初始化当前目录下文件的数据
	 */
	public static void initView(String filePath){
			List<FileItem> list = new ArrayList<FileItem>();
			File folder =new File(filePath);
			files = folder.listFiles();
			String fileType = "image/*";
			for(File file : files){
				FileItem fileItem = new FileItem();
				fileItem.setName(file.getName());
				fileItem.setfilePath(file.getAbsolutePath());
				fileItem.setImageId(R.drawable.file);
				if(file.isDirectory()){
					fileItem.setImageId(R.drawable.file);
				}else if(file.isFile()){
					fileType = FileAdapter.getMIMEType(file);
					if(fileType.equals("audio/*")){
						fileItem.setImageId(R.drawable.music);
					}else if(fileType.equals("video/*")){
						fileItem.setImageId(R.drawable.video);
					}else if(fileType.equals("image/*")){
						fileItem.setImageId(R.drawable.gallery);
					}else if(fileType.equals("text/plain")){
						fileItem.setImageId(R.drawable.text);
					}else if(fileType.equals("application/vnd.android.package-archive")){
						fileItem.setImageId(R.drawable.apk);
					}else if(fileType.equals("application/msword")){
						fileItem.setImageId(R.drawable.word);
					}else if(fileType.equals("application/vnd.ms-powerpoint")){
						fileItem.setImageId(R.drawable.ppt);
					}else if(fileType.equals("application/pdf")){
						fileItem.setImageId(R.drawable.pdf);
					}else if(fileType.equals("application/vnd.ms-excel")){
						fileItem.setImageId(R.drawable.excel);
					}else if(fileType.equals("application/zip")){
						fileItem.setImageId(R.drawable.zip);
					}
					else if(fileType.equals("*/*")){
						fileItem.setImageId(R.drawable.unknown);
					}	
				}
				list.add(fileItem);	
			}
			catalogIndex.add(list);
			fileList = list;
	}
	
		
	
	/*
	 * 功能点击事件，对选中的文件进行操作
	 */
	 public void onClick(View v) {
		 switch (v.getId()) {
		 case R.id.rename_file:    //重命名
			 if(FileAdapter.fileSelected.size() == 0){
					Toast.makeText(MainActivity.this,"请选择需要重命名的文件进行重命名",Toast.LENGTH_SHORT).show();
				}else{
					isMulChoice = false;
				 	if(FileAdapter.fileSelected.size() == 1){
				 		AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
				 		dialog1.setTitle("重命名");
				 		final View view1 = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
				 		TextView editTip1 = (TextView)view1.findViewById(R.id.edit_tip);
				 		editTip1.setText("请输入新文件名");
				 		final EditText editContent = (EditText)view1.findViewById(R.id.edit_content);
				 		if((new File(FileAdapter.fileSelected.get(0).getfilePath())).isFile()){  //文件重命名，补上文件后缀
				 			String type = FileAdapter.fileSelected.get(0).getName();
				 			editContent.setText(type.substring(type.lastIndexOf(".")).toLowerCase());
				 			editContent.setSelection(0);  //光标定在最前面
				 		}
				 		dialog1.setView(view1);
				 		dialog1.setCancelable(false);
				 		dialog1.setPositiveButton("确定",new DialogInterface.OnClickListener() {
						
				 			public void onClick(DialogInterface dialog, int which) {
				 				// TODO Auto-generated method stub
						    String fileName = editContent.getText().toString();
						    File newFile = new File(currentPath +  fileName + File.separator);
						    if(!newFile.exists() && !TextUtils.isEmpty(fileName)){				    	
						    	for(File file : files){
						  			if(file.getName().equals(((FileItem)FileAdapter.fileSelected.get(0)).getName())){
						  				file.renameTo(newFile);   //重命名
						  				fileList.get(fileList.indexOf((FileItem)FileAdapter.fileSelected.get(0))).setName(fileName);
						  				if(newFile.isFile()){   //更新数据库中相应的文件信息
						  					ContentValues values = new ContentValues();
							  				values.put("name",fileName);
							  				values.put("path", currentPath +  fileName);
							  				db.update("File",values,"path=?",new String[]{FileAdapter.fileSelected.get(0).getfilePath()});
						  				}
						  				Log.d("newname",newFile.getAbsolutePath());
						  				
						  			}
								}
						    }else
						    	Toast.makeText(MainActivity.this,"重命名不成功，请重新输入正确、不重复的文件名",Toast.LENGTH_SHORT).show();	      
							FileAdapter.fileSelected.clear();
						    for(int i=0;i<fileList.size();i++){
						    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
						    }
						    optionLayout.setVisibility(View.GONE);
						    fileselectedNumber.setVisibility(View.GONE);
						}
					} );
			 		dialog1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							FileAdapter.fileSelected.clear();
							for(int i=0;i<fileList.size();i++){
						    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
						    }
						    optionLayout.setVisibility(View.GONE);
						    fileselectedNumber.setVisibility(View.GONE);
						}
					});
			 		dialog1.show();
				 	} else{
				    	Toast.makeText(MainActivity.this, "不能同时命名文件", Toast.LENGTH_SHORT).show();
				    	FileAdapter.fileSelected.clear();
						for(int i=0;i<fileList.size();i++){
					    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
					    }
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
				    } 
				}
		 		break;
		 case R.id.delete_file:  //删除
		 		isMulChoice = false;
		 		File folder =new File(currentPath);
		 		files = folder.listFiles();
		 		AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		 		dialog.setTitle("删除");
		 		dialog.setMessage("确定删除此文件");
		 		dialog.setCancelable(false);
		 		dialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub		
						for(FileItem fileSelected: FileAdapter.fileSelected){
						  	for(File file : files){
						  		if(file.getName().equals(fileSelected.getName())){
						  			deleteFile(file);   //删除文件
						  			fileList.remove(fileSelected);
						  			SharedPreferences pref = getSharedPreferences(fileSelected.getName(),MODE_PRIVATE);
						  			String filePath = pref.getString("file","");
									if(filePath.equals(fileSelected.getfilePath()))   //若为加密文件，删除加密时保存的所有信息
											pref.edit().clear().commit(); 
						  		}				  		
						  	}   
						} 	
						FileAdapter.fileSelected.clear();
					    for(int i=0;i<fileList.size();i++){
					    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
					    }
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
					  updateMemory();
					}
				} );
		 		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						FileAdapter.fileSelected.clear();
					    for(int i=0;i<fileList.size();i++){
					    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
					    }
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
					}
				});
		 		dialog.show();
		 	break;
		
		case R.id.new_file:  //新建
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
					EditText editContent = (EditText)view.findViewById(R.id.edit_content);
				    String fileName = editContent.getText().toString();
				    File newFile = new File(currentPath + fileName  + File.separator);  //实例化File
				    if (!newFile.exists() && !TextUtils.isEmpty(fileName)) {   //文件夹不存在且文件夹名不为空
				    	newFile.mkdir();    //创建文件夹
				    	FileItem fileItem = new FileItem();
						fileItem.setImageId(R.drawable.file);
						fileItem.setName(fileName);
						fileItem.setfilePath(currentPath + fileName  + File.separator);
						fileList.add(0,fileItem);    //放在列表第一位
	                }else
	                	Toast.makeText(MainActivity.this,"创建不成功，请重新输入正确、不重复的文件名",Toast.LENGTH_SHORT).show();
					FileAdapter.fileSelected.clear();
					FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
				    listView.setAdapter(adapter);
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				    popupWindow.dismiss(); 
				   
				}
			} );
	 		dialog0.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					FileAdapter.fileSelected.clear();
					FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
				    listView.setAdapter(adapter);
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				    popupWindow.dismiss(); 
				}
			});
	 		dialog0.show();
	 		break; 	
		 		
		case R.id.sortup_file:	//排序 （升序）
	        Collections.sort(fileList,new Comparator<FileItem>(){ 
	            public int compare(FileItem arg0, FileItem arg1) {
	            	if(arg0.getName().charAt(0) == arg1.getName().charAt(0) + 32
	            	|| arg1.getName().charAt(0) == arg0.getName().charAt(0) + 32){  //大小写之间的排序
	            		return arg0.getName().compareTo(arg1.getName());
	            	}else{
	            		String str0 = arg0.getName().toLowerCase();
		            	String str1 = arg1.getName().toLowerCase();
	            		if(str0.getBytes().length == str0.length()&& str1.getBytes().length == str1.length()){  //不同字母间的排序          			
			            	return str0.compareTo(str1);
	            		}else if(str0.getBytes().length != str0.length()&& str1.getBytes().length != str1.length()){  //不同汉字间的排序
			            	return str1.compareTo(str0);
	            		}else  //汉字和字母间的排序
	            			return str0.compareTo(str1);
	            		
	            	}	
	             }
	        } );
	        FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
		    listView.setAdapter(adapter);
		    popupWindow.dismiss(); 
	 		break; 		
		 	
		case R.id.sortdown_file:   //排序（降序）
			Collections.sort(fileList,new Comparator<FileItem>(){ 
	            public int compare(FileItem arg0, FileItem arg1) {
	            	if(arg0.getName().charAt(0) == arg1.getName().charAt(0) + 32
	            	|| arg1.getName().charAt(0) == arg0.getName().charAt(0) + 32){
	            		return arg1.getName().compareTo(arg0.getName());
	            	}else{
	            		String str0 = arg0.getName().toLowerCase();
		            	String str1 = arg1.getName().toLowerCase();
	            		if(str0.getBytes().length == str0.length()&& str1.getBytes().length == str1.length()){            			
			            	return str1.compareTo(str0);
	            		}else if(str0.getBytes().length != str0.length()&& str1.getBytes().length != str1.length()){
			            	return str0.compareTo(str1);
	            		}else
	            			return str1.compareTo(str0);
	            	}	
	             } 
	        });
	 		FileAdapter adapter1 = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
			listView.setAdapter(adapter1);
			popupWindow.dismiss(); 
	 		break; 	
	 		
	
		case R.id.more_function:   //触发更多功能事件，置顶窗口出现
			View v1 = LayoutInflater.from(this).inflate(R.layout.activity_main,null);
			showPopupWindow(v1);
			break;
		
		case  R.id.copy_file:  //复制
			flag = 0;    //0标识为复制
			isMulChoice = false;
		    for(int i=0;i<fileList.size();i++){
		    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
		    }
		    optionLayout.setVisibility(View.GONE);
		    fileselectedNumber.setVisibility(View.GONE);   
			break;
			
		case R.id.crop_file:  //剪切
			flag = 1;  //1标识为剪切
			isMulChoice = false;
			for(FileItem fileSelected: FileAdapter.fileSelected)
			  	fileList.remove(fileSelected);				  		
			FileAdapter adapter7 = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
		    listView.setAdapter(adapter7);
			optionLayout.setVisibility(View.GONE);
		    fileselectedNumber.setVisibility(View.GONE);
			break;
		
					
		case R.id.paste_file:  //粘贴
			if(FileAdapter.fileSelected.size() == 0){
				Toast.makeText(MainActivity.this,"操作错误，未选择文件进行操作",Toast.LENGTH_SHORT).show();
			}else{
				for(FileItem fileSelected: FileAdapter.fileSelected){  //文件夹
					if( (new File(fileSelected.getfilePath())).isDirectory()){
						copyFolder(fileSelected.getfilePath(),currentPath +  (fileSelected.getName()).toString(),fileSelected.getName());
						FileItem fileItem = new FileItem();
						fileItem.setImageId(R.drawable.file);
						fileItem.setName(fileSelected.getName());
						fileItem.setfilePath(currentPath + fileSelected.getName());
						fileList.add(0,fileItem);
					}
						
					if((new File(fileSelected.getfilePath())).isFile()){   //文件
						copyFile(fileSelected.getfilePath(),currentPath +  (fileSelected.getName()).toString());
						FileItem fileItem = new FileItem();
						String fileType = FileAdapter.getMIMEType(new File(fileSelected.getfilePath()));
							fileType = FileAdapter.getMIMEType(new File(fileSelected.getfilePath()));
							if(fileType.equals("audio/*")){
								fileItem.setImageId(R.drawable.music);
							}else if(fileType.equals("video/*")){
								fileItem.setImageId(R.drawable.video);
							}else if(fileType.equals("image/*")){
								fileItem.setImageId(R.drawable.gallery);
							}else if(fileType.equals("text/plain")){
								fileItem.setImageId(R.drawable.text);
							}else if(fileType.equals("application/vnd.android.package-archive")){
								fileItem.setImageId(R.drawable.apk);
							}else if(fileType.equals("application/msword")){
								fileItem.setImageId(R.drawable.word);
							}else if(fileType.equals("application/vnd.ms-powerpoint")){
								fileItem.setImageId(R.drawable.ppt);
							}else if(fileType.equals("application/pdf")){
								fileItem.setImageId(R.drawable.pdf);
							}else if(fileType.equals("application/vnd.ms-excel")){
								fileItem.setImageId(R.drawable.excel);
							}else if(fileType.equals("application/zip")){
								fileItem.setImageId(R.drawable.zip);
							}
							else if(fileType.equals("*/*")){
								fileItem.setImageId(R.drawable.unknown);
							}	
						
						fileItem.setName(fileSelected.getName());
						fileItem.setfilePath(currentPath + fileSelected.getName());
						fileList.add(0,fileItem);	
					}		
				}
				if(flag == 1){  //前一操作为剪切，删除原来的文件
					for(FileItem fileSelected: FileAdapter.fileSelected){
					  	File targetFile = new File(fileSelected.getfilePath());
					  	deleteFile(targetFile);			  		  
					} 	
				}
				FileAdapter adapter4 = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
			    listView.setAdapter(adapter4);
			    FileAdapter.fileSelected.clear();
			    popupWindow.dismiss(); 
			    updateMemory();   //更新存储空间的数据
			}	
			break; 
		case R.id.zip_file:   //压缩
			if(FileAdapter.fileSelected.size() == 0){
				Toast.makeText(MainActivity.this,"请选择需要压缩的文件再进行压缩",Toast.LENGTH_SHORT).show();
			}else if(FileAdapter.fileSelected.size() > 1){
				Toast.makeText(MainActivity.this,"不可同时对多个文件夹进行压缩",Toast.LENGTH_SHORT).show();
				isMulChoice = false;
				FileAdapter.fileSelected.clear();
			    for(int i=0;i<fileList.size();i++){
			    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
			    }
			    optionLayout.setVisibility(View.GONE);
			    fileselectedNumber.setVisibility(View.GONE);
			}
			else if(FileAdapter.fileSelected.size() == 1){
				isMulChoice = false;
		 		AlertDialog.Builder dialog4 = new AlertDialog.Builder(MainActivity.this);
		 		dialog4.setTitle("压缩");
		 		final View view4 = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
		 		final EditText editContent = (EditText)view4.findViewById(R.id.edit_content);
		 		editContent.setText(".zip");   //设置EditText控件的内容
		 		editContent.setSelection(0);   //将光标移至最前面
		 		TextView editTip4 = (TextView)view4.findViewById(R.id.edit_tip);
		 		editTip4.setText("压缩到");
		 		dialog4.setView(view4);
		 		dialog4.setCancelable(false);
		 		dialog4.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					    String fileName = editContent.getText().toString();
					    if (!(new File(currentPath + fileName).exists()) && !TextUtils.isEmpty(fileName) && !fileName.equals(".zip")){
					    	FileItem selectedFileItem = (FileItem)FileAdapter.fileSelected.get(0);
						    try{
						    	ZipUtil.zipFile(selectedFileItem.getfilePath(),currentPath + fileName);
						    	/*
						    	 * 将生成的Zip文件信息添加到数据库中
						    	 */
						    	ContentValues values = new ContentValues();
				                values.put("name", fileName);
				                values.put("path",currentPath + fileName);
				                values.put("type","zip");
				                db.insert("File",null,values);
						    	}catch(Exception e){
						    		 e.printStackTrace(); 
						    	}
						    FileItem fileItem = new FileItem();
							fileItem.setImageId(R.drawable.zip);
							fileItem.setName(fileName);
							fileItem.setfilePath(currentPath + fileName);
							fileList.add(0,fileItem);
					    }else{
					    	Toast.makeText(MainActivity.this,"压缩不成功，请重新输入正确、不重复的文件名",Toast.LENGTH_SHORT).show();
					    }
					  			    
						FileAdapter.fileSelected.clear();
						FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
					    listView.setAdapter(adapter);
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
					    popupWindow.dismiss();
					    updateMemory();
					}
				} );
		 		dialog4.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						FileAdapter.fileSelected.clear();
						FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
					    listView.setAdapter(adapter);
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
					    popupWindow.dismiss(); 
					}
				});
		 		dialog4.show();
			}		
	 		break; 	
		case R.id.unzip_file:   //解压
			if(FileAdapter.fileSelected.size() == 0){
				Toast.makeText(MainActivity.this,"请选择需要解压的文件再进行解压",Toast.LENGTH_SHORT).show();
			}else if(FileAdapter.fileSelected.size() > 1 ){
				Toast.makeText(MainActivity.this,"不可同时对多个文件夹解压",Toast.LENGTH_SHORT).show();
				isMulChoice = false;
				FileAdapter.fileSelected.clear();
			    for(int i=0;i<fileList.size();i++){
			    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
			    }
			    optionLayout.setVisibility(View.GONE);
			    fileselectedNumber.setVisibility(View.GONE);
			}else if(FileAdapter.fileSelected.size() == 1){
				if(FileAdapter.fileSelected.get(0).getName().indexOf(".zip") > -1){
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
							EditText editContent = (EditText)view5.findViewById(R.id.edit_content);
						    String fileName = editContent.getText().toString();
						    if (!(new File(currentPath + fileName).exists()) && !TextUtils.isEmpty(fileName)){
						    	FileItem selectedFileItem = FileAdapter.fileSelected.get(0);
							    try{
							    	ZipUtil.unZipFile(selectedFileItem.getfilePath(),currentPath + fileName);
							    	addlFileToDB(currentPath + fileName);
							    	}catch(Exception e){
							    		 e.printStackTrace(); 
							    	}
							    FileItem fileItem = new FileItem();
								fileItem.setImageId(R.drawable.file);
								fileItem.setName(fileName);
								fileItem.setfilePath(currentPath + fileName);
								fileList.add(0,fileItem);
						    }else{
						    	Toast.makeText(MainActivity.this,"解压不成功，请重新输入正确、不重复的文件名",Toast.LENGTH_SHORT).show();
						    }			    
							FileAdapter.fileSelected.clear();
							FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
						    listView.setAdapter(adapter);
						    optionLayout.setVisibility(View.GONE);
						    fileselectedNumber.setVisibility(View.GONE);
						    popupWindow.dismiss(); 
						    updateMemory();
						}
					} );
			 		dialog5.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							FileAdapter.fileSelected.clear();
							FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
						    listView.setAdapter(adapter);
						    optionLayout.setVisibility(View.GONE);
						    fileselectedNumber.setVisibility(View.GONE);
						    popupWindow.dismiss(); 
						}
					});
			 		dialog5.show();
				}else{
					Toast.makeText(MainActivity.this,"只能对ZIP格式的文件进行解压",Toast.LENGTH_SHORT).show();	
					isMulChoice = false;
					FileAdapter.fileSelected.clear();
				    for(int i=0;i<fileList.size();i++){
				    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
				    }
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				}
					
				
			}
	 		break; 	
			
		case R.id.encrypt_file:  //加密
			if(FileAdapter.fileSelected.size() == 0){
				Toast.makeText(MainActivity.this,"请选择需要加密的文件进行加密",Toast.LENGTH_SHORT).show();
			}else if(FileAdapter.fileSelected.size() > 1){
				Toast.makeText(MainActivity.this,"不可同时对多个文件加密",Toast.LENGTH_SHORT).show();
				isMulChoice = false;
				FileAdapter.fileSelected.clear();
			    for(int i=0;i<fileList.size();i++){
			    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
			    }
			    optionLayout.setVisibility(View.GONE);
			    fileselectedNumber.setVisibility(View.GONE);
			}else if(FileAdapter.fileSelected.size() == 1){
				isMulChoice = false;
		 		AlertDialog.Builder dialog6 = new AlertDialog.Builder(MainActivity.this);
		 		dialog6.setTitle("设置为私密文件");
		 		final View view6 = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
		 		TextView editTip6 = (TextView)view6.findViewById(R.id.edit_tip);
		 		editTip6.setText("请输入密码");
		 		final EditText editContent1 = (EditText)view6.findViewById(R.id.edit_content);
		 		editContent1.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);  //输入内容为不可见
		 		dialog6.setView(view6);
		 		dialog6.setCancelable(false);
		 		dialog6.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub			
					    String password = editContent1.getText().toString();
					    if (!password.equals("")){  //密码不为空，将密码保存起来
					    	SharedPreferences.Editor editor = getSharedPreferences(FileAdapter.fileSelected.get(0).getName(),MODE_PRIVATE).edit();
					    	editor.putString("file",FileAdapter.fileSelected.get(0).getfilePath());
							editor.putString("password", password);
							editor.commit();
							/*
							 * 在数据库中移出加密的文件
							 */
							if((new File(FileAdapter.fileSelected.get(0).getfilePath())).isFile()){
								db.delete("File", "path = ?",  new String[]{FileAdapter.fileSelected.get(0).getfilePath()});
							}else
								removelFileFromDB(FileAdapter.fileSelected.get(0).getfilePath());
				
							Toast.makeText(MainActivity.this,"设置成功!不要随意更改加密文件的路径",Toast.LENGTH_SHORT).show();
					    }else{
					    	Toast.makeText(MainActivity.this,"密码不能为空!",Toast.LENGTH_SHORT).show();
					    }
						FileAdapter.fileSelected.clear();
					    for(int i=0;i<fileList.size();i++){
					    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
					    }
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
					}
				} );
		 		dialog6.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						FileAdapter.fileSelected.clear();
					    for(int i=0;i<fileList.size();i++){
					    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
					    }
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
					   
					}
				});
		 		dialog6.show();
			}
	 		break; 		 		
		default:
        	break; 		
	        
	        }   
	    }

	
		 

	 public void showPopupWindow(View view) {
	        // 一个自定义的布局，作为显示的内容
	        View contentView = LayoutInflater.from(this).inflate( R.layout.pop_window, null);
	        popupWindow = new PopupWindow(contentView,
	        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
	        popupWindow.setTouchable(true);   
	        popupWindow.setFocusable(true);   // 使其聚集  
	        popupWindow.setOutsideTouchable(true);   // 设置允许在外点击消失  
	        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
	        popupWindow.setBackgroundDrawable(new BitmapDrawable());      

	        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
	        // 我觉得这里是API的一个bug
	        popupWindow.setBackgroundDrawable(getResources().getDrawable(
	                R.drawable.ppt)); 
	       	popupWindow.showAtLocation(view, Gravity.TOP, 0, 0);      // 设置好参数之后再show
	        TextView newFile = (TextView)contentView.findViewById(R.id.new_file);
	        newFile.setOnClickListener(this);
	        TextView pasteFile = (TextView)contentView.findViewById(R.id.paste_file);
			pasteFile.setOnClickListener(this);
	        TextView sortupFile = (TextView)contentView.findViewById(R.id.sortup_file);
			sortupFile.setOnClickListener(this);
			TextView sortdownFile = (TextView)contentView.findViewById(R.id.sortdown_file);
			sortdownFile.setOnClickListener(this);
			TextView zipFile = (TextView)contentView.findViewById(R.id.zip_file);
			zipFile.setOnClickListener(this);
			TextView unzipFile = (TextView)contentView.findViewById(R.id.unzip_file);
			unzipFile.setOnClickListener(this);			

	    }

	 /*
	  * 删除文件
	  */
	 public void deleteFile(File file){
		if(file.isFile()){
			file.delete();
			db.delete("File", "path = ?", new String[]{file.getAbsolutePath()});
		}else{			
			File[] childFiles = file.listFiles();			
		    if (childFiles == null || childFiles.length == 0) {  
		          file.delete();	          
		     }else{
		    	 for (int i = 0; i < childFiles.length; i++) {  		      
		    		 deleteFile(childFiles[i]);  
		    	 }	     
		     }  
		    file.delete(); 
		}
	 }
	 
	 /*
	  * 复制文件
	  */
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
                 ContentValues values = new ContentValues();
                 values.put("name", oldfile.getName());
                 values.put("path", newPath);
                 values.put("type",newPath.substring(newPath.lastIndexOf(".") + 1).toLowerCase());
                 db.insert("File",null,values);
	         }   
	       }   
	      catch (Exception e) {   
	    	  Toast.makeText(this, "复制文件操作出错", Toast.LENGTH_SHORT).show();
		      e.printStackTrace();   
	     }   
   }   

	 /*
	  * 复制文件夹
	  */
	 public void copyFolder(String oldPath, String newPath,String folderName) {   
		 try {  
			 (new File(newPath)).mkdirs();    //建立新文件夹目录   
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

	 
	 /*
	  * 返回事件
	  */
	 public void onBackPressed(){	 
		 if(isMulChoice == true){   //各项选择框出现的情况下，返回让其消失
			 isMulChoice = false;
			 FileAdapter.fileSelected.clear();
			    for(int i=0;i<fileList.size();i++){
			    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
			    }
			    optionLayout.setVisibility(View.GONE);
			    fileselectedNumber.setVisibility(View.GONE);
		 }else{     //各项选择框不出现的情况下
			 if(catalogIndex.size() > 1){   //路径不为根目录，返回到父目录
				 catalogIndex.remove(catalogIndex.get(catalogIndex.size()-1));
				 fileList = catalogIndex.get(catalogIndex.size()-1);
				 FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
				 listView = (ListView)findViewById(R.id.list_view);
				 listView.setAdapter(adapter);
				 currentPath = new File(currentPath).getParent() + File.separator;
				 File folder =new File(currentPath);
			 	 files = folder.listFiles(); 		 	
			 }else if(catalogIndex.size() == 1){   //路径为根目录，返回退出
				catalogIndex.clear();
				 finish(); 
			 }
		 }	
		 
	 }
	 
	 
}