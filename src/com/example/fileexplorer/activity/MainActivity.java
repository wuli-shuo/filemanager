package com.example.fileexplorer.activity;

import java.io.File;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;


public class MainActivity extends Activity implements OnClickListener {
	
	private List<FileItem> fileList = new ArrayList<FileItem>();
	private String rootPath = "/mnt/sdcard/";
	public MemoryCaculation memoryCaculation = new MemoryCaculation();
	public static boolean isMulChoice = false;  //�Ƿ��ѡ
	public static TableLayout optionLayout;  //�ļ��������ܿ��
	public static TextView fileselectedNumber;
	public static ListView listView;     //�ļ��б�
	

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
		initView(rootPath);
		FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
		listView = (ListView)findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		
		
		//���ܰ�ťע������¼�
		Button newFile = (Button)findViewById(R.id.new_file);
		newFile.setOnClickListener(this);
		Button deleteFile = (Button)findViewById(R.id.delete_file);
		deleteFile.setOnClickListener(this);
		Button renameFile = (Button)findViewById(R.id.rename_file);
		renameFile.setOnClickListener(this);
		Button cropFile = (Button)findViewById(R.id.crop_file);
		cropFile.setOnClickListener(this);

		
		
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
	
	private void initView(String filePath){
			File folder =new File(filePath);
			File[] files = folder.listFiles();
			for(File file : files){
				FileItem fileItem = new FileItem();
				fileItem.setImageId(R.drawable.file);
				fileItem.setName(file.getName());
				fileList.add(fileItem);
				}
			
	}
	
	//���ܵ���¼�����ѡ�е��ļ����в���
	 public void onClick(View v) {
		 switch (v.getId()) {
		 	case R.id.new_file:
		 		
		 		//���� 
		        Collections.sort(fileList,new Comparator<FileItem>(){ 
		            public int compare(FileItem arg0, FileItem arg1) { 
		                return arg0.getName().compareTo(arg1.getName());
		                } 
		        });
		        Log.d("function","new file");
		        FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
			    listView.setAdapter(adapter);
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
						Log.d("function","delete file");
						FileAdapter.fileSelected.clear();
						FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
					    listView.setAdapter(adapter);
					    optionLayout.setVisibility(View.GONE);
					    fileselectedNumber.setVisibility(View.GONE);
					}
				} );
		 		dialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						FileAdapter.fileSelected.clear();
						FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
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
		 	case R.id.rename_file:
		 		isMulChoice = false;
		 		AlertDialog.Builder dialog1 = new AlertDialog.Builder(MainActivity.this);
		 		dialog1.setTitle("������");
		 		View view = LayoutInflater.from(this).inflate(R.layout.defined_dialog, null); 
		 		TextView editTip = (TextView)view.findViewById(R.id.edit_tip);
		 		editTip.setText("���������ļ���");
		 		dialog1.setView(view);
		 		dialog1.setCancelable(false);
		 		dialog1.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.d("function","delete file");
						
						/*FileAdapter.fileSelected.clear();
						FileAdapter adapter = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
					    listView.setAdapter(adapter);*/
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
		 	case R.id.crop_file:
		 		Log.d("function","crop file");
		 		Collections.reverse(fileList);
		 		Log.d("function","rename file");
		 		FileAdapter adapter1 = new FileAdapter(MainActivity.this,R.layout.file_item,fileList);
				listView.setAdapter(adapter1);
		 		break;
	        default:
	        	break;
	        }   
	    }

	
	

}