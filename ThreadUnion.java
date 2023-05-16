package com.epam.rd.autotasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public interface ThreadUnion extends ThreadFactory {
    int totalSize();

    int activeSize();
    void awaitTermination();
    void shutdown();

    boolean isShutdown();


    boolean isFinished();

    List<FinishedThreadResult> results();

    static ThreadUnion newInstance(String name) {
        return new ThreadUnionImpl(name);
    }

     class ThreadUnionImpl implements ThreadUnion {

        private final String name;
        private final List<Thread> threads;
        private final List<FinishedThreadResult> results;
        private final AtomicInteger totalThreads;
        private final AtomicInteger activeThreads;
        private final AtomicBoolean shutdown;
         private final ScheduledExecutorService executorService;

         public ThreadUnionImpl(String name) {
            this.name = name;
            this.threads = new ArrayList<>();
            this.results = new ArrayList<>();
            this.totalThreads = new AtomicInteger(0);
            this.activeThreads = new AtomicInteger(0);
            this.shutdown = new AtomicBoolean(false);
             this.executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

         }
        @Override
        public Thread newThread(Runnable runnable) {
            if (shutdown.get()) {
                throw new IllegalStateException("ThreadUnion has been shutdown");
            }
            Thread thread = new Thread(() -> {
                try {
                    runnable.run();

                } catch (Throwable t) {
                    results.add(new FinishedThreadResult(Thread.currentThread().getName(), t));
                } finally {
//                    executorService.execute(thread);
                    activeThreads.decrementAndGet();
                }
            }, name + "-worker-" + totalThreads.getAndIncrement());

            // Установка обработчика исключений
            thread.setUncaughtExceptionHandler((t, e) -> {
                results.add(new FinishedThreadResult(t.getName(), e));
                activeThreads.decrementAndGet();

            });

            threads.add(thread);
            activeThreads.incrementAndGet();
            executorService.execute(thread);
            return thread;
        }

        @Override
        public int totalSize() {
            return threads.size();
        }

         @Override
         public int activeSize() {
             int count = 0;
             for (Thread thread : Thread.getAllStackTraces().keySet()) {
                 if (thread.getName().startsWith(name) ) {
                     count++;
                 }
             }
             return count;
         }
         @Override
        public  void awaitTermination() {
             for (Thread thread : threads) {
                 try {
                     thread.join();
                 } catch (InterruptedException e) {
                     throw new RuntimeException(e);
                 }
             }
        }
             @Override
        public void shutdown() {
            if (shutdown.getAndSet(true)) {
                // already shut down
                return;
            }
                 executorService.shutdownNow();
             }

        @Override
        public boolean isShutdown() {
            return shutdown.get();
        }


        @Override
        public boolean isFinished() {
            if (!shutdown.get()) {
                return false;
            }
            for (Thread thread : threads) {
                if (thread.isAlive() && !thread.getName().startsWith(name + "-worker-")) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public List<FinishedThreadResult> results() {
            List<FinishedThreadResult> finishedThreads = new ArrayList<>();
            for (Thread thread : threads) {
                if (!thread.isAlive()) {
                    Throwable throwable = null;
                    if (thread.getUncaughtExceptionHandler() instanceof FinishedThreadResult) {
                        FinishedThreadResult result = (FinishedThreadResult) thread.getUncaughtExceptionHandler();
                        throwable = result.getThrowable();
                    }
                    finishedThreads.add(new FinishedThreadResult(thread.getName(), throwable));
                }
            }
            return finishedThreads;
        }
    }
}

