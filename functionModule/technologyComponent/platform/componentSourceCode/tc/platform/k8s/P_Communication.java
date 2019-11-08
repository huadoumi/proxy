package tc.platform.k8s;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import cn.com.agree.afa.svc.javaengine.AppLogger;
import cn.com.agree.afa.svc.javaengine.TCResult;
import cn.com.agree.afa.svc.javaengine.context.JavaDict;
import cn.com.agree.afa.svc.javaengine.context.JavaList;
import cn.com.agree.afa.util.ExceptionUtils;
import galaxy.ide.tech.cpt.Component;
import galaxy.ide.tech.cpt.ComponentGroup;
import galaxy.ide.tech.cpt.InParams;
import galaxy.ide.tech.cpt.OutParams;
import galaxy.ide.tech.cpt.Param;
import galaxy.ide.tech.cpt.Return;
import galaxy.ide.tech.cpt.Returns;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Namespace;
import io.kubernetes.client.models.V1NamespaceList;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;
import tc.platform.P_Json;

/**
 * 通信，交互
 * 
 * @date 2018-11-22 21:42:36
 */
@ComponentGroup(level = "平台", groupName = "k8s,镜像仓库通信，交互")
public class P_Communication {

	private static CoreV1Api getAPIServerClient() {
		try {
			ApiClient client = Config.fromCluster();
			Class<CoreV1Api> clazz = CoreV1Api.class;
			CoreV1Api t = clazz.newInstance();
			PropertyDescriptor pd = new PropertyDescriptor("apiClient", clazz);
			pd.getWriteMethod().invoke(t, client);
			return t;
		} catch (Exception e) {
			AppLogger.error(e);
			return null;
		}
	}

