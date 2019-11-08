package tc.platform;

import galaxy.ide.tech.cpt.Component;
import galaxy.ide.tech.cpt.ComponentGroup;
import galaxy.ide.tech.cpt.InParams;
import galaxy.ide.tech.cpt.OutParams;
import galaxy.ide.tech.cpt.Param;
import galaxy.ide.tech.cpt.Return;
import galaxy.ide.tech.cpt.Returns;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.com.agree.afa.svc.javaengine.AppLogger;
import cn.com.agree.afa.svc.javaengine.TCResult;
import cn.com.agree.afa.svc.javaengine.context.JavaDict;
import cn.com.agree.afa.svc.javaengine.context.JavaList;

/**
 * @date 2015-07-01 16:17:30
 */
@ComponentGroup(level = "平台", groupName = "字符串处理类组件")
public class P_String {

	private static final String GBK_CODE = "GB18030";
	private static final int INDEX_NOT_FOUND = -1;
	private static final String EMPTY = "";
	private static final String UPPER = "upper";
	private static final String LOWER = "lower";
	private static final String DOT = ".";
	private static final String LEFT = "left";
	private static final String RIGHT = "right";
	private static final String CENTER = "center";
	private static final char WHITE_SPACE_CHAR = ' ';
	private static final char ZERO_CHAR = '0';
	private static final String AMOUNT = "amount";
	private static final String DECIMAL = "decimal";
	private static final String STRING = "string";
	private static final String NUMBER = "number";
	private static final String ZERO_STRING = "0";
	private static final String ZERO_FLOAT_STRING = "0.00";
	private static final String INTEGER = "Integer";

	private static boolean IS_FLAG = false;

	private static final Set<String> ATTRS = Collections
			.unmodifiableSet(newHashSet("n", "a", "x", "c", "G", "g", "F", "E"));

	private static final Set<Character> X = Collections
			.unmodifiableSet(newHashSet('.', ',', '-', '_', '(', ')', '/', '=',
					'+', '?', '!', '&', '*', '<', '>', ';', '@', '#', '\r',
					'\n', ' '));

	private static final Set<Character> C = Collections
			.unmodifiableSet(newHashSet('.', ',', '-', '_', '(', ')', '/', '=',
					'+', '?', '!', '&', '*', '<', '>', ';', '@', '#', ' '));

	private static final Set<Character> F = Collections
			.unmodifiableSet(newHashSet('A', 'B', 'C', 'D', 'E', 'F'));

	private static final Set<Byte> RH = Collections.unmodifiableSet(newHashSet(
			(byte) 0x7B, (byte) 0x7D, (byte) 0x7C, (byte) 0x5B, (byte) 0x5D));

	/**
	 * @param inStr
	 *            入参|原字符串|{@link java.lang.String}
	 * @param len
	 *            入参|补足后的字符长度|int
	 * @param direct
	 *            入参|补足位置|{@link java.lang.String}
	 * @param fillChar
	 *            入参|补位字符|char
	 * @param outStr
	 *            出参|补足后的字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "inStr", comment = "原字符串", type = java.lang.String.class),
			@Param(name = "len", comment = "补足后的字符长度", type = int.class),
			@Param(name = "direct", comment = "补足位置", type = java.lang.String.class),
			@Param(name = "fillChar", comment = "补位字符", type = char.class) })
	@OutParams(param = { @Param(name = "outStr", comment = "补足后的字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "字符串补足", style = "判断型", type = "同步组件", comment = "对指定字符串进行补足处理,left-表示左补(rjust),right-表示右补(ljust),center-表示两边补齐(center)", date = "Thu Jul 02 10:33:19 CST 2015")
	public static TCResult fillStr(String inStr, int len, String direct,
			char fillChar) {
		if (inStr == null) {
			return TCResult.newFailureResult("BIPE0620", "输入字符串参数为空");
		}

		if (direct == null) {
			return TCResult.newFailureResult("BIPE0621", "补足位置参数为空");
		}

		int padLen = len - inStr.length();

		if (padLen < 0) {
			return TCResult.newFailureResult("BIPE0622",
					"补足最终长度非法，小于原有字符串的长度");
		}

		String filledStr = inStr;
		if (direct.equalsIgnoreCase(LEFT)) {
			filledStr = rjust(inStr, padLen, fillChar);
		} else if (direct.equalsIgnoreCase(RIGHT)) {
			filledStr = ljust(inStr, padLen, fillChar);
		} else if (direct.equalsIgnoreCase(CENTER)) {
			filledStr = centerJust(filledStr, padLen, fillChar);
		} else {
			return TCResult.newFailureResult("BIPE0623", "补足位置参数类型非法,"
					+ direct);
		}

		return TCResult.newSuccessResult(filledStr);

	}

	/**
	 * 产生重复字符的字符串
	 * 
	 * @param ch
	 * @param repeat
	 * @return
	 */
	private static String repeat(final char ch, final int repeat) {
		final char[] buf = new char[repeat];
		for (int i = repeat - 1; i >= 0; i--) {
			buf[i] = ch;
		}
		return new String(buf);
	}

	/**
	 * 右补充
	 * 
	 * @param text
	 * @param padLen
	 * @param ch
	 * @return
	 */
	private static String ljust(String text, int padLen, char ch) {
		if (padLen < 0) {
			throw new IllegalArgumentException("追加的长度不能小于0:" + padLen);
		}
		String repeatedChars = repeat(ch, padLen);
		return text.concat(repeatedChars);
	}

