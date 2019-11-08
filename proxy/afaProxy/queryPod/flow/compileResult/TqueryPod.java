package afaProxy.queryPod;

import cn.com.agree.afa.svc.javaengine.AppLogger;
import cn.com.agree.afa.svc.javaengine.AppLogger.LogLevel;
import cn.com.agree.afa.svc.javaengine.BCScript;
import cn.com.agree.afa.svc.javaengine.EndNode;
import cn.com.agree.afa.svc.javaengine.INode;
import cn.com.agree.afa.svc.javaengine.JScript;
import cn.com.agree.afa.svc.javaengine.TCResult;
import cn.com.agree.afa.svc.javaengine.context.JavaContext;
import cn.com.agree.afa.svc.javaengine.context.JavaDict;
import cn.com.agree.afa.svc.javaengine.context.JavaList;
import cn.com.agree.afa.util.ExceptionUtils;
import cn.com.agree.afa.util.future.IFuture;
import cn.com.agree.afa.util.future.IFutureListener;
import java.util.ArrayList;
import java.util.List;
import static cn.com.agree.afa.jcomponent.GlobalErrorHolder.setGlobalError;
import tc.platform.P_HttpCodec;
import tc.platform.P_Json;
import tc.platform.k8s.P_Communication;
import tc.platform.k8s.P_Init;

/**
 * 应用描述：和afa交互的代理逻辑 <br/>
 *
 * 交易描述：查询pod信息 <br/>
 *
 * 创建时间：2018-11-24 02:34 <br/>
 *
 * 修改时间：2018-11-30 05:54 <br/>
 *
 * @author  <br/>
 * @version 1.0 <br/>
 *
 */
public class TqueryPod extends JScript {
    protected INode startStep;
    protected long startTime;
     
    public TqueryPod(JavaDict __REQ__, JavaDict __RSP__) {
        super(__REQ__, __RSP__);
    }
    
    protected INode getNextStep(int id) {
        INode step = null;
		switch (id) {
			case 1	: step = new Step1(); break;
			case 2	: step = new Step2(); break;
			case 3	: step = new Step3(); break;
		}
		return step;
	}
	
    @Override
    public synchronized INode execute() {
        String uuid = __REQ__.getStringItem("__UUID__", "");
        if (startStep == null) {
            startTime = System.currentTimeMillis();
            __REQ__.setItem("__TRADENAME__", "查询pod信息");
            log(LogLevel.INFO, "log start:" + uuid);
            log(LogLevel.INFO, "开始交易  queryPod:查询pod信息");
            printTrace();
            if (__REQ__.getStringItem("__SESSION_ID__") != null) {
            	log(LogLevel.INFO,
                "渠道请求ID:" + __REQ__.getStringItem("__SESSION_ID__") + ",线程名:"
                        + __REQ__.getStringItem("__THREAD_NAME__") + ",本机地址:"
                        + __REQ__.getStringItem("__LOCAL_IP__"));
            }
            startStep = new Step1();
        } else {
            log(LogLevel.INFO, "唤醒交易");
        }

        INode step = startStep;
        while (canExecute(step)) {
            step = step.execute();
        }

        if (step == EndNode.SUSPEND_END) {
            log(LogLevel.INFO, "挂起交易");
        } else {
            log(LogLevel.INFO, "结束交易 queryPod");
            long endTime = System.currentTimeMillis();
            long scriptExecutedTime = endTime - startTime;
            long appExecutedTime = endTime - getContext().getCreationTime();
            log(LogLevel.INFO, "脚本执行时间:" + scriptExecutedTime + "毫秒");
            log(LogLevel.INFO, "交易处理时间:" + appExecutedTime + "毫秒");
            __REQ__.setItem("__SCRIPT_EXECUTED_TIME__", scriptExecutedTime);
            __REQ__.setItem("__APP_EXECUTED_TIME__", appExecutedTime);
        }

        if (step instanceof EndNode) {
            return step;
        } else {
            return EndNode.EXCEPTION_END;
        }
    }
    protected class Step1 implements INode {
        protected INode startNode;
        protected long startTime;
        @Override
        public INode execute() {
            if (startNode == null) {
            	startTime = System.currentTimeMillis();
                log(LogLevel.valueOf(2), "Step1 拆包");
                startNode = new Node1();
                setExceptionHandler(null);
            }
            
            INode node = startNode;
            while (canExecute(node)) {
                node = node.execute();
            }
            
            if (node == EndNode.SUSPEND_END) {
                startStep = this;
                return node;
            }
            if (node instanceof EndNode) {
            	gatherStat("Step1", "拆包", ((EndNode) node).getType(), startTime);
            }
            
            if (node instanceof EndNode) {
                switch (((EndNode) node).getType()) {
                case 0:
                    return getNextStep(3);
                case 1:
                    return getNextStep(2);
                }
            }
            return node;
        }
        private class Node1 implements INode {
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step1_Node1 开始");
                return new Node2();
            }    
        }
        
