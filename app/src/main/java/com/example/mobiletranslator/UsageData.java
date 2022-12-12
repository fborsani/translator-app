package com.example.mobiletranslator;

import com.deepl.api.Usage;

import java.text.NumberFormat;
import java.util.Locale;

public class UsageData {
    private final boolean limitReached;
    private final boolean charLimitActive;
    private final boolean docLimitActive;

    private final long charLimit;
    private final long charCount;
    private final long docLimit;
    private final long docCount;

    private final NumberFormat nf = NumberFormat.getPercentInstance(Locale.getDefault());

    public UsageData(Usage usage){
        limitReached = usage.anyLimitReached();

        if(usage.getCharacter() != null){
            charLimitActive = true;
            charLimit = usage.getCharacter().getLimit();
            charCount = usage.getCharacter().getCount();
        }
        else{
            charLimitActive = false;
            charLimit = -1;
            charCount = -1;
        }

        if(usage.getDocument() != null){
            docLimitActive = true;
            docLimit = usage.getDocument().getLimit();
            docCount = usage.getDocument().getCount();
        }
        else{
            docLimitActive = false;
            docLimit = -1;
            docCount = -1;
        }
    }

    public boolean isLimitReached() {
        return limitReached;
    }

    public boolean isCharLimitActive() {
        return charLimitActive;
    }

    public boolean isDocLimitActive() {
        return docLimitActive;
    }

    public String getCharLimit() {
        return charLimit == -1 ?  null : String.valueOf(charLimit);
    }

    public String getCharCount() {
        return charCount == -1 ?  null : String.valueOf(charCount);
    }

    public String getDocLimit() {
        return docLimit == -1 ?  null : String.valueOf(docLimit);
    }

    public String getDocCount() {
        return docCount == -1 ?  null : String.valueOf(docCount);
    }

    public String getCharPerc() {
        if(!(charLimit == -1 && charCount == -1)){
            double quota = charCount / charLimit;
            return nf.format(quota);
        }
        else
            return null;
    }

    public String getDocPerc() {
        if(!(charLimit == -1 && charCount == -1)){
            double quota = charCount / charLimit;
            return nf.format(quota);
        }
        else
            return null;
    }
}
