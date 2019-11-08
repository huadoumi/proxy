package tc.platform;

import galaxy.ide.tech.cpt.Component;
import galaxy.ide.tech.cpt.ComponentGroup;
import galaxy.ide.tech.cpt.InParams;
import galaxy.ide.tech.cpt.OutParams;
import galaxy.ide.tech.cpt.Param;
import galaxy.ide.tech.cpt.Return;
import galaxy.ide.tech.cpt.Returns;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.com.agree.afa.svc.javaengine.TCResult;
import cn.com.agree.afa.svc.javaengine.context.JavaList;

/**
 * 金额处理类组件
 * 
 * @date 2015-07-03 9:45:51
 */
@ComponentGroup(level = "平台", groupName = "金额处理类组件")
public class P_Amount {
	

	/**
	 * @param instr
	 *            入参|输入字符串|{@link java.lang.String}
	 * @param outstr
	 *            出参|去掉小数点的字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "instr", comment = "输入字符串", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "outstr", comment = "去掉小数点的字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "删除失败"),
			@Return(id = "1", desp = "删除成功") })
	@Component(label = "删除小数点", style = "判断型", type = "同步组件", comment = "删除给定金额字符串中的小数点,小数点精度精确到2位,多余位数四舍五入,如果字符串无小数点则后补2个0", date = "Fri Jul 03 09:47:46 CST 2015")
	public static TCResult deleteDot(String instr) {
		if (instr == null) {
			return TCResult.newFailureResult("BIPE0100", "参数 instr不能为空！");
		}
		return TCResult.newSuccessResult(deleteDot0(instr));
	}
	

	private static String deleteDot0(String instr) {
		BigDecimal bg = new BigDecimal(Double.valueOf(instr));
        double finstr= bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		instr = String.valueOf(finstr);
		int index = instr.indexOf(".");
		String outstr = instr;
		if (index < 0) {
			outstr = instr;
		} else {
			String befortDot = instr.substring(0, index);
			String afterDot = instr.substring(index + 1);
			if (afterDot.length() < 2) {
				afterDot = afterDot + "0";
			} else {
				afterDot = instr.substring(index + 1, index + 3);
			}
			outstr = befortDot + afterDot;
		}
		return outstr;
	}

	/**
	 * @param instr
	 *            入参|输入字符串|{@link java.lang.String}
	 * @param outstr
	 *            出参|去掉千分符的字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "instr", comment = "输入字符串", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "outstr", comment = "去掉千分符的字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "删除失败"),
			@Return(id = "1", desp = "删除成功") })
	@Component(label = "删除千分符", style = "判断型", type = "同步组件", comment = "删除给定金额字符串中的千分符(金额格式合法性未进行校验,仅仅是删除字符串中的千分符)", date = "Fri Jul 03 10:14:15 CST 2015")
	public static TCResult deleteComma(String instr) {
		if (instr == null) {
			return TCResult.newFailureResult("BIPE0100", "参数 instr不能为空！");
		}
		String outStr = deleteComma0(instr);
		return TCResult.newSuccessResult(outStr);
	}

	private static String deleteComma0(String instr) {
		String[] strings = instr.split(",");
		String outStr = "";
		for (String str : strings) {
			outStr += str;
		}
		return outStr;
	}

	/**
	 * @param instr
	 *            入参|输入字符串|{@link java.lang.String}
	 * @param outstr
	 *            出参|去掉小数点和千分符的字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "instr", comment = "输入字符串", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "outstr", comment = "去掉小数点和千分符的字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "删除失败"),
			@Return(id = "1", desp = "删除成功") })
	@Component(label = "删除小数点和千分符", style = "判断型", type = "同步组件", comment = "删除给定金额字符串中的小数点和千分符,小数点精度精确到2位,多余位数四舍五入,如果字符串无小数点则后补2个0", date = "Fri Jul 03 10:28:16 CST 2015")
	public static TCResult deleteDotComma(String instr) {
		if (instr == null) {
			return TCResult.newFailureResult("BIPE0100", "参数 instr不能为空！");
		}
		return TCResult.newSuccessResult(deleteDot0(deleteComma0(instr)));
	}

	/**
	 * @param instr
	 *            入参|输入字符串|{@link java.lang.String}
	 * @param position
	 *            入参|小数点位置|int
	 * @param outstr
	 *            出参|插入小数点的字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "instr", comment = "输入字符串", type = java.lang.String.class),
			@Param(name = "position", comment = "小数点位置", type = int.class) })
	@OutParams(param = { @Param(name = "outstr", comment = "插入小数点的字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "插入失败"),
			@Return(id = "1", desp = "插入成功") })
	@Component(label = "插入小数点", style = "判断型", type = "同步组件", comment = "对给定金额字符串中插入小数点,如果数字字符串中有小数点提示异常,position要大于等于0,如果等于0则只是在字符串后面追加一个\".\"", date = "Fri Jul 03 10:42:10 CST 2015")
	public static TCResult insertDot(String instr, int position) {
		if (instr == null) {
			return TCResult.newFailureResult("BIPE0100", "参数 instr不能为空！");
		}
		if (position < 0) {
			return TCResult.newFailureResult("BIPE0108",
					"插入小数点时小数点位置参数非法,小于0");
		}
		int index = instr.indexOf(".");
		if (index != -1) {
			return TCResult.newFailureResult("BIPE0109",
					"插入小数点时发现里面已经有小数点,数据格式异常!");
		}
		String result = insertDot0(instr, position);
		return TCResult.newSuccessResult(result);
	}

	private static String insertDot0(String instr, int position) {
		String result = "";
		boolean isNegative = true;
		if (position == 0) {
			result = instr + ".";
		} else {
			String temp = "";
			if((instr.subSequence(0, 1).equals("-"))){
				temp = instr.substring(1);
				isNegative = true;
			}else{
				temp = instr;
				isNegative = false;
			}
			int t_len = temp.length();
			if (t_len > position) {
				result =  temp.substring(0, t_len-position) + "."
						+ temp.substring(t_len-position);
			} else if(t_len == position){
				result = "0."+ temp;
			}else {
				String p = "0";
				for (int i = 0; i < position - t_len - 1; i++) {
					p += p;
				}
				result = "0." + p + temp;
			}
			if(isNegative){
				result = "-" + result;
			}
		}
		return result;
	}

	/**
	 * @param instr
	 *            入参|输入字符串|{@link java.lang.String}
	 * @param outstr
	 *            出参|插入千分符的字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "instr", comment = "输入字符串", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "outstr", comment = "插入千分符的字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "插入失败"),
			@Return(id = "1", desp = "插入成功") })
	@Component(label = "插入千分符", style = "判断型", type = "同步组件", comment = "给定金额字符串中插入千分符,如果金额字符串中已经有千分符提示错误", date = "Fri Jul 03 11:31:42 CST 2015")
	public static TCResult insertComma(String instr) {
		if (instr == null) {
			return TCResult.newFailureResult("BIPE0100", "参数 instr不能为空！");
		}
		String result = "";
		int index = instr.indexOf(",");
		if (index != -1) {
			TCResult.newFailureResult("BIPE0106",
					"插入千分符时输入字符串里有千分符,格式非法");
		}
		if (!(instr.subSequence(0, 1).equals("-"))) {
			result = processString(instr);
		} else {
			result = "-" + processString(instr.substring(1));
		}
		return TCResult.newSuccessResult(result);
	}

	private static String processString(String strSumData) {
		String strSecondPart = "";// .加小数部分
		String strFirstPart = "";// 整数部分3位相隔用逗号隔开后的字符串
		String strZheng = "";// 整数部分
		int dot_posi = strSumData.indexOf(".");
		if (dot_posi == -1) {
			strZheng = strSumData;
		} else {
			strZheng = strSumData.split("\\.")[0].toString();
			// 第二部分为小数点+小数部分
			strSecondPart = "." + strSumData.split("\\.")[1].toString();
		}
		ArrayList<String> arrayFirstPart = new ArrayList<String>();
		// 整数部分用逗号隔开处理
		int i = 1;
		int length = strZheng.length();
		if (length > 3) {
			while (length > 3 * i) {// 循环取3位，前加逗号
				arrayFirstPart.add(","
						+ strZheng.substring(length - (3 * i), length - (3 * i)
								+ 3));
				i++;
				if (length <= 3 * i) {
					// 将最后不需要加逗号的几位添加到arraylist最后
					arrayFirstPart.add(strZheng.substring(0, length - 3
							* (i - 1)));
				}
			}
			// 数组中的元素拼接组成最终加千分符以后的字符串
			for (int j = arrayFirstPart.size() - 1; j > -1; j--) {
				strFirstPart += arrayFirstPart.get(j).toString();
			}
		} else {
			strFirstPart = strZheng;
		}
		// 最终返回字符串（加上小数点 及 小数点以后的部分）
		return strFirstPart + strSecondPart;
	}

	/**
	 * @param instr
	 *            入参|输入字符串|{@link java.lang.String}
	 * @param position
	 *            入参|小数点位置|int
	 * @param outstr
	 *            出参|插入小数点和千分符的字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "instr", comment = "输入字符串", type = java.lang.String.class),
			@Param(name = "position", comment = "小数点位置", type = int.class) })
	@OutParams(param = { @Param(name = "outstr", comment = "插入小数点和千分符的字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "插入失败"),
			@Return(id = "1", desp = "插入成功") })
	@Component(label = "插入小数点和千分符", style = "判断型", type = "同步组件", comment = "对给定金额字符串中插入小数点和千分符,字符串中不能有小数点或者千分符,有的话则处理失败", date = "Fri Jul 03 15:23:04 CST 2015")
	public static TCResult insertDotComma(String instr, int position) {
		if (instr == null) {
			return TCResult.newFailureResult("BIPE0100", "参数 instr不能为空！");
		}

		if (instr.indexOf(",") != -1) {
			return TCResult.newFailureResult("BIPE0110",
					"插入小数点和千分符时输入字符串里有千分符,格式非法");
		}
		if (instr.indexOf(".") != -1) {
			return TCResult.newFailureResult("BIPE0111",
					"插入小数点和千分符时发现里面已经有小数点,数据格式异常");
		}
		TCResult result1 = insertDot(instr, position);
		if (result1.getStatus() == 0) {
			return TCResult.newFailureResult("BIPE0112",
					"插入小数点和千分符时插入小数点失败," + result1.getErrorMsg());
		}
		TCResult result2 = insertComma((String) result1.getOutputParams()
				.get(0));
		return result2;
	}

	/**
	 * @param currency
	 *            入参|币种/None|{@link java.lang.String}
	 * @param instr
	 *            入参|金融字符串|{@link java.lang.String}
	 * @param format
	 *            入参|金额格式(Point/NoPoint)|{@link java.lang.String}
	 * @return 0 不是金融字符串<br/>
	 *         1 等于零<br/>
	 *         2 不等于零<br/>
	 */
	@InParams(param = {
			@Param(name = "currency", comment = "币种/None", type = java.lang.String.class),
			@Param(name = "instr", comment = "金融字符串", type = java.lang.String.class),
			@Param(name = "format", comment = "金额格式(Point/NoPoint)", type = java.lang.String.class) })
	@OutParams(param = {})
	@Returns(returns = { @Return(id = "0", desp = "不是金融字符串"),
			@Return(id = "1", desp = "等于零"), @Return(id = "2", desp = "不等于零") })
	@Component(label = "判断金额字符串是否为零", style = "判断型", type = "同步组件", comment = "对指定的币种和金额格式化的金额字符串进行判断,暂只支持人民币,\n 如果传入的金融字符串为None或空字符串则返回成功", date = "Fri Jul 03 15:48:39 CST 2015")
	public static TCResult isAmountZero(String currency, String instr,
			String format) {
		if (format == null) {
			return TCResult.newFailureResult("BIPE0100", "参数 format不能为空！");
		}
		if (instr == null || instr.trim().length() == 0) {
			return new TCResult(1, null, null, null);
		}
		if (format.equalsIgnoreCase("NoPoint") && instr.indexOf(".") != -1) {
			return TCResult.newFailureResult("BIPE0114",
					"非小数点类金融字符串非法,里面有小数点");
		}
		Double f = Double.valueOf(instr);
		if (format.equalsIgnoreCase("NoPoint")) {
			instr = instr.trim();
			int start = 0;
			if (instr.substring(0, 1).equals("-")) {
				start = 1;
			} else {
				start = 0;
			}
			for (int i = 0; i < instr.length() - start; i++) {
				if (!instr.substring(start + i, start + i + 1).equals("0")) {
					return new TCResult(2, "BIPE0115", "不等于0");
				}
			}
			return new TCResult(1, null, null, null);
		} else if (format.equalsIgnoreCase("Point")) {
			if (f < -0.001 || f > 0.001) {
				return new TCResult(2, "BIPE0115", "不等于0");
			} else {
				return new TCResult(1, null, null, null);
			}
		} else {
			return new TCResult(0, "BIPE0116", "非法的金额格式串" + format);
		}
	}

