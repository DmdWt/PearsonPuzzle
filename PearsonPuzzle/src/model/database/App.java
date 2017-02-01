package model.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;


public class App
{ 
	
//	public static void main (String args[]){
//		App app = new App();
//		Vector<String> filevec= new Vector<String>();
//		filevec.add("test.txt");
//		filevec.add("test2.txt");
//		
//		App.zipIt(filevec, "C:\\java");
//		app.unZipIt("C:\\java\\exportdatei.zip", "C:\\java\\outputfolder");
//	}
    public static boolean zipIt( Vector<String> fileNames, String diskplace )
    {
    	byte[] buffer = new byte[1048576];
    	

    	
    	String outputStream= diskplace + File.separator + "exportdatei.zip";

    	
    	try{

    		FileOutputStream fos = new FileOutputStream(outputStream);
    		ZipOutputStream zos = new ZipOutputStream(fos);
    		for(int index = 0; index<fileNames.size();index++){
    			String inputStream= diskplace+File.separator + fileNames.get(index); 
    			ZipEntry ze= new ZipEntry(fileNames.elementAt(index) );
    			zos.putNextEntry(ze);
    			FileInputStream in = new FileInputStream(inputStream);

    		int len;
    		while ((len = in.read(buffer)) > 0) {
    			zos.write(buffer, 0, len);
    			
    		}

    		in.close();
    		zos.closeEntry();
    		// gezipte files löschen
    		File file = new File(inputStream);
			file.delete();
    		}
    		
    		//remember close it
    		zos.close();

    		return true;
    	}catch(IOException ex){
    	   ex.printStackTrace();
    	   return false;
    	}
    }
    

    public App( )
    {    }

    /**
     * Unzip it
     * @param zipFile input zip file
     * @param output zip file output folder
     */
    public Vector<String> unZipIt(String zipFile, String outputFolder){
    	
    	Vector<String> dataNames = new Vector<String>();

     byte[] buffer = new byte[1048576];

     try{

    	//create output directory if not exists
    	File folder = new File(outputFolder);
    	if(!folder.exists()){
    		folder.mkdir();
    	}

    	//get the zip file content
    	ZipInputStream zis = new ZipInputStream(new FileInputStream(outputFolder + File.separator + zipFile));
    	
    	//get the zipped file list entry
    	ZipEntry ze = zis.getNextEntry();

    	while(ze!=null){

    	   String fileName = ze.getName();
           File newFile = new File(outputFolder + File.separator + fileName);

           System.out.println("file unzip : "+ newFile.getName());

            //create all non exists folders
            //else you will hit FileNotFoundException for compressed folder
            new File(newFile.getParent()).mkdirs();

            FileOutputStream fos = new FileOutputStream(newFile);

            int len;
            while ((len = zis.read(buffer)) > 0) {
       		fos.write(buffer, 0, len);
            }
            
            dataNames.add(newFile.getName());
            fos.close();
            ze = zis.getNextEntry();
    	}

        zis.closeEntry();
    	zis.close();

    	System.out.println("Done");
    	return dataNames;
    	
    }catch(IOException ex){
       ex.printStackTrace();
       return dataNames;
    }
   }
}