package tc.platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import galaxy.ide.tech.cpt.Component;
import galaxy.ide.tech.cpt.ComponentGroup;
import galaxy.ide.tech.cpt.InParams;
import galaxy.ide.tech.cpt.Param;
import galaxy.ide.tech.cpt.Return;
import galaxy.ide.tech.cpt.Returns;
import cn.com.agree.afa.svc.javaengine.AppLogger;
import cn.com.agree.afa.svc.javaengine.TCResult;
import cn.com.agree.afa.svc.javaengine.context.JavaList;

/**
 * 系统调用类组件
 * 
 * @date 2015-07-07 23:21:19
 */
@ComponentGroup(level = "平台", groupName = "系统调用类组件")
public class P_SystemCall {

	private static final String WINDOWS = "Windows";
	private static final String LINUX = "Linux";
	private static final String STR_AFAHOME = "${afahome}";
    
	private static final String AFA_HOME = getAFAHome();
	/**
	 * @param cmd
	 *            入参|命令|{@link java.lang.String}
	 * @param argList
	 *            入参|参数列表|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "cmd", comment = "命令", type = java.lang.String.class),
			@Param(name = "argList", comment = "参数列表", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "系统调用", style = "判断型", type = "同步组件", comment = "执行shell命令或windows可执行脚本。支持在命令中填入${afahome}来引用AFA4J的主目录。", date = "2015-12-21 11:38:44")
	public static TCResult systemCall(String cmd, JavaList argList) {
		Process process = null;
		BufferedReader bufferedReader = null;
		BufferedReader brError = null;
		try {
			if (cmd == null || cmd.isEmpty()) {
				return TCResult
						.newFailureResult("BIPE0162", "入参参数列表参数非法，为空");
			}
			String printinfo="";
			cmd = replaceAFAHome(cmd);
			process = Runtime.getRuntime().exec(generateCommand(cmd, argList));
			// 读取标准输出流
			bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			while (bufferedReader.readLine() != null) {}
			// 读取标准错误流
			brError = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			while (( printinfo=brError.readLine()) != null) {AppLogger.info(printinfo);}

			int exitCode = process.waitFor();
			if (exitCode != 0) {
				return TCResult.newFailureResult("BIPE0163",
						"命令执行返回非零结果码:" + exitCode);
			}
			return TCResult.newSuccessResult();
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				AppLogger.error( e.getMessage());
				return TCResult.newFailureResult("BIPE0305", "系统异常");
			}
		} finally {
			if (process != null) {
				process.destroy();
			}
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {}
			}
			if (brError != null) {
				try {
					brError.close();
				} catch (IOException e) {}
			}
		}
	}

	private static String replaceAFAHome(String sourceStr) {
		return sourceStr.replace(STR_AFAHOME, AFA_HOME);
	}

	private static String getAFAHome() {
		String afaHome = System.getProperty("afa.home");
		if (afaHome == null || afaHome.trim().isEmpty()) {
			afaHome = System.getProperty("user.dir");
		}
		return afaHome;
	}
	
	

	private static String[] generateCommand(String cmd, JavaList argList) {
		String os = getOSType();
		if (os.equalsIgnoreCase(WINDOWS)) {
			return generateCommand4Windows(cmd, argList);
		} else {
			return generateCommand4Linux(cmd, argList);
		}
	}

	private static String[] generateCommand4Windows(String cmd, JavaList argList) {
		String[] commandWithArgs = null;
		if (argList != null) {
			commandWithArgs = new String[3 + argList.size()];
			String[] args = P_String.toStringArray(argList);
			System.arraycopy(args, 0, commandWithArgs, 3, args.length);
		} else {
			commandWithArgs = new String[3];
		}
		commandWithArgs[0] = "cmd";
		commandWithArgs[1] = "/c";
		commandWithArgs[2] = cmd;

		return commandWithArgs;
	}

	private static String[] generateCommand4Linux(String cmd, JavaList argList) {
		String[] commandWithArgs = null;
		if (argList != null) {
			commandWithArgs = new String[1 + argList.size()];
			String[] args = P_String.toStringArray(argList);
			System.arraycopy(args, 0, commandWithArgs, 1, args.length);
		} else {
			commandWithArgs = new String[1];
		}
		commandWithArgs[0] = cmd;
		return commandWithArgs;
	}

	private static String getOSType() {
		String osName = System.getProperty("os.name");
		if (osName.contains(WINDOWS)) {
			return WINDOWS;
		} else {
			return LINUX;
		}
	}
}