	/**
	 * @param inlist
	 *            入参|输入字符串|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param outlist
	 *            出参|去掉小数点的字符串|{@link java.lang.String}
	 * @return 0 删除失败<br/>
	 *         1 删除成功<br/>
	 */
	@InParams(param = { @Param(name = "inlist", comment = "输入字符串", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class) })
	@OutParams(param = { @Param(name = "outlist", comment = "去掉小数点的字符串", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class) })
	@Returns(returns = { @Return(id = "0", desp = "删除失败"),
			@Return(id = "1", desp = "删除成功") })
	@Component(label = "批量删除小数点", style = "判断型", type = "同步组件", comment = "删除给定list中金额字符串的小数点,小数点精度精确到2位,多余位数四舍五入,如果字符串无小数点则后补2个0", date = "Fri Jul 03 15:56:00 CST 2015")
	public static TCResult deleteMultiDot(JavaList inlist) {
		if (inlist == null || inlist.size() == 0) {
			return TCResult.newFailureResult("BIPE0104",
					"批量删除小数点时输入字符串list类型非法,list不能为空值或者list不包含任何字符串");
		}
		JavaList outlist = new JavaList();
		for (int i = 0; i < inlist.size(); i++) {
			outlist.add(deleteDot0((String) inlist.get(i)));
		}
		return TCResult.newSuccessResult(outlist);
	}

	/**
	 * @param inlist
	 *            入参|输入字符串|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param position
	 *            入参|小数点位置|int
	 * @param outlist
	 *            出参|插入小数点的字符串|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @return 0 插入失败<br/>
	 *         1 插入成功<br/>
	 */
	@InParams(param = {
			@Param(name = "inlist", comment = "输入字符串", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class),
			@Param(name = "position", comment = "小数点位置", type = int.class) })
	@OutParams(param = { @Param(name = "outlist", comment = "插入小数点的字符串", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class) })
	@Returns(returns = { @Return(id = "0", desp = "插入失败"),
			@Return(id = "1", desp = "插入成功") })
	@Component(label = "批量插入小数点", style = "判断型", type = "同步组件", comment = "对给定list中金额字符串插入小数点,如果数字字符串中有小数点提示异常,position要大于等于0,如果等于0则只是在字符串后面追加一个\".\"", date = "Fri Jul 03 16:06:39 CST 2015")
	public static TCResult insertMultiDot(JavaList inlist, int position) {
		if (inlist == null || inlist.size() == 0) {
			return TCResult.newFailureResult("BIPE0303",
					"批量插入小数点时输入字符串list类型非法,list不能为空值或者list不包含任何字符串");
		}
		JavaList outlist = new JavaList();
		for (int i = 0; i < inlist.size(); i++) {
			outlist.add(insertDot0((String) inlist.get(i), position));
		}
		return TCResult.newSuccessResult(outlist);
	}

