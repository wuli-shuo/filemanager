package com.example.fileexplorer.util;

import java.io.File;
import android.os.Environment;
import android.os.StatFs;

public class MemoryCaculation {
	public int memoryProprtion;  //����/�ܴ洢�ռ�
	public String totalMemorySize;  //�ܴ洢�ռ�Ĵ�С
	public String availableMemorySize;  //��ʹ�õĴ洢�ռ�Ĵ�С
	
	public void MemorySizeCaculation(){
				File path = Environment.getExternalStorageDirectory();
				StatFs statfs = new StatFs(path.getPath());    //��ȡSD�����ļ�·��
				long totalBlocks = statfs.getBlockCount();   //��ȡBLOCK����
		        long availableBlock = statfs.getAvailableBlocks();   //��ʹ�õ�Block������
		        long blocSize = statfs.getBlockSize();     //��ȡblock��SIZE
		        totalMemorySize = fileSize(totalBlocks * blocSize);
		        availableMemorySize = fileSize(availableBlock * blocSize);
		        memoryProprtion = (int) ((totalBlocks - availableBlock) / totalBlocks *100);
	}
	
	
	//���ر�ʾ�洢�ռ��С���ַ���
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
