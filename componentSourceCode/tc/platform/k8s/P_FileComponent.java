package tc.platform.k8s;

import java.io.File;
import java.io.IOException;

import cn.com.agree.afa.svc.javaengine.AppLogger;
import cn.com.agree.afa.svc.javaengine.TCResult;
import galaxy.ide.tech.cpt.Component;
import galaxy.ide.tech.cpt.ComponentGroup;
import galaxy.ide.tech.cpt.InParams;
import galaxy.ide.tech.cpt.Param;
import galaxy.ide.tech.cpt.Return;
import galaxy.ide.tech.cpt.Returns;

/**
 * 封装了FileUtils里面相关的操作，以组件的形式暴露
 * 
 * @date 2018-11-22 15:0:42
 */
@ComponentGroup(level = "平台", groupName = "文件相关的操作")
public class P_FileComponent {
	
	/**
	 * @category 删除文件
	 * @param path
	 *            入参|文件完整路径|{@link java.lang.String}
	 * @param deleteRoot
	 *            入参|如果此文件是目录，标识是否删除|boolean
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "path", comment = "文件完整路径", type = java.lang.String.class),
			@Param(name = "deleteRoot", comment = "如果此文件是目录，标识是否删除", type = boolean.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "删除文件", style = "判断型", type = "同步组件", comment = "删除文件", author = "13570", date = "2018-11-22 07:48:03")
	public static TCResult P_delete(String path, boolean deleteRoot) {
		File file = new File(path);
		FileUtils.delete(file, deleteRoot);
		return TCResult.newSuccessResult();
	}
	
	/**
	 * @category 解压aar包
	 * @param src
	 *            入参|src|{@link java.lang.String}
	 * @param dst
	 *            入参|dst|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "src", comment = "src", type = java.lang.String.class),
			@Param(name = "dst", comment = "dst", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "解压aar包", style = "判断型", type = "同步组件", comment = "解压aar包", author = "13570", date = "2018-11-22 07:54:00")
	public static TCResult P_unzip(String src, String dst) {
		File source = new File(src);
		File dest = new File(dst);
		try {
			FileUtils.unzip(source, dest, true);
		} catch (IOException e) {
			AppLogger.error(e);
		}
		return TCResult.newSuccessResult();
	}

	/**
	 * @category 复制文件
	 * @param src
	 *            入参|src|{@link java.lang.String}
	 * @param dst
	 *            入参|dst|{@link java.lang.String}
	 * @return 0 失败<br/>
	 * 		1 成功<br/>
	 */
	@InParams(param = { @Param(name = "src", comment = "src", type = java.lang.String.class),
			@Param(name = "dst", comment = "dst", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "复制文件", style = "判断型", type = "同步组件", author = "13570", date = "2018-11-22 07:56:58")
	public static TCResult P_copy(String src, String dst) {
		File source = new File(src);
		File dest = new File(dst);
		try {
			FileUtils.copy(source, dest);
		} catch (Exception e) {
			AppLogger.error(e);
		}
		return TCResult.newSuccessResult();
	}
	
	

}
