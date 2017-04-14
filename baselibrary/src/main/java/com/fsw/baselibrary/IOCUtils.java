package com.fsw.baselibrary;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author fsw
 * @version 1.0
 * @teacher Darren
 * @time 2017/4/13
 * @desc IOCUtils
 */
public class IOCUtils {
    /**
     * 兼容以下三个方法
     *
     * @param helper
     * @param object 反射需要执行的类
     */
    public static void bind(ViewHelper helper, Object object) {
        injectFiled(helper, object);
        injectEvent(helper, object);
    }

    /**
     * 注入属性
     *
     * @param helper
     * @param object
     */
    private static void injectFiled(ViewHelper helper, Object object) {
        //TODO 1.获取类中所有的属性
        Class<?> clazz = object.getClass();
        //获取类中所有的属性包括private public protect 和默认的
        Field[] fields = clazz.getDeclaredFields();
        //TODO 2.获取FindViewById Annotation中的value值
        //通过循环遍历属性
        for (Field field : fields) {
            FindViewById findViewById = field.getAnnotation(FindViewById.class);
            if (findViewById != null) {
                //获取Annotation中value值
                int viewId = findViewById.value();
                //TODO 3.findViewById获取View
                View view = helper.findViewById(viewId);
                if (view != null) {
                    //能够注入任意修饰符修饰的属性
                    field.setAccessible(true);
                    //TODO 4.动态注入找到的View
                    try {
                        field.set(object, view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 注入方法
     *
     * @param helper
     * @param object
     */
    private static void injectEvent(ViewHelper helper, Object object) {
        //TODO 1.获取类中所有的方法
        Class<?> clazz = object.getClass();
        //获取类中所有的方法包括private public protect 和默认的
        Method[] methods = clazz.getDeclaredMethods();
        //TODO 2.获取OnClick注解中的value值
        for (Method method : methods) {
            OnClick onClick = method.getAnnotation(OnClick.class);
            if (onClick != null) {
                int[] ids = onClick.value();
                for (int id : ids) {
                    //TODO 3.调用findViewById获取到View
                    View view = helper.findViewById(id);
                    //TODO 拓展功能 检测网络
                    CheckNet checkNet = method.getAnnotation(CheckNet.class);
                    if (view != null) {
                        //TODO 4.设置onClickListener
                        view.setOnClickListener(new DeclaredOnClickListener(method, object, checkNet));
                    }
                }
            }
        }
    }

    /**
     * 注解的使用场景--Activity
     *
     * @param activity
     */
    public static void bind(Activity activity) {
        bind(new ViewHelper(activity), activity);
    }

    /**
     * 注解的使用场景--View
     *
     * @param view
     */
    public static void bind(View view) {
        bind(new ViewHelper(view), view);
    }

    /**
     * 注解的使用场景--Fragment
     *
     * @param view
     * @param obj
     */
    public static void bind(View view, Object obj) {
        bind(new ViewHelper(view), obj);
    }

    private static class DeclaredOnClickListener implements View.OnClickListener {

        private Object object;

        private Method method;

        private CheckNet checkNet;

        public DeclaredOnClickListener(Method method, Object object, CheckNet checkNet) {
            this.object = object;
            this.method = method;
            this.checkNet = checkNet;
        }


        @Override
        public void onClick(View v) {
            //TODO 5.反射执行方法
            boolean isCheck = checkNet != null;
            //判断是否需要判断网络状况
            if (isCheck) {//代表需要
                String toastStr = checkNet.value();
                if (!networkIsAvailable(v.getContext())) {
                    Toast.makeText(v.getContext(), toastStr, Toast.LENGTH_SHORT).show();
                    return;
                }
            }


            try {
                method.setAccessible(true);
                method.invoke(object, v);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                try {
                    method.invoke(object);
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                try {
                    method.invoke(object);
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                try {
                    method.invoke(object);
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static boolean networkIsAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

}
