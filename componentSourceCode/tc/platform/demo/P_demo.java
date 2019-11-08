package tc.platform.demo;

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
 * demo场景相关
 * 
 * @date 2018-11-24 16:36:5
 */
@ComponentGroup(level = "平台", groupName = "demo场景相关")
public class P_demo {

	/**
	 * @category demo场景逻辑
	 * @param __REQ__
	 *            入参|__REQ__|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @since str 出参|str|{@link java.lang.String}
	 * @return 0 失败<br/>
	 * 		1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "__REQ__", comment = "__REQ__", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class) })
	@OutParams(param = { @Param(name = "str", comment = "str", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"), @Return(id = "1", desp = "成功") })
	@Component(label = "demo场景逻辑", style = "判断型", type = "同步组件", comment = "demo场景逻辑", author = "13570", date = "2018-11-25 06:09:55")
	public static TCResult P_myDemo(JavaDict __REQ__) {
		Object rcv = __REQ__.getItem("__RCVPCK__");
		AppLogger.info(rcv);
		return TCResult.newSuccessResult("hello,nerd!!!");
	}

}
