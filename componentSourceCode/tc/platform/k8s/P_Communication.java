package tc.platform.k8s;

import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
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
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import tc.platform.P_Json;

/**
 * 通信，交互
 * 
 * @date 2018-11-22 21:42:36
 */
@ComponentGroup(level = "平台", groupName = "k8s,镜像仓库通信，交互")
public class P_Communication {

	private static CoreV1Api getApi(String hostIp, String certPath) {
		String host = "https://" + hostIp + ":6443";
		String certPass = "apiserver-kubelet-client";
		String certType = "PKCS12";

		ApiClient client = new ApiClient();
		try (InputStream is = new FileInputStream(certPath)) {
			client.setVerifyingSsl(false);
			char[] tableauCertPassword = certPass.toCharArray();
			KeyStore appKeyStore = KeyStore.getInstance(certType);
			appKeyStore.load(is, tableauCertPassword);
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("sunX509");
			keyManagerFactory.init(appKeyStore, tableauCertPassword);
			client.setKeyManagers(keyManagerFactory.getKeyManagers());
			client.setBasePath(host);
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

	private static CloseableHttpClient getHttpClient(String trustStorePath, String passwd) {
		try {
			String keyStorePwd = passwd;
			String keystoreName = trustStorePath;

			KeyStore clientStore = KeyStore.getInstance("JKS");
			InputStream instream = new FileInputStream(keystoreName);
			try {
				clientStore.load(instream, keyStorePwd.toCharArray());
			} finally {
				instream.close();
			}
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
			KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmfactory.init(clientStore, keyStorePwd != null ? keyStorePwd.toCharArray() : null);
			KeyManager[] keymanagers = kmfactory.getKeyManagers();
			sslCtx.init(keymanagers, new TrustManager[] { tm }, null);
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
	 * @param hostIp
	 *            入参|k8s主机ip|{@link java.lang.String}
	 * @param namespace
	 *            入参|namespace|{@link java.lang.String}
	 * @param serviceCode
	 *            入参|serviceCode|{@link java.lang.String}
	 * @param certName
	 *            入参|证书名字|{@link java.lang.String}
	 * @since pods 出参|pod的信息|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "hostIp", comment = "k8s主机ip", type = java.lang.String.class),
			@Param(name = "namespace", comment = "namespace", type = java.lang.String.class),
			@Param(name = "serviceCode", comment = "serviceCode", type = java.lang.String.class),
			@Param(name = "certName", comment = "证书名字", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "pods", comment = "pod的信息", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "查询pod信息", style = "判断型", type = "同步组件", comment = "查询pod信息", author = "13570", date = "2018-11-25 06:07:11")
	public static TCResult P_getPod(String hostIp, String namespace, String serviceCode, String certName) {
		String certPath = PlatformConstants.AFA_HOME + File.separator + "conf" + File.separator + certName;
		CoreV1Api v1Api = getApi(hostIp, certPath);
		String labelSelector = "serviceCode=" + serviceCode;
		String resultInJson = getPodsInfo(v1Api, namespace, labelSelector);
		return TCResult.newSuccessResult(resultInJson);
	}

	/**
	 * @category 获取镜像信息
	 * @param trustStoreName
	 *            入参|trustStore名字|{@link java.lang.String}
	 * @param passwd
	 *            入参|trustStore密码|{@link java.lang.String}
	 * @since imageInfo 出参|json格式的镜像信息|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "trustStoreName", comment = "trustStore名字", type = java.lang.String.class),
			@Param(name = "passwd", comment = "trustStore密码", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "imageInfo", comment = "json格式的镜像信息", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "获取镜像信息", style = "判断型", type = "同步组件", comment = "获取镜像信息", author = "13570", date = "2018-11-25 03:57:43")
	public static TCResult P_getImagesInfo(String trustStoreName, String passwd) {
		String imageRegistryProperty = PlatformConstants.AFA_HOME + File.separator + "conf" + File.separator
				+ "imageRegistry.properties";
		String registryString = FileUtils.readLine(imageRegistryProperty, "utf-8");
		int ix = registryString.indexOf('=');
		String registry = registryString.substring(ix + 1);
		registry = registry.trim();

		String trustStorePath = PlatformConstants.AFA_HOME + File.separator + "conf" + File.separator + trustStoreName;
		CloseableHttpClient httpClient = getHttpClient(trustStorePath, passwd);

		try {
			AppLogger.debug("registry: " + registry);
			String string = getImageInfo0(registry, httpClient);
			return TCResult.newSuccessResult(string);
		} catch (Exception e) {
			AppLogger.error(e);
			return TCResult.newSuccessResult();
		} finally {
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					AppLogger.error(e);
				}
			}
		}
	}

	private static String getImageInfo0(String registry, CloseableHttpClient httpClient) throws Exception {
		HttpGet get = new HttpGet("https://" + registry + "/v2/_catalog");
		CloseableHttpResponse resp = httpClient.execute(get);
		String string = EntityUtils.toString(resp.getEntity());
		if (resp != null) {
			try {
				resp.close();
			} catch (IOException e) {
				AppLogger.error(e);
				throw e;
			}
		}
		JavaDict imageDict = new JavaDict();
		P_Json.strToDict(imageDict, string);
		JavaList repositories = (JavaList) imageDict.get("repositories");
		Iterator<Object> repositoryItr = repositories.iterator();
		JavaList imageTemplates = new JavaList();
		while (repositoryItr.hasNext()) {
			String repository = (String) repositoryItr.next();
			HttpGet versionGet = new HttpGet("https://" + registry + "/v2/" + repository + "/tags/list");
			CloseableHttpResponse versionResp = httpClient.execute(versionGet);
			String verionStr = EntityUtils.toString(versionResp.getEntity());
			if (versionResp != null) {
				try {
					versionResp.close();
				} catch (IOException e) {
					AppLogger.error(e);
					throw e;
				}
			}
			JavaDict versionDict = new JavaDict();
			P_Json.strToDict(versionDict, verionStr);
			versionDict.setItem("repository", versionDict.remove("name"));
			Object tags = versionDict.remove("tags");
			if (tags == null) {
				continue;
			}
			versionDict.setItem("versions", tags);
			JavaDict templateDict = new JavaDict();
			templateDict.setItem("tag", versionDict);
			imageTemplates.add(templateDict);
		}
		JavaDict allInfoDict = new JavaDict();
		allInfoDict.setItem("imageTemplates", imageTemplates);
		allInfoDict.setItem("status", "success");
		return (String) P_Json.dictToStr(allInfoDict).getOutputParams().get(0);
	}

	public static void main(String[] args) throws Exception {
		// 测试获取镜像json字符串
		// CloseableHttpClient httpClient = getHttpClient(
		// "D:/agree/ide/sichuan/AFAIDE_oxygen_v4.1.2_20180910/workspace_2/functionModule/technologyComponent/platform/componentSourceCode/tc/platform/registry.truststore",
		// "123456");
		// System.out.println(getImageInfo0("registry.agree.com.cn",httpClient));

		// 测试获取pod信息json字符串
		// String hostIp = "10.8.6.115";
		// String certPath =
		// "D:/agree/ide/sichuan/AFAIDE_oxygen_v4.1.2_20180910/workspace_2/functionModule/technologyComponent/platform/componentSourceCode/tc/platform/apiserver-kubelet-client.p12";
		// CoreV1Api v1Api = getApi(hostIp, certPath);
		// String labelSelector = null;
		// String namespace = "default";
		// System.out.println(getPodsInfo(v1Api,namespace,labelSelector));

	}

	/**
	 * @category 上传资源
	 * @param podInfoList
	 *            入参|pod信息列表|{@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param aarPath
	 *            入参|上传资源包的完整路径|{@link java.lang.String}
	 * @since rspDict
	 *        出参|rspDict|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "podInfoList", comment = "pod信息列表", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class),
			@Param(name = "aarPath", comment = "上传资源包的完整路径", type = java.lang.String.class) })
	@OutParams(param = {
			@Param(name = "rspDict", comment = "rspDict", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "上传资源", style = "判断型", type = "同步组件", author = "13570", date = "2018-11-26 11:38:30")
	public static TCResult P_uploadResource(JavaList podInfoList, String aarPath) {
		Iterator<Object> podInfoItr = podInfoList.iterator();
		JavaDict resultDict = new JavaDict();
		JavaList resultList = new JavaList();
		resultDict.setItem("responses", resultList);
		//TODO 完善错误判定
		resultDict.setItem("status", "success");
		while (podInfoItr.hasNext()) {

			JavaDict podInfoDict = (JavaDict) podInfoItr.next();
			String ip = (String) podInfoDict.get("podIp");
			String fullUrl = "http://" + ip + ":7921/service";
			String id = podInfoDict.getStringItem("podId");
			JavaDict eachResult = new JavaDict();
			eachResult.setItem("podId", id);
			resultList.add(eachResult);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("action", "deploy");
			params.put("gid", "1");
			params.put("restart", "true");
			params.put("autostart", "true");
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
				HttpEntity en = response.getEntity();
				InputStream instreams = en.getContent();
				InputStreamReader streamReader = new InputStreamReader(instreams);
				BufferedReader reader = new BufferedReader(streamReader);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				//TODO 完善错误判定
				AppLogger.info("id: " + id + ",response: " + sb.toString());
				eachResult.setItem("status", "success");
			} catch (Exception e) {
				AppLogger.error(e);
				eachResult.setItem("status", "failure");
				eachResult.setItem("reason", ExceptionUtils.toDetailString(e));
				continue;
			} finally {
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
