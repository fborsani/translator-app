package com.example.mobiletranslator;

import android.content.Context;

import com.deepl.api.DeepLException;
import com.deepl.api.Formality;
import com.deepl.api.TextTranslationOptions;
import com.deepl.api.Translator;
import com.example.mobiletranslator.db.DbManager;

import java.util.Objects;
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
        public String call(){
            try {
                TextTranslationOptions options = new TextTranslationOptions();
                if(useFormal){
                    options.setFormality(Formality.More);
                }
                return translator.translateText(text, langFrom, langTo, options).getText();

            }
            catch(DeepLException | InterruptedException e){
                e.printStackTrace();
                return null;
            }
        }
    }

    private static class UsageDataWorker implements Callable<Object> {
        private final Translator translator;

        UsageDataWorker(Translator translator){
            this.translator = translator;
        }

        @Override
        public UsageData call(){
            try {
                return new UsageData(translator.getUsage());
            } catch (DeepLException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public TranslatorManager(DbManager db){
        String apiKey = db.getApiKey();
        translator = new Translator(apiKey);
    }

    public TranslatorManager(String apiKey){
        translator = new Translator(apiKey);
    }

    public TranslatorManager(Context context){
        DbManager dbm = new DbManager(context);
        String apiKey = dbm.getApiKey();
        translator = new Translator(apiKey);
    }

    public String translate(String text, String langFrom, String langTo, boolean useFormal) throws AppException{
        return Objects.requireNonNull(ThreadService.execute(new TranslatorWorker(translator,text,langFrom,langTo,useFormal))).toString();
    }

    public UsageData getUsageStats() throws AppException{
        return (UsageData) Objects.requireNonNull(ThreadService.execute(new UsageDataWorker(translator)));
    }
}



