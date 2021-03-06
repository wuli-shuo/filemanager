package com.example.fileexplorer.file;

import android.widget.CheckBox;

public class FileItem {
	private String name;   //文件名字
	private String filePath;   //文件路径
	private int imageId;    //显示的图片
	private boolean boxChecked;   //选择框的状态（是否被选择）
	private int boxVisible = CheckBox.INVISIBLE;   //选择框是否可见
	
	
	public String getName(){
		return name;
	}
	
	public int getImageId(){
		return imageId;
	}
	
	public boolean getboxChecked(){
		return boxChecked;
	}
	
	public int getboxVisible(){
		return boxVisible;
	}
	
	public String getfilePath(){
		return filePath;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setImageId(int imageId){
		this.imageId = imageId;
	}
	
	public void setboxChecked(boolean boxChecked){
		this.boxChecked = boxChecked;
	}
	
	public void setboxVisible(int boxVisible){
		this.boxVisible = boxVisible;
	}
	
	public void setfilePath(String filePath){
		this.filePath = filePath;
	}
	
}
