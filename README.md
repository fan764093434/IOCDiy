## IOC学习笔记

首先感谢老师Darren的无私分享，贴上老师的个人地址

***Darren学习群:546270670***

[Darren的BLOG](http://blog.csdn.net/z240336124/)

[Darren的简书](http://www.jianshu.com/p/c0ec2a7fc26a)

**什么是IOC？**

*IOC是Inversion of control的简写，控制反转的意思。*

**应用场景**

比如一个类中需要很多成员变量，传统的写法你要用这些变量，需要用new来实例化进行使用，而
这样写就会造成代码代码一个高耦合的现象，而使用IOC之后，你需要配置一个xml文件，里面表
明这个类后，里面用到的那些成员变量，当你在加载这个类时，IOC会帮你注入进来。

**源码分析**

我们通过查看源码分析一下XUtils和主流的黄油刀（ButterKnife）的区别，
XUtils实在编译时通过放射找到类中的带有自定义Annotation的属性和方法，
然后用类通过Annotation的value找到属性和方法然后将属性注入到类中，再
放射调用方法，而ButterKnife则是用反射获取带有Annotation的属性和方
法，在通过AbstractProcessor动态生成类，然后在编译时生成class文件，
在调用时则是直接调用生成的class文件中的属性方法，这也就是为什么用
ButterKnife时，属性和方法不能用private修饰的原因。

**优劣势**

XUtils 编译较快，调用效率不及ButterKnife，而ButterKnife则是编译效率
较慢，使用效率较快，但是他会生成很多class文件打包进入apk中，所以会导致APK
比使用XUtils要大一点，各有千秋。

**代码展示**

```
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
```
```
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
```