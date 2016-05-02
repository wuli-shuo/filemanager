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
	/** 
     * ѹ���ļ�(������Ŀ¼) 
     *  
     * @param baseDir 
     *            Ҫѹ�����ļ���(����·��) 
     * @param fileName 
     *            ������ļ�����(�����ļ�·��) 
     * @throws Exception 
     */  
   public static void zipFile(String baseDir, String fileName)throws Exception {  
	     List<File> fileList = getSubFiles(new File(baseDir));  
         ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileName));  
         zos.setEncoding("UTF-8");//���ñ���,����������������  
         ZipEntry ze = null;  
         byte[] buf = new byte[1024];  
         int readLen = 0;  
         for (int i = 0; i < fileList.size(); i++) {  
             File f = (File) fileList.get(i);  
             ze = new ZipEntry(getAbsFileName(baseDir, f));  
             ze.setSize(f.length());  
             ze.setTime(f.lastModified());  
             /*��ʼд��һ���µ�ZIP�ļ���Ŀ������ZipOutputStream����λ����Ŀ���ݵ���ʼλ��
             	Ϊд��zip entry��ǰ��׼���ķ���*/
             zos.putNextEntry(ze); 
             InputStream is = new BufferedInputStream(new FileInputStream(f));  
             while ((readLen = is.read(buf, 0, 1024)) != -1) {  
	              zos.write(buf, 0, readLen);  
             }  
	         is.close();  
       }  
      zos.closeEntry();  
      zos.close();  
	  }  
	
	   /** 
        * ������Ŀ¼��������һ���ļ��������·��������zip�ļ��е�·��. 
	    *  
        * @param baseDir 
        *            java.lang.String ��Ŀ¼ 
        * @param realFileName 
        *            java.io.File ʵ�ʵ��ļ��� 
        * @return ����ļ��� 
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
 
       /** 
	    * ȡ��ָ��Ŀ¼�µ������ļ��б�������Ŀ¼. 
	    *  
	    * @param baseDir 
	    *            File ָ����Ŀ¼ 
	    * @return ����java.io.File��List 
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
  
	   /** 
	    * ��ѹ������. ��ZIP_FILENAME�ļ���ѹ��ZIP_DIRĿ¼��. 
	    * @param zipFileName ZIP�ļ�����·�� 
	    * @param zipDir 
	    * @throws Exception 
	    */  
	    @SuppressWarnings("unchecked")  
	    public static void unZipFile(String zipFileName, String zipDir)throws Exception {  
        ZipFile zip = new ZipFile(zipFileName);  
        Enumeration<ZipEntry> en = zip.getEntries();  //��һ�����ݽṹ�õ���������
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
	    
	  /*  public static void main(String[] args) {  
	    	try {  
	    		// ZipToFile.zipFile("E:\\�����ܱ�", "E:\\�����ܱ�.zip");  
	    		ZipUtil.unZipFile("E:\\�����ܱ�.zip", "E:\\�����ܱ�2");  
        		} catch (Exception e) {  
        			e.printStackTrace();  
        		}  
	    }  */
}  