        private class Node2 implements INode {
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step1_Node2 默认逻辑错误委托");
                setExceptionHandler(new Node4());
                log(LogLevel.valueOf(2), "将默认异常委托到Node4节点");
                return new Node5();
            }    
        }
        
        private class Node3 implements INode {
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step1_Node3 正常结束");
                return EndNode.NORMAL_END;
            }    
        }
        
        private class Node4 implements INode {
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step1_Node4 失败结束");
                setExceptionHandler(null);
                return EndNode.EXCEPTION_END;
            }    
        }
        
        private class Node5 implements INode {
            private long startTime;
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step1_Node5 设置jsonDict");
                startTime = System.currentTimeMillis();
                try {
                    TCResult result = P_Init.P_setJsonDict();
                    if (result == null) {
                        log(LogLevel.ERROR, "技术组件返回值不能为空");
                    	gatherStat("Step1_Node5", "设置jsonDict", startTime, "技术组件返回值不能为空");
                        return getExceptionHandler(new Node4());
                    }
                    
                    int status = result.getStatus();
                   log(LogLevel.valueOf(2), "逻辑返回值=" + status);
                    
                    if (result.getErrorCode() != null || result.getErrorMsg() != null) {
                        setGlobalError("D", result.getErrorCode(), result.getErrorMsg());
                    }
                    
                    List<?> outputParams = result.getOutputParams();
                    if (outputParams != null) {
                        if (outputParams.size() != 1) {
                            log(LogLevel.ERROR, "出参的实参个数与形参个数不一致");
                        	gatherStat("Step1_Node5", "设置jsonDict", startTime, "出参的实参个数与形参个数不一致");
                            return getExceptionHandler(new Node4());
                        }
                        __REQ__.setItem("jsonDict", outputParams.get(0));
                        logVar(LogLevel.valueOf(4), "出参0", outputParams.get(0));
                    }
                	gatherStat("Step1_Node5", "设置jsonDict", status, startTime);
                    switch (status) {
                    case 1:
                        return new Node6();
                    default:
                        return getExceptionHandler(new Node4());
                    }
                } catch (Throwable e) {
                	gatherStat("Step1_Node5", "设置jsonDict", startTime, ExceptionUtils.toDetailString(e));
                    setGlobalError("E", "ACMP0E001", e.toString());
                    log(LogLevel.ERROR, e);
                    INode exceptionHandler = getExceptionHandler(new Node4());
                    if (exceptionHandler == null) {
                    	throw new RuntimeException(e.getMessage(), e);
                    }
                    return exceptionHandler;
                }
            }    
        }
        
        private class Node6 implements INode {
            private long startTime;
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step1_Node6 提取请求报文体");
                startTime = System.currentTimeMillis();
                try {
                    JavaDict _arg0_ = __REQ__;
                    log(LogLevel.valueOf(4), "入参0=__REQ__");
                    String _arg1_ = "utf-8";
                    logVar(LogLevel.valueOf(4), "入参1", _arg1_);
                    TCResult result = P_HttpCodec.P_unpackRequest(_arg0_, _arg1_);
                    if (result == null) {
                        log(LogLevel.ERROR, "技术组件返回值不能为空");
                    	gatherStat("Step1_Node6", "提取请求报文体", startTime, "技术组件返回值不能为空");
                        return getExceptionHandler(new Node4());
                    }
                    
                    int status = result.getStatus();
                   log(LogLevel.valueOf(2), "逻辑返回值=" + status);
                    
                    if (result.getErrorCode() != null || result.getErrorMsg() != null) {
                        setGlobalError("D", result.getErrorCode(), result.getErrorMsg());
                    }
                    
                    List<?> outputParams = result.getOutputParams();
                    if (outputParams != null) {
                        if (outputParams.size() != 1) {
                            log(LogLevel.ERROR, "出参的实参个数与形参个数不一致");
                        	gatherStat("Step1_Node6", "提取请求报文体", startTime, "出参的实参个数与形参个数不一致");
                            return getExceptionHandler(new Node4());
                        }
                        __REQ__.setItem("requestBody", outputParams.get(0));
                        logVar(LogLevel.valueOf(4), "出参0", outputParams.get(0));
                    }
                	gatherStat("Step1_Node6", "提取请求报文体", status, startTime);
                    switch (status) {
                    case 1:
                        return new Node7();
                    default:
                        return getExceptionHandler(new Node4());
                    }
                } catch (Throwable e) {
                	gatherStat("Step1_Node6", "提取请求报文体", startTime, ExceptionUtils.toDetailString(e));
                    setGlobalError("E", "ACMP0E001", e.toString());
                    log(LogLevel.ERROR, e);
                    INode exceptionHandler = getExceptionHandler(new Node4());
                    if (exceptionHandler == null) {
                    	throw new RuntimeException(e.getMessage(), e);
                    }
                    return exceptionHandler;
                }
            }    
        }
        
        private class Node7 implements INode {
            private long startTime;
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step1_Node7 json串转换javaDict");
                startTime = System.currentTimeMillis();
                try {
                    JavaDict _arg0_ = __REQ__.getItem("jsonDict");
                    logVar(LogLevel.valueOf(4), "入参0", _arg0_);
                    String _arg1_ = __REQ__.getItem("requestBody");
                    logVar(LogLevel.valueOf(4), "入参1", _arg1_);
                    TCResult result = P_Json.strToDict(_arg0_, _arg1_);
                    if (result == null) {
                        log(LogLevel.ERROR, "技术组件返回值不能为空");
                    	gatherStat("Step1_Node7", "json串转换javaDict", startTime, "技术组件返回值不能为空");
                        return getExceptionHandler(new Node4());
                    }
                    
                    int status = result.getStatus();
                   log(LogLevel.valueOf(2), "逻辑返回值=" + status);
                    
                    if (result.getErrorCode() != null || result.getErrorMsg() != null) {
                        setGlobalError("D", result.getErrorCode(), result.getErrorMsg());
                    }
                    
                	gatherStat("Step1_Node7", "json串转换javaDict", status, startTime);
                    switch (status) {
                    case 1:
                        return new Node3();
                    default:
                        return getExceptionHandler(new Node4());
                    }
                } catch (Throwable e) {
                	gatherStat("Step1_Node7", "json串转换javaDict", startTime, ExceptionUtils.toDetailString(e));
                    setGlobalError("E", "ACMP0E001", e.toString());
                    log(LogLevel.ERROR, e);
                    INode exceptionHandler = getExceptionHandler(new Node4());
                    if (exceptionHandler == null) {
                    	throw new RuntimeException(e.getMessage(), e);
                    }
                    return exceptionHandler;
                }
            }    
        }
        
    
    }
    
    protected class Step2 implements INode {
        protected INode startNode;
        protected long startTime;
        @Override
        public INode execute() {
            if (startNode == null) {
            	startTime = System.currentTimeMillis();
                log(LogLevel.valueOf(2), "Step2 查询pod信息");
                startNode = new Node1();
                setExceptionHandler(null);
            }
            
            INode node = startNode;
            while (canExecute(node)) {
                node = node.execute();
            }
            
            if (node == EndNode.SUSPEND_END) {
                startStep = this;
                return node;
            }
            if (node instanceof EndNode) {
            	gatherStat("Step2", "查询pod信息", ((EndNode) node).getType(), startTime);
            }
            
            if (node instanceof EndNode) {
                switch (((EndNode) node).getType()) {
                case 0:
                    return getNextStep(3);
                case 1:
                    return getNextStep(3);
                }
            }
            return node;
        }
        private class Node1 implements INode {
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step2_Node1 开始");
                return new Node2();
            }    
        }
        
        private class Node2 implements INode {
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step2_Node2 默认逻辑错误委托");
                setExceptionHandler(new Node4());
                log(LogLevel.valueOf(2), "将默认异常委托到Node4节点");
                return new Node5();
            }    
        }
        
        private class Node3 implements INode {
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step2_Node3 正常结束");
                return EndNode.NORMAL_END;
            }    
        }
        
        private class Node4 implements INode {
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step2_Node4 失败结束");
                setExceptionHandler(null);
                return EndNode.EXCEPTION_END;
            }    
        }
        
        private class Node5 implements INode {
            private long startTime;
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step2_Node5 查询pod信息");
                startTime = System.currentTimeMillis();
                try {
                    String _arg0_ = ((JavaDict)__REQ__.getItem("jsonDict")).getItem("namespace");
                    logVar(LogLevel.valueOf(4), "入参0", _arg0_);
                    String _arg1_ = ((JavaDict)__REQ__.getItem("jsonDict")).getItem("serviceCode");
                    logVar(LogLevel.valueOf(4), "入参1", _arg1_);
                    TCResult result = P_Communication.P_getPod(_arg0_, _arg1_);
                    if (result == null) {
                        log(LogLevel.ERROR, "技术组件返回值不能为空");
                    	gatherStat("Step2_Node5", "查询pod信息", startTime, "技术组件返回值不能为空");
                        return getExceptionHandler(new Node4());
                    }
                    
                    int status = result.getStatus();
                   log(LogLevel.valueOf(2), "逻辑返回值=" + status);
                    
                    if (result.getErrorCode() != null || result.getErrorMsg() != null) {
                        setGlobalError("D", result.getErrorCode(), result.getErrorMsg());
                    }
                    
                    List<?> outputParams = result.getOutputParams();
                    if (outputParams != null) {
                        if (outputParams.size() != 1) {
                            log(LogLevel.ERROR, "出参的实参个数与形参个数不一致");
                        	gatherStat("Step2_Node5", "查询pod信息", startTime, "出参的实参个数与形参个数不一致");
                            return getExceptionHandler(new Node4());
                        }
                        __RSP__.setItem("jsonStr", outputParams.get(0));
                        logVar(LogLevel.valueOf(4), "出参0", outputParams.get(0));
                    }
                	gatherStat("Step2_Node5", "查询pod信息", status, startTime);
                    switch (status) {
                    case 1:
                        return new Node3();
                    default:
                        return getExceptionHandler(new Node4());
                    }
                } catch (Throwable e) {
                	gatherStat("Step2_Node5", "查询pod信息", startTime, ExceptionUtils.toDetailString(e));
                    setGlobalError("E", "ACMP0E001", e.toString());
                    log(LogLevel.ERROR, e);
                    INode exceptionHandler = getExceptionHandler(new Node4());
                    if (exceptionHandler == null) {
                    	throw new RuntimeException(e.getMessage(), e);
                    }
                    return exceptionHandler;
                }
            }    
        }
        
    
    }
    
    protected class Step3 implements INode {
        protected INode startNode;
        protected long startTime;
        @Override
        public INode execute() {
            if (startNode == null) {
            	startTime = System.currentTimeMillis();
                log(LogLevel.valueOf(2), "Step3 拼包");
                startNode = new Node1();
                setExceptionHandler(null);
            }
            
            INode node = startNode;
            while (canExecute(node)) {
                node = node.execute();
            }
            
            if (node == EndNode.SUSPEND_END) {
                startStep = this;
                return node;
            }
            gatherStat("Step3", "拼包", 1, startTime);
            
            return node;
        }
        private class Node1 implements INode {
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step3_Node1 开始");
                return new Node2();
            }    
        }
        
        private class Node2 implements INode {
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step3_Node2 默认逻辑错误委托");
                setExceptionHandler(new Node4());
                log(LogLevel.valueOf(2), "将默认异常委托到Node4节点");
                return new Node5();
            }    
        }
        
        private class Node3 implements INode {
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step3_Node3 正常结束");
                return EndNode.NORMAL_END;
            }    
        }
        
        private class Node4 implements INode {
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step3_Node4 失败结束");
                setExceptionHandler(null);
                return EndNode.EXCEPTION_END;
            }    
        }
        
        private class Node5 implements INode {
            private long startTime;
            
            @Override
            public INode execute() {
                log(LogLevel.valueOf(2), "Step3_Node5 HTTP响应报文拼包");
                startTime = System.currentTimeMillis();
                try {
                    String _arg0_ = "200";
                    logVar(LogLevel.valueOf(4), "入参0", _arg0_);
                    JavaDict _arg1_ = new JavaDict("content-type", "application/json");
                    logVar(LogLevel.valueOf(4), "入参1", _arg1_);
                    String _arg2_ = __RSP__.getItem("jsonStr");
                    logVar(LogLevel.valueOf(4), "入参2", _arg2_);
                    String _arg3_ = "utf-8";
                    logVar(LogLevel.valueOf(4), "入参3", _arg3_);
                    String _arg4_ = null;
                    logVar(LogLevel.valueOf(4), "入参4", _arg4_);
                    TCResult result = P_HttpCodec.packResponse(_arg0_, _arg1_, _arg2_, _arg3_, _arg4_);
                    if (result == null) {
                        log(LogLevel.ERROR, "技术组件返回值不能为空");
                    	gatherStat("Step3_Node5", "HTTP响应报文拼包", startTime, "技术组件返回值不能为空");
                        return getExceptionHandler(new Node4());
                    }
                    
                    int status = result.getStatus();
                   log(LogLevel.valueOf(2), "逻辑返回值=" + status);
                    
                    if (result.getErrorCode() != null || result.getErrorMsg() != null) {
                        setGlobalError("D", result.getErrorCode(), result.getErrorMsg());
                    }
                    
                    List<?> outputParams = result.getOutputParams();
                    if (outputParams != null) {
                        if (outputParams.size() != 1) {
                            log(LogLevel.ERROR, "出参的实参个数与形参个数不一致");
                        	gatherStat("Step3_Node5", "HTTP响应报文拼包", startTime, "出参的实参个数与形参个数不一致");
                            return getExceptionHandler(new Node4());
                        }
                        __RSP__.setItem("__SNDPCK__", outputParams.get(0));
                        logVar(LogLevel.valueOf(4), "出参0", outputParams.get(0));
                    }
                	gatherStat("Step3_Node5", "HTTP响应报文拼包", status, startTime);
                    switch (status) {
                    case 1:
                        return new Node3();
                    default:
                        return getExceptionHandler(new Node4());
                    }
                } catch (Throwable e) {
                	gatherStat("Step3_Node5", "HTTP响应报文拼包", startTime, ExceptionUtils.toDetailString(e));
                    setGlobalError("E", "ACMP0E001", e.toString());
                    log(LogLevel.ERROR, e);
                    INode exceptionHandler = getExceptionHandler(new Node4());
                    if (exceptionHandler == null) {
                    	throw new RuntimeException(e.getMessage(), e);
                    }
                    return exceptionHandler;
                }
            }    
        }
        
    
    }
    

}   