package com.example.fileexplorer.file;

import android.widget.CheckBox;

public class FileItem {
	private String name;
	private String filePath;
	private int imageId;
	private boolean boxChecked;
	private int boxVisible = CheckBox.INVISIBLE;
	
	
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
