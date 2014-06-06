package com.christopher_colahan.file_utils.UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

public class UI {
	
	private static String path = "";
	private static boolean keepRaw = true;
	
	private static JFileChooser chooser;
	
	public static void main(String args[]) {
		getSettings();
		
		final JFrame frame = new JFrame("File Utilities");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = new Dimension(screenSize.width/2, screenSize.height/2);
		frame.setPreferredSize(windowSize);
		frame.setLocation(screenSize.width/2 - windowSize.width/2, screenSize.height/2 - windowSize.height/2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		
		frame.setLayout(new BorderLayout());
		
		JPanel settingsPanel = new JPanel();
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
		
		settingsPanel.setLayout(new BorderLayout());
		settingsPanel.add(BorderLayout.CENTER, new JLabel(path));
		JButton changePath = new JButton("Change path");
		changePath.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chooser = new JFileChooser(File.listRoots()[0]);
				FileFilter filter = new FileFilter() {

					@Override
					public boolean accept(File arg0) {
						return (arg0 != null && arg0.exists() && arg0.isDirectory());
					}

					@Override
					public String getDescription() {
						return "Directories";
					}
				};
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(frame);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	setSettings(chooser.getSelectedFile().getAbsolutePath(), keepRaw);
			    }
			}
		});
		settingsPanel.add(BorderLayout.SOUTH, changePath);
		
		final JCheckBox setKeepRaw = new JCheckBox("Keep Raw Files");
		setKeepRaw.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setSettings(path, setKeepRaw.isSelected());
			}
		});
		setKeepRaw.setSelected(keepRaw);
		
		settingsPanel.add(BorderLayout.NORTH, setKeepRaw);
		
		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		
		JLabel title = new JLabel("<html><h2><u>FILE UTILITIES SETTINGS</u></h2></html>");
		title.setVerticalAlignment(SwingConstants.CENTER);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		center.add(BorderLayout.NORTH, title);
		center.add(BorderLayout.CENTER, settingsPanel);
		center.add(BorderLayout.SOUTH, new JLabel("<html><p>Created by Chistopher Colahan. Software provided AS_IS and without warranty. You may freely use and re-distribute this software.</p></html>"));
		
		frame.add(BorderLayout.CENTER, center);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void getSettings() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("res/splitter_config.txt"));
			String tmp;
			while((tmp = reader.readLine()) != null) {
				
				if(tmp.trim().startsWith("#")) continue;	//comment
				if(tmp.trim().equals("")) continue;			//empty line
				
				if(tmp.startsWith("path")) {
					try {
						String[] tmpArray = tmp.split("=");
						path = tmpArray[tmpArray.length - 1].trim();
					} catch(Exception e) {
						reader.close();
						return;
					}
				}
				if(tmp.startsWith("keep_raw")) {
					try {
						String[] tmpArray = tmp.split("=");
						keepRaw = Boolean.parseBoolean(tmpArray[tmpArray.length - 1].trim());
					} catch(Exception e) {
						reader.close();
						return;
					}
				}
			}
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setSettings(String newPath, boolean newKeepRaw) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("res/splitter_config.txt"));
			writer.write("#the directory where you keep the files\n");
			writer.write("path=" + newPath);
			writer.write("\n");
			writer.write("#if you want to keep the original files or not\n");
			writer.write("keep_raw=" + newKeepRaw);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}