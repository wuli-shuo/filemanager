package com.example.fileexplorer.util;

import java.io.File;
import android.os.Environment;
import android.os.StatFs;

public class MemoryCaculation {
	public int memoryProprtion;  //已用/总存储空间
	public String totalMemorySize;  //总存储空间的大小
	public String availableMemorySize;  //已使用的存储空间的大小
	
	public void MemorySizeCaculation(){
				File path = Environment.getExternalStorageDirectory();
				StatFs statfs = new StatFs(path.getPath());    //获取SD卡的文件路径
				long totalBlocks = statfs.getBlockCount();   //获取BLOCK数量
		        long availableBlock = statfs.getAvailableBlocks();   //己使用的Block的数量
		        long blocSize = statfs.getBlockSize();     //获取block的SIZE
		        totalMemorySize = fileSize(totalBlocks * blocSize);
		        availableMemorySize = fileSize(availableBlock * blocSize);
		        memoryProprtion = (int) ((totalBlocks - availableBlock) / totalBlocks *100);
	}
	
	
	//返回表示存储空间大小的字符串
	public String fileSize(long size){
	       String str = "";
	       if(size >= 1024){
	           size = size /1024;
	           str = size + "KB";
	           if(size>=1024){
	               size = size /1024;
	               str = size + "MB";
	            }
	       }
	       return str;
	 }
	
	
	
	
	
}
