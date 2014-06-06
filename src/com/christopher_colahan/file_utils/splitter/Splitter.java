package com.christopher_colahan.file_utils.splitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.swing.JOptionPane;

public class Splitter {
	public static void main(String args[]) {
		run();
	}
	
	public static void run() {
		File file = new File("res/splitter_config.txt");
		
		if(! file.exists()) {
			error("configuration file " + file.getPath() + " does not exist!");
			return;
		}
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String tmp;
			String path = null;
			boolean keepRaw = false;
			while((tmp = reader.readLine()) != null) {
				
				if(tmp.trim().startsWith("#")) continue;	//comment
				if(tmp.trim().equals("")) continue;			//empty line
				
				if(tmp.startsWith("path")) {
					try {
						String[] tmpArray = tmp.split("=");
						path = tmpArray[tmpArray.length - 1].trim();
					} catch(Exception e) {
						error("splitter_config.txt malformatted. Error details -> " + e.getMessage());
						reader.close();
						return;
					}
				}
				if(tmp.startsWith("keep_raw")) {
					try {
						String[] tmpArray = tmp.split("=");
						keepRaw = Boolean.parseBoolean(tmpArray[tmpArray.length - 1].trim());
					} catch(Exception e) {
						error("splitter_config.txt malformatted. Error details -> " + e.getMessage());
						reader.close();
						return;
					}
				}
			}
			reader.close();
			
			path.replaceAll("\\\\", "/");
			
			File pathFile = new File(path);
			if(! pathFile.exists()) {
				error("Path directory (" + pathFile.getPath() + ") does not exist!");
				return;
			}
			if(! pathFile.isDirectory()) {
				error("Path (" + pathFile.getPath() + ") is not a directory!");
				return;
			}
			if(! pathFile.canRead()) {
				error("No read permissions! (" + pathFile.getPath() + ")");
				return;
			}
			if(! pathFile.canWrite()) {
				error("No write permissions! (" + pathFile.getPath() + ")");
				return;
			}
			
			//copy files
			for(File f : pathFile.listFiles()) {
				if(f.isDirectory()) continue;
				
				String name = f.getName();
				String ending = "";
				if(name.contains(".")) {
					String[] split = name.split("\\.");
					ending = split[split.length - 1];
				}
				else {
					ending = "-";
				}
				
				File tmpFile = new File(f.getParent() + "/" + ending + "/");
				if(! tmpFile.exists()) {
					tmpFile.mkdir();
				}
				copy(f,new File(tmpFile.getPath() + "/" + f.getName()));
				if(! keepRaw)
					f.delete();
			}
		} catch (FileNotFoundException e) {
			error(e.getMessage());
			return;
		} catch (IOException e) {
			error(e.getMessage());
			return;
		}
	}
	
	public static void error(String messege) {
		JOptionPane.showMessageDialog(null, messege, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static void copy(File source, File dest) throws IOException {
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			inputChannel = new FileInputStream(source).getChannel();
		    outputChannel = new FileOutputStream(dest).getChannel();
		    outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
		} finally {
		    inputChannel.close();
		    outputChannel.close();
		}
	}
}