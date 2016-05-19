package com.example.fileexplorer.file;

import java.io.File;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.fileexplorer.R;


public class FileTypeAdapter extends ArrayAdapter<FileItem>{
	
	private int resourceId;
	private List<FileItem> fileList;    //�ļ��б�
	private static Context context;
	
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
		fileImage.setImageResource(file.getImageId());   //����ͼƬ
		fileName.setText(file.getName());   //�����ļ�����
		
		//����¼������ļ�
		view.setOnClickListener(new OnClickListener() {   
			public void onClick(View v) {
				File file = new File(fileList.get(position).getfilePath()); 
				openTypeFile(file);
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
