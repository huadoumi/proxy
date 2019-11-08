package tc.platform;

import java.io.File;
import java.io.FileFilter;

public class UnzipFileFilter implements FileFilter{
	
	/*
     * @see java.io.FileFilter#accept(java.io.File)
     */
    @Override
    public boolean accept(File pathname) {
    	//非aar包即是解压缩后的目录
        if (!pathname.getName().endsWith(".aar")) {
            return true;
        }
        return false;
    }

}
