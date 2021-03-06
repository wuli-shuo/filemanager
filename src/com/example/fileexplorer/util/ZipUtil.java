package com.example.fileexplorer.util;

import java.io.BufferedInputStream;  
import java.io.BufferedOutputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.InputStream;  
import java.util.ArrayList;  
import java.util.Enumeration;  
import java.util.List;  
import org.apache.tools.zip.ZipEntry;  
import org.apache.tools.zip.ZipFile;  
import org.apache.tools.zip.ZipOutputStream; 



public class ZipUtil {
	/*
     * 压缩文件(包括子目录)   
     */  
   public static void zipFile(String baseDir, String fileName)throws Exception {  
	     List<File>  fileList;
	     if(new File(baseDir).isDirectory()){  //文件夹，得出文件夹下的总文件
	    	 fileList = getSubFiles(new File(baseDir));  
	     }else{
	    	 fileList = new ArrayList<File>(); 
	    	 fileList.add(new File(baseDir)); 
	     }   	 
         ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileName));  
         zos.setEncoding("UTF-8"); //设置编码,解决中文乱码的问题  
         ZipEntry ze = null;  
         byte[] buf = new byte[1024];  
         int readLen = 0;  
         for (int i = 0; i < fileList.size(); i++) {  
             File f = (File) fileList.get(i);  
             ze = new ZipEntry(getAbsFileName(baseDir, f));  
             ze.setSize(f.length());  
             ze.setTime(f.lastModified());  
             /*开始写入一个新的ZIP文件条目，并把ZipOutputStream流定位到条目数据的起始位置
             	为写入zip entry做前期准备的方法*/
             zos.putNextEntry(ze); 
             InputStream is = new BufferedInputStream(new FileInputStream(f));  
             while ((readLen = is.read(buf, 0, 1024)) != -1) {  
	              zos.write(buf, 0, readLen);  
             }  
	         is.close();  
       }
      zos.flush();
      zos.closeEntry(); 
      zos.close(); 
	  }  
	
	   /*
        * 给定根目录，返回另一个文件名的相对路径，用于zip文件中的路径. 保证绝对路径发生变化还能解压
        */  
       private static String getAbsFileName(String baseDir, File realFileName) {  
    	   File real = realFileName;  
	       File base = new File(baseDir);  
           String ret = real.getName();  
	       while (true) {  
             real = real.getParentFile();  
             if (real == null)  
            	 break;  
             if (real.equals(base))  
            	 break;  
             else  
            	 ret = real.getName() + "/" + ret;  
	       }  
	       return ret;  
	   }  
 
       /*
	    * 取得指定目录下的所有文件列表，包括子目录.
	    */  
	    private static List<File> getSubFiles(File baseDir) {  
	       List<File> ret = new ArrayList<File>();  
	       File[] tmp = baseDir.listFiles();  
	       for (int i = 0; i < tmp.length; i++) {  
	    	   if (tmp[i].isFile()){  
	    		   ret.add(tmp[i]);  
	    	   }  
	    	   if (tmp[i].isDirectory()){  
	    		   ret.addAll(getSubFiles(tmp[i]));  
	           }  
	       }  
	       return ret;  
	   }  
  
	   /*
	    * 解压缩功能. 将ZIP_FILENAME文件解压到ZIP_DIR目录下.
	    */  
	    @SuppressWarnings("unchecked")  
	    public static void unZipFile(String zipFileName, String zipDir)throws Exception {  
        ZipFile zip = new ZipFile(zipFileName);  
        Enumeration<ZipEntry> en = zip.getEntries();  //从一个数据结构得到连续数据
        ZipEntry entry = null;  
        byte[] buffer = new byte[1024];  
        int length = -1;  
        InputStream input = null;  
        BufferedOutputStream bos = null;  
        File file = null;  
        while (en.hasMoreElements()) {  
        	entry = (ZipEntry) en.nextElement();  
        	if (entry.isDirectory()) {  
        		file = new File(zipDir, entry.getName());  
                if (!file.exists()) {  
                	file.mkdir();  
                }  
            continue;  
        	}  
            input = zip.getInputStream(entry);  
            file = new File(zipDir, entry.getName());  
            if (!file.getParentFile().exists()) {  
            	file.getParentFile().mkdirs();  
            }  
            bos = new BufferedOutputStream(new FileOutputStream(file));  
            while (true) {  
            	length = input.read(buffer);  
                if (length == -1){  
                	break;  
               }  
               bos.write(buffer, 0, length);  
            }  
           bos.close();  
           input.close();  
        	}  
        	zip.close();  
	    }  
}  

