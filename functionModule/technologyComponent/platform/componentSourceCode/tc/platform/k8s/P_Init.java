package tc.platform.k8s;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;

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

	public static String getNativeRegistryHostName() {
		String registry = EnvUtils.getEnv("IMAGE_REGISTRY_DOMAIN", "registry.agree.com.cn").trim();
		AppLogger.info("registry: " + registry);
		return registry;
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
			@Param(name = "response", comment = "响应报文", type = java.lang.Object.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "设置响应dict", style = "判断型", type = "同步组件", comment = "设置响应dict", author = "13570", date = "2018-11-25 07:50:44")
	public static TCResult P_setRspDict(String status, String reason, JavaDict __RSP__, Object response) {
		JavaDict rspDict = __RSP__.getDictItem("rspDict");
		if (rspDict == null) {
			rspDict = new JavaDict();
			__RSP__.setItem("rspDict", rspDict);
		}

		if ("success".equals(status)) {
			if (response != null) {
				rspDict.setItem("response", response.toString()); // 返回toString, 兼容rpc返回值为对象的情况
			}
			rspDict.setItem("status", "success");
		} else {
			rspDict.setItem("status", "failure");
			rspDict.setItem("reason", reason);
		}

		return TCResult.newSuccessResult();
	}

	public static String getCurrentTime(String format) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}

	private static boolean isNullOrEmpty(String valueStr) {
		if (valueStr == null || valueStr.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @category 设置debug结果
	 * @param __RSP__
	 *            入参|__RSP__|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @param reason
	 *            入参|失败原因|{@link java.lang.String}
	 * @since status 出参|状态|{@link java.lang.String}
	 * @since reason 出参|原因|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "__RSP__", comment = "__RSP__", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class),
			@Param(name = "reason", comment = "失败原因", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "status", comment = "状态", type = java.lang.String.class),
			@Param(name = "reason", comment = "原因", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "设置debug结果", style = "判断型", type = "同步组件", comment = "设置debug结果", author = "13570", date = "2019-01-06 10:01:07")
	public static TCResult P_setDebugResult(JavaDict __RSP__, String reason) {
		return TCResult.newSuccessResult("failure", reason);
	}

	/**
	 * @category 设置成功debug结果
	 * @param __RSP__
	 *            入参|__RSP__|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "__RSP__", comment = "__RSP__", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "设置成功debug结果", style = "判断型", type = "同步组件", comment = "设置成功debug结果", author = "13570", date = "2019-01-06 09:59:08")
	public static TCResult P_setSuccessDebugResult(JavaDict __RSP__) {
		__RSP__.setItem("status", "success");
		return TCResult.newSuccessResult();
	}

	private static DockerClient dockerClient = null;
	private static String registryUrl = null;
	static {
		String cloudType = EnvUtils.getEnv("CLOUD_TYPE", "");
		DockerClientConfig config = null;
		if ("acaas".equalsIgnoreCase(cloudType)) {
			String dockerHost = "tcp://" + EnvUtils.getEnv("ACAAS_DOCKER_HOST", "acaas-dockerhost.agree:31375");
			String userName = EnvUtils.getEnv("ACAAS_USERNAME", "afademo");
			String passwd = EnvUtils.getEnv("ACAAS_PASSWD", "afademo123");
			registryUrl = EnvUtils.getEnv("ACAAS_REGISTRY_URL", "acaas-registry.agree:80") + "/" + userName;

			config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(dockerHost)
					.withRegistryUsername(userName).withRegistryPassword(passwd)
					.withRegistryUrl("http://" + registryUrl).withRegistryEmail("fake@dummy.com").build();
		} else {
			String dockerHost = "unix:///var/run/docker.sock";
			registryUrl = getNativeRegistryHostName();
			config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(dockerHost)
					.withRegistryUrl("https://" + registryUrl + ":443").build();
		}

		@SuppressWarnings("resource")
		DockerCmdExecFactory dockerCmdExecFactory = new NettyDockerCmdExecFactory().withConnectTimeout(3000);
		//DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory().withConnectTimeout(3000);
		dockerClient = DockerClientBuilder.getInstance(config).withDockerCmdExecFactory(dockerCmdExecFactory).build();
	}

	
	/**
	 * @category 生成镜像
	 * @param reqDict
	 *            入参|请求容器|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @return 0 失败<br/>
	 * 		1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "reqDict", comment = "请求容器", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "生成镜像", style = "判断型", type = "同步组件", comment = "生成镜像", author = "Hasee", date = "2019-04-18 11:34:02")
	public static TCResult P_createImg(JavaDict reqDict) {
		String makeImgPath = reqDict.getStringItem("upPath");
		try {
			genDockerFile(reqDict, registryUrl, makeImgPath);
		} catch (IOException e) {
			return TCResult.newFailureResult("err001", "不能生成Dockerfile\n" + e.getMessage());
		}

		String buildName = buildTagName(registryUrl, reqDict.getDictItem("tag"));
		try {
			String imgId = dockerClient.buildImageCmd(new File(makeImgPath)).withTag(buildName)
					.exec(new BuildImageResultCallback()).awaitImageId();
			dockerClient.pushImageCmd(buildName).exec(new PushImageResultCallback() {
				@Override
				public void onComplete() {
					super.onComplete();
					dockerClient.removeImageCmd(imgId).exec();
				}
			});
		} catch (Exception e) {
			return TCResult.newFailureResult("err002", "创建镜像失败\n" + e.getMessage());
		}
		return TCResult.newSuccessResult();
	}
	
	
	private static String buildTagName(String regUrl, JavaDict dict) {
		return regUrl + "/" + dict.getStringItem("repository") + ":" + dict.getStringItem("version");
	}

	/**
	 * @category 初始化打镜像环境
	 * @param projectAarName
	 *            入参|项目aar包名|{@link java.lang.String}
	 * @param shareAarName
	 *            入参|组件aar包名|{@link java.lang.String}
	 * @param resPath
	 *            入参|上传资源路径|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "projectAarName", comment = "项目aar包名", type = java.lang.String.class),
			@Param(name = "shareAarName", comment = "组件aar包名", type = java.lang.String.class),
			@Param(name = "resPath", comment = "上传资源路径", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "初始化打镜像环境", style = "判断型", type = "同步组件", comment = "初始化打镜像环境: 解压缩arr包, 删除aar包", author = "Hasee", date = "2019-03-05 02:57:45")
	public static TCResult P_createInit(String projectAarName, String shareAarName, String resPath) {

		String projectsZip = resPath + File.separator + projectAarName;
		String shareZip = resPath + File.separator + shareAarName;
		String toUnzip = resPath + File.separator + "workspace";

		P_FileComponent.P_unzip(projectsZip, toUnzip);
		P_FileComponent.P_unzip(shareZip, toUnzip);
		new File(projectsZip).delete();
		new File(shareZip).delete();
		return TCResult.newSuccessResult();
	}

	private static void genDockerFile(JavaDict reqDict, String registryUrl, String resPath) throws IOException {
		String dateValue = reqDict.getStringItem("date");
		if (isNullOrEmpty(dateValue)) {
			dateValue = "";
		}
		String description = reqDict.getStringItem("description");
		if (isNullOrEmpty(description)) {
			description = "";
		}
		String author = reqDict.getStringItem("author");
		if (isNullOrEmpty(author)) {
			author = "";
		}
		String serviceCode = reqDict.getStringItem("serviceCode");
		if (isNullOrEmpty(serviceCode)) {
			serviceCode = "";
		}
		String systemCode = reqDict.getStringItem("systemCode");
		if (isNullOrEmpty(systemCode)) {
			systemCode = "";
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileWriter(resPath + "/Dockerfile"));
			pw.println("FROM " + buildTagName(registryUrl, reqDict.getDictItem("imageTemplate")));
			pw.println("LABEL date=" + dateValue + "\\");
			pw.println("    description=" + description + "\\");
			pw.println("    author=" + author + "\\");
			pw.println("    serviceCode=" + serviceCode + "\\");
			pw.println("    systemCode=" + systemCode + "\\");
			pw.println("    archieve=afa" + "\\");
			pw.println("    \"image.afa.container.xml\"=\"" + getContextContent(resPath) + "\"");
			pw.println("ENV AFA_ENV k8s");
			pw.println("COPY workspace/ ${AFA_HOME}/workspace/");
			pw.print("CMD ${AFA_HOME}/bin/startup.sh");
			if (pw.checkError()) {
				throw new IOException("检查到printwriter流发生错误");
			}
		} finally {
			if (null != pw) {
				pw.close();
			}
		}
	}

	private static String getContextContent(String resPath) {
		String projects = resPath + File.separator + "workspace" + File.separator + "projects";
		File pjDir = new File(projects);
		FileFilter filter = new DirFilter();
		File[] pjs = pjDir.listFiles(filter);
		String apps = projects + File.separator + pjs[0].getName() + File.separator + "apps";
		File appsDir = new File(apps);
		File[] aps = appsDir.listFiles(filter);
		String contextFileName = apps + File.separator + aps[0].getName() + File.separator + "container.xml";
		return FileUtils.readAllContent(contextFileName, "utf-8", true);
	}

	/**
	 * @category 清理上传目录
	 * @param resPath
	 *            入参|上传目录|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "resPath", comment = "上传目录", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "清理上传目录", style = "判断型", type = "同步组件", comment = "清理上传目录", author = "Hasee", date = "2019-03-05 04:17:38")
	public static TCResult P_deleteRes(String resPath) {
		P_FileComponent.P_delete(resPath, true);
		return TCResult.newSuccessResult();
	}


}
