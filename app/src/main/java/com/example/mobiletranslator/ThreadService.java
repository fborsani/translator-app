package com.example.mobiletranslator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class ThreadService {
    public static Object execute(Callable<Object> callable){
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Object> result = executorService.submit(callable);
            return result.get();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
            e.getCause();
            return null;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
