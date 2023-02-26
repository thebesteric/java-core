package org.example.juc;

import sun.misc.Unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;

public class UnsafeOperateClass {
    public static void main(String[] args) throws NoSuchFieldException {
        Unsafe unsafe = MyUnsafe.getUnsafe();

        // 获取静态变量字段
        Field nameField = User.class.getDeclaredField("NAME");
        Field ageField = User.class.getDeclaredField("AGE");

        // 通过静态变量字段，获取静态变量的偏移量
        long staticNameOffset = unsafe.staticFieldOffset(nameField);
        System.out.println("staticNameOffset = " + staticNameOffset);
        long staticAgeOffset = unsafe.staticFieldOffset(ageField);
        System.out.println("staticAgeOffset = " + staticAgeOffset);

        // 通过静态变量字段，获取该静态变量对应的类对象（可以通过任意静态变量获取该类的对象）
        Object staticNameBase = unsafe.staticFieldBase(nameField);
        System.out.println("staticNameBase = " + staticNameBase);

        // 检测该类是否需要初始化
        boolean shouldBeInitialized = unsafe.shouldBeInitialized(User.class);
        System.out.println("shouldBeInitialized = " + shouldBeInitialized);
        if (shouldBeInitialized) { // 需要初始化
            User user = new User();
            System.out.println("初始化成功");
        }
        shouldBeInitialized = unsafe.shouldBeInitialized(User.class);
        System.out.println("shouldBeInitialized = " + shouldBeInitialized);

        // 获取该静态变量对应的值
        Object nameValue = unsafe.getObject(staticNameBase, staticNameOffset);
        System.out.println("nameValue = " + nameValue);

        Object ageValue = unsafe.getObject(staticNameBase, staticAgeOffset);
        System.out.println("ageValue = " + ageValue);

        // ================ 动态定义 Class =================
        String filePatch = "/Users/wangweijun/mine/IdeaProjects/java-core/juc/target/classes/org/example/juc/UnsafeOperateClass$User.class";
        File file = new File(filePatch);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            // 读取文件动态创建 class
            Class<?> aClass = unsafe.defineAnonymousClass(UnsafeOperateClass.class, buffer, null);
            // 实例化
            Object obj = aClass.getDeclaredConstructor().newInstance();
            System.out.println(aClass);
            // 设置值
            aClass.getMethod("setId", String.class).invoke(obj, "999");
            // 获取值
            Object id = aClass.getMethod("getId").invoke(obj);
            System.out.println("id = " + id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println(unsafe.addressSize());
        System.out.println(unsafe.pageSize());
    }

    static class User {
        private static String NAME = "eric";
        private static Integer AGE = 18;
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
