package com.example.fadin.myapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fadin.myapp.R;
import com.example.fadin.myapp.common.ResultDao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by alexhowe on 10/31/18.
 */

public class ForgetPwdActivity extends AppCompatActivity{
    private AutoCompleteTextView edt_mail;
    private EditText edt_username;
    private Button btn_fpwd;
    private final String fpwd_url = "http://188.131.169.241:8080/Project/Account/Forget";
    OkHttpClient my_client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            val = val.replace("\""," ");
            ResultDao resudao = (ResultDao) msg.obj;
            try {
                String r = val.substring(val.indexOf("password")+9,val.indexOf("}}"));
                Log.i("mylog", "请求结果为-->" +val);
                if (val.contains("1")){
                    Toast.makeText(ForgetPwdActivity.this, "验证成功!你的密码是"+r,Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ForgetPwdActivity.this,"验证失败！",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(ForgetPwdActivity.this,"验证失败！请稍后重试。",Toast.LENGTH_SHORT).show();
            }

            // UI界面的更新等相关操作
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpwd);
        init();
        btn_fpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
       // regiser();
    }
    public void attemptRegister() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.
        edt_mail.setError(null);
        edt_username.setError(null);
        // Store values at the time of the login attempt.
        String mail = String.valueOf(edt_mail.getText());
        String username = String.valueOf(edt_username.getText());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.

        if (!TextUtils.isEmpty(username) && !isUsernameValid(username)) {
            edt_username.setError(getString(R.string.error_invalid_username));
            focusView = edt_username;
            cancel = true;
        }

        if(TextUtils.isEmpty(username)) {
            edt_username.setError(getString(R.string.error_field_required));
            focusView = edt_username;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mail)) {
            edt_mail.setError(getString(R.string.error_field_required));
            focusView = edt_mail;
            cancel = true;
        } else if (!isEmailValid(mail)) {
            edt_mail.setError(getString(R.string.error_invalid_email));
            focusView = edt_mail;
            cancel = true;
        }
        Log.e("ERR", String.valueOf(cancel));
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.

            focusView.requestFocus();
        } else {
            Runnable networkTask = new Runnable() {

                @Override
                public void run() {
                    // 在这里进行 http request.网络请求相关操作

//                        String data1 = "{email:"+mail+",username:"+username+",password:"+pwd1+",password1:"+pwd2+"}";
//
                    String nrg_url = fpwd_url +"?email="+mail+"&username="+username;
                    try {

                        String result = get(nrg_url);
                        Message msg = new Message();
                        Bundle data = new Bundle();
//                        result= result.replace(":{",":/");
//                        result=result.replace("}}","]}");
//                        String r = result.substring(result.indexOf("password"),result.indexOf("}}"));
                        Log.i("mylog1", "请求结果为-->" +result);
                        data.putString("value", "请求结果:"+result);
//                        result.replace("{","[");
//                        result.replace("}","]");
                        if (result.equals("")){
                            return;
                        }else if (!result.contains("code")){
                            Toast.makeText(ForgetPwdActivity.this,"Sever Error, Please Try Again Later.",Toast.LENGTH_SHORT).show();
                            return;
                        }
//                        Gson gson = new Gson();
//                        java.lang.reflect.Type type = new TypeToken<ResultDao>() {}.getType();
//                        msg.obj = gson.<ResultDao>fromJson(result, type);
//                                Log.d("rdao",resultDao.getMsg());
                        msg.setData(data);
                        handler.sendMessage(msg);
//                            Toast.makeText(RegisterActivity.this,result,Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            };
            new Thread(networkTask).start();
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            showProgress(true);
//            mAuthTask = new LoginActivity.UserLoginTask(email, password);
//            mAuthTask.execute((Void) null);
//            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
//            startActivity(intent);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
    private boolean isUsernameValid(String username) {

        return username.length() > 2 && username.equals(stringFilter(username));
    }
    public static String stringFilter(String str)throws PatternSyntaxException { // 只允许字母、数字和汉字      
        String regEx ="[^a-zA-Z0-9\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return  m.replaceAll("").trim();
    }

    String post(String url, RequestBody formbody) throws IOException {
//        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(formbody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
    String get(String url) throws IOException {
//        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            Log.e("FPWD", String.valueOf(response.code()));
            if (response.code() != 200){
                Looper.prepare();
                Toast.makeText(ForgetPwdActivity.this,"NetWork Error,Please Check Your NetWork.",Toast.LENGTH_SHORT).show();
                Looper.loop();
                return "";
            }
            return response.body().string();

        }catch (Exception e){
            Looper.prepare();
            Toast.makeText(ForgetPwdActivity.this,"NetWork Error,Please Check Your NetWork.",Toast.LENGTH_SHORT).show();
            Looper.loop();
            e.printStackTrace();

            return "";

        }

    }
    private void init() {
        edt_mail = findViewById(R.id.fpwd_email);
        edt_username = findViewById(R.id.fpwd_username);
        btn_fpwd = findViewById(R.id.fpwd_comfirm_button);
    }

    OkHttpClient client = new OkHttpClient();
}
