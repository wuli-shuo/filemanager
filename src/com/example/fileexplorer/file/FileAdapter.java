package com.example.fileexplorer.file;

import java.util.List;
import com.example.fileexplorer.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FileAdapter extends ArrayAdapter<FileItem>{
	
	private int resourceId;
	
	public FileAdapter(Context context,int textViewResourceId,List<FileItem> objects){
		super(context,textViewResourceId,objects);
		resourceId = textViewResourceId;
	}
	
	@Override //ÿ�����������Ļ�ڵ�ʱ����ô˷���
	public View getView(int position,View convertView,ViewGroup parent){
		FileItem file = getItem(position);  //��ȡ��ǰ���FileItemʵ��
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);  //Ϊ������ز���
		ImageView fileImage = (ImageView)view.findViewById(R.id.file_image);
		TextView fileName = (TextView)view.findViewById(R.id.file_name); 
		fileImage.setImageResource(file.getImageId());
		fileName.setText(file.getName());
		return view;
	}

}
