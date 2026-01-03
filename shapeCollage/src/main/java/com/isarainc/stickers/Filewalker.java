package com.isarainc.stickers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Filewalker {
	private List<File>  files=new ArrayList<File>();
	private String path;
	
	public Filewalker(String path) {
		super();
		this.path = path;
		walk(path);
	}


	public List<File> getFiles() {
		return files;
	}


	private void walk( String path ) {
	
        File root = new File( path );
        File[] list = root.listFiles();

        if (list == null) return;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk( f.getAbsolutePath() );
               // System.out.println( "Dir:" + f.getAbsoluteFile() );
            }
            else {
            	files.add(f);
              //  System.out.println( "File:" + f.getAbsoluteFile() );
            }
        }
    }

}
