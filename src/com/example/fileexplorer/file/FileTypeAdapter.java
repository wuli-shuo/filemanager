package com.example.fileexplorer.file;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fileexplorer.R;
import com.example.fileexplorer.activity.MainActivity;

public class FileTypeAdapter extends ArrayAdapter<FileItem>{
	
	private int resourceId;
	private List<FileItem> fileList;    //�ļ��б�
	private static Context context;
	private int tag = 0;  //��־���������ж��ļ��Ƿ��Ѿ�����
	
	public FileTypeAdapter(Context context,int textViewResourceId,List<FileItem> objects){
		super(context,textViewResourceId,objects);
		resourceId = textViewResourceId;
		fileList = objects;
		this.context = context;
		
		for(int i=0;i<fileList.size();i++){
			fileList.get(i).setboxChecked(false);	
			 fileList.get(i).setboxVisible(CheckBox.INVISIBLE);
	    }

	}
	
	
	@Override //ÿ�����������Ļ�ڵ�ʱ����ô˷���
	public View getView(final int position,View convertView,ViewGroup parent){
		final FileItem file = getItem(position);  //��ȡ��ǰ���FileItemʵ��
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);  //Ϊ������ز���
		ImageView fileImage = (ImageView)view.findViewById(R.id.file_image);
		TextView fileName = (TextView)view.findViewById(R.id.file_name);
		fileImage.setImageResource(file.getImageId());
		fileName.setText(file.getName());
		
		//����¼�
		view.setOnClickListener(new OnClickListener() {   
			public void onClick(View v) {
				final SharedPreferences pref = context.getSharedPreferences(file.getName(),context.MODE_PRIVATE);
				String filePath = pref.getString("file","");
				if(filePath.equals(file.getfilePath()) && tag == 0){
					AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			 		dialog.setTitle("��˽���ļ�");
			 		final View view = LayoutInflater.from(context).inflate(R.layout.defined_dialog, null); 
			 		TextView editTip = (TextView)view.findViewById(R.id.edit_tip);
			 		editTip.setText("����������");
			 		dialog.setView(view);
			 		dialog.setCancelable(false);
			 		dialog.setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							EditText editContent = (EditText)view.findViewById(R.id.edit_content);
						    String input = editContent.getText().toString();
						    if (!input.equals("")){			    	
								String password = pref.getString("password", "");
								if(password.equals(input)){
									tag = 1;
									Toast.makeText(context,"������ȷ!",Toast.LENGTH_SHORT).show();
								}					
						    	else
						    		Toast.makeText(context,"�������!",Toast.LENGTH_SHORT).show();
							}
						    else
						    	Toast.makeText(context,"����������!",Toast.LENGTH_SHORT).show();					
						}	
						} );
			 		 dialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
						
						}
					});
			 		dialog.show();
				}
				else if(!filePath.equals(file.getfilePath()) || tag == 1){
					tag = 0;
					File file = new File(fileList.get(position).getfilePath()); 
					openTypeFile(file);
				}
		}
	});

	return view;
	}
	
	
	
	public void openTypeFile(File file){
		 Intent intent = new Intent(); 
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
	     intent.setAction(android.content.Intent.ACTION_VIEW); 
	      // ��ȡ�ļ�ý������    
	     String type = FileAdapter.getMIMEType(file);
	      if(type == null)  
		        return;  
	      intent.setDataAndType(Uri.fromFile(file), type);  
	      context.startActivity(intent);
		}  
	
	
	
	
	
}
