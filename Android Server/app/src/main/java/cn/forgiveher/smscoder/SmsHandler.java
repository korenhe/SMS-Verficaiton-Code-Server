package cn.forgiveher.smscoder;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.forgiveher.model.Host;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SmsHandler extends Handler {

    private SmsResponseCallback mCallback;
    public static int[] default_ip = {192, 168, 0, 111, 8001};

    public String getVerify_url() {
        return verify_url;
    }

    private String verify_url;

    private List<Host> list = new ArrayList<>();

    public void initData(Context context) {
        LitePal.initialize(context);
        list = LitePal.findAll(Host.class);

        if (list.size() == 0) {
            // set default url
            verify_url = "http://" +
                    default_ip[0] + "." +
                    default_ip[1] + "." +
                    default_ip[2] + "." +
                    default_ip[3] + ":" +
                    default_ip[4] + "/api/sms/";
        } else {
            Host last = LitePal.findLast(Host.class);
            verify_url = last.getIp();
        }

    }

    public void setVerify_url(String url) {
        verify_url = url;
        Host host = new Host();
        host.setIP(url);
        list.add(host);
        LitePal.saveAll(list);
    }

    /***
     * 短信过滤器
     */
    private SmsFilter smsFilter;

    public SmsHandler(SmsResponseCallback callback) {
        this.mCallback = callback;
    }

    public SmsHandler(SmsResponseCallback callback, SmsFilter smsFilter) {
        this(callback);
        this.smsFilter = smsFilter;
    }

    /***
     * 设置短信过滤器
     * @param smsFilter 短信过滤器
     */
    public void setSmsFilter(SmsFilter smsFilter) {
        this.smsFilter = smsFilter;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == SmsObserver.MSG_RECEIVED_CODE) {
            String[] smsInfos = (String[]) msg.obj;
            if (smsInfos != null && smsInfos.length == 2 && mCallback != null) {
                if (smsFilter == null) {
                    smsFilter = new DefaultSmsFilter();
                }
                /**
                 * 阿里小号取真实手机和号码
                 */
                Pattern pattern = Pattern.compile("0?(13|14|15|17|18|19|16)[0-9]{9}");
                Matcher matcher = pattern.matcher(smsInfos[1]);
                if (matcher.find()){
                    Log.i(getClass().getName(),matcher.group(0));
                    smsInfos[0] = matcher.group(0);
                }
                pattern = Pattern.compile("\\(The message is from 0?(13|14|15|17|18|19|16)[0-9]{9}\\)");
                matcher = pattern.matcher(smsInfos[1]);
                if (matcher.find()){
                    smsInfos[1] = matcher.replaceAll("");
                }
                Log.i(getClass().getName(),smsInfos[0]+smsInfos[1]);
                mCallback.onCallbackSmsContent(smsInfos[0], smsInfos[1]);
            }
        }
    }

    void submit(final String sender, final String code){
        String enstr;
        OkHttpClient okHttpClient  = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        //post方式提交的数据
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sender", sender);
            jsonObject.put("code", code);
            String json = jsonObject.toString();
            enstr = AES.encrypt(json);
            //enstr = json;
        } catch (JSONException e) {
            SqliteHelper.insert(MainActivity.database,sender,code,"json error" + "[" + e.getMessage() + "]");
            return;
        }

        /*
        FormBody formBody = new FormBody.Builder()
                .add("data", enstr)
                .add("sign", Client2Server.md5(enstr + "client!!!"))
                .build();
                */

        FormBody formBody = new FormBody.Builder()
                .add("sender", sender)
                .add("code", code)
                .build();

        Log.i("okhttp3",formBody.toString());
        final Request request = new Request.Builder()
                .url(verify_url)//请求的url
                .post(formBody)
                .build();
        final String[] result = new String[1];
        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new okhttp3.Callback() {
            //请求错误回调方法
            @Override
            public void onFailure(Call call, IOException e) {

                result[0] = "server failure";
                Log.i("okhttp3",result[0]);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("okhttp3","Response" + response.code());
                if(response.code()==200) {
                    String res = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        String message = jsonObject.getString("message");
                        result[0] = message;
                    } catch (JSONException e) {
                        result[0] = e.getMessage();
                    }
                }
                SqliteHelper.insert(MainActivity.database,sender,code,result[0]);
            }
        });
    }
}

