package tc.platform;

import galaxy.ide.tech.cpt.Component;
import galaxy.ide.tech.cpt.ComponentGroup;
import galaxy.ide.tech.cpt.InParams;
import galaxy.ide.tech.cpt.OutParams;
import galaxy.ide.tech.cpt.Param;
import galaxy.ide.tech.cpt.Return;
import galaxy.ide.tech.cpt.Returns;

import java.util.Map;

import cn.com.agree.afa.svc.javaengine.AppLogger;
import cn.com.agree.afa.svc.javaengine.TCResult;
import cn.com.agree.afa.svc.javaengine.context.JavaDict;
import cn.com.agree.afa.svc.javaengine.context.JavaList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * json形式字符串与javaBean，字典等之间的转换
 * 
 * @date 2015-12-20 10:13:6
 */
@ComponentGroup(level = "平台", groupName = "json转换类组件")
public class P_Json {

	/**
	 * @param obj
	 *            入参|javaBean|{@link java.lang.Object}
	 * @param jsonStr
	 *            出参|json字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "obj", comment = "javaBean", type = java.lang.Object.class) })
	@OutParams(param = { @Param(name = "jsonStr", comment = "json字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "javaBean转换json串", style = "判断型", type = "同步组件", comment = "将javaBean转换成json字符串", date = "2015-12-22 10:04:52")
	public static TCResult beanToStr(Object obj) {
		if (obj == null) {
			return TCResult.newFailureResult("BIPE0735", "参数为null");
		}
		String jsonStr = JSON.toJSONString(obj,
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.DisableCircularReferenceDetect);
		return TCResult.newSuccessResult(jsonStr);
	}

	/**
	 * @param dict
	 *            入参|字典|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @param jsonStr
	 *            出参|json字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "dict", comment = "字典", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class) })
	@OutParams(param = { @Param(name = "jsonStr", comment = "json字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "javaDict转换json串", style = "判断型", type = "同步组件", comment = "平台字典数据结构的对象转换成json字符串", date = "2015-12-22 10:05:20")
	public static TCResult dictToStr(JavaDict dict) {
		if (dict == null) {
			return TCResult.newFailureResult("BIPE0735", "参数为null");
		}
		String jsonStr = JSON.toJSONString(dict,
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.DisableCircularReferenceDetect);
		return TCResult.newSuccessResult(jsonStr);
	}

	/**
	 * @param obj
	 *            入参|javaBean|{@link java.lang.Object}
	 * @param formattedJsonStr
	 *            出参|格式优雅的json字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "obj", comment = "javaBean", type = java.lang.Object.class) })
	@OutParams(param = { @Param(name = "formattedJsonStr", comment = "格式优雅的json字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "javaBean转换格式化json串", style = "判断型", type = "同步组件", comment = "将javaBean转换成格式优雅的json字符串", date = "2015-12-22 10:05:47")
	public static TCResult beanToFormattedStr(Object obj) {
		if (obj == null) {
			return TCResult.newFailureResult("BIPE0735", "参数为null");
		}
		String formattedJsonStr = JSON.toJSONString(obj,
				SerializerFeature.PrettyFormat,
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.DisableCircularReferenceDetect);
		return TCResult.newSuccessResult(formattedJsonStr);
	}

	/**
	 * @param dict
	 *            入参|字典|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @param formattedJsonStr
	 *            出参|格式优美的json字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "dict", comment = "字典", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class) })
	@OutParams(param = { @Param(name = "formattedJsonStr", comment = "格式优美的json字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "javaDict转换格式化json串", style = "判断型", type = "同步组件", comment = "将平台字典数据结构的对象转换格式优美的json字符串", date = "2015-12-22 10:06:10")
	public static TCResult dictToFormattedStr(JavaDict dict) {
		if (dict == null) {
			return TCResult.newFailureResult("BIPE0735", "参数为null");
		}
		String formattedJsonStr = JSON.toJSONString(dict,
				SerializerFeature.PrettyFormat,
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.DisableCircularReferenceDetect);
		return TCResult.newSuccessResult(formattedJsonStr);
	}

	private static JavaList getJavaList(JSONArray seq) {
		JavaList list = new JavaList();
		for (int i = 0; i < seq.size(); i++) {
			Object value = seq.get(i);
			if (value == null) {
				list.add(null);
				continue;
			}
			if (value.getClass() == JSONArray.class) {
				list.add(getJavaList((JSONArray) value));
			} else if (value.getClass() == JSONObject.class) {
				list.add(getJavaDict((JSONObject) value));
			} else {
				list.add(value);
			}
		}
		return list;
	}

	private static JavaDict getJavaDict(JSONObject jsonObj) {
		JavaDict dict = new JavaDict();

		for (@SuppressWarnings("rawtypes")
		Map.Entry entry : jsonObj.entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (value == null) {
				dict.setItem(key, null);
				continue;
			}
			if (value.getClass() == JSONArray.class) {
				dict.setItem(key, getJavaList((JSONArray) value));
			} else if (value.getClass() == JSONObject.class) {
				dict.setItem(key, getJavaDict((JSONObject) value));
			} else {
				dict.setItem(key, value);
			}
		}
		return dict;
	}

	/**
	 * @param className
	 *            入参|完整的类的名称，要包含包名|{@link java.lang.String}
	 * @param jsonStr
	 *            入参|json字符串|{@link java.lang.String}
	 * @param obj
	 *            出参|javaBean|{@link Object}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "className", comment = "完整的类的名称，要包含包名", type = java.lang.String.class),
			@Param(name = "jsonStr", comment = "json字符串", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "obj", comment = "javaBean", type = Object.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "json串转换javaBean", style = "判断型", type = "同步组件", comment = "json字符串转换为javaBean", date = "2015-12-22 10:13:44")
	public static TCResult strToBean(String className, String jsonStr) {
		if (className == null || className.equals("")) {
			return TCResult.newFailureResult("BIPE0736",
					"className参数为null或为空字符串");
		}
		if (jsonStr == null || jsonStr.equals("")) {
			return TCResult.newFailureResult("BIPE0737",
					"jsonStr参数为null或为空字符串");
		}
		Class<?> cls = null;
		try {
			cls = Class.forName(className);
		} catch (ClassNotFoundException e) {
			AppLogger.error( e.getMessage());
			return TCResult.newFailureResult("BIPE0305", "系统异常");
		}
		Object bean = JSON.parseObject(jsonStr, cls);
		return TCResult.newSuccessResult(bean);
	}

	/**
	 * @param dict
	 *            入参|字典|{@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @param jsonStr
	 *            入参|json串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "dict", comment = "字典", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class),
			@Param(name = "jsonStr", comment = "json串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "json串转换javaDict", style = "判断型", type = "同步组件", comment = "json字符串转换平台字典类型对象", date = "2015-12-22 10:41:15")
	public static TCResult strToDict(JavaDict dict, String jsonStr) {
		if (jsonStr == null || jsonStr.equals("")) {
			return TCResult.newFailureResult("BIPE0737",
					"jsonStr参数为null或为空字符串");
		}
		if (dict == null) {
			return TCResult.newFailureResult("BIPE0738", "dict参数为null");
		}
		JSONObject jsonObj = (JSONObject) JSON.parseObject(jsonStr);
		JavaDict jsonDict = getJavaDict(jsonObj);
		dict.putAll(jsonDict);
		return TCResult.newSuccessResult();
	}

}
