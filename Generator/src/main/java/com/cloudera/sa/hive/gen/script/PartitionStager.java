package com.cloudera.sa.hive.gen.script;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class PartitionStager {


	
	
	/**
	 * 
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		String stage = args[0];
		String tableName = args[1];
		String rootPath = "/user/hive/warehouse";
		if (args.length > 1) {
			rootPath = args[2];
		}
		
		Configuration config = new Configuration();
		FileSystem hdfs = FileSystem.get(config);
		
		Path rootFolder = new Path(rootPath + "/" + tableName);
		
		System.out.println("----------------------------- Starting " + stage);
		if (stage.equals("ePrep")) {
			Path tempRootFolder = new Path(rootPath + "/" + tableName + Const.EXISTING_TEMP_POST_DIR_NAME);
			moveTableDir(hdfs, rootFolder, tempRootFolder, true);
		}else if (stage.equals("dPrep")) {
			Path tempRootFolder = new Path(rootPath + "/" + tableName + Const.DELTA_TEMP_POST_DIR_NAME);
			moveTableDir(hdfs, rootFolder, tempRootFolder, false);
		}else if (stage.equals("cleanup")){
			Path tempRootFolder = new Path(rootPath + "/" + tableName + Const.EXISTING_TEMP_POST_DIR_NAME);
			System.out.println("Deleting " + tempRootFolder);
			hdfs.delete(tempRootFolder, true);
			
			tempRootFolder = new Path(rootPath + "/" + tableName + Const.DELTA_TEMP_POST_DIR_NAME);
			System.out.println("Deleting " + tempRootFolder);
			hdfs.delete(tempRootFolder, true);
		}
		
		hdfs.close();
		System.out.println("----------------------------- Finished " + stage);
	}


	private static void moveTableDir(FileSystem hdfs, Path rootFolder,
			Path tempRootFolder, boolean remakeTableDirector) throws IOException, FileNotFoundException {
		System.out.println();
		System.out.println("Moving " + rootFolder + " to " + tempRootFolder);
		hdfs.rename(rootFolder, tempRootFolder);
		System.out.println("Making " + rootFolder);
		
		System.out.println(hdfs.listStatus(tempRootFolder).length + " files is " + tempRootFolder);
		
		
		if (remakeTableDirector) {
			hdfs.mkdirs(rootFolder);
			System.out.println(hdfs.listStatus(rootFolder).length + " files is " + rootFolder);
		}
		
	}

}
