package com.example.fileexplorer.util;

import java.io.File;
import java.text.DecimalFormat;

import android.os.Environment;
import android.os.StatFs;

public class MemoryCaculation {
	public int memoryProprtion;  //已用/总存储空间
	public String totalMemorySize;  //总存储空间的大小
	public String availableMemorySize;  //已使用的存储空间的大小
	public DecimalFormat decimalFormat=new DecimalFormat(".00");//精确到小数点两位
	
	public void MemorySizeCaculation(){
				File path = Environment.getExternalStorageDirectory();
				StatFs statfs = new StatFs(path.getPath());    //获取SD卡的文件路径
				float totalBlocks = statfs.getBlockCount();   //获取BLOCK数量
		        float availableBlock = statfs.getAvailableBlocks();   //己使用的Block的数量
		        float blocSize = statfs.getBlockSize();     //获取block的SIZE
		        totalMemorySize = fileSize(totalBlocks * blocSize);
		        availableMemorySize = fileSize(availableBlock * blocSize);
		        memoryProprtion = (int) ((totalBlocks - availableBlock)*100 / totalBlocks );
	}
	
	
	//返回表示存储空间大小的字符串
	public String fileSize(float size){
	       String str = "";
	       if(size >= 1024){
	           size = size /1024;
	           str = decimalFormat.format(size) + "KB";
	           if(size>=1024){
	               size = size /1024;
	               str = decimalFormat.format(size) + "MB";
	               if(size >= 1024){
	    	           size = size /1024;
	    	           str = decimalFormat.format(size) + "GB";
	               }
	            }
	       }
	       return str;
	 }
	
	
	
	
	
}
