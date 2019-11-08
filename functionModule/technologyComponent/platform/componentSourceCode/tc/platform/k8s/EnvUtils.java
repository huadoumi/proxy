package tc.platform.k8s;

import java.util.Map;

public class EnvUtils {
	public static String getEnv(String key,String defaultValue) {
		Map<String, String> envs = System.getenv();
		String value = envs.get(key);
		if(value == null || value.isEmpty()) {
			return defaultValue;
		}else {
			return value.trim();
		}
	}
}
