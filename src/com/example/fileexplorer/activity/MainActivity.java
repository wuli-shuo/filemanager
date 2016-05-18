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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.example.fileexplorer.R;
import com.example.fileexplorer.database.FileDB;
import com.example.fileexplorer.database.FileOpenHelper;
import com.example.fileexplorer.file.FileAdapter;
import com.example.fileexplorer.file.FileItem;
import com.example.fileexplorer.util.MemoryCaculation;
import com.example.fileexplorer.util.ZipUtil;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener{
	
	
	public static List<FileItem> fileList ;
	public static List<List> catalogIndex = new ArrayList<List>();
	public static  String rootPath =Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	public static MemoryCaculation memoryCaculation = new MemoryCaculation();
	public static boolean isMulChoice = false;  //�Ƿ��ѡ
	public static TableLayout optionLayout;  //�ļ��������ܿ��
	public static TextView fileselectedNumber;    //��ʾѡ�е�����
	public static ListView listView;     //�ļ��б�
	public static String currentPath ;
	public static File[] files;
	public  List< Map<String, String> > totalFileList = new ArrayList< Map<String, String> >();
	public Button moreFunction;
	public PopupWindow popupWindow;
	public int flag = 0;   //������ճ��ǰһ�����Ǹ��ƻ��Ǽ���
	public int mark = 0;   //������Backǰ�жϹر��ļ�
	public  FileOpenHelper dbHelper = new FileOpenHelper(this,"File.db",null,1);
	public static SQLiteDatabase db;
	public static int progress = 0;
	public TextView memoryAvailable;
	public TextView memoryAll;
	public ProgressBar progressBar;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		TextView catalogFile = (TextView)findViewById(R.id.catalog_file);
		catalogFile.setTextColor(0xFFFF8C00);
		TextView classifyFile = (TextView)findViewById(R.id.classify_file);
		classifyFile.setTextColor(0xFF000000);
		
		

		
		
		currentPath= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		
		db = dbHelper.getWritableDatabase();
		Cursor myCursor = db.rawQuery("select * from File", null);
		if(myCursor.getCount() == 0){
			new Thread(new Runnable(){
				public void run(){
					
					db.beginTransaction();
					try{
						saveFile(rootPath);
						db.setTransactionSuccessful();   //�����Ѿ�ִ�гɹ�
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						progress = 1;
						db.endTransaction();
					}
			}
		}).start();
	}else{
		progress = 1;
	}
	
		
		
		
		
		
		
		
	
	
		
		
		//��ʾ�ļ��б�
		initView(currentPath);
		FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
		listView = (ListView)findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		
		
		//���ܰ�ťע������¼�
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
		
		
		
		//������������
		EditText searchMessage = (EditText)findViewById(R.id.search_message);
		searchMessage.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(MainActivity.this,SearchActivity.class);
				startActivity(intent);
				
			}
		});
		
		
		//��������ࡱ�����뵽�ļ��������
		
		classifyFile.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(progress == 0){
					ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
					progressDialog.setTitle("���ڼ�������....");
					progressDialog.setMessage("���Ժ���");
					progressDialog.setCancelable(true);
					progressDialog.show();
				}else{
					Intent intent = new Intent(MainActivity.this,AnotherActivity.class);
					startActivity(intent);
				}	
			}
		});
		
		
		
		Button moreFunction = (Button)findViewById(R.id.more_function);
		moreFunction.setOnClickListener(this);
		
		//�ļ���������ѡ����
		optionLayout = (TableLayout)findViewById(R.id.function_option);
		fileselectedNumber = (TextView)findViewById(R.id.fileselected_number);

		//��ʾ�洢���
		memoryAvailable = (TextView)findViewById(R.id.memory_available);
		memoryAll = (TextView)findViewById(R.id.memory_all);
		ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		memoryCaculation.MemorySizeCaculation();
		memoryAvailable.setText("���ã�" + memoryCaculation.availableMemorySize);
		memoryAll.setText("�ܹ���" + memoryCaculation.totalMemorySize);
		progressBar.setProgress(memoryCaculation.memoryProprtion);
							
	}
	
	
	
	public void updateMemory(){
		memoryAvailable = (TextView)findViewById(R.id.memory_available);
		memoryAll = (TextView)findViewById(R.id.memory_all);
		ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		memoryCaculation.MemorySizeCaculation();
		memoryAvailable.setText("���ã�" + memoryCaculation.availableMemorySize);
		memoryAll.setText("�ܹ���" + memoryCaculation.totalMemorySize);
		progressBar.setProgress(memoryCaculation.memoryProprtion);
	}

	
	
	
	
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
			if(file.isDirectory()){     //������ļ��У��ݹ����
				saveFile(file.getAbsolutePath());
			}
			
		}
	}
	
	
	public void removelFileFromDB(String path){
			File[] totalFile = new File(path).listFiles();
			if(totalFile == null || totalFile.length == 0)
				return ;
			for(File file : totalFile){
				if(file.isFile()){
					db.delete("File", "path = ?",  new String[]{file.getAbsolutePath()});
				}
				if(file.isDirectory()){     //������ļ��У��ݹ����
					removelFileFromDB(file.getAbsolutePath());
				}	
			}
	}
	
	
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
			if(file.isDirectory()){     //������ļ��У��ݹ����
				addlFileToDB(file.getAbsolutePath());
			}	
		}
}
	
	
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
	
	
	/*public static void initView(String filePath){
		List<FileItem> list = new ArrayList<FileItem>();
		File folder =new File(filePath);
		files = folder.listFiles();
		for(File file : files){
			FileItem fileItem = new FileItem();
			fileItem.setName(file.getName());
			fileItem.setfilePath(file.getAbsolutePath());
			fileItem.setImageId(R.drawable.file);
			fileItem.setImageId(R.drawable.file);
			list.add(fileItem);	
		}
		catalogIndex.add(list);
		fileList = list;
	}*/
	
	
	
	
	//���ܵ���¼�����ѡ�е��ļ����в���
	 public void onClick(View v) {
		 switch (v.getId()) {
		 case R.id.rename_file:
			 if(FileAdapter.fileSelected.size() == 0){
					Toast.makeText(MainActivity.this,"��ѡ����Ҫ���������ļ�����������",Toast.LENGTH_SHORT).show();
				}else{
					isMulChoice = false;
				 	if(FileAdapter.fileSelected.size() == 1){
				 		AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
				 		dialog1.setTitle("������");
				 		final View view1 = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
				 		TextView editTip1 = (TextView)view1.findViewById(R.id.edit_tip);
				 		editTip1.setText("���������ļ���");
				 		final EditText editContent = (EditText)view1.findViewById(R.id.edit_content);
				 		if((new File(FileAdapter.fileSelected.get(0).getfilePath())).isFile()){
				 			String type = FileAdapter.fileSelected.get(0).getName();
				 			editContent.setText(type.substring(type.lastIndexOf(".")).toLowerCase());
				 			editContent.setSelection(0);  
				 		}
				 		dialog1.setView(view1);
				 		dialog1.setCancelable(false);
				 		dialog1.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
						
				 			public void onClick(DialogInterface dialog, int which) {
				 				// TODO Auto-generated method stub
						    String fileName = editContent.getText().toString();
						    File newFile = new File(currentPath +  fileName + File.separator);
						    if(!newFile.exists() && !TextUtils.isEmpty(fileName)){				    	
						    	for(File file : files){
						  			if(file.getName().equals(((FileItem)FileAdapter.fileSelected.get(0)).getName())){
						  				file.renameTo(newFile);
						  				fileList.get(fileList.indexOf((FileItem)FileAdapter.fileSelected.get(0))).setName(fileName);
						  				if(newFile.isFile()){
						  					ContentValues values = new ContentValues();
							  				values.put("name",fileName);
							  				values.put("path", currentPath +  fileName);
							  				db.update("File",values,"path=?",new String[]{FileAdapter.fileSelected.get(0).getfilePath()});
						  				}
						  				Log.d("newname",newFile.getAbsolutePath());
						  				
						  			}
								}
						    }else
						    	Toast.makeText(MainActivity.this,"���������ɹ���������������ȷ�����ظ����ļ���",Toast.LENGTH_SHORT).show();	      
							FileAdapter.fileSelected.clear();
						    for(int i=0;i<fileList.size();i++){
						    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
						    }
						    optionLayout.setVisibility(View.GONE);
						    fileselectedNumber.setVisibility(View.GONE);
						}
					} );
			 		dialog1.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
						
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
				    	Toast.makeText(MainActivity.this, "����ͬʱ�����ļ�", Toast.LENGTH_SHORT).show();
				    	FileAdapter.fileSelected.clear();
						for(int i=0;i<fileList.size();i++){
					    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
					    }
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
				    } 
				}
		 		break;
		 case R.id.delete_file:
		 		isMulChoice = false;
		 		File folder =new File(currentPath);
		 		files = folder.listFiles();
		 		AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
		 		dialog.setTitle("ɾ��");
		 		dialog.setMessage("ȷ��ɾ�����ļ�");
		 		dialog.setCancelable(false);
		 		dialog.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub		
						for(FileItem fileSelected: FileAdapter.fileSelected){
						  	for(File file : files){
						  		if(file.getName().equals(fileSelected.getName())){
						  			deleteFile(file);
						  			fileList.remove(fileSelected);
						  			SharedPreferences pref = getSharedPreferences(fileSelected.getName(),MODE_PRIVATE);
						  			String filePath = pref.getString("file","");
									if(filePath.equals(fileSelected.getfilePath())) 
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
		 		dialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					
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
		
		case R.id.new_file:
	 		isMulChoice = false;
	 		AlertDialog.Builder dialog0 = new AlertDialog.Builder(MainActivity.this);
	 		dialog0.setTitle("�½��ļ���");
	 		final View view = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
	 		TextView editTip = (TextView)view.findViewById(R.id.edit_tip);
	 		editTip.setText("�������½��ļ���");
	 		dialog0.setView(view);
	 		dialog0.setCancelable(false);
	 		dialog0.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					EditText editContent = (EditText)view.findViewById(R.id.edit_content);
				    String fileName = editContent.getText().toString();
				    File newFile = new File(currentPath + fileName  + File.separator);  //ʵ����File
				    if (!newFile.exists() && !TextUtils.isEmpty(fileName)) {
				    	newFile.mkdir();    //�����ļ���
				    	FileItem fileItem = new FileItem();
						fileItem.setImageId(R.drawable.file);
						fileItem.setName(fileName);
						fileItem.setfilePath(currentPath + fileName  + File.separator);
						fileList.add(0,fileItem);
						Log.d("newfile",newFile.getAbsolutePath());
	                }else
	                	Toast.makeText(MainActivity.this,"�������ɹ���������������ȷ�����ظ����ļ���",Toast.LENGTH_SHORT).show();
					FileAdapter.fileSelected.clear();
					FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
				    listView.setAdapter(adapter);
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				    popupWindow.dismiss(); 
				   
				}
			} );
	 		dialog0.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
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
		 		
		case R.id.sortup_file:	
	 		//���� 
	        Collections.sort(fileList,new Comparator<FileItem>(){ 
	            public int compare(FileItem arg0, FileItem arg1) {
	            	if(arg0.getName().charAt(0) == arg1.getName().charAt(0) + 32
	            	|| arg1.getName().charAt(0) == arg0.getName().charAt(0) + 32){
	            		return arg0.getName().compareTo(arg1.getName());
	            	}else{
	            		String str0 = arg0.getName().toLowerCase();
		            	String str1 = arg1.getName().toLowerCase();
	            		if(str0.getBytes().length == str0.length()&& str1.getBytes().length == str1.length()){            			
			            	return str0.compareTo(str1);
	            		}else if(str0.getBytes().length != str0.length()&& str1.getBytes().length != str1.length()){
			            	return str1.compareTo(str0);
	            		}else
	            			return str0.compareTo(str1);
	            		
	            		
	            		
	            		
	            		
	            		
	            		/*if(arg0.getName().getBytes().length == arg0.getName().length()&& 
	            				arg1.getName().getBytes().length == arg1.getName().length()){
	            			String str0 = arg0.getName().toLowerCase();
			            	String str1 = arg1.getName().toLowerCase();
			            	return str0.compareTo(str1);
	            		}else if(arg0.getName().getBytes().length == arg0.getName().length()&& 
	            				arg1.getName().getBytes().length == arg1.getName().length()){
	            			String str0 = arg0.getName().toLowerCase();
			            	String str1 = arg1.getName().toLowerCase();
			            	return str0.compareTo(str1);
	            		}*/
	            		
	            	}	
	             }
	        } );
	        FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
		    listView.setAdapter(adapter);
		    popupWindow.dismiss(); 
	 		break; 		
		 	
		case R.id.sortdown_file:
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
	 		/*Collections.reverse(fileList);*/
	 		FileAdapter adapter1 = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
			listView.setAdapter(adapter1);
			popupWindow.dismiss(); 
	 		break; 	
	 		
	
		case R.id.more_function:
			View v1 = LayoutInflater.from(this).inflate(R.layout.activity_main,null);
			showPopupWindow(v1);
			break;
		
		case  R.id.copy_file:
			flag = 0;
			isMulChoice = false;
		    for(int i=0;i<fileList.size();i++){
		    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
		    }
		    optionLayout.setVisibility(View.GONE);
		    fileselectedNumber.setVisibility(View.GONE);
		    
			break;
			
		case R.id.crop_file:
			flag = 1;
			isMulChoice = false;
			for(FileItem fileSelected: FileAdapter.fileSelected)
			  	fileList.remove(fileSelected);				  		
			FileAdapter adapter7 = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
		    listView.setAdapter(adapter7);
			optionLayout.setVisibility(View.GONE);
		    fileselectedNumber.setVisibility(View.GONE);
			break;
		
					
		case R.id.paste_file:
			if(FileAdapter.fileSelected.size() == 0){
				Toast.makeText(MainActivity.this,"��������δѡ���ļ����в���",Toast.LENGTH_SHORT).show();
			}else{
				for(FileItem fileSelected: FileAdapter.fileSelected){
					if( (new File(fileSelected.getfilePath())).isDirectory()){
						copyFolder(fileSelected.getfilePath(),currentPath +  (fileSelected.getName()).toString(),fileSelected.getName());
						FileItem fileItem = new FileItem();
						fileItem.setImageId(R.drawable.file);
						fileItem.setName(fileSelected.getName());
						fileItem.setfilePath(currentPath + fileSelected.getName());
						fileList.add(0,fileItem);
					}
						
					if((new File(fileSelected.getfilePath())).isFile()){
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
				if(flag == 1){
					for(FileItem fileSelected: FileAdapter.fileSelected){
					  	File targetFile = new File(fileSelected.getfilePath());
					  	deleteFile(targetFile);			  		  
					} 	
				}
				Log.d("selest",FileAdapter.fileSelected.get(0).getfilePath());
				FileAdapter adapter4 = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
			    listView.setAdapter(adapter4);
			    FileAdapter.fileSelected.clear();
			    popupWindow.dismiss(); 
			    updateMemory();
			}	
			break; 
		case R.id.zip_file:
			if(FileAdapter.fileSelected.size() == 0){
				Toast.makeText(MainActivity.this,"��ѡ����Ҫѹ�����ļ��ٽ���ѹ��",Toast.LENGTH_SHORT).show();
			}else{
				isMulChoice = false;
		 		AlertDialog.Builder dialog4 = new AlertDialog.Builder(MainActivity.this);
		 		dialog4.setTitle("ѹ��");
		 		final View view4 = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
		 		final EditText editContent = (EditText)view4.findViewById(R.id.edit_content);
		 		editContent.setText(".zip");   //����EditText�ؼ�������
		 		editContent.setSelection(0);   //�����������ǰ��
		 		TextView editTip4 = (TextView)view4.findViewById(R.id.edit_tip);
		 		editTip4.setText("ѹ����");
		 		dialog4.setView(view4);
		 		dialog4.setCancelable(false);
		 		dialog4.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					    String fileName = editContent.getText().toString();
					    if (!(new File(currentPath + fileName).exists()) && !TextUtils.isEmpty(fileName) && !fileName.equals(".zip")){
					    	FileItem selectedFileItem = (FileItem)FileAdapter.fileSelected.get(0);
						    try{
						    	ZipUtil.zipFile(selectedFileItem.getfilePath(),currentPath + fileName);
						    	ContentValues values = new ContentValues();
				                values.put("name", fileName);
				                values.put("path",currentPath + fileName);
				                values.put("type","zip");
				                db.insert("File",null,values);
						    	Log.e("function","zip file");
						    	}catch(Exception e){
						    		 e.printStackTrace(); 
						    	}
						    FileItem fileItem = new FileItem();
							fileItem.setImageId(R.drawable.zip);
							fileItem.setName(fileName);
							fileItem.setfilePath(currentPath + fileName);
							fileList.add(0,fileItem);
					    }else{
					    	Toast.makeText(MainActivity.this,"ѹ�����ɹ���������������ȷ�����ظ����ļ���",Toast.LENGTH_SHORT).show();
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
		 		dialog4.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					
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
		case R.id.unzip_file:
			if(FileAdapter.fileSelected.size() == 0){
				Toast.makeText(MainActivity.this,"��ѡ����Ҫ��ѹ���ļ��ٽ��н�ѹ",Toast.LENGTH_SHORT).show();
			}else{
				isMulChoice = false;
		 		AlertDialog.Builder dialog5 = new AlertDialog.Builder(MainActivity.this);
		 		dialog5.setTitle("��ѹ");
		 		final View view5 = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
		 		TextView editTip5 = (TextView)view5.findViewById(R.id.edit_tip);
		 		editTip5.setText("��ѹ��");
		 		dialog5.setView(view5);
		 		dialog5.setCancelable(false);
		 		dialog5.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.d("function","delete file");
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
					    	Toast.makeText(MainActivity.this,"��ѹ���ɹ���������������ȷ�����ظ����ļ���",Toast.LENGTH_SHORT).show();
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
		 		dialog5.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					
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
			}
	 		break; 	
			
		case R.id.encrypt_file:
			if(FileAdapter.fileSelected.size() == 0){
				Toast.makeText(MainActivity.this,"��ѡ����Ҫ���ܵ��ļ����м���",Toast.LENGTH_SHORT).show();
			}else{
				isMulChoice = false;
		 		AlertDialog.Builder dialog6 = new AlertDialog.Builder(MainActivity.this);
		 		dialog6.setTitle("����Ϊ˽���ļ�");
		 		final View view6 = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
		 		TextView editTip6 = (TextView)view6.findViewById(R.id.edit_tip);
		 		editTip6.setText("����������");
		 		final EditText editContent1 = (EditText)view6.findViewById(R.id.edit_content);
		 		editContent1.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
		 		dialog6.setView(view6);
		 		dialog6.setCancelable(false);
		 		dialog6.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub			
					    String password = editContent1.getText().toString();
					    if (!password.equals("")){
					    	SharedPreferences.Editor editor = getSharedPreferences(FileAdapter.fileSelected.get(0).getName(),MODE_PRIVATE).edit();
					    	editor.putString("file",FileAdapter.fileSelected.get(0).getfilePath());
							editor.putString("password", password);
							editor.commit();
							if((new File(FileAdapter.fileSelected.get(0).getfilePath())).isFile()){
								db.delete("File", "path = ?",  new String[]{FileAdapter.fileSelected.get(0).getfilePath()});
							}else
								removelFileFromDB(FileAdapter.fileSelected.get(0).getfilePath());
							
							Log.d("file",FileAdapter.fileSelected.get(0).getName());
							Toast.makeText(MainActivity.this,"���óɹ�!",Toast.LENGTH_SHORT).show();
					    }else{
					    	Toast.makeText(MainActivity.this,"���벻��Ϊ��!",Toast.LENGTH_SHORT).show();
					    }
						FileAdapter.fileSelected.clear();
					    for(int i=0;i<fileList.size();i++){
					    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
					    }
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
					}
				} );
		 		dialog6.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					
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
	        // һ���Զ���Ĳ��֣���Ϊ��ʾ������
	        View contentView = LayoutInflater.from(this).inflate(
	                R.layout.pop_window, null);
	        popupWindow = new PopupWindow(contentView,
	        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
	        popupWindow.setTouchable(true);
	        // ʹ��ۼ�  
	        popupWindow.setFocusable(true);  
	        // ����������������ʧ  
	        popupWindow.setOutsideTouchable(true);  
	        // �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı���  
	        popupWindow.setBackgroundDrawable(new BitmapDrawable());      
	   /* popupWindow.setTouchInterceptor(new OnTouchListener()  {

	            @Override
	            public boolean onTouch(View v, MotionEvent event) {
					if (popupWindow != null && popupWindow.isShowing()) { 
						popupWindow.dismiss(); 
						popupWindow = null; 
					}
	                return false;
	                // �����������true�Ļ���touch�¼���������
	                // ���غ� PopupWindow��onTouchEvent�������ã���������ⲿ�����޷�dismiss
	            }
	        });*/

	        // ���������PopupWindow�ı����������ǵ���ⲿ������Back�����޷�dismiss����
	        // �Ҿ���������API��һ��bug
	        popupWindow.setBackgroundDrawable(getResources().getDrawable(
	                R.drawable.ppt)); 

	        // ���úò���֮����show
	       	popupWindow.showAtLocation(view, Gravity.TOP, 0, 0);  
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
	 
	 
	 
	 
	 public void copyFile(String oldPath, String newPath) {   
		 try {   
	         int byteSum = 0;   
	         int byteRead = 0;   
	         File oldfile = new File(oldPath);   
	         if (oldfile.exists()) { //�ļ�����ʱ   
	        	 InputStream input = new FileInputStream(oldPath); //����ԭ�ļ�   
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
	    	  Toast.makeText(this, "�����ļ���������", Toast.LENGTH_SHORT).show();
		      e.printStackTrace();   
	     }   
   }   

	 
	 public void copyFolder(String oldPath, String newPath,String folderName) {   
		 try {  
			 (new File(newPath)).mkdirs();    //�������ļ���Ŀ¼   
			  File[] file = (new File(oldPath)).listFiles(); 
	          for (int i = 0; i < file.length; i++) {  
	                if (file[i].isFile()) {        
	                	String sourceFile=file[i].getPath();           
	                	String targetFile = newPath + File.separator + file[i].getName();  
	                    copyFile(sourceFile,targetFile);  
	                 }  
	                if (file[i].isDirectory()) {    
	                   String path1=oldPath + File.separator + file[i].getName();  // ׼�����Ƶ�Դ�ļ��� 
	                   String path2=newPath + File.separator + file[i].getName();   // ׼�����Ƶ�Ŀ���ļ���   
	                   copyFolder(path1, path2,file[i].getName());  
	                }  
	               
	          } 
	   } catch (Exception e) {   
		   	Toast.makeText(this, "�����ļ��в�������", Toast.LENGTH_SHORT).show();   
	        e.printStackTrace();   
	      }   
    }  


	 
	 
	 protected void onActivityResult(int requestCode,int resultCode,Intent data){
		 switch(requestCode){
		 case 1:
			 mark = 1;
			 ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
			 List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1) ;
			 String a = (runningTaskInfos.get(0).topActivity).toString() ;
			 Log.d("debug",a);
			 break;
		default:
		 }
	 }
	 
	 
	 
	 
	 
	 
	 
	 
	 public void onBackPressed(){
		 
		 
		 if(isMulChoice == true){
			 isMulChoice = false;
			 FileAdapter.fileSelected.clear();
			    for(int i=0;i<fileList.size();i++){
			    	fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
			    }
			    optionLayout.setVisibility(View.GONE);
			    fileselectedNumber.setVisibility(View.GONE);
		 }else{
			 if(catalogIndex.size() > 1){
				 catalogIndex.remove(catalogIndex.get(catalogIndex.size()-1));
				 fileList = catalogIndex.get(catalogIndex.size()-1);
				 FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
				 listView = (ListView)findViewById(R.id.list_view);
				 listView.setAdapter(adapter);
				 Log.e("Back",currentPath);
				 currentPath = new File(currentPath).getParent() + File.separator;
				 File folder =new File(currentPath);
			 	 files = folder.listFiles(); 
			 	
			 }else if(catalogIndex.size() == 1){
				catalogIndex.clear();
				 finish(); 
			 }
		 }
		 
		 
		 	
			 
				 
		 
		
		 
	 }
	 
	 
	

}