package com.mobo.funplay.gamebox.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.manager.SearchEngineHelpManager;

/**
 * @author : ydli
 * @time : 19-12-9 上午9:14
 * @description WebView搜索
 */
public class WebViewSearchDialog extends BaseDialog {

    private OnSearchClick onSearchClick;
    private Activity activity;
    private EditText editText;
    private ImageView editClose;


    public WebViewSearchDialog(Activity activity) {
        super(activity, R.style.dialog_soft_input);
        this.activity = activity;
    }

    public interface OnSearchClick {
        void SearchOnClick(String uri);
    }

    public void setOnSearchClickListener(OnSearchClick click) {
        this.onSearchClick = click;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_web_search);

        Window window = this.getWindow();
        window.setGravity(Gravity.TOP);
        window.setWindowAnimations(R.style.top_dialog_anim_style);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //设置dialog横向占比为全屏
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        setCanceledOnTouchOutside(true);

        initView();
        setEditActionListener();


    }

    private void initView() {
        editText = findViewById(R.id.web_edit);
        editClose = findViewById(R.id.iv_close);
        editClose.setOnClickListener(v -> {
            if (editText != null && editText.length() != 0) {
                editText.setText(null);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //内容输出前
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //内容输出中
                if (count == 0) {
                    editClose.setVisibility(View.GONE);
                } else if (editClose.getVisibility() == View.GONE) {
                    editClose.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //内容输出后
            }
        });
    }

    /**
     * 监听软件盘，控制软键盘回车按钮完成搜索功能
     */
    private void setEditActionListener() {
        editText.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 在这里写搜索的操作,一般都是网络请求数据
                String forUri = editText.getText().toString();
                if (!TextUtils.isEmpty(forUri)) {
                    //如果是链接则拼接https进行请求，如果是关键字则匹配关键字进行搜索
                    onSearchClick.SearchOnClick(isURL(forUri) ? uriHttpFirst(forUri) : getSearchUrl(forUri));
                    hideKeyboard(editText);
                    dismiss();
                } else {
                    Toast.makeText(activity, "Page link is empty", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void show() {
        super.show();
        if (editText != null) {
            editText.setFocusable(true);
            showKeyboard(editText);
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param view :一般为EditText
     */
    public void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示软键盘
     *
     * @param view :一般为EditText
     */
    private void showKeyboard(View view) {
        view.postDelayed(() -> {

            final InputMethodManager manager = (InputMethodManager) view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.showSoftInput(editText, 0);
        }, 10);
    }

    //地址HTTP协议判断，无HTTP打头的，增加http://，并返回。
    private String uriHttpFirst(String strUri) {
        if (strUri.indexOf("http://", 0) != 0 &&
                strUri.indexOf("https://", 0) != 0) {
            strUri = "https://" + strUri;
        }
        return strUri;
    }

    /**
     * 判断一个字符串是否为url
     *
     * @param str String 字符串
     * @return boolean 是否为url
     * @author peng1 chen
     *   
     **/
    public static boolean isURL(String str) {
        //转换为小写
        str = str.toLowerCase();
        String regex = "^((https|http|ftp|rtsp|mms)?://)"  //https、http、ftp、rtsp、mms
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 例如：199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // 一级域名- .com or .museum
                + "(:[0-9]{1,5})?" // 端口号最大为65535,5位数
                + "((/?)|" // 如果没有文件名则不需要斜杠
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return str.matches(regex);
    }

    /**
     * 如果是关键词，则打开google引擎进行搜索
     *
     * @param url
     * @return
     */
    public static String getSearchUrl(String url) {
        String searchOriginalUrl = SearchEngineHelpManager.SEARCH_GOOGLE;
        String searchEncode = "";
        return SearchEngineHelpManager.getFormattedUri(searchOriginalUrl, url, searchEncode);
    }
}
