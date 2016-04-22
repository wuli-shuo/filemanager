package com.example.fileexplorer.file;

public class FileItem {
	private String name;
	private int imageId;
	
	
	public String getName(){
		return name;
	}
	
	public int getImageId(){
		return imageId;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setImageId(int imageId){
		this.imageId = imageId;
	}
}
