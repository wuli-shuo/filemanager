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
	
	@Override //每个子项被滚到屏幕内的时候调用此方法
	public View getView(int position,View convertView,ViewGroup parent){
		FileItem file = getItem(position);  //获取当前项的FileItem实例
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);  //为子项加载布局
		ImageView fileImage = (ImageView)view.findViewById(R.id.file_image);
		TextView fileName = (TextView)view.findViewById(R.id.file_name); 
		fileImage.setImageResource(file.getImageId());
		fileName.setText(file.getName());
		return view;
	}

}
