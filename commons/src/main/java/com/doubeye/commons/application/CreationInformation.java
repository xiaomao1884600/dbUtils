package com.doubeye.commons.application;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author doubeye
 * @version 1.0.0
 * 自我维护类的创建信息
 */
@SuppressWarnings("unused")
public class CreationInformation {
    /**
     * 创建时间
     */
    private Date createDate = new Date();
    /**
     * 执行销毁的助手类
     */
    private Class closeHelperClass;
    /**
     * 执行销毁的助手方法
     */
    private Method closeHelperMethod;
    /**
     * 应该被销毁的对象
     */
    private Object closable;

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Class getCloseHelperClass() {
        return closeHelperClass;
    }

    public void setCloseHelperClass(Class closeHelperClass) {
        this.closeHelperClass = closeHelperClass;
    }

    public Method getCloseHelperMethod() {
        return closeHelperMethod;
    }

    public void setCloseHelperMethod(Method closeHelperMethod) {
        this.closeHelperMethod = closeHelperMethod;
    }

    public Object getClosable() {
        return closable;
    }

    public void setClosable(Object closable) {
        this.closable = closable;
    }
}
