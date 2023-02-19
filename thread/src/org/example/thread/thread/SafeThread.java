package org.example.thread.thread;

import java.io.IOException;
import java.security.*;
import java.util.concurrent.TimeUnit;

/**
 * 解释私有线程构造函数中 AccessControlContext 的用处
 */
public class SafeThread {

    public static void main(String[] args) throws InterruptedException {
        // 正常执行
        new Thread(new SafeRunnable() {
            @Override
            public void execute() {
                System.out.println(Thread.currentThread().getName() + " run...");
            }
        }, "safe-thread-1").start();

        // 抛异常：access denied ("java.lang.RuntimePermission" "exitVM.0")
        new Thread(new SafeRunnable() {
            @Override
            public void execute() {
                System.exit(0);
            }
        }, "safe-thread-2").start();

        // 抛异常：access denied ("java.io.FilePermission" "<<ALL FILES>>" "execute")
        new Thread(new SafeRunnable() {
            @Override
            public void execute() {
                try {
                    Runtime.getRuntime().exec("rm -f /test.txt");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "safe-thread-3").start();

        TimeUnit.SECONDS.sleep(10);
    }

    public static abstract class SafeRunnable implements Runnable {

        static AccessControlContext accessControlContext;

        static {
            // 资源：所有资源
            CodeSource codeSource = new CodeSource(null, (CodeSigner[]) null);
            // 权限：所有权限
            Permissions permissions = new Permissions();
            // 保护域
            ProtectionDomain protectionDomain = new ProtectionDomain(codeSource, permissions);
            // 访问控制器
            accessControlContext = new AccessControlContext(new ProtectionDomain[]{protectionDomain});
        }

        public abstract void execute();

        @Override
        public void run() {
            // 表示当前方法需要保护
            // 一定要有锁机制，否则由于多线程导致重复 setSecurityManager，抛出异常
            if (System.getSecurityManager() == null) {
                synchronized (SafeThread.class) {
                    if (System.getSecurityManager() == null) {
                        SecurityManager securityManager = new SecurityManager();
                        System.setSecurityManager(securityManager);
                    }
                }
            }

            // 将 execute() 方法保护起来
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    execute();
                    return null;
                }
            }, accessControlContext);
        }
    }

}
