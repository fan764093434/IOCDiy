package com.fsw.baselibrary;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fsw
 * @version 1.0
 * @teacher Darren
 * @time 2017/4/13
 * @desc View事件的注解
 */
//代表Annotation的位置, FIELD代表放在属性上,TYPE表示放在类上,详情看ElementType类中注释
@Target(ElementType.METHOD)
//什么时候生效
@Retention(RetentionPolicy.RUNTIME)
public @interface OnClick {
    //@OnClick(R.id.text_one)
    int[] value();
}
