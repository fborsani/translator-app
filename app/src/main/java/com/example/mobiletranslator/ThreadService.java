package com.example.mobiletranslator;

import com.example.mobiletranslator.ui.NotificationUtility.CauseCode;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class ThreadService {
    public static Object execute(Callable<Object> callable) throws AppException{
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Object> result = executorService.submit(callable);
            return result.get();
        }
        catch (ExecutionException e) {
            AppException cause = (AppException) Objects.requireNonNull(e.getCause());
            throw new AppException(e.getCause(), cause.getCauseCode());
        }
        catch (InterruptedException e) {
            throw new AppException(e, CauseCode.THREAD_INTERRUPTED);
        }
    }
}
