package tc.platform.k8s;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.agree.afa.svc.javaengine.AppLogger;
import cn.com.agree.afa.svc.javaengine.TCResult;
import cn.com.agree.afa.svc.javaengine.context.JavaDict;
import galaxy.ide.tech.cpt.Component;
import galaxy.ide.tech.cpt.ComponentGroup;
import galaxy.ide.tech.cpt.InParams;
import galaxy.ide.tech.cpt.OutParams;
import galaxy.ide.tech.cpt.Param;
import galaxy.ide.tech.cpt.Return;
import galaxy.ide.tech.cpt.Returns;
import tc.platform.UnzipFileFilter;

/**
 * 初始化相关组件
 * 
 * @date 2018-11-22 14:45:17
 */
@ComponentGroup(level = "平台", groupName = "交易初始化或者设值相关的组件")
public class P_Init {

	private static final String AFA_HOME = PlatformConstants.AFA_HOME;

	/**
	 * @category 设置jsonDict
	 * @since jsonDict
	 *        出参|解释json之后把元素放到这个dict里面|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@OutParams(param = {
			@Param(name = "jsonDict", comment = "解释json之后把元素放到这个dict里面", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "设置jsonDict", style = "判断型", type = "同步组件", comment = "把一个jsonDict放到__REQ__里面", author = "13570", date = "2018-11-25 08:55:28")
	public static TCResult P_setJsonDict() {
		JavaDict jsonDict = new JavaDict();
		return TCResult.newSuccessResult(jsonDict);
	}

	/**
	 * @category 获取完整的tag
	 * @param tagDict
	 *            入参|tagDict|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @param templateDict
	 *            入参|templateDict|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "tagDict", comment = "tagDict", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class),
			@Param(name = "templateDict", comment = "templateDict", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "获取完整的tag", style = "判断型", type = "同步组件", comment = "获取完整的镜像和模版tag", author = "13570", date = "2018-11-24 02:28:25")
	public static TCResult P_concatTag(JavaDict tagDict, JavaDict templateDict) {

		String imageRegistryProperty = AFA_HOME + File.separator + "conf" + File.separator + "imageRegistry.properties";
		String registryString = FileUtils.readLine(imageRegistryProperty, "utf-8");
		int ix = registryString.indexOf('=');
		String registry = registryString.substring(ix + 1);
		registry = registry.trim();
		AppLogger.info("registry: " + registry);

		String tagRepository = tagDict.getStringItem("repository");
		String tagVersion = tagDict.getStringItem("version");
		tagDict.setItem("tag", registry + "/" + tagRepository + ":" + tagVersion);

		String templateRepository = templateDict.getStringItem("repository");
		String templateVersion = templateDict.getStringItem("version");
		templateDict.setItem("tag", registry + "/" + templateRepository + ":" + templateVersion);

		return TCResult.newSuccessResult();
	}

	/**
	 * @category 设置http响应flag
	 * @param flag
	 *            入参|flag|{@link java.lang.String}
	 * @since flag 出参|flag|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "flag", comment = "flag", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "flag", comment = "flag", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "设置http响应flag", style = "判断型", type = "同步组件", comment = "设置http响应flag", author = "13570", date = "2018-11-25 08:45:56")
	public static TCResult P_setHttpStatusFlag(String flag) {
		return TCResult.newSuccessResult(flag);
	}

	/**
	 * @category 清空打镜像的环境
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "清空打镜像的环境", style = "判断型", type = "同步组件", comment = "清空打镜像的环境", author = "13570", date = "2018-11-22 08:21:02")
	public static TCResult P_clearAll() {
		// TODO 加文件锁，保证独占性操作打镜像环境

		String workspace = AFA_HOME + File.separator + "temp" + File.separator + "imageFactory" + File.separator
				+ "afa5.0" + File.separator + "workspace";
		AppLogger.info("workspace: " + workspace);

		String aarDirStr = AFA_HOME + File.separator + "data" + File.separator + "proxy";
		AppLogger.info("aarDir: " + aarDirStr);

		File aarDir = new File(aarDirStr);

		File[] stuff = aarDir.listFiles(new UnzipFileFilter());
		P_FileComponent.P_delete(workspace, false);
		for (File stuffDir : stuff) {
			try {
				String canonicalPath = stuffDir.getCanonicalPath();
				AppLogger.debug("stuffDir: " + canonicalPath);
				P_FileComponent.P_delete(canonicalPath, true);
			} catch (IOException e) {
				AppLogger.error(e);
			}
		}

		return TCResult.newSuccessResult();
	}

	/**
	 * @category 解压缩aar包
	 * @param projectAarName
	 *            入参|项目aar包名称|{@link java.lang.String}
	 * @param shareAarName
	 *            入参|shareAar包名称|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "projectAarName", comment = "项目aar包名称", type = java.lang.String.class),
			@Param(name = "shareAarName", comment = "shareAar包名称", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "解压缩aar包", style = "判断型", type = "同步组件", comment = "解压缩此次上传的所有aar包", author = "13570", date = "2018-11-24 03:22:36")
	public static TCResult P_unzipAll(String projectAarName, String shareAarName) {
		String projectsZip = AFA_HOME + File.separator + "data" + File.separator + "proxy" + File.separator
				+ projectAarName;
		String projectsUnzip = projectsZip.substring(0, projectsZip.lastIndexOf('.'));
		AppLogger.info("projectsZip: " + projectsZip);
		AppLogger.info("projectsUnzip: " + projectsUnzip);

		String shareZip = AFA_HOME + File.separator + "data" + File.separator + "proxy" + File.separator + shareAarName;
		String shareUnzip = shareZip.substring(0, shareZip.lastIndexOf('.'));
		AppLogger.info("shareZip: " + shareZip);
		AppLogger.info("shareUnzip: " + shareUnzip);

		P_FileComponent.P_unzip(projectsZip, projectsUnzip);
		P_FileComponent.P_unzip(shareZip, shareUnzip);
		return TCResult.newSuccessResult();
	}

	/**
	 * @category 安置afa镜像所需要的应用层资源
	 * @param projectAarName
	 *            入参|projectAarName|{@link java.lang.String}
	 * @param shareAarName
	 *            入参|shareAarName|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "projectAarName", comment = "projectAarName", type = java.lang.String.class),
			@Param(name = "shareAarName", comment = "shareAarName", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "安置afa镜像所需要的应用层资源", style = "判断型", type = "同步组件", comment = "afa镜像所需要的应用层资源放置好", author = "13570", date = "2018-11-25 08:32:23")
	public static TCResult P_constructAll(String projectAarName, String shareAarName) {
		String projectsZip = AFA_HOME + File.separator + "data" + File.separator + "proxy" + File.separator
				+ projectAarName;
		String projectsUnzip = projectsZip.substring(0, projectsZip.lastIndexOf('.'));
		AppLogger.info("projectsUnzip: " + projectsUnzip);

		String shareZip = AFA_HOME + File.separator + "data" + File.separator + "proxy" + File.separator + shareAarName;
		String shareUnzip = shareZip.substring(0, shareZip.lastIndexOf('.'));
		AppLogger.info("shareUnzip: " + shareUnzip);

		String workspace = AFA_HOME + File.separator + "temp" + File.separator + "imageFactory" + File.separator
				+ "afa5.0" + File.separator + "workspace";
		AppLogger.info("workspace: " + workspace);


		P_FileComponent.P_copy(projectsUnzip, workspace);
		P_FileComponent.P_copy(shareUnzip, workspace);
		return TCResult.newSuccessResult();
	}

	/**
	 * @category 拼接createImages的完整路径
	 * @since scriptPath 出参|脚本路径|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@OutParams(param = { @Param(name = "scriptPath", comment = "脚本路径", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "拼接createImages的完整路径", style = "判断型", type = "同步组件", comment = "拼接createImages脚本的完整路径", author = "13570", date = "2018-11-25 08:49:08")
	public static TCResult P_concatScriptPath() {
		String scriptPath = AFA_HOME + File.separator + "temp" + File.separator + "imageFactory" + File.separator
				+ "createImages";
		return TCResult.newSuccessResult(scriptPath);
	}

	/**
	 * @category 获取k8s的证书路径
	 * @param host
	 *            入参|k8s集群的master节点|{@link java.lang.String}
	 * @param serviceCode
	 *            入参|服务名|{@link java.lang.String}
	 * @since certPath 出参|cert路径|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "host", comment = "k8s集群的master节点", type = java.lang.String.class),
			@Param(name = "serviceCode", comment = "服务名", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "certPath", comment = "cert路径", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "获取k8s的证书路径", style = "判断型", type = "同步组件", comment = "获取k8s的证书路径", author = "13570", date = "2018-11-22 09:58:48")
	public static TCResult P_getK8sCert(String host, String serviceCode) {
		// TODO 如果不在微服务目录下，则在config目录下找到对应的资源
		String backupCert = AFA_HOME + File.separator + "conf" + File.separator + "apiserver-kubelet-client.p12";
		return TCResult.newSuccessResult(backupCert);
	}

	/**
	 * @category 删除解压缩目录
	 * @param projectAarName
	 *            入参|projectAarName|{@link java.lang.String}
	 * @param shareAarName
	 *            入参|shareAarName|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "projectAarName", comment = "projectAarName", type = java.lang.String.class),
			@Param(name = "shareAarName", comment = "shareAarName", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "删除解压缩目录", style = "判断型", type = "同步组件", comment = "删除projects和share的解压缩目录", author = "13570", date = "2018-11-24 03:45:27")
	public static TCResult P_deleteUnzipFiles(String projectAarName, String shareAarName) {
		String projectsZip = AFA_HOME + File.separator + "data" + File.separator + "proxy" + File.separator
				+ projectAarName;
		String projectsUnzip = projectsZip.substring(0, projectsZip.lastIndexOf('.'));
		AppLogger.info("projectsUnzip: " + projectsUnzip);
		String shareZip = AFA_HOME + File.separator + "data" + File.separator + "proxy" + File.separator + shareAarName;
		String shareUnzip = shareZip.substring(0, shareZip.lastIndexOf('.'));
		AppLogger.info("shareUnzip: " + shareUnzip);

		P_FileComponent.P_delete(projectsUnzip, true);
		P_FileComponent.P_delete(shareUnzip, true);
		return TCResult.newSuccessResult();
	}

	/**
	 * @category 设置响应dict
	 * @param status
	 *            入参|状态|{@link java.lang.String}
	 * @param reason
	 *            入参|原因|{@link java.lang.String}
	 * @param __RSP__
	 *            入参|__RSP__|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @param response
	 *            入参|响应报文|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "status", comment = "状态", type = java.lang.String.class),
			@Param(name = "reason", comment = "原因", type = java.lang.String.class),
			@Param(name = "__RSP__", comment = "__RSP__", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class),
			@Param(name = "response", comment = "响应报文", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "设置响应dict", style = "判断型", type = "同步组件", comment = "设置响应dict", author = "13570", date = "2018-11-25 07:50:44")
	public static TCResult P_setRspDict(String status, String reason, JavaDict __RSP__, String response) {
		JavaDict rspDict = __RSP__.getDictItem("rspDict");
		if (rspDict == null) {
			rspDict = new JavaDict();
			__RSP__.setItem("rspDict", rspDict);
		}

		if ("success".equals(status)) {
			if (response != null) {
				rspDict.setItem("response", response);
			}
			rspDict.setItem("status", "success");
		} else {
			rspDict.setItem("status", "failure");
			rspDict.setItem("reason", reason);
		}

		return TCResult.newSuccessResult();
	}

	/**
	 * @category 获取上传资源路径
	 * @param resourceName
	 *            入参|资源名称|{@link java.lang.String}
	 * @since resourcePath 出参|资源完整路径|{@link java.lang.String}
	 * @return 0 失败<br/>
	 * 		1 成功<br/>
	 */
	@InParams(param = { @Param(name = "resourceName", comment = "资源名称", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "resourcePath", comment = "资源完整路径", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "获取上传资源路径", style = "判断型", type = "同步组件", comment = "获取上传资源完整路径", author = "13570", date = "2018-11-25 09:37:12")
	public static TCResult P_getUploadResourcePath(String resourceName) {
		String resourceZip = AFA_HOME + File.separator + "data" + File.separator + "proxy" + File.separator + resourceName;
		AppLogger.info("resourceZip: " + resourceZip);
		return TCResult.newSuccessResult(resourceZip);
	}
	
	public static String getCurrentTime(String format) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	} 

}