	/**
	 * @param instr
	 *            入参|原字符串|{@link java.lang.String}
	 * @param outstr
	 *            出参|输出字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "instr", comment = "原字符串", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "outstr", comment = "输出字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "去除金额字符串前的0", style = "判断型", type = "同步组件", comment = "去除金额字符串前的0", date = "Fri Jul 03 16:16:46 CST 2015")
	public static TCResult delZeroBeforeStr(String instr) {
		if (instr == null) {
			return TCResult.newFailureResult("BIPE0100", "参数 instr 不能为空值");
		}
		String outstr = instr.replaceAll("^(0+)", "");
		return TCResult.newSuccessResult(outstr);
	}

	/**
	 * @param amt
	 *            入参|交易金额|{@link java.lang.String}
	 * @param cmpamt
	 *            入参|比较金额|{@link java.lang.String}
	 * @param ispoint
	 *            入参|是否为小数点|boolean
	 * @return 0 异常<br/>
	 *         1 交易金额小于比较金额<br/>
	 *         2 交易金额大于比较金额<br/>
	 *         3 交易金额等于比较金额<br/>
	 */
	@InParams(param = {
			@Param(name = "amt", comment = "交易金额", type = java.lang.String.class),
			@Param(name = "cmpamt", comment = "比较金额", type = java.lang.String.class),
			@Param(name = "ispoint", comment = "是否为小数点,true 为有小数点，false"
					+ "为不含有小数点", type = boolean.class) })
	@OutParams(param = {})
	@Returns(returns = { @Return(id = "0", desp = "异常"),
			@Return(id = "1", desp = "交易金额小于比较金额"),
			@Return(id = "2", desp = "交易金额大于比较金额"),
			@Return(id = "3", desp = "交易金额等于比较金额") })
	@Component(label = "比较金额", style = "选择型", type = "同步组件", comment = "1:小于，2：大于，3：等于", date = "Fri Jul 03 16:35:27 CST 2015")
	public static TCResult compareAmount(String amt, String cmpamt,
			boolean ispoint) {
		if (amt == null || cmpamt == null) {
			return TCResult
					.newFailureResult("BIPE0117", "入参amt,cmpamt不能为空值");
		}
		if (ispoint) {
			Double diff = Double.valueOf(amt) - Double.valueOf(cmpamt);
			if (diff < -0.001) {
				return new TCResult(1, null, null, null);
			} else if (diff > 0.001) {
				return new TCResult(2, null, null, null);
			} else {
				return new TCResult(3, null, null, null);
			}
		} else {
			int diff = Integer.valueOf(amt) - Integer.valueOf(cmpamt);
			if (diff < 0) {
				return new TCResult(1, null, null, null);
			} else if (diff > 0) {
				return new TCResult(2, null, null, null);
			} else {
				return new TCResult(3, null, null, null);
			}
		}
	}

