package tc.platform.k8s;

class PlatformConstants {
	public static final String AFA_HOME = getAFAHome();

	private static String getAFAHome() {
		String afaHome = System.getProperty("afa.home");
		if (afaHome == null || afaHome.trim().isEmpty()) {
			afaHome = System.getProperty("user.dir");
		}
		return afaHome;
	}
}
