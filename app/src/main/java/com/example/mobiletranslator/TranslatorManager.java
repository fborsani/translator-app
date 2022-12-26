package com.example.mobiletranslator;

import android.content.Context;

import com.deepl.api.DeepLException;
import com.deepl.api.Formality;
import com.deepl.api.TextTranslationOptions;
import com.deepl.api.Translator;
import com.example.mobiletranslator.db.DbManager;

public class TranslatorManager {
    private final Translator translator;

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

    public String translate(String text, String langFrom, String langTo, boolean useFormal){
        try {
            TextTranslationOptions options = new TextTranslationOptions();
            if(useFormal){
                options.setFormality(Formality.More);
            }
            return translator.translateText(text, langFrom, langTo, options).getText();

        }
        catch(DeepLException e){
            return null;
        }
        catch(InterruptedException e){
            return null;
        }
    }

    public UsageData getUsageStats(){
        try {
            return new UsageData(translator.getUsage());
        } catch (DeepLException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }
}



