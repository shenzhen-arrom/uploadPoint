package com.arrom.uploadpoint.aspect;


import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.arrom.uploadpoint.annotation.CommonAnnotationBase;
import com.arrom.uploadpoint.annotation.ParameterAccotion;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
public class MaiDianAspect {
    /**
     * 定义切点，然和进行匹配
     */
    @Pointcut("execution(@com.arrom.uploadpoint.aspect.MaiDianAspect * *(..))")
    public void uploadMaiDian(){}


    @Around("uploadMaiDian()")
    public void executionSettingMaiDianData(ProceedingJoinPoint joinPoint)  {
        //获取到方法的反射对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method=signature.getMethod();
        //获取到它上面的所有注解
        Annotation[] annotations=method.getAnnotations();
        Annotation[] methodParameterNameByAnnotions = getMethodParameterNameByAnnotions(method);
        //创建一个CommonAnnototaionBase
        CommonAnnotationBase commonAnnotationBase = null;
        //遍历这个方法所有的注解
        for (Annotation annotation : annotations) {
            //获取到它的类型
            Class<?> annotationType = annotation.annotationType();
            commonAnnotationBase=annotationType.getAnnotation(CommonAnnotationBase.class);
            if(commonAnnotationBase == null){
                break;
            }
        }
        if(commonAnnotationBase==null){
            try {
                joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return;
        }
        //获取到注解携带的type以及ID
        String type = commonAnnotationBase.type();
        String actionId = commonAnnotationBase.actionId();
        //获取到这个方法所有的接收的参数
        Object[] args=joinPoint.getArgs();
        JSONObject jsonObject=getData(methodParameterNameByAnnotions,args);
        //把收集起来的数据上传到服务器
        String msg = "上传埋点:"+"type: "+ type + " actionId: " +actionId + " data: " +jsonObject.toString();
        Log.e("arrom>>>>>>>",msg);
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 将采集到的数据key value 化
     * @param parameterAnnitations
     * @param args
     * @return
     */
    private JSONObject getData(Annotation[] parameterAnnitations, Object[] args) {
        JSONObject data = new JSONObject();
        if(parameterAnnitations ==null || parameterAnnitations.length<=0){
            return null;
        }
        for (int i = 0 ;i < parameterAnnitations.length;i++){
            Annotation parameterAnnitation = parameterAnnitations[i];
            if(parameterAnnitation instanceof ParameterAccotion){
                String paramName = ((ParameterAccotion) parameterAnnitation).value();
                data.put(paramName,args[i].toString());
            }
        }
        return data;
    }

    /**
     * 获取到方法参数的注解
     * @param method
     * @return
     */
    private Annotation[] getMethodParameterNameByAnnotions(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations==null|| parameterAnnotations.length==0){
            return null;
        }
        Annotation[] annotations = new Annotation[parameterAnnotations.length];
        int i= 0;
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            for (Annotation annotation : parameterAnnotation) {
                annotations[i++] = annotation;
            }
        }

        return annotations;
    }

}