	/**
	 * 左补充
	 * 
	 * @param text
	 * @param padLen
	 * @param ch
	 * @return
	 */
	private static String rjust(String text, int padLen, char ch) {
		if (padLen < 0) {
			throw new IllegalArgumentException("追加的长度不能小于0:" + padLen);
		}
		String repeatedChars = repeat(ch, padLen);
		return repeatedChars.concat(text);
	}

	/**
	 * 两端填充
	 * 
	 * @param text
	 * @param padLen
	 * @param ch
	 * @return
	 */
	private static String centerJust(String text, int padLen, char ch) {
		if (padLen < 0) {
			throw new IllegalArgumentException("追加的长度不能小于0:" + padLen);
		}
		int half = padLen / 2;
		if (padLen % 2 == 0) {
			String eachSide = repeat(ch, half);
			return eachSide.concat(text).concat(eachSide);
		} else {
			String leftSide = repeat(ch, half);
			String rightSide = repeat(ch, half + 1);
			return leftSide.concat(text).concat(rightSide);
		}
	}

	/**
	 * @param inStr
	 *            入参|输入字符串|{@link java.lang.String}
	 * @param start
	 *            入参|开始位置|int
	 * @param end
	 *            入参|结束位置|int
	 * @param outStr
	 *            出参|截取后的字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "inStr", comment = "输入字符串", type = java.lang.String.class),
			@Param(name = "start", comment = "开始位置", type = int.class),
			@Param(name = "end", comment = "结束位置", type = int.class) })
	@OutParams(param = { @Param(name = "outStr", comment = "截取后的字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "字符串截取", style = "判断型", type = "同步组件", comment = "获取输入字符串的子串,取start到end-1的子串", date = "Thu Jul 02 11:31:10 CST 2015")
	public static TCResult subStr(String inStr, int start, int end) {
		if (inStr == null) {
			return TCResult.newFailureResult("BIPE0283", "输入字符串参数非法，为空");
		}
		return TCResult.newSuccessResult(inStr.substring(start, end));
	}

	/**
	 * @param inStr
	 *            入参|输入字符串|{@link java.lang.String}
	 * @param old
	 *            入参|待替换字符串|{@link java.lang.String}
	 * @param replacement
	 *            入参|替换字符串|{@link java.lang.String}
	 * @param count
	 *            入参|替换最大次数(全部则是-1)|int
	 * @param outStr
	 *            出参|替换后的字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "inStr", comment = "输入字符串", type = java.lang.String.class),
			@Param(name = "old", comment = "待替换字符串", type = java.lang.String.class),
			@Param(name = "replacement", comment = "替换字符串", type = java.lang.String.class),
			@Param(name = "count", comment = "替换最大次数(全部则是-1)", type = int.class) })
	@OutParams(param = { @Param(name = "outStr", comment = "替换后的字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "字符串替换", style = "判断型", type = "同步组件", comment = "将指定的子串替换为指定的另外子串", date = "Thu Jul 02 12:01:07 CST 2015")
	public static TCResult replaceStr(String inStr, String old,
			String replacement, int count) {
		if (inStr == null) {
			return TCResult.newFailureResult("BIPE0283", "输入字符串参数非法，为空");
		}

		if (old == null) {
			return TCResult.newFailureResult("BIPE0610", "待替换字符串参数非法，为空");
		}

		if (replacement == null) {
			return TCResult.newFailureResult("BIPE0611", "替换字符串参数非法，为空");
		}
		if (count <= 0) {
			if (count == -1) {
				return TCResult.newSuccessResult(inStr.replaceAll(old,
						replacement));
			} else {
				return TCResult.newFailureResult("BIPE0612", "最大替换次数参数大小错误:"
						+ count);
			}
		} else {
			return TCResult.newSuccessResult(replace(inStr, old, replacement,
					count));
		}
	}

	/**
	 * 替换字符串
	 * 
	 * @param source
	 * @param searchString
	 * @param replacement
	 * @param max
	 * @return
	 */
	private static String replace(final String source,
			final String searchString, final String replacement, int max) {
		if (isEmpty(source) || isEmpty(searchString) || replacement == null
				|| max == 0) {
			return source;
		}

		int start = 0;
		int end = source.indexOf(searchString, start);

		if (end == INDEX_NOT_FOUND) {
			return source;
		}

		final int replLength = searchString.length();
		int increase = replacement.length() - replLength;
		increase = increase < 0 ? 0 : increase;
		increase *= max < 0 ? 16 : max > 64 ? 64 : max;
		final StringBuilder buf = new StringBuilder(source.length() + increase);

		while (end != INDEX_NOT_FOUND) {
			buf.append(source.substring(start, end)).append(replacement);
			start = end + replLength;
			if (--max == 0) {
				break;
			}
			end = source.indexOf(searchString, start);
		}

		buf.append(source.substring(start));
		return buf.toString();
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param text
	 * @return
	 */
	static boolean isEmpty(String text) {
		return text == null || text.length() == 0;
	}

	/**
	 * @param inStr
	 *            入参|输入字符串|{@link java.lang.String}
	 * @param sep
	 *            入参|分割标识字符|{@link java.lang.String}
	 * @param outStrs
	 *            出参|拆分后的字符串list|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "inStr", comment = "输入字符串", type = java.lang.String.class),
			@Param(name = "sep", comment = "分割标识字符", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "outStrs", comment = "拆分后的字符串list", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "字符串分割", style = "判断型", type = "同步组件", comment = "分割字符串", date = "Fri Jul 03 15:42:48 CST 2015")
	public static TCResult split(String inStr, String sep) {
		if (inStr == null) {
			return TCResult.newFailureResult("BIPE0283", "输入字符串参数非法，为空");
		}

		if (sep == null) {
			return TCResult.newFailureResult("BIPE0582", "分割标识字符参数非法，为空");
		}

		if (isNumeric(sep)) {
			final int step = Integer.parseInt(sep);
			List<String> list = new ArrayList<String>();
			final int size = inStr.length();
			for (int i = 0; i < size; i += step) {
				int nextIndex = i + step;
				if (nextIndex >= size) {
					list.add(inStr.substring(i));
				} else {
					list.add(inStr.substring(i, i + step));
				}
			}

			sep = "\\|";
			inStr = join(list.toArray(new String[0]), sep, 0, list.size());
		}

		JavaList resultList = new JavaList();
		String[] strArrs = inStr.split(sep);
		for (int i = 0; i < strArrs.length; i++) {
			AppLogger.info("substr:"+strArrs[i]);
			resultList.add(strArrs[i]);
		}
		return TCResult.newSuccessResult(resultList);
	}

	/**
	 * 用特定连接符连接字符数组
	 * 
	 * @param array
	 * @param sep
	 * @param start
	 * @param end
	 * @return
	 */
	private static String join(final String[] array, String sep,
			final int start, final int end) {
		if (array == null) {
			return null;
		}

		if (sep == null) {
			return EMPTY;
		}

		final int itemCount = end - start;

		if (itemCount < 0) {
			return EMPTY;
		}

		final StringBuilder buf = new StringBuilder();

		for (int i = start; i < end; i++) {
			if (i > start) {
				buf.append(sep);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}

		return buf.toString();
	}

	/**
	 * 判断一个字符串是否是一个正整数
	 * 
	 * @param text
	 * @return
	 */
	private static boolean isNumeric(final String text) {
		if (isEmpty(text)) {
			return false;
		}

		final int size = text.length();
		for (int i = 0; i < size; i++) {
			if (Character.isDigit(text.charAt(i)) == false) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @param strList
	 *            入参|输入字符串列表|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param sep
	 *            入参|分割标识字符|{@link java.lang.String}
	 * @param leftDes
	 *            入参|左侧修饰符|{@link java.lang.String}
	 * @param rightDes
	 *            入参|右侧修饰符|{@link java.lang.String}
	 * @param outStr
	 *            出参|创建后的字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "strList", comment = "输入字符串列表", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class),
			@Param(name = "sep", comment = "分割标识字符", type = java.lang.String.class),
			@Param(name = "leftDes", comment = "左侧修饰符", type = java.lang.String.class),
			@Param(name = "rightDes", comment = "右侧修饰符", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "outStr", comment = "创建后的字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "格式化字符串创建", style = "判断型", type = "同步组件", comment = "使用分隔符和修饰符创建字符串", date = "Wed Jul 15 14:58:55 CST 2015")
	public static TCResult createStr(JavaList strList, String sep,
			String leftDes, String rightDes) {
		if (strList == null) {
			return TCResult.newFailureResult("BIPE0301", "输入字符串列表参数非法，为空");
		}
		if (sep == null) {
			return TCResult.newFailureResult("BIPE0582", "分割标识字符参数非法，为空");
		}
		if (leftDes == null) {
			return TCResult.newFailureResult("BIPE0588", "左侧修饰符参数非法，为空");
		}
		if (rightDes == null) {
			return TCResult.newFailureResult("BIPE0589", "右侧修饰符参数非法，为空");
		}
		String[] array = toStringArray(strList);
		for (int i = 0; i < array.length; i++) {
			array[i] = leftDes + array[i] + rightDes;
		}
		return TCResult.newSuccessResult(join(array, sep, 0, array.length));
	}

	/**
	 * 将String列表转化为String数组
	 * 
	 * @param list
	 * @return
	 */
	static String[] toStringArray(JavaList list) {
		if (list == null) {
			return null;
		}

		int size = list.size();
		String[] array = new String[size];
		for (int i = 0; i < size; i++) {
			array[i] = list.getItem(i);
		}

		return array;
	}

	/**
	 * @param strList
	 *            入参|需要连接的字符串list|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param sep
	 *            入参|连接符号(不需要连接符则传null)|{@link java.lang.String}
	 * @param outStr
	 *            出参|连接结果|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "strList", comment = "需要连接的字符串list", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class),
			@Param(name = "sep", comment = "连接符号(不需要连接符则传null)", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "outStr", comment = "连接结果", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "字符串拼接", style = "判断型", type = "同步组件", comment = "将输入的字符串key连接为一个长串", date = "Fri Jul 03 16:51:20 CST 2015")
	public static TCResult joinStr(JavaList strList, String sep) {
		if (strList == null) {
			return TCResult
					.newFailureResult("BIPE0608", "入参需要连接的字符串参数非法，为空");
		}

		final StringBuilder buf = new StringBuilder();
		final int size = strList.size();
		boolean isSepEmpty = isEmpty(sep);
		for (int i = 0; i < size; i++) {
			String item = strList.getItem(i);
			if (isSepEmpty) {
				buf.append(item);
			} else {
				if (i == 0) {
					buf.append(item);
				} else {
					buf.append(sep).append(item);
				}
			}
		}
		return TCResult.newSuccessResult(buf.toString());
	}

	/**
	 * @param inStr
	 *            入参|被查找字符串|{@link java.lang.String}
	 * @param substr
	 *            入参|子串|{@link java.lang.String}
	 * @param start
	 *            入参|开始下标|{@link int}
	 * @param position
	 *            出参|子串下标|{@link int}
	 * @return 2 没有查找到<br/>
	 *         1 成功<br/>
	 *         0入参有误<br/>
	 */
	@InParams(param = {
			@Param(name = "inStr", comment = "被查找字符串", type = java.lang.String.class),
			@Param(name = "substr", comment = "子串", type = java.lang.String.class),
			@Param(name = "start", comment = "开始下标", type = int.class) })
	@OutParams(param = { @Param(name = "position", comment = "子串下标", type = int.class) })
	@Returns(returns = { @Return(id = "2", desp = "没有查找到"),
			@Return(id = "1", desp = "成功"),
			@Return(id = "0", desp = "入参有误")})
	@Component(label = "字符串查找", style = "判断型", type = "同步组件", comment = "查找字符串中的子串,可指定开始下标，返回查找到的下标结果", date = "Fri Jul 03 17:13:45 CST 2015")
	public static TCResult findStr(String inStr, String substr, int start) {
		if (inStr == null) {
			return TCResult.newFailureResult("BIPE0613", "入参被查找字符串参数非法，为空");
		}

		if (substr == null) {
			return TCResult.newFailureResult("BIPE0614", "入参子串参数非法，为空");
		}

		int position = inStr.indexOf(substr, start);
		if (position < 0) {
			return new TCResult(2,"BIPE0615", "无匹配的子串");
		}

		return TCResult.newSuccessResult(position);
	}

	/**
	 * @param inStr
	 *            入参|输入字符串|{@link java.lang.String}
	 * @param convertType
	 *            入参|转换类型|{@link java.lang.String}
	 * @param outStr
	 *            出参|转换后的字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "inStr", comment = "输入字符串", type = java.lang.String.class),
			@Param(name = "convertType", comment = "转换类型", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "outStr", comment = "转换后的字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "字符串转换", style = "判断型", type = "同步组件", comment = "将输入的字符串按照转换类型转换,暂只支持大小写转换,转换类型为'upper'和'lower'", date = "Fri Jul 03 17:17:43 CST 2015")
	public static TCResult convertStr(String inStr, String convertType) {
		if (inStr == null) {
			return TCResult.newFailureResult("BIPE0618", "入参输入字符串参数非法，为空");
		}

		if (convertType == null) {
			return TCResult.newFailureResult("BIPE0433", "入参转换类型参数非法， 为空");
		}

		if (convertType.equalsIgnoreCase(UPPER)) {
			return TCResult.newSuccessResult(inStr.toUpperCase());
		} else if (convertType.equalsIgnoreCase(LOWER)) {
			return TCResult.newSuccessResult(inStr.toLowerCase());
		} else {
			return TCResult.newFailureResult("BIPE0626", "不支持的转换类型,"
					+ convertType);
		}
	}

	/**
	 * @param inStr
	 *            入参|输入字符串|{@link java.lang.String}
	 * @param decodeType
	 *            入参|转换类型|{@link java.lang.String}
	 * @param originalCodeType
	 *            入参|原编码类型|{@link java.lang.String}
	 * @param outStr
	 *            出参|转换后的串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "inStr", comment = "输入字符串", type = java.lang.String.class),
			@Param(name = "decodeType", comment = "转换类型", type = java.lang.String.class),
			@Param(name = "originalCodeType", comment = "原编码类型", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "outStr", comment = "转换后的串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "字符串编码转换", style = "判断型", type = "同步组件", comment = "将输入的字符串按照指定类型编码进行转换", date = "Fri Jul 03 17:22:49 CST 2015")
	public static TCResult convertCoding(String inStr, String decodeType,
			String originalCodeType) {
		if (inStr == null) {
			return TCResult.newFailureResult("BIPE0618", "入参输入字符串参数非法，为空");
		}

		if (decodeType == null) {
			return TCResult.newFailureResult("BIPE0433", "入参转换类型参数非法， 为空");
		}

		if (originalCodeType == null) {
			return TCResult.newFailureResult("BIPE0619", "入参原编码类型参数非法，为空");
		}

		try {
			byte[] bytes = inStr.getBytes(originalCodeType);
			return TCResult.newSuccessResult(new String(bytes, decodeType));
		} catch (UnsupportedEncodingException e) {
			AppLogger.error( e.getMessage());
			return TCResult.newFailureResult("BIPE0305", "系统异常");
		}
	}

	/**
	 * @param inStr
	 *            入参|源字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 有半角字符<br/>
	 *         2 无半角字符<br/>
	 */
	@InParams(param = { @Param(name = "inStr", comment = "源字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "有半角字符"),
			@Return(id = "2", desp = "无半角字符") })
	@Component(label = "判断字符串是否有半角字符", style = "判断型", type = "同步组件", comment = "判断输入的字符串里面是否有半角字符串", date = "Wed Jul 29 09:53:31 CST 2015")
	public static TCResult haveHalfStr(String inStr) {
		if (inStr == null) {
			return TCResult.newFailureResult("BIPE0609", "入参源字符串参数非法，为空");
		}
		final int size = inStr.length();
		for (int i = 0; i < size; i++) {
			int charValue = (int) inStr.charAt(i);
			if (charValue >= 32 && charValue <= 126) {
				return new TCResult(1);
			}
		}
		return new TCResult(2);
	}

	/**
	 * @param productCode
	 *            入参|产品代码|{@link java.lang.String}
	 * @param attr
	 *            入参|属性符号|{@link java.lang.String}
	 * @param checkStr
	 *            入参|预被检验的字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "productCode", comment = "产品代码", type = java.lang.String.class),
			@Param(name = "attr", comment = "属性符号", type = java.lang.String.class),
			@Param(name = "checkStr", comment = "预被检验的字符串", type = java.lang.String.class) })
	@OutParams(param = {})
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "判断字符串是否符合对应属性符号", style = "判断型", type = "同步组件", comment = "判断输入的字符串里面是否满足输入的对应属性符号", date = "Mon Jul 06 10:17:42 CST 2015")
	public static TCResult checkAttributeSign(String productCode, String attr,
			String checkStr) {
		if (isEmpty(productCode) || isEmpty(attr) || isEmpty(checkStr)) {
			return TCResult.newFailureResult("BIPE0697", "入参不符合输入要求，为null");
		}

		if (!ATTRS.contains(attr)) {
			return TCResult
					.newFailureResult("BIPE0237", "属性符号数据类型参数不是有效的参数");
		}

		boolean result = false;
		try {
			if (attr.equals("n")) {
				result = isNumeric(checkStr);
			} else if (attr.equals("a")) {
				result = isLetterOrDigit(checkStr);
			} else if (attr.equals("x")) {
				result = isX(checkStr);
			} else if (attr.equals("c")) {
				result = isC(checkStr);
			} else if (attr.equals("G")) {
				result = isG(checkStr);
			} else if (attr.equals("g")) {
				result = isg(checkStr);
			} else if (attr.equals("F")) {
				result = isF(checkStr);
			} else if (attr.equals("E")) {
				result = true;
			} else {
				result = false;
			}

			if (result) {
				return TCResult.newSuccessResult();
			} else {
				return TCResult.newFailureResult("BIPE0293", "属性符号:" + attr
						+ "校验内容:" + checkStr + "检查不通过!");
			}
		} catch (UnsupportedEncodingException e) {
			AppLogger.error( e.getMessage());
			return TCResult.newFailureResult("BIPE0305", "系统异常");
		}
	}

	/**
	 * G型字符检查
	 * 
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static boolean isG(String text) throws UnsupportedEncodingException {
		if (isEmpty(text)) {
			return false;
		}

		for (char ch : text.toCharArray()) {
			byte[] bytes = String.valueOf(ch).getBytes(GBK_CODE);
			if (bytes.length != 2) {
				return false;
			}
		}
		return true;
	}

	/**
	 * g型字符检查
	 * 
	 * @param text
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static boolean isg(String text) throws UnsupportedEncodingException {
		if (isEmpty(text)) {
			return false;
		}

		if (IS_FLAG) {
			for (char ch : text.toCharArray()) {
				byte[] bytes = String.valueOf(ch).getBytes(GBK_CODE);
				if (bytes.length == 1 && !isX(String.valueOf(ch))) {
					return false;
				}
			}
		} else {
			byte[] bytes = text.getBytes(GBK_CODE);
			for (int i = 0; i < bytes.length; i++) {
				byte first = bytes[i];
				byte second = bytes[i + 1];
				if ((first & 0x80) > 0) {// 第一个字节是否为汉字字节
					if (second >= 0x40 && second <= 0xfe) {// 第二个字节是否在指定区间内
						if (RH.contains(second)) {
							return false;
						} else {// 人行要求第二字节排除以下几项
							i++;
						}
					} else {
						// 第二字节不在指定区间，检查不通过
						return false;
					}
				} else {
					// 如果非汉字又不为x字符集，则校验不通过
					if (!isX(String.valueOf((char) first))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * F型字符检查
	 * 
	 * @param text
	 * @return
	 */
	private static boolean isF(String text) {
		if (isEmpty(text)) {
			return false;
		}

		if (text.getBytes().length % 2 != 0) {
			return false;
		}

		final int size = text.length();
		for (int i = 0; i < size; i++) {
			char ch = text.charAt(i);
			if (!Character.isDigit(ch)
					&& !F.contains(Character.toUpperCase(ch))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串是否由字母或数字组成
	 * 
	 * @param text
	 * @return
	 */
	private static boolean isLetterOrDigit(String text) {
		if (isEmpty(text)) {
			return false;
		}

		final int size = text.length();
		for (int i = 0; i < size; i++) {
			if (!Character.isLetterOrDigit(text.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * X型字符检查
	 * 
	 * @param text
	 * @return
	 */
	private static boolean isX(String text) {
		if (isEmpty(text)) {
			return false;
		}

		final int size = text.length();
		for (int i = 0; i < size; i++) {
			char ch = text.charAt(i);
			if ((!Character.isLetterOrDigit(ch)) && !X.contains(ch)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * C型字符检查
	 * 
	 * @param text
	 * @return
	 */
	private static boolean isC(String text) {
		if (isEmpty(text)) {
			return false;
		}

		final int size = text.length();

		for (int i = 0; i < size; i++) {
			char ch = text.charAt(i);
			if (!(Character.isLetterOrDigit(ch)) && !C.contains(ch)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 新建HashSet
	 * 
	 * @param elements
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> HashSet<T> newHashSet(T... elements) {
		HashSet<T> result = new HashSet<T>();
		for (T element : elements) {
			result.add(element);
		}

		return result;
	}

	/**
	 * private static final boolean isChinese(char c) { Character.UnicodeBlock
	 * ub = Character.UnicodeBlock.of(c); if (ub ==
	 * Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub ==
	 * Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub ==
	 * Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub ==
	 * Character.UnicodeBlock.GENERAL_PUNCTUATION || ub ==
	 * Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub ==
	 * Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) { return true; }
	 * return false; }
	 */

	/**
	 * @param dataContext
	 *            入参|字典容器|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @param keyList
	 *            入参|关键字列表|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "dataContext", comment = "字典容器", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class),
			@Param(name = "keyList", comment = "关键字列表", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class) })
	@OutParams(param = {})
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "字典默认赋值", style = "判断型", type = "同步组件", comment = "字典中如果没有该关键字，那么该关键字对应的值将为空", date = "Mon Jul 06 15:53:50 CST 2015")
	public static TCResult getDefaultValue(JavaDict dataContext,
			JavaList keyList) {
		if (dataContext == null) {
			return TCResult.newFailureResult("BIPE0418", "请求容器类型非法(null)");
		}
		if (keyList == null) {
			return TCResult.newFailureResult("BIPE0583", "关键字列表类型非法(null)");
		}
		int keyListSize = keyList.size();
		for (int i = 0; i < keyListSize; i++) {
			Object key = keyList.get(i);
			if (!dataContext.containsKey(key)) {
				dataContext.put(key, EMPTY);
			}
		}
		return TCResult.newSuccessResult();
	}

	/**
	 * @param fixData
	 *            入参|定长串|{@link java.lang.String}
	 * @param fixLen
	 *            入参|每条长度|{@link int}
	 * @param lenFmt
	 *            入参|长度格式|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param dataFmt
	 *            入参|数据域|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param typeFmt
	 *            入参|数据域类型(String|Number|Decimal|Amount|Integer)|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param dataContext
	 *            入参|扩展字典|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "fixData", comment = "定长串", type = java.lang.String.class),
			@Param(name = "fixLen", comment = "每条长度", type = int.class),
			@Param(name = "lenFmt", comment = "长度格式", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class),
			@Param(name = "dataFmt", comment = "数据域", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class),
			@Param(name = "typeFmt", comment = "数据域类型(String|Number|Decimal|Amount|Integer)", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class),
			@Param(name = "dataContext", comment = "扩展字典", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class) })
	@OutParams(param = {})
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "拆分定长串", style = "判断型", type = "同步组件", comment = "将定长串按指定格式拆分到字典容器", date = "Mon Jul 06 16:13:33 CST 2015")
	public static TCResult splitFixData(String fixData, int fixLen,
			JavaList lenFmt, JavaList dataFmt, JavaList typeFmt,
			JavaDict dataContext) {
		if (isEmpty(fixData)) {
			TCResult.newFailureResult("BIPE0586", "入参定长串非法,为null");
		}

		if (lenFmt == null) {
			TCResult.newFailureResult("BIPE0585", "入参长度格式非法,为null");
		}

		if (dataFmt == null) {
			TCResult.newFailureResult("BIPE0391", "入参数据域非法,为null");
		}

		if (typeFmt == null) {
			TCResult.newFailureResult("BIPE0323", "入参数据域类型非法,为null");
		}

		if (dataContext == null) {
			TCResult.newFailureResult("BIPE0390", "入参数据容器非法,为null");
		}

		int fixDataLength = fixData.length();
		int fixNum = 0;

		if (fixDataLength % fixLen != 0) {
			return TCResult.newFailureResult("BIPE0550", "定长串长度校验非法");
		} else {
			fixNum = fixDataLength / fixLen;
		}

		if (lenFmt.size() != dataFmt.size()) {
			return TCResult.newFailureResult("BIPE0722", "长度格式个数与数据域格式个数不符");
		}

		JavaDict tmpData = new JavaDict();

		for (Object key : dataFmt) {
			tmpData.put(key, new JavaList());
		}

		String tempLine = EMPTY;

		int tpointer = 0;
		for (int i = 0; i < fixNum; i++) {
			if (i == 0) {
				tempLine = fixData.substring(0, fixLen);
				tpointer = fixLen;
			} else {
				tempLine = fixData.substring(tpointer, tpointer + fixLen);
				tpointer += fixLen;
			}

			String tempValue = EMPTY;

			int pointer = 0;
			for (int j = 0; j < lenFmt.size(); j++) {
				if (j == 0) {
					tempValue = tempLine.substring(0, (Integer) lenFmt.get(j));
					pointer = lenFmt.getItem(j);
				} else {
					tempValue = tempLine.substring(pointer, pointer
							+ (Integer) lenFmt.get(j));
					pointer += (Integer) lenFmt.get(j);
				}
				String type = typeFmt.getItem(j);
				if (type.equals(STRING)) {
					tempValue = tempValue.trim();
				} else if (type.equals(INTEGER)) {
					tempValue = tempValue.trim();
				} else if (type.equals(NUMBER)) {
					// 删除小数点号"."
					if (tempValue.indexOf(".") != -1) {
						TCResult result = P_Amount.deleteDot(tempValue);
						if (result.getStatus() != TCResult.STAT_SUCCESS) {
							return result;
						}
						tempValue = (String) result.getOutputParams().get(0);
					}
					tempValue = lstrip(tempValue, ZERO_STRING);
				} else if (type.equals(DECIMAL)) {
					DecimalFormat df = new DecimalFormat(ZERO_FLOAT_STRING);
					Double f = Double.parseDouble(tempValue.trim());
					tempValue = String.valueOf(df.format(f));
				} else if (type.equals(AMOUNT)) {
					tempValue = lstrip(tempValue, ZERO_STRING);
					if (tempValue.indexOf(DOT) == -1) {
						// 若无小数点，加上小数点
						// 重用P_AMOUNT组件
						TCResult result = P_Amount.insertDot(tempValue, 2);
						if (result.getStatus() != TCResult.STAT_SUCCESS) {
							return result;
						}
						tempValue = (String) result.getOutputParams().get(0);
					}
				} else {
					tempValue = tempValue.trim();
				}

				JavaList list = (JavaList) tmpData.getItem(dataFmt.get(j));
				list.add(tempValue);
			}
		}

		dataContext.putAll(tmpData);

		return TCResult.newSuccessResult();
	}

	/**
	 * 左侧替换
	 * 
	 * @param text
	 * @param stripChars
	 * @return
	 */
	private static String lstrip(String text, String stripChars) {
		int strLen;
		if (text == null || (strLen = text.length()) == 0) {
			return text;
		}
		int start = 0;
		if (stripChars == null) {
			while (start != strLen
					&& Character.isWhitespace(text.charAt(start))) {
				start++;
			}
		} else if (stripChars.isEmpty()) {
			return text;
		} else {
			while (start != strLen
					&& stripChars.indexOf(text.charAt(start)) != INDEX_NOT_FOUND) {
				start++;
			}
		}
		return text.substring(start);
	}

	/**
	 * @param dataContext
	 *            入参|数据字典|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaDict}
	 * @param fixNum
	 *            入参|条数|{@link int}
	 * @param lenFmt
	 *            入参|长度格式|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param dataFmt
	 *            入参|数据域|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param typeFmt
	 *            入参|数据域类型(String|Number|Decimal|Amount)|
	 *            {@link cn.com.agree.afa.svc.javaengine.context.JavaList}
	 * @param fixData
	 *            出参|定长串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = {
			@Param(name = "dataContext", comment = "数据字典", type = cn.com.agree.afa.svc.javaengine.context.JavaDict.class),
			@Param(name = "fixNum", comment = "条数", type = int.class),
			@Param(name = "lenFmt", comment = "长度格式", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class),
			@Param(name = "dataFmt", comment = "数据域", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class),
			@Param(name = "typeFmt", comment = "数据域类型(String|Number|Decimal|Amount)", type = cn.com.agree.afa.svc.javaengine.context.JavaList.class) })
	@OutParams(param = { @Param(name = "fixData", comment = "定长串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "拼接定长串", style = "判断型", type = "同步组件", comment = "将按指定格式拼接定长串", date = "Wed Jul 15 15:05:39 CST 2015")
	public static TCResult createFixData(JavaDict dataContext, int fixNum,
			JavaList lenFmt, JavaList dataFmt, JavaList typeFmt) {
		if (lenFmt == null) {
			TCResult.newFailureResult("BIPE0585", "入参长度格式非法,为null");
		}
		if (dataFmt == null) {
			TCResult.newFailureResult("BIPE0391", "入参数据域非法,为null");
		}
		if (typeFmt == null) {
			TCResult.newFailureResult("BIPE0323", "入参数据域类型非法,为null");
		}
		if (dataContext == null) {
			TCResult.newFailureResult("BIPE0723", "入参数据字典非法,为null");
		}
		StringBuilder fixDataBuilder = new StringBuilder();
		for (int i = 0; i < fixNum; i++) {
			StringBuilder tempLineBuilder = new StringBuilder();
			int lenFmtSize = lenFmt.size();
			for (int j = 0; j < lenFmtSize; j++) {
				Object dataFmtKey = dataFmt.get(j);
				if (fixNum == 1) {
					Object dataContextValue = dataContext.get(dataFmtKey);
					if (dataContextValue instanceof String) {
						JavaList list = new JavaList(dataContextValue);
						dataContext.put(dataFmtKey, list);
					}
				}
				String tempValue = ((JavaList) dataContext.get(dataFmtKey))
						.getItem(i);
				String type = typeFmt.getItem(j);
				int padLen = (Integer) lenFmt.getItem(j) - tempValue.length();
				if (type.equalsIgnoreCase(STRING)) {
					tempValue = ljust(tempValue, padLen, WHITE_SPACE_CHAR);
				} else if (type.equalsIgnoreCase(NUMBER)) {
					if (tempValue.indexOf(DOT) != -1) {
						TCResult result = P_Amount.deleteDot(tempValue);
						if (result.getStatus() != TCResult.STAT_SUCCESS) {
							return result;
						}
						tempValue = (String) result.getOutputParams().get(0);
					}
					tempValue = rjust(tempValue, padLen, ZERO_CHAR);
				} else if (type.equalsIgnoreCase(DECIMAL)) {
					tempValue = rjust(tempValue, padLen, ZERO_CHAR);
				} else if (type.equalsIgnoreCase(AMOUNT)) {
					if (tempValue.indexOf(DOT) != -1) {
						TCResult result = P_Amount.insertDot(tempValue, 2);
						if (result.getStatus() != 1) {
							return result;
						}
						tempValue = (String) result.getOutputParams().get(0);
					}
					tempValue = rjust(tempValue, padLen, ZERO_CHAR);
				} else {
					tempValue = ljust(tempValue, padLen, WHITE_SPACE_CHAR);
				}
				tempLineBuilder.append(tempValue);
			}
			fixDataBuilder.append(tempLineBuilder.toString());
		}
		return TCResult.newSuccessResult(fixDataBuilder.toString());
	}

	/**
	 * @param ustring
	 *            入参|需转换字符串|{@link java.lang.String}
	 * @param rstring
	 *            出参|转换后字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "ustring", comment = "需转换字符串", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "rstring", comment = "转换后字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "字符串全角转半角", style = "判断型", type = "同步组件", comment = "字符串全角转半角，半角字符原样返回", date = "Wed Jul 08 15:19:28 CST 2015")
	public static TCResult q2b(String ustring) {
		if (ustring == null) {
			return TCResult.newFailureResult("BIPE0605",
					"入参需转换字符串参数非法，为null");
		}

		char[] chars = ustring.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '\u3000') {
				chars[i] = ' ';
			} else if (chars[i] > '\uFF00' && chars[i] < '\uFF5F') {
				chars[i] = (char) (chars[i] - 65248);
			}
		}
		return TCResult.newSuccessResult(new String(chars));
	}

	/**
	 * @param ustring
	 *            入参|需转换字符串|{@link java.lang.String}
	 * @param rstring
	 *            出参|转换后字符串|{@link java.lang.String}
	 * @return 0 失败<br/>
	 *         1 成功<br/>
	 */
	@InParams(param = { @Param(name = "ustring", comment = "需转换字符串", type = java.lang.String.class) })
	@OutParams(param = { @Param(name = "rstring", comment = "转换后字符串", type = java.lang.String.class) })
	@Returns(returns = { @Return(id = "0", desp = "失败"),
			@Return(id = "1", desp = "成功") })
	@Component(label = "字符串半角转全角", style = "判断型", type = "同步组件", comment = "把字符串半角转全角，全角字符原样返回", date = "Wed Jul 08 15:21:09 CST 2015")
	public static TCResult b2q(String ustring) {
		if (ustring == null) {
			return TCResult.newFailureResult("BIPE0605",
					"入参需转换字符串参数非法，为null");
		}

		char[] chars = ustring.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == ' ') {
				chars[i] = '\u3000';
			} else if (chars[i] < '\177') {
				chars[i] = (char) (chars[i] + 65248);
			}
		}
		return TCResult.newSuccessResult(new String(chars));
	}

	/**
	 * @param inStr
	 *            入参|输入的字符串|{@link java.lang.String}
	 * @param compareStr
	 *            入参|要对比的子串|{@link java.lang.String}
	 * @param start
	 *            入参|要截取的起始位，第一位为0，以此类推|{@link int}
	 * @param length
	 *            入参|要截取的长度|{@link int}
	 * 
	 * @return 0 不相等<br/>
	 *         1 相等<br/>
	 *         2 异常<br/>
	 */
	@InParams(param = {
			@Param(name = "inStr", comment = "输入的字符串", type = java.lang.String.class),
			@Param(name = "compareStr", comment = "要对比的子串", type = java.lang.String.class),
			@Param(name = "start", comment = "要截取的起始位，第一位为0，以此类推", type = int.class),
			@Param(name = "length", comment = "要截取的长度", type = int.class) })
	@Returns(returns = { @Return(id = "0", desp = "不存在"),
			@Return(id = "1", desp = "存在"), @Return(id = "2", desp = "异常") })
	@Component(label = "字符串截取子串是否存在", style = "判断型", type = "同步组件", comment = "字符串截取子串是否存在，比如字符串，“1112222”，比如截取1122，如果开始位置为1，最后位置为4，则为true，否则为false，异常出口为2", date = "2016-01-04 03:06:43")
	public static TCResult P_IndexEquals(String inStr, String compareStr,
			int start, int length) {
		if (inStr == null) {
			return new TCResult(2, "BIPE0714", "入参字符串参数inStr非法，为null");
		}
		try {
			String subString = inStr.substring(start, start + length);
			if (subString.equals(compareStr)) {
				return new TCResult(1);
			} else {
				return new TCResult(0);
			}
		} catch (Exception e) {
			AppLogger.error( e.getMessage());
			return TCResult.newFailureResult("BIPE0305", "系统异常");
		}
	}

}
