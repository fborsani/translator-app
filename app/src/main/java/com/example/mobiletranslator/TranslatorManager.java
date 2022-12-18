package com.example.mobiletranslator;

import com.deepl.api.DeepLException;
import com.deepl.api.Translator;
import com.example.mobiletranslator.db.DbManager;

public class TranslatorManager {
    private final Translator translator;

    public TranslatorManager(DbManager db){
        String authKey = db.getApiKey();
        translator = new Translator(authKey);
    }

    public String translate(String text, String langFrom, String langTo){
        try {
            return translator.translateText(text, langFrom, langTo).getText();
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



