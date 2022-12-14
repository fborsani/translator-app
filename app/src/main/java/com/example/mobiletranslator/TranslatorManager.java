package com.example.mobiletranslator;

import android.content.Context;

import com.deepl.api.DeepLException;
import com.deepl.api.Formality;
import com.deepl.api.SentenceSplittingMode;
import com.deepl.api.TextTranslationOptions;
import com.deepl.api.Translator;
import com.example.mobiletranslator.db.DbManager;

import java.util.concurrent.Callable;

public class TranslatorManager {
    private final Translator translator;

    private static class TranslatorWorker implements Callable<Object> {
        private final Translator translator;
        private final String text;
        private final String langFrom;
        private final String langTo;
        private final boolean useFormal;

        public TranslatorWorker(Translator translator, String text, String langFrom, String langTo, boolean useFormal){
            this.translator = translator;
            this.text = text;
            this.langFrom = langFrom;
            this.langTo = langTo;
            this.useFormal = useFormal;
        }

        @Override
        public String call() throws DeepLException, InterruptedException{
            TextTranslationOptions options = new TextTranslationOptions();
            options.setSentenceSplittingMode(SentenceSplittingMode.NoNewlines);

            if(useFormal){
                options.setFormality(Formality.More);
            }

            return translator.translateText(text, langFrom, langTo, options).getText();
        }
    }

    private static class UsageDataWorker implements Callable<Object> {
        private final Translator translator;

        UsageDataWorker(Translator translator){
            this.translator = translator;
        }

        @Override
        public UsageData call() throws DeepLException, InterruptedException {
            return new UsageData(translator.getUsage());
        }
    }

    public TranslatorManager(Context context) throws AppException{
        DbManager dbm = new DbManager(context);
        String apiKey = dbm.getApiKey();
        if(apiKey != null && !apiKey.trim().isEmpty()) {
            translator = new Translator(apiKey);
        }
        else{
            throw new AppException("Api key is not defined");
        }
    }

    public String translate(String text, String langFrom, String langTo, boolean useFormal) throws AppException{
        return ThreadService.execute(new TranslatorWorker(translator,text,langFrom,langTo,useFormal)).toString();
    }

    public UsageData getUsageStats() throws AppException{
        return (UsageData) ThreadService.execute(new UsageDataWorker(translator));
    }
}



