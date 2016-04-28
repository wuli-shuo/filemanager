package com.example.fileexplorer.file;

import java.util.ArrayList;
import java.util.List;
import com.example.fileexplorer.R;
import com.example.fileexplorer.activity.MainActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FileAdapter extends ArrayAdapter<FileItem>{
	
	private int resourceId;
	private Context context;
	private List<FileItem> fileList;    //�ļ��б�
	public static List<String> fileSelected = new ArrayList<String>();   //��ѡ����ļ���
	
	
	public FileAdapter(Context context,int textViewResourceId,List<FileItem> objects){
		super(context,textViewResourceId,objects);
		resourceId = textViewResourceId;
		fileList = objects;
		this.context = context;
		//����ǰ������������������ȷ������ȡ������ť���������ٴμ����б�
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
	
	@Override //ÿ�����������Ļ�ڵ�ʱ����ô˷���
	public View getView(final int position,View convertView,ViewGroup parent){
		FileItem file = getItem(position);  //��ȡ��ǰ���FileItemʵ��
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);  //Ϊ������ز���
		ImageView fileImage = (ImageView)view.findViewById(R.id.file_image);
		TextView fileName = (TextView)view.findViewById(R.id.file_name);
		final CheckBox checkBox = (CheckBox)view.findViewById(R.id.check_box);

		fileImage.setImageResource(file.getImageId());
		fileName.setText(file.getName());
		checkBox.setChecked(file.getboxChecked());
		checkBox.setVisibility(file.getboxVisible());
		
		//��������¼�����ʾ���ع���ѡ���ѡ���
		view.setOnLongClickListener(new OnLongClickListener(){
	       public boolean onLongClick(View v) {
	    	   MainActivity.isMulChoice = true;
	    	   fileSelected.clear();
	    	   MainActivity.optionLayout.setVisibility(View.VISIBLE);
	    	   MainActivity.fileselectedNumber.setVisibility(View.VISIBLE);
		       for(int i=0;i<fileList.size();i++){
		    	   fileList.get(i).setboxVisible(CheckBox.VISIBLE);
		        }
		       FileAdapter adapter = new FileAdapter(context,R.layout.file_item,fileList);
		       MainActivity.listView.setAdapter(adapter);
		       return true;
	       }
		
	 });
		
		//����¼���ѡ�л�ȡ��ѡ���ѡ��
		view.setOnClickListener(new OnClickListener() {   
			public void onClick(View v) {
				if(MainActivity.isMulChoice){
					if(checkBox.isChecked()){
						checkBox.setChecked(false);
						fileSelected.remove(fileList.get(position).getName());
					}else{
						checkBox.setChecked(true);
						fileSelected.add(fileList.get(position).getName());
					}
				MainActivity.fileselectedNumber.setText("��ѡ��"+fileSelected.size()+"��");
				}
			}
		});

		
		
		
		

		return view;
	}

}
