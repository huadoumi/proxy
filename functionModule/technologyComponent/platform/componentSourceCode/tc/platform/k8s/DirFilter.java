/**
 * 
 */
package tc.platform.k8s;

import java.io.File;
import java.io.FileFilter;

/**
 * @author 13570
 *
 */
public class DirFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}

}
