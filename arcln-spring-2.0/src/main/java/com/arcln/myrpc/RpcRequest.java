package com.arcln.myrpc;

import java.io.Serializable;

public class RpcRequest implements Serializable {
    private static final long serialVersionUID = -6758012981950762073L;
    private String methodName;
    private Object[] parameterTypes;
    private String className;
    public RpcRequest(){}
    public RpcRequest(String className, String methodName, Object[] parameterTypes) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Object[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
