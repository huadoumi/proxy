/*
 * Copyright(C) 2013 Agree Corporation. All rights reserved.
 * 
 * Contributors:
 *     Agree Corporation - initial API and implementation
 */
package tc.platform.k8s;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.com.agree.afa.svc.javaengine.AppLogger;

/**
 *
 *
 * @author beanlam
 * @date 2017年8月22日 下午5:51:12
 * @version 1.0
 *
 */
class FileUtils {

	/**
	 * 文件锁最大等待时间
	 */
	private static final int FILE_LOCK_MAX_WAITING_TIME = 10 * 1000;

	/**
	 * 目录限定过滤器
	 */
	public static final FileFilter DIR_REQUIRED = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};

	/**
	 * 文件限定过滤器
	 */
	public static final FileFilter FILE_REQUIRED = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isFile();
		}
	};

	private FileUtils() {
		throw new UnsupportedOperationException("Unable to initialize a util class");
	}

	public static String toNames(List<File> files) {
		if (files.isEmpty()) {
			return "[]";
		}
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < files.size(); i++) {
			if (i == 0) {
				buf.append("[");
			}

			buf.append(files.get(i).getName());

			if (i == files.size() - 1) {
				buf.append("]");
			} else {
				buf.append(", ");
			}
		}
		return buf.toString();
	}

	public static void deleteFilesInDirectory(File directory, String nameSuffix) {
		if (!directory.isDirectory()) {
			return;
		}

		for (File file : directory.listFiles(FileUtils.FILE_REQUIRED)) {
			if (nameSuffix != null) {
				if (file.getName().endsWith(nameSuffix)) {
					file.delete();
				}
			} else {
				file.delete();
			}
		}
	}

	public static void deleteFilesInDirectory(File directory) {
		if (!directory.isDirectory()) {
			return;
		}

		for (File file : directory.listFiles(FileUtils.FILE_REQUIRED)) {
			if (file.isFile()) {
				file.delete();
			}
		}
	}

	public static void deleteDirectoriesInDirectory(File directory) {
		if (!directory.isDirectory()) {
			return;
		}

		for (File dir : directory.listFiles(FileUtils.DIR_REQUIRED)) {
			delete(dir, true);
		}
	}

	public static void copyFilesInDirectory(File source, File dest) throws IOException {
		if (!source.isDirectory() || !dest.isDirectory()) {
			return;
		}

		for (File file : source.listFiles(FILE_REQUIRED)) {
			File destFile = new File(dest, file.getName());
			copyFile(file, destFile);
		}
	}

	public static void copyDirectoriesInDirectory(File source, File dest) throws Exception {
		if (!source.isDirectory() || !dest.isDirectory()) {
			return;
		}

		for (File dir : source.listFiles(DIR_REQUIRED)) {
			File destDir = new File(dest, dir.getName());
			copyDir(dir, destDir);
		}
	}

	/**
	 * 检查指定目录是否存在足够的磁盘空间
	 *
	 * @param baseExpectedSize
	 *            期望空间大小
	 * @param dest
	 *            被检查目录
	 * @param rate
	 *            期望空间大小的倍数
	 * @return
	 */
	public static boolean checkIfDiskSpaceEnough(long baseExpectedSize, File dest, int rate) {
		long available = dest.getUsableSpace();
		if (available == 0L) {
			return false;
		}

		if (available < rate * baseExpectedSize) {
			return false;
		}

		return true;
	}

	/**
	 * 保存字节数组到文件中
	 *
	 * @param dest
	 * @param content
	 * @throws IOException
	 */
	public static void save(File dest, byte[] content) throws IOException {

		if (!dest.exists()) {
			dest.getParentFile().mkdirs();
		}

		RandomAccessFile raf = null;
		FileLock writeLock = null;
		try {
			raf = new RandomAccessFile(dest, "rw");
			writeLock = raf.getChannel().lock();
			raf.seek(raf.length());
			raf.write(content);
		} finally {
			if (writeLock != null) {
				try {
					writeLock.release();
				} catch (IOException e) {
				}
			}
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * zip 解压实现
	 *
	 * @param source
	 * @param destPath
	 * @param mkDirsIfNotExisted
	 * @throws IOException
	 */
	public static void unzip(File source, File destPath, boolean mkDirsIfNotExisted) throws IOException {
		if (!destPath.exists()) {
			if (mkDirsIfNotExisted) {
				destPath.mkdirs();
			} else {
				throw new FileNotFoundException(destPath.getCanonicalPath() + " does not exist");
			}
		}

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(source);
			for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements();) {
				ZipEntry entry = entries.nextElement();
				String zipEntryName = entry.getName();
				InputStream in = null;
				OutputStream out = null;
				String outPath = (destPath.getPath() + File.separator + zipEntryName);
				File outFile = new File(outPath);

				if (outPath.endsWith("/")) {
					if (!outFile.exists()) {
						outFile.mkdirs();
					}
					continue;
				} else if (!outFile.getParentFile().exists()) {
					outFile.getParentFile().mkdirs();
				}

				try {
					in = zipFile.getInputStream(entry);
					out = new FileOutputStream(outPath);
					byte[] buffer = new byte[1024];
					int count = -1;
					while ((count = in.read(buffer)) != -1) {
						out.write(buffer, 0, count);
					}
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (Exception e) {
						}
					}

					if (out != null) {
						try {
							out.close();
						} catch (Exception e) {

						}
					}
				}
			}
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static void copy(File source, File dest, boolean override) throws Exception {
		if (dest.exists() && dest.isDirectory() && override) {
			delete(dest, false);
		}
		copy(source, dest);
	}

	public static void copy(File source, File dest) throws Exception {
		if (source.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdirs();
			}
			copyDir(source, dest);
		} else {
			if (!dest.getParentFile().exists()) {
				dest.getParentFile().mkdirs();
			}
			copyFile(source, dest);
		}

	}

	private static void copyDir(File source, File dest) throws Exception {
		if (!dest.exists()) {
			dest.mkdir();
		}
		for (File file : source.listFiles()) {
			if (file.isDirectory()) {
				copyDir(file, new File(dest, file.getName()));
			} else {
				copyFile(file, new File(dest, file.getName()));
			}
		}
	}

	public static void copyFile(File source, File dest) throws IOException {
		if (!dest.exists()) {
			dest.createNewFile();
		}
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);
			int count = -1;
			byte[] buffer = new byte[8196];
			while ((count = in.read(buffer)) != -1) {
				out.write(buffer, 0, count);
			}
			out.flush();
		} catch (IOException e) {
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void delete(File toDelete) {
		if (!toDelete.exists()) {
			return;
		}
		if (toDelete.isDirectory()) {
			for (File file : toDelete.listFiles()) {
				delete(file);
			}
			if (toDelete.listFiles().length == 0) {
				toDelete.delete();
			}
		} else {
			toDelete.delete();
		}
	}

	/**
	 * deleteRoot 决定toDelete目录是否删除
	 * 
	 */
	public static void delete(File toDelete, boolean deleteRoot) {

		if (!toDelete.exists()) {
			return;
		}

		if (toDelete.isDirectory()) {
			for (File file : toDelete.listFiles()) {
				delete(file, true);
			}
			if (toDelete.listFiles().length == 0) {
				if (deleteRoot == true) {
					toDelete.delete();
				}
			}
		} else {
			toDelete.delete();
		}
	}

	/**
	 * 获取文件内容
	 * 
	 * @return 配置数据源
	 * @throws IOException
	 *             读取文件异常
	 * @throws LockStolenException
	 *             获取配置文件共享锁失败
	 */
	public static byte[] getFileContent(String path) throws IOException {
		RandomAccessFile raf = null;
		FileLock lock = null;
		try {
			raf = new RandomAccessFile(path, "r");
			lock = getFileLock(raf);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len = -1;
			while ((len = raf.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			return baos.toByteArray();
		} finally {
			if (lock != null) {
				try {
					lock.release();
				} catch (IOException e) {
				}
			}
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 获取配置文件的共享锁
	 * 
	 * @param raf
	 * @return 如果在最大等待时间内取得锁，则返回锁实例，否则返回null
	 * @throws IOException
	 * @throws LockStolenException
	 */
	private static FileLock getFileLock(RandomAccessFile raf) throws IOException {
		FileLock lock = null;
		int sleepInterval = FILE_LOCK_MAX_WAITING_TIME / 100;
		long endTime = System.currentTimeMillis() + FILE_LOCK_MAX_WAITING_TIME;
		while (System.currentTimeMillis() < endTime) {
			try {
				lock = raf.getChannel().tryLock(0, raf.length(), true);
			} catch (OverlappingFileLockException e) {
			}
			if (lock == null) {
				try {
					Thread.sleep(sleepInterval);
				} catch (InterruptedException e) {
				}
			} else {
				break;
			}
		}

		if (lock == null) {
			throw new IOException("Lock has been stolen by another process");
		}

		return lock;
	}

	public static void updateContent(String filePath, byte[] content) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(filePath, false);
		try {
			fileOutputStream.write(content);
			fileOutputStream.flush();
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		}
	}

	/**
	 * 连接文件路径
	 * 
	 * @param paths
	 *            路径数组
	 * @return 连接后的路径
	 */
	public static String concat(String... paths) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			if (path.endsWith("\\") || path.endsWith("/")) {
				path = path.substring(0, path.length() - 1);
			}

			buf.append(path);
			if (i < paths.length - 1) {
				buf.append(File.separator);
			}
		}
		return buf.toString();
	}

	public static void writeContent(String filePath, byte[] content) throws IOException {
		File f = new File(filePath);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		if (!f.exists()) {
			f.createNewFile();
		}
		updateContent(filePath, content, false);
	}

	public static void updateContent(String filePath, byte[] content, boolean isAppend) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(filePath, isAppend);
		try {
			fileOutputStream.write(content);
			fileOutputStream.flush();
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		}
	}

	public static boolean hasSubFile(File file, boolean recursion) {
		if (file.isDirectory()) {
			if (file.listFiles(FILE_REQUIRED).length == 0) {
				if (recursion) {
					for (File dir : file.listFiles(DIR_REQUIRED)) {
						return hasSubFile(dir, recursion);
					}
				} else {
					return false;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	public static String readAllContent(String fileName, String charset, boolean exceptEmpty) {
		BufferedReader bufin = null;
		try {
			bufin = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charset));
			String lineInfo = null;
			StringBuilder sb = new StringBuilder();
			while ((lineInfo = bufin.readLine()) != null) {
				if (!lineInfo.isEmpty()) {
					if (exceptEmpty) {
						//context.xml的内容全部将双引号改为单引号
						lineInfo = lineInfo.replaceAll("\"", "'");
						lineInfo = lineInfo.trim();
						sb.append(lineInfo);
					} else {
						sb.append(lineInfo + '\n');
					}
				}
			}
			return sb.toString();
		} catch (Exception e) {
			AppLogger.error(e.getMessage());
			return null;
		} finally {
			if (bufin != null) {
				try {
					bufin.close();
				} catch (IOException e) {
					AppLogger.error(e);
				}
				bufin = null;
			}
		}
	}

	// public static void main(String[] args) {
	// File dir = new File("");
	// try {
	// System.out.println(dir.getCanonicalPath());
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// System.out.println(System.getProperty("user.dir"));
	// }
}
