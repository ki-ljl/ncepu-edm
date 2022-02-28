package com.example.NCEPU.Student.User;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.ToastUtil;

public class SignatureActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editText;
    private TextView affirm;
    private int maxLength = 140;
    private TextView count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        InitData();

        editText.addTextChangedListener(new TextWatcher() {
            private CharSequence wordNum;//记录输入的字数
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wordNum= s;//实时记录输入的字数
            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = maxLength - s.length();
                //TextView显示剩余字数
                count.setText(String.valueOf(s.length()) + "/" + number);
                selectionStart=editText.getSelectionStart();
                selectionEnd = editText.getSelectionEnd();
                if (wordNum.length() > maxLength) {
                    //删除多余输入的字（不会显示出来）
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionEnd;
                    editText.setText(s);
                    editText.setSelection(tempSelection);//设置光标在最后
                }
            }
        });
    }

    //UserFragment的初始化
    private void initUserInfo() {

    }

    @Override
    public void onBackPressed() {
        ToastUtil.showMessage(this, "请点击确认!");
    }

    private void InitData() {

        toolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.editText);
        affirm = findViewById(R.id.affirm);
        count = findViewById(R.id.id_editor_count);

        Bundle bundle = getIntent().getExtras();
        String signature = bundle.getString("signature");
        editText.setText(signature);
        count.setText(String.valueOf(signature.length()) + "/" + String.valueOf(140));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText.getText().toString();
                Intent intent = new Intent(SignatureActivity.this, UserFragment.class);
                intent.putExtra("mod_sign", str);
                SignatureActivity.this.setResult(110, intent);
                finish();
            }
        });


        affirm.setOnClickListener(v -> {
            String str = editText.getText().toString();
            if(str.equals("")) {
                ToastUtil.showMessage(SignatureActivity.this,"请输入内容!!");
            }else {
                SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("sign", str);  //更新
                editor.commit();
                Intent intent = new Intent(SignatureActivity.this, UserFragment.class);
                intent.putExtra("mod_sign", str);
                SignatureActivity.this.setResult(110, intent);
                ToastUtil.showMessage(SignatureActivity.this,"更新成功！");
                finish();
            }
        });
    }

}
