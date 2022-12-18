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
        public static final String COLUMN_NAME_SUPPORT_FORMAL = "support_formal";
        public static final String COLUMN_NAME_INSTALLED= "installed";
    }
}
