package com.example.fileexplorer.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.example.fileexplorer.R;
import com.example.fileexplorer.file.FileAdapter;
import com.example.fileexplorer.file.FileItem;
import com.example.fileexplorer.util.MemoryCaculation;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
	public static boolean isMulChoice = false;  //�Ƿ��ѡ
	public static TableLayout optionLayout;  //�ļ��������ܿ��
	public static TextView fileselectedNumber;
	public static ListView listView;     //�ļ��б�
	public static String currentPath  = "/mnt/sdcard/";
	public static File[] files;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*new Thread(new Runnable(){
			public void run(){
				initView(rootPath);
			}
		}).start();	*/
		
		//��ʾ�ļ��б�
		initView(currentPath);
		FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
		listView = (ListView)findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		/*listView.setOnItemClickListener(this);  */
		
		
		//���ܰ�ťע������¼�
		Button renameFile = (Button)findViewById(R.id.rename_file);
		renameFile.setOnClickListener(this);
		Button deleteFile = (Button)findViewById(R.id.delete_file);
		deleteFile.setOnClickListener(this);
		Button encryptFile = (Button)findViewById(R.id.encrypt_file);
		encryptFile.setOnClickListener(this);
		Button zipFile = (Button)findViewById(R.id.zip_file);
		zipFile.setOnClickListener(this);
		
		
		
		
		Button moreFunction = (Button)findViewById(R.id.more_function);
		moreFunction.setOnClickListener(this);
		
		//�ļ���������ѡ����
		optionLayout = (TableLayout)findViewById(R.id.function_option);
		fileselectedNumber = (TextView)findViewById(R.id.fileselected_number);

		//��ʾ�洢���
		TextView memoryAvailable = (TextView)findViewById(R.id.memory_available);
		TextView memoryAll = (TextView)findViewById(R.id.memory_all);
		ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		memoryCaculation.MemorySizeCaculation();
		memoryAvailable.setText("���ã�" + memoryCaculation.availableMemorySize);
		memoryAll.setText("�ܹ���" + memoryCaculation.totalMemorySize);
		progressBar.setProgress(memoryCaculation.memoryProprtion);
						
	
	}
	
	public static void initView(String filePath){
			fileList = new ArrayList<FileItem>();
			File folder =new File(filePath);
			files = folder.listFiles();
			for(File file : files){
				FileItem fileItem = new FileItem();
				fileItem.setImageId(R.drawable.file);
				fileItem.setName(file.getName());
				fileItem.setfilePath(file.getAbsolutePath());
				fileList.add(fileItem);
				}
			catalogIndex.add(fileList);
	}
	
	//���ܵ���¼�����ѡ�е��ļ����в���
	 public void onClick(View v) {
		 switch (v.getId()) {
		/* case R.id.more_function:
		 		showPopupWindow(v);
		 		break; 		*/
		 case R.id.rename_file:
		 		isMulChoice = false;
		 		AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
		 		dialog1.setTitle("������");
		 		final View view1 = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
		 		TextView editTip1 = (TextView)view1.findViewById(R.id.edit_tip);
		 		editTip1.setText("���������ļ���");
		 		dialog1.setView(view1);
		 		dialog1.setCancelable(false);
		 		dialog1.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
					
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
		 		dialog1.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					
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
		 		dialog.setTitle("ɾ��");
		 		dialog.setMessage("ȷ��ɾ�����ļ�");
		 		dialog.setCancelable(false);
		 		dialog.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
					
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
		 		dialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					
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
	 		dialog0.setTitle("�½��ļ���");
	 		final View view = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
	 		TextView editTip = (TextView)view.findViewById(R.id.edit_tip);
	 		editTip.setText("�������½��ļ���");
	 		dialog0.setView(view);
	 		dialog0.setCancelable(false);
	 		dialog0.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Log.d("function","delete file");
					EditText editContent = (EditText)view.findViewById(R.id.edit_content);
				    String fileName = editContent.getText().toString();
				    File newFile = new File(currentPath + File.separator + fileName);  //ʵ����File
				    if (!newFile.exists()) {
				    	newFile.mkdir();    //�����ļ���
				    	FileItem fileItem = new FileItem();
						fileItem.setImageId(R.drawable.file);
						fileItem.setName(fileName);
						fileItem.setfilePath(newFile.getAbsolutePath());
						fileList.add(fileItem);
	                }
					FileAdapter.fileSelected.clear();
					FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
				    listView.setAdapter(adapter);
				    optionLayout.setVisibility(View.GONE);
				    fileselectedNumber.setVisibility(View.GONE);
				}
			} );
	 		dialog0.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
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
	 		//���� 
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
	 		
		case R.id.encrypt_file:
			isMulChoice = false;
			FileAdapter adapter3 = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
		    listView.setAdapter(adapter3);
			optionLayout.setVisibility(View.GONE);
		    fileselectedNumber.setVisibility(View.GONE);
		    
			break;
		
		case R.id.crop_file:
			break;
			
		case R.id.more_function:
			
			for(FileItem fileSelected: FileAdapter.fileSelected){
			/*	if(.isDirectory())
					copyFolder(fileSelected.getfilePath(),currentPath);
				if(fileSelected)
					copyFile(fileSelected.getfilePath(),currentPath);
			}*/copyFolder(fileSelected.getfilePath(),currentPath + File.separator +  fileSelected.getName(),fileSelected.getName());
			 }
			Toast.makeText(this,"OK  ", Toast.LENGTH_SHORT).show();
			FileAdapter adapter4 = new FileAdapter(MainActivity.this,R.layout.file_item,catalogIndex.get(catalogIndex.size()-1));
		    listView.setAdapter(adapter4);
		    FileAdapter.fileSelected.clear();
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

	        // һ���Զ���Ĳ��֣���Ϊ��ʾ������
	        View contentView = LayoutInflater.from(this).inflate(
	                R.layout.pop_window, null);
	        final PopupWindow popupWindow = new PopupWindow(contentView,
	                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
	        
	        
	        
	        
	        popupWindow.setTouchable(true);
	        // ʹ��ۼ�  
	        popupWindow.setFocusable(true);  
	        // ����������������ʧ  
	        popupWindow.setOutsideTouchable(true);  
	        // �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı���  
	        popupWindow.setBackgroundDrawable(new BitmapDrawable());  


	     /*   popupWindow.setTouchInterceptor(new OnTouchListener() {

	            @Override
	            public boolean onTouch(View v, MotionEvent event) {

	                Log.i("mengdd", "onTouch : ");

	                return false;
	                // �����������true�Ļ���touch�¼���������
	                // ���غ� PopupWindow��onTouchEvent�������ã���������ⲿ�����޷�dismiss
	            }
	        });

	        // ���������PopupWindow�ı����������ǵ���ⲿ������Back�����޷�dismiss����
	        // �Ҿ���������API��һ��bug
	        popupWindow.setBackgroundDrawable(getResources().getDrawable(
	                R.drawable.selectmenu_bg_downward));*/

	        // ���úò���֮����show
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