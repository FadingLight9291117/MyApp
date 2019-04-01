package com.example.fadin.myapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by alexhowe on 10/31/18.
 */

public class RegisterActivity extends AppCompatActivity{
    private AutoCompleteTextView edt_mail;
    private EditText edt_username;
    private EditText edt_password1;
    private EditText edt_password2;
    private Button   btn_reg;
    private final String reg_url = "http://188.131.169.241:8080/Project/Account/Register";
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

            Log.i("mylog", "请求结果为-->" +val);
            switch (resudao.getCode()){
                case "1":
                    Intent intent = new Intent();
                    //把返回数据存入Intent
                    intent.putExtra("result", String.valueOf(edt_username.getText()));
                    //设置返回数据
                    RegisterActivity.this.setResult(RESULT_OK, intent);
                    //关闭Activity
                    Toast.makeText(RegisterActivity.this,"Successfully Registered!",Toast.LENGTH_SHORT).show();
                    RegisterActivity.this.finish();

                    break;
                case "-1":
                    View focusView = null;
                    if (resudao.getMsg().equals("用户名或密码已被注册")){
                        edt_username.setError(getString(R.string.error_invalid_username1));
                        focusView = edt_username;
                    }else if(resudao.getMsg().equals("两次密码不同")){
                        edt_password1.setError(getString(R.string.error_incorrect_password1));
                        edt_password2.setError(getString(R.string.error_incorrect_password1));
                        focusView = edt_password1;

                    }
                    focusView.requestFocus();
                    Toast.makeText(RegisterActivity.this,resudao.getMsg()+"",Toast.LENGTH_LONG).show();
            }
            // UI界面的更新等相关操作
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        btn_reg.setOnClickListener(new View.OnClickListener() {
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
        edt_password1.setError(null);
        edt_password2.setError(null);
        edt_username.setError(null);
        // Store values at the time of the login attempt.
        String mail = String.valueOf(edt_mail.getText());
        String username = String.valueOf(edt_username.getText());
        String pwd1 = String.valueOf(edt_password1.getText());
        String pwd2 = String.valueOf(edt_password2.getText());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(pwd1) && !isPasswordValid(pwd1)) {
            edt_password1.setError(getString(R.string.error_invalid_password));
            focusView = edt_password1;
            cancel = true;
        }

        if(TextUtils.isEmpty(pwd1)) {
            edt_password1.setError(getString(R.string.error_field_required));
            focusView = edt_password1;
            cancel = true;
        }
        if (!TextUtils.isEmpty(pwd2) && !isPasswordValid(pwd2)) {
            edt_password2.setError(getString(R.string.error_invalid_password));
            focusView = edt_password2;
            cancel = true;
        }

        if(TextUtils.isEmpty(pwd2)) {
            edt_password2.setError(getString(R.string.error_field_required));
            focusView = edt_password2;
            cancel = true;
        }
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
                    String nrg_url = reg_url+"?email="+mail+"&username="+username+"&password="+pwd1+"&password1="+pwd2;
                    try {

                        String result = get(nrg_url);
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        Log.i("mylog1", "请求结果为-->" +result);
                        data.putString("value", "请求结果:"+result);
                        result.replace("{","[");
                        result.replace("}","]");
                        if (result.equals("")){
                            return;
                        }else if (!result.contains("code")){
                            Toast.makeText(RegisterActivity.this,"Sever Error, Please Try Again Later.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Gson gson = new Gson();
                        ResultDao resultDao = gson.fromJson(result, ResultDao.class);
                        msg.obj = resultDao;
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
            if (response.code() != 200){
                Toast.makeText(RegisterActivity.this,"NetWork Error,Please Check Your NetWork.",Toast.LENGTH_SHORT).show();
                return "";
            }
            return response.body().string();
        }
    }
    private void init() {
        edt_mail = findViewById(R.id.reg_email);
        edt_username = findViewById(R.id.reg_username);
        edt_password1 = findViewById(R.id.reg_pwd1);
        edt_password2 = findViewById(R.id.reg_pwd2);
        btn_reg = findViewById(R.id.email_sign_up_button);
    }

    OkHttpClient client = new OkHttpClient();
}
