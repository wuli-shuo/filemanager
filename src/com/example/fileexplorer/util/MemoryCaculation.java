package com.example.fileexplorer.util;

import java.io.File;
import java.text.DecimalFormat;

import android.os.Environment;
import android.os.StatFs;

public class MemoryCaculation {
	public int memoryProprtion;  //����/�ܴ洢�ռ�
	public String totalMemorySize;  //�ܴ洢�ռ�Ĵ�С
	public String availableMemorySize;  //��ʹ�õĴ洢�ռ�Ĵ�С
	public DecimalFormat decimalFormat=new DecimalFormat(".00");//��ȷ��С������λ
	
	public void MemorySizeCaculation(){
				File path = Environment.getExternalStorageDirectory();
				StatFs statfs = new StatFs(path.getPath());    //��ȡSD�����ļ�·��
				float totalBlocks = statfs.getBlockCount();   //��ȡBLOCK����
		        float availableBlock = statfs.getAvailableBlocks();   //��ʹ�õ�Block������
		        float blocSize = statfs.getBlockSize();     //��ȡblock��SIZE
		        totalMemorySize = fileSize(totalBlocks * blocSize);
		        availableMemorySize = fileSize(availableBlock * blocSize);
		        memoryProprtion = (int) ((totalBlocks - availableBlock)*100 / totalBlocks );
	}
	
	
	//���ر�ʾ�洢�ռ��С���ַ���
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
