package com.example.mobiletranslator.db;

import android.provider.BaseColumns;

public final class Contract {
    private Contract(){}

    public static class Param implements BaseColumns {
        public static final String TABLE_NAME = "param";
        public static final String COLUMN_NAME_DESCR = "descr";
        public static final String COLUMN_NAME_VALUE = "value";
    }

    public static class Language implements BaseColumns {
        public static final String TABLE_NAME = "language";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ISO_CODE = "iso_code";
        public static final String COLUMN_NAME_ISO_CODE3 = "iso_code3";
        public static final String COLUMN_NAME_VISIBILITY = "visibility";
        public static final String COLUMN_NAME_SUPPORT_FORMAL = "support_formal";
        public static final String COLUMN_OCR_FILENAME = "ocr_filename";
        public static final String COLUMN_NAME_INSTALLED = "installed";
    }

    public static class LanguageOptionVisibility{
        public static final String VISIBILITY_BOTH = "both";
        public static final String VISIBILITY_IN = "in";
        public static final String VISIBILITY_OUT = "out";
    }
}