	private static CloseableHttpClient getRegistryClient() {
		String cloudType = EnvUtils.getEnv("CLOUD_TYPE", "native");
		if ("native".equals(cloudType)) {
			try {
				// Trust everybody
				X509TrustManager tm = new X509TrustManager() {
					@Override
					public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
							throws CertificateException {
					}

					@Override
					public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
							throws CertificateException {
					}

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				};
				SSLContext sslCtx = SSLContext.getInstance("TLS");
				sslCtx.init(null, new TrustManager[] { tm }, null);
				SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslCtx);
				Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
						.register("https", sslConnectionFactory).register("http", new PlainConnectionSocketFactory())
						.build();
				PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager(registry);
				HttpClientBuilder hcb = HttpClientBuilder.create();
				hcb.setConnectionManager(pcm);
				CloseableHttpClient httpClient = hcb.build();
				return httpClient;
			} catch (Exception e) {
				AppLogger.error(e);
				return null;
			}
		} else {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			return httpclient;
		}
	}

	private static String getPodsInfo(CoreV1Api v1Api, String namespace, String selector) {

		ApiException apiException = null;
		V1PodList list = null;
		try {
			list = v1Api.listNamespacedPod(namespace, null, null, null, null, selector, null, null, null, null);
		} catch (ApiException e) {
			apiException = e;
		}

		JavaDict rsp = new JavaDict();
		JavaList podList = new JavaList();
		rsp.setItem("pods", podList);

		if (list != null) {
			rsp.setItem("status", "success");
			for (V1Pod v1pod : list.getItems()) {
				JavaDict podDict = new JavaDict();
				podDict.setItem("podId", v1pod.getMetadata().getName());
				podDict.setItem("hostIp", v1pod.getStatus().getHostIP());
				podDict.setItem("podIp", v1pod.getStatus().getPodIP());
				podList.add(podDict);
			}
		} else {
			rsp.setItem("status", "failure");
			rsp.setItem("reason", ExceptionUtils.toDetailString(apiException));
		}

		return (String) P_Json.dictToStr(rsp).getOutputParams().get(0);

	}

	/**
	 * @category 查询pod信息
	 * @param namespace
	 *            入参|namespace|{@link java.lang.String}
	 * @param serviceCode
	 *            入参|serviceCode|{@link java.lang.String}
	 * @since pods 出参|pod的信息|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "namespace", comment = "namespace", type = java.lang.String.class),
			@Param(name = "serviceCode", comment = "serviceCode", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "pods", comment = "pod的信息", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "查询pod信息", style = "判断型", type = "同步组件", comment = "查询pod信息", author = "Hasee", date = "2018-11-30 05:27:40")
	public static TCResult P_getPod(String namespace, String serviceCode) {
		CoreV1Api v1Api = getAPIServerClient();
		String labelSelector = null;
		if (null != serviceCode) {
			String cloudType = EnvUtils.getEnv("CLOUD_TYPE", "native");
			if ("native".equals(cloudType)) {
				labelSelector = "serviceCode=" + serviceCode;
			} else {
				labelSelector = "agree-app-name=" + serviceCode;
			}
		}
		String resultInJson = getPodsInfo(v1Api, namespace, labelSelector);
		return TCResult.newSuccessResult(resultInJson);
	}

	/**
	 * @category 获取所有namespaces
	 * @since responseStr 出参|作为响应的json字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@OutParams(param = { @Param(name = "responseStr", comment = "作为响应的json字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "获取所有namespaces", style = "判断型", type = "同步组件", comment = "获取所有namespaces", author = "Hasee", date = "2018-11-30 05:02:05")
	public static TCResult P_getNamespaces() {
		JavaDict respDict = new JavaDict();
		try {
			JavaList names = new JavaList();
			String cloudType = EnvUtils.getEnv("CLOUD_TYPE", "native");
			AppLogger.info("cloudType: " + cloudType);
			if ("native".equals(cloudType)) {
				V1NamespaceList namespaceList = getAPIServerClient().listNamespace(null, null, null, null, null, null,
						null, null, null);
				for (V1Namespace namespace : namespaceList.getItems()) {
					names.add(namespace.getMetadata().getName());
				}
			} else {
				//acaas目前以username作为k8s的namespace, 以后可能会改
				names.add(EnvUtils.getEnv("ACAAS_USERNAME", "afademo"));
			}
			respDict.setItem("status", "success");
			respDict.setItem("namespaces", names);
		} catch (ApiException e) {
			AppLogger.info(e.toString());
			respDict.setItem("status", "failure");
			respDict.setItem("reason", e.getMessage());
		}
		return TCResult.newSuccessResult((String) P_Json.dictToStr(respDict).getOutputParams().get(0));
	}

	/**
	 * @category 获取镜像信息
	 * @since imageInfo 出参|json格式的镜像信息|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@OutParams(param = { @Param(name = "imageInfo", comment = "json格式的镜像信息", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "获取镜像信息", style = "判断型", type = "同步组件", comment = "获取镜像信息", author = "Hasee", date = "2018-11-30 05:24:50")
	public static TCResult P_getImagesInfo() {
		CloseableHttpClient registryClient = getRegistryClient();
		try {
			String cloudType = EnvUtils.getEnv("CLOUD_TYPE", "native");
			String str = null;
			if ("native".equals(cloudType)) {
				str = getImageInfo0V2(P_Init.getNativeRegistryHostName(), registryClient);
			} else {
				str = getImageInfo0V1(registryClient);
			}
			return TCResult.newSuccessResult(str);
		} catch (Exception e) {
			AppLogger.error(e);
			return TCResult.newSuccessResult();
		} finally {
			if (registryClient != null) {
				try {
					registryClient.close();
				} catch (IOException e) {
					AppLogger.error(e);
				}
			}
		}
	}

	// 传统版本使用
	private static String getImageInfo0V2(String registry, CloseableHttpClient httpClient) throws Exception {
		JavaDict result = new JavaDict();

		HttpGet repoHttpReq = new HttpGet("https://" + registry + "/v2/_catalog");
		CloseableHttpResponse repoHttpRsp = httpClient.execute(repoHttpReq);
		String repoListStr = EntityUtils.toString(repoHttpRsp.getEntity());
		if (repoHttpRsp != null) {
			try {
				repoHttpRsp.close();
			} catch (IOException e) {
				AppLogger.error(e);
				throw e;
			}
		}
		JavaDict repoDict = new JavaDict();
		P_Json.strToDict(repoDict, repoListStr);

		JavaList repoErrInfo = (JavaList) repoDict.get("errors");
		if (repoErrInfo != null && repoErrInfo.size() > 0) {
			result.put("status", "failure");
			result.put("reason", ((JavaDict) repoErrInfo.get(0)).getStringItem("message"));
		} else {

			JavaList repoList = repoDict.getListItem("repositories");
			JavaList templateLists = new JavaList();
			result.put("imageTemplates", templateLists);

			if (null != repoDict && repoList.size() > 0) {
				boolean allFind = true;
				for (Object repo : repoList) {
					String repoStr = (String) repo;
					if (Pattern.matches("agree/.+/platform/.+", repoStr)) {
						HttpGet get2 = new HttpGet("https://" + registry + "/v2/" + repoStr + "/tags/list");
						CloseableHttpResponse resp2 = httpClient.execute(get2);
						String tagListStr = EntityUtils.toString(resp2.getEntity());
						if (resp2 != null) {
							try {
								resp2.close();
							} catch (IOException e) {
								AppLogger.error(e);
								throw e;
							}
						}
						JavaDict tagDict = new JavaDict();
						P_Json.strToDict(tagDict, tagListStr);
						JavaList tagErrInfo = (JavaList) tagDict.get("errors");
						if (tagErrInfo != null && tagErrInfo.size() > 0) {
							allFind = false;
						} else {
							JavaDict templateDict = new JavaDict();
							templateLists.add(templateDict);
							JavaDict versionDict = new JavaDict();
							templateDict.setItem("tag", versionDict);
							versionDict.setItem("repository", repoStr);
							versionDict.setItem("versions", tagDict.getListItem("tags"));
						}
					}
				}
				if (allFind) {
					result.put("status", "success");
				} else {
					result.put("status", "failure");
					result.put("reason", "query fail on some tag");
				}
			}
		}

		return (String) P_Json.dictToStr(result).getOutputParams().get(0);
	}

	// Acaas版本使用
	private static String getImageInfo0V1(CloseableHttpClient httpClient) throws Exception {
		String ip = EnvUtils.getEnv("ACAAS_SERVER_ADDR", "acaas-server.kube-system:50002");

		HttpPost repoHttpost = new HttpPost("http://" + ip
				+ "/servlets/CloudService/acms/trade/HarborCsd/HarborRepositoryCSD/GetUserRepoListController/GetUserRepoListController.csd");
		RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).setSocketTimeout(30000).build();
		repoHttpost.setConfig(defaultRequestConfig);

		String username = EnvUtils.getEnv("ACAAS_USERNAME", "afademo");
		String passwd = EnvUtils.getEnv("ACAAS_PASSWD", "afademo123");
		String projectId = EnvUtils.getEnv("ACAAS_PROJECT_ID", "20");

		JavaDict jsonMap = new JavaDict();
		JavaDict xmMap = new JavaDict();
		JavaDict userInfoMap = new JavaDict();
		userInfoMap.put("UserName", username);
		userInfoMap.put("PassWord", passwd);
		userInfoMap.put("ProjectId", Integer.valueOf(projectId));
		xmMap.put("Type", "list");
		jsonMap.put("XM", xmMap);
		jsonMap.put("InArgs", userInfoMap);
		String json = (String) P_Json.dictToStr(jsonMap).getOutputParams().get(0);

		HttpEntity entity = new StringEntity(json, "utf-8");
		repoHttpost.setEntity(entity);

		CloseableHttpResponse response = null;
		String responseInJson;
		try {
			response = httpClient.execute(repoHttpost);
			responseInJson = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
		} finally {
			if (response != null) {
				response.close();
			}
		}
		
		JavaDict responseDict = new JavaDict();
		P_Json.strToDict(responseDict, responseInJson);
		JavaList result = ((JavaList) ((JavaDict) ((JavaDict) responseDict.get("OutArgs")).get("OutArgsBean"))
				.get("result"));
		JavaList repoNames = new JavaList();
		for (Object r : result) {
			JavaDict repoInfo = (JavaDict) r;
			String repoName = (String) repoInfo.get("repo_name");
			if (Pattern.matches(".+/agree/.+/platform/.+", repoName)) {
				repoNames.add(repoName);
			}
		}

		JavaDict imageJsonMap = new JavaDict();
		JavaDict imageXmMap = new JavaDict();
		imageXmMap.put("Type", "select");
		imageJsonMap.put("XM", imageXmMap);
		JavaDict resultDict = new JavaDict();
		JavaList templateLists = new JavaList();
		resultDict.put("imageTemplates", templateLists);
		for (Object rn : repoNames) {
			String repoName = (String) rn;
			JavaList versions = new JavaList();
			JavaDict tagDict = new JavaDict();
			tagDict.put("repository", repoName.substring(repoName.indexOf("/")+1));
			tagDict.put("versions", versions);
			JavaDict versionDict = new JavaDict();
			versionDict.put("tag", tagDict);
			HttpPost imageHttpost = new HttpPost("http://" + ip
					+ "/servlets/CloudService/acms/trade/HarborCsd/HarborRepositoryCSD/HarborRepositoryCSD.csd");
			imageHttpost.setConfig(defaultRequestConfig);

			JavaDict imageInfoMap = new JavaDict();
			imageInfoMap.put("RepositoryName", repoName);
			imageJsonMap.put("InArgs", imageInfoMap);
			String imageJson = (String) P_Json.dictToStr(imageJsonMap).getOutputParams().get(0);
			HttpEntity imagEntity = new StringEntity(imageJson, "utf-8");
			imageHttpost.setEntity(imagEntity);
			CloseableHttpResponse imageResponse = null;
			String imageResponseInJson = null;
			try {
				imageResponse = httpClient.execute(imageHttpost);
				imageResponseInJson = EntityUtils.toString(imageResponse.getEntity(), Charset.forName("utf-8"));
			} finally {
				if (imageResponse != null) {
					imageResponse.close();
				}
			}

			JavaDict imageReponseDict = new JavaDict();
			P_Json.strToDict(imageReponseDict, imageResponseInJson);
			JavaList imageResult = ((JavaList) ((JavaDict) ((JavaDict) imageReponseDict.get("OutArgs"))
					.get("OutArgsBean")).get("result"));
			for (Object i : imageResult) {
				JavaDict imageInfo = (JavaDict) i;
				String tag = (String) imageInfo.get("tag");
				versions.add(tag);
			}
			templateLists.add(versionDict);
		}
		resultDict.put("status", "success");
		return (String) P_Json.dictToStr(resultDict).getOutputParams().get(0);

	}

	/**
	 * @category 获取镜像的tag列表
	 * @param imageName
	 *            入参|镜像名字|{@link java.lang.String}
	 * @since respStr 出参|响应json字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "imageName", comment = "镜像名字", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "respStr", comment = "响应json字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "获取镜像的tag列表", style = "判断型", type = "同步组件", comment = "获取镜像的tag列表", author = "Hasee", date = "2018-11-30 11:01:08")
	public static TCResult P_getTagList(String imageName) {
		CloseableHttpClient registryClient = getRegistryClient();

		try {
			String cloudType = EnvUtils.getEnv("CLOUD_TYPE", "native");
			String result = null;
			if ("native".equals(cloudType)) {
				result = getTagList0(P_Init.getNativeRegistryHostName(), registryClient, imageName);
			} else {
				result = getTagList1(registryClient, imageName);
			}
			return TCResult.newSuccessResult(result);
		} catch (Exception e) {
			AppLogger.error(e);
			return TCResult.newFailureResult("error", e);
		} finally {
			if (registryClient != null) {
				try {
					registryClient.close();
				} catch (IOException e) {
					AppLogger.error(e);
				}
			}
		}
	}

	// 传统使用
	private static String getTagList0(String registry, CloseableHttpClient httpClient, String imageName)
			throws IOException {
		HttpGet get = new HttpGet("https://" + registry + "/v2/" + imageName + "/tags/list");
		CloseableHttpResponse resp = httpClient.execute(get);
		String tagLitStr = EntityUtils.toString(resp.getEntity());
		if (resp != null) {
			try {
				resp.close();
			} catch (IOException e) {
				AppLogger.error(e);
				throw e;
			}
		}
		JavaDict respDict = new JavaDict();
		respDict.put("imageName", imageName);
		JavaDict tagJSONObj = new JavaDict();
		P_Json.strToDict(tagJSONObj, tagLitStr);
		JavaList errInfo = (JavaList) tagJSONObj.get("errors");
		if (errInfo != null && errInfo.size() > 0) {
			String reason = ((JavaDict) errInfo.get(0)).getStringItem("message");
			if ("repository name not known to registry".equals(reason)) {
				// 镜像还没有打
				respDict.put("status", "success");
				respDict.put("tags", new JavaList());
			} else {
				respDict.put("status", "failure");
				respDict.put("reason", ((JavaDict) errInfo.get(0)).getStringItem("message"));
			}
		} else {
			respDict.put("status", "success");
			respDict.put("tags", tagJSONObj.getListItem("tags"));
		}
		return (String) P_Json.dictToStr(respDict).getOutputParams().get(0);
	}

	// Acaas使用
	private static String getTagList1(CloseableHttpClient httpClient, String repoName) throws IOException {
		String ip = EnvUtils.getEnv("ACAAS_SERVER_ADDR", "acaas-server.kube-system:50002");
		String domain = EnvUtils.getEnv("ACAAS_USERNAME", "afademo");
		
		HttpPost imageHttpost = new HttpPost("http://" + ip
				+ "/servlets/CloudService/acms/trade/HarborCsd/HarborRepositoryCSD/HarborRepositoryCSD.csd");
		RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).setSocketTimeout(30000).build();
		imageHttpost.setConfig(defaultRequestConfig);
		JavaDict imageJsonMap = new JavaDict();
		JavaDict imageXmMap = new JavaDict();
		imageXmMap.put("Type", "select");
		imageJsonMap.put("XM", imageXmMap);
		JavaDict imageInfoMap = new JavaDict();
		imageInfoMap.put("RepositoryName",  domain + "/" + repoName);
		imageJsonMap.put("InArgs", imageInfoMap);
		String imageJson = (String) P_Json.dictToStr(imageJsonMap).getOutputParams().get(0);
		
		HttpEntity imagEntity = new StringEntity(imageJson, "utf-8");
		imageHttpost.setEntity(imagEntity);
		CloseableHttpResponse imageResponse = null;
		String responseInJson;
		try {
			imageResponse = httpClient.execute(imageHttpost);
			responseInJson = EntityUtils.toString(imageResponse.getEntity(), Charset.forName("utf-8"));
		} finally {
			if (imageResponse != null) {
				imageResponse.close();
			}
		}

		JavaDict imageReponseDict = new JavaDict();
		JavaList versions = new JavaList();
		P_Json.strToDict(imageReponseDict, responseInJson);
		JavaDict outArgsBean = (JavaDict) ((JavaDict) imageReponseDict.get("OutArgs")).get("OutArgsBean");
		JavaDict resultDict = new JavaDict();
		resultDict.put("imageName", repoName);
		if (outArgsBean.isEmpty()) {
			resultDict.put("status", "success");
			resultDict.put("tags", new JavaList());
		} else {
			JavaList imageResult = (JavaList) outArgsBean.get("result");
			for (Object i : imageResult) {
				JavaDict imageInfo = (JavaDict) i;
				String tag = (String) imageInfo.get("tag");
				versions.add(tag);
			}
			resultDict.put("status", "success");
			resultDict.put("tags", versions);
		}
		return (String) P_Json.beanToStr(resultDict).getOutputParams().get(0);
	}

	// 多线程版本
	/**
	 * @throws InterruptedException
	 * @category 上传资源
	 * @param podInfoList
	 *            入参|pod信息列表|{@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param aarPath
	 *            入参|上传资源包的完整路径|{@link java.lang.String}
	 * @param restart
	 *            入参|是否重启|{@link java.lang.String}
	 * @param autostart
	 *            入参|是否自启|{@link java.lang.String}
	 * @param groupName
	 *            入参|工作组名称|{@link java.lang.String}
	 * @since rspDict
	 *        出参|rspDict|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	/*
	 * @InParams(param = {
	 * 
	 * @Param(name = "podInfoList", comment = "pod信息列表", type =
	 * cn.com.agree.afa.svc.javaengine.context.JavaList.class),
	 * 
	 * @Param(name = "aarPath", comment = "上传资源包的完整路径", type =
	 * java.lang.String.class),
	 * 
	 * @Param(name = "restart", comment = "是否重启", type = java.lang.String.class),
	 * 
	 * @Param(name = "autostart", comment = "是否自启", type = java.lang.String.class),
	 * 
	 * @Param(name = "groupName", comment = "工作组名称", type = java.lang.String.class)
	 * })
	 * 
	 * @OutParams(param = {
	 * 
	 * @Param(name = "rspDict", comment = "rspDict", type =
	 * cn.com.agree.afa.svc.javaengine.context.JavaDict.class) })
	 * 
	 * @Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp =
	 * "成功") })
	 * 
	 * @Component(label = "上传资源", style = "判断型", type = "同步组件", author = "13570",
	 * date = "2018-12-04 06:25:00") public static TCResult
	 * P_uploadResource(JavaList podInfoList, String aarPath, String restart, String
	 * autostart, final String groupName) throws InterruptedException { final
	 * Iterator<Object> podInfoItr = podInfoList.iterator(); final
	 * LinkedTransferQueue<Object> resultQueue = new LinkedTransferQueue<>(); final
	 * CountDownLatch latch = new CountDownLatch(podInfoList.size()); final
	 * AtomicBoolean isSuccess = new AtomicBoolean(true); while
	 * (podInfoItr.hasNext()) { final JavaDict infoDict = (JavaDict)
	 * podInfoItr.next(); new Thread(new Runnable() {
	 * 
	 * @Override public void run() { JavaDict podInfoDict = infoDict; String ip =
	 * (String) podInfoDict.get("podIp"); String fullUrl = "http://" + ip +
	 * ":7921/service"; String id = podInfoDict.getStringItem("podId"); JavaDict
	 * eachResult = new JavaDict(); eachResult.setItem("podId", id); Map<String,
	 * Object> params = new HashMap<String, Object>(); params.put("action",
	 * "deploy"); params.put("gid", groupName); params.put("restart", restart);
	 * params.put("autostart", autostart); params.put("deployTS",
	 * P_Init.getCurrentTime("yyyyMMddhhmmss")); File aarFile = new File(aarPath);
	 * params.put("filenames", aarFile.getName()); params.put("files", aarFile);
	 * CloseableHttpClient client = HttpClients.createDefault(); HttpPost post = new
	 * HttpPost(fullUrl); RequestConfig requestConfig =
	 * RequestConfig.custom().setSocketTimeout(60000)
	 * .setConnectTimeout(10000).build(); post.setConfig(requestConfig);
	 * post.setEntity(getParamEntity(params).build()); CloseableHttpResponse
	 * response = null; InputStream instreams = null; try { response =
	 * client.execute(post); String deployResp =
	 * EntityUtils.toString(response.getEntity()); eachResult.setItem("deployResp",
	 * deployResp); JavaDict deployJsonObj = new JavaDict();
	 * P_Json.strToDict(deployJsonObj, deployResp); JavaDict respDataItem =
	 * deployJsonObj.getListItem("data").getDictItem(0); if
	 * ("success".equals(respDataItem.getStringItem("result"))) { JavaList
	 * respDeployResults = respDataItem.getListItem("deployResults"); StringBuilder
	 * deployServiceSB = new StringBuilder(); if (null != respDeployResults) { for
	 * (Object deployResult : respDeployResults) { if (null != deployResult) {
	 * JavaDict deployResultDict = ((JavaDict) deployResult); if
	 * (!"success".equals(deployResultDict.getStringItem("result"))) {
	 * deployServiceSB.append(deployResultDict.getStringItem("service"));
	 * deployServiceSB.append(", "); } } } } if (deployServiceSB.length() > 0) {
	 * eachResult.setItem("status", "failure"); eachResult.setItem("reason",
	 * "service: " + deployServiceSB.substring(0, deployServiceSB.length() - 2) +
	 * " deploy failed"); isSuccess.set(false); } else {
	 * eachResult.setItem("status", "success"); } } else {
	 * eachResult.setItem("status", "failure"); eachResult.setItem("reason",
	 * respDataItem.getStringItem("errorMsg")); isSuccess.set(false); } } catch
	 * (Exception e) { e.printStackTrace(); eachResult.setItem("status", "failure");
	 * eachResult.setItem("reason", ExceptionUtils.toDetailString(e));
	 * isSuccess.set(false); } finally { resultQueue.add(eachResult);
	 * latch.countDown(); if (instreams != null) { try { instreams.close(); } catch
	 * (IOException e) { AppLogger.error(e); } } if (response != null) { try {
	 * response.close(); } catch (IOException e) { AppLogger.error(e); } } if
	 * (client != null) { try { client.close(); } catch (IOException e) {
	 * AppLogger.error(e); } } } } }).start(); } latch.await(); JavaDict resultDict
	 * = new JavaDict(); JavaList responses = new JavaList(); for (Object
	 * eachResultTmp : resultQueue) { responses.add(eachResultTmp); }
	 * resultDict.setItem("responses", responses); resultDict.setItem("status",
	 * isSuccess.get() ? "success" : "failure"); return
	 * TCResult.newSuccessResult(resultDict); }
	 */

	/**
	 * @throws InterruptedException
	 * @category 上传资源
	 * @param podInfoList
	 *            入参|pod信息列表|{@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param aarPath
	 *            入参|上传资源包的完整路径|{@link java.lang.String}
	 * @param restart
	 *            入参|是否重启|{@link java.lang.String}
	 * @param autostart
	 *            入参|是否自启|{@link java.lang.String}
	 * @param groupName
	 *            入参|工作组名称|{@link java.lang.String}
	 * @since rspDict
	 *        出参|rspDict|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "podInfoList", comment = "pod信息列表", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class),
			@Param(name = "aarPath", comment = "上传资源包的完整路径", type = java.lang.String.class),
			@Param(name = "restart", comment = "是否重启", type = java.lang.String.class),
			@Param(name = "autostart", comment = "是否自启", type = java.lang.String.class),
			@Param(name = "groupName", comment = "工作组名称", type = java.lang.String.class) })
	@OutParams(param = {
			@Param(name = "rspDict", comment = "rspDict", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "上传资源", style = "判断型", type = "同步组件", author = "13570", date = "2018-12-04 06:25:00")
	public static TCResult P_uploadResource(JavaList podInfoList, String aarPath, String restart, String autostart,
			final String groupName) throws InterruptedException {
		JavaList responses = new JavaList();
		boolean isAllSuccess = true;

		Iterator<Object> podInfoItr = podInfoList.iterator();
		while (podInfoItr.hasNext()) {
			JavaDict podInfoDict = (JavaDict) podInfoItr.next();
			String ip = (String) podInfoDict.get("podIp");
			String fullUrl = "http://" + ip + ":7921/service";
			String id = podInfoDict.getStringItem("podId");
			JavaDict eachResult = new JavaDict();
			eachResult.setItem("podId", id);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("action", "deploy");
			params.put("gid", groupName);
			params.put("restart", restart);
			params.put("autostart", autostart);
			params.put("deployTS", P_Init.getCurrentTime("yyyyMMddhhmmss"));
			File aarFile = new File(aarPath);
			params.put("filenames", aarFile.getName());
			params.put("files", aarFile);
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(fullUrl);
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(10000)
					.build();
			post.setConfig(requestConfig);
			post.setEntity(getParamEntity(params).build());
			CloseableHttpResponse response = null;
			try {
				response = client.execute(post);
				String deployResp = EntityUtils.toString(response.getEntity());
				AppLogger.info("deployResp: " + deployResp);
				JavaDict deployJsonObj = new JavaDict();
				P_Json.strToDict(deployJsonObj, deployResp);
				JavaDict respDataItem = deployJsonObj.getListItem("data").getDictItem(0);
				if ("success".equals(respDataItem.getStringItem("result"))) {
					JavaList respDeployResults = respDataItem.getListItem("deployResults");
					StringBuilder deployServiceSB = new StringBuilder();
					JavaList reasonDetails = new JavaList();
					if (null != respDeployResults) {
						for (Object deployResult : respDeployResults) {
							if (null != deployResult) {
								JavaDict deployResultDict = ((JavaDict) deployResult);
								if (!"success".equals(deployResultDict.getStringItem("result"))) {
									deployServiceSB.append(deployResultDict.getStringItem("service"));
									deployServiceSB.append(", ");
									JavaDict reasonDetailItem = new JavaDict();
									reasonDetailItem.setItem("service", deployResultDict.getStringItem("service"));
									reasonDetailItem.setItem("exceptionCode",
											deployResultDict.getStringItem("exceptionCode"));
									reasonDetailItem.setItem("errorMsg", deployResultDict.getStringItem("errorMsg"));
									reasonDetails.add(reasonDetailItem);
								}
							}
						}
					}
					if (deployServiceSB.length() > 0) {
						eachResult.setItem("status", "failure");
						eachResult.setItem("reason", "service: "
								+ deployServiceSB.substring(0, deployServiceSB.length() - 2) + " deploy failed");
						// eachResult.setItem("reasonDetails", reasonDetails);
						isAllSuccess = false;
					} else {
						eachResult.setItem("status", "success");
					}
				} else {
					eachResult.setItem("status", "failure");
					eachResult.setItem("reason", respDataItem.getStringItem("errorMsg"));
					isAllSuccess = false;
				}
			} catch (Exception e) {
				eachResult.setItem("status", "failure");
				eachResult.setItem("reason", ExceptionUtils.toDetailString(e));
				isAllSuccess = false;
			} finally {
				responses.add(eachResult);
				if (response != null) {
					try {
						response.close();
					} catch (IOException e) {
						AppLogger.error(e);
					}
				}
				if (client != null) {
					try {
						client.close();
					} catch (IOException e) {
						AppLogger.error(e);
					}
				}
			}
		}
		JavaDict resultDict = new JavaDict();
		resultDict.setItem("responses", responses);
		resultDict.setItem("status", isAllSuccess ? "success" : "failure");
		return TCResult.newSuccessResult(resultDict);
	}

	private static MultipartEntityBuilder getParamEntity(Map<String, Object> params) {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		for (String key : params.keySet()) {
			Object obj = params.get(key);

			if (obj instanceof String) {
				builder.addTextBody(key, (String) obj, ContentType.TEXT_PLAIN.withCharset("UTF-8"));
			} else if (obj instanceof File) {
				builder.addBinaryBody(key, (File) obj);
			}
		}
		return builder;
	}

}
