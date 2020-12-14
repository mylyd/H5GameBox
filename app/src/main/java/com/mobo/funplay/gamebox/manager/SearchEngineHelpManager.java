package com.mobo.funplay.gamebox.manager;

import android.text.TextUtils;
import android.util.Log;

import java.net.URLEncoder;
import java.util.Locale;

public class SearchEngineHelpManager {
    public final static String SEARCH_GOOGLE = "http://www.google.com/search?hl={language}&ie={inputEncoding}&source=android-browser&q={searchTerms}";

    private static String TAG = "SearchEngineUtil";

    // google请求模板里面一些参数.
    private static final String PARAMETER_LANGUAGE_COUNTRY = "{language-country}";
    private static final String PARAMETER_SEARCH_TERMS = "{searchTerms}";
    private static final String PARAMETER_INPUT_ENCODING = "{inputEncoding}";
    private static final String PARAMETER_LANGUAGE = "{language}";
    private static final String PARAMETER_COUNTRY = "{country}";
    private static final String DEFAULT_ENCODE = "UTF-8";


    /**
     * 将关键词调用google模板进行搜索
     *
     * @param templateUri Google搜索模板
     * @param query       关键字
     * @param encode      编码
     * @return
     */
    public static String getFormattedUri(String templateUri, String query, String encode) {

        String result = null;
        if (TextUtils.isEmpty(templateUri)) {
            return null;
        }
        // Add the current language/country information to the URIs.
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        StringBuilder localeBuilder = new StringBuilder(language);
        if (!TextUtils.isEmpty(country)) {
            localeBuilder.append('-');
            localeBuilder.append(country);
        }
        String localeStr = localeBuilder.toString();
        String temp = templateUri.replace(PARAMETER_LANGUAGE_COUNTRY, localeStr);
        temp = temp.replace(PARAMETER_LANGUAGE, localeBuilder.toString());
        temp = temp.replace(PARAMETER_COUNTRY, country);

        // Encode the query terms in the requested encoding
        if (TextUtils.isEmpty(encode)) {
            encode = DEFAULT_ENCODE;
        }
        temp = temp.replace(PARAMETER_INPUT_ENCODING, encode);

        try {
            result = temp.replace(PARAMETER_SEARCH_TERMS, URLEncoder.encode(query, encode));
        } catch (java.io.UnsupportedEncodingException e) {
            Log.e(TAG, String.format("Exception occured when encoding query s% to s%", query, encode));
        }
        return result;
    }

}