	/**
	 * @param amt
	 *            入参|交易金额|{@link java.lang.String}
	 * @param tamt
	 *            入参|总金额|{@link java.lang.String}
	 * @param ispoint
	 *            入参|是否为小数点|{@link java.lang.String}
	 * @param tamt
	 *            出参|累计金额|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "amt", comment = "交易金额", type = java.lang.String.class),
			@Param(name = "tamt", comment = "总金额", type = java.lang.String.class),
			@Param(name = "ispoint", comment = "是否为小数点，Point： 为有小数点，NoPoint：为没有，其他原值返回", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "tamt", comment = "累计金额", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "累加金额", style = "判断型", type = "同步组件", comment = "累计金额", date = "Fri Jul 03 16:50:38 CST 2015")
	public static TCResult addAmount(String amt, String tamt, String ispoint) {
		if (amt == null || tamt == null) {
			return TCResult.newFailureResult("BIPE0186", "入参不能为空值！");
		}
		if (ispoint.equalsIgnoreCase("Point")) {
			Double result = Double.valueOf(tamt) + Double.valueOf(amt);
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			String p = decimalFormat.format(result);
			return TCResult.newSuccessResult(p);
		} else if (ispoint.equalsIgnoreCase("NoPoint")) {
			int result = Integer.valueOf(tamt) + Integer.valueOf(amt);
			return TCResult.newSuccessResult(String.valueOf(result));
		} else {
			return TCResult.newSuccessResult(tamt);
		}
	}

	/**
	 * @param inAmount
	 *            入参|金额字符串|{@link java.lang.String}
	 * @param outAmount
	 *            出参|金额中文大写|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "inAmount", comment = "金额字符串", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "outAmount", comment = "金额中文大写", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "将金额转换成中文大写", style = "判断型", type = "同步组件", comment = "将金额字符串(可含小数点)转换为中文大写金额", date = "Fri Jul 03 17:11:28 CST 2015")
	public static TCResult amountToCN(String inAmount) {
		if (inAmount == null) {
			return TCResult.newFailureResult("BIPE0119",
					"入参 inAmount 不能为空值！");
		}
		Double b = Double.valueOf(inAmount);
		return TCResult.newSuccessResult(digitUppercase(b));
	}

	public static String digitUppercase(double n) {
		String fraction[] = { "角", "分" };
		String digit[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		String unit[][] = { { "元", "万", "亿" }, { "", "拾", "佰", "仟" } };

		String head = n < 0 ? "负" : "";
		n = Math.abs(n);

		String s = "";
		for (int i = 0; i < fraction.length; i++) {
			s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i])
					.replaceAll("(零.)+", "");
		}
		if (s.length() < 1) {
			s = "整";
		}
		int integerPart = (int) Math.floor(n);

		for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
			String p = "";
			for (int j = 0; j < unit[1].length && n > 0; j++) {
				p = digit[integerPart % 10] + unit[1][j] + p;
				integerPart = integerPart / 10;
			}
			s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i]
					+ s;
		}
		return head
				+ s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "")
						.replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
	}

}