package com.example.facetect.ui;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.example.facetect.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Setting extends FrameLayout {
    private String  address;
    int port;
    boolean [] division = new boolean[4];
    private EditText AddressTv;
    private EditText PortTv;
    private EditText DivisionTv;
    private Button ConfirmButton;
    private ConfigChangeListener configChangeListener;
    public Setting(@NonNull Context context) {
        super(context);
    }

    public Setting(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Setting(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Setting(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }
    public void SetConfigChangeListener(ConfigChangeListener configChangeListener){
        this.configChangeListener = configChangeListener;
    }
    public interface ConfigChangeListener{
        public void OnConfigChangeConfirm(String address,int port ,boolean [] division);

    }
     public  void initView(){
         LayoutInflater.from(getContext()).inflate(R.layout.ui_setting_layout, this);
         AddressTv = findViewById(R.id.addressEt);
         PortTv = findViewById(R.id.portEt);
         DivisionTv = findViewById(R.id.divisionEt);
         ConfirmButton = findViewById(R.id.confirm);
         ConfirmButton.setOnClickListener(new OnClickListener() {
             @Override
             public void onClick(View view) {
                 configChangeListener.OnConfigChangeConfirm(address,port,division);
             }
         });
         AddressTv.setOnFocusChangeListener(new OnFocusChangeListener() {
             @Override
             public void onFocusChange(View view, boolean b) {
                 if(AddressTv.getText().toString().isEmpty()){
                     return;
                 }
                 address = AddressTv.getText().toString();
                 System.out.println("change address"+address);
             }
         });

         PortTv.setOnFocusChangeListener(new OnFocusChangeListener() {
             @Override
             public void onFocusChange(View view, boolean b) {
                 if(b&&PortTv.getText().toString().isEmpty()){
                     return;
                 }
                 port = Integer.parseInt(PortTv.getText().toString());
                 System.out.println("change port"+port);
             }
         });
         DivisionTv.setOnFocusChangeListener(new OnFocusChangeListener() {
             @Override
             public void onFocusChange(View view, boolean b) {
                 String text = DivisionTv.getText().toString();
                 if(text.isEmpty()||text.length()<4){
                     return;
                 }
                 Editable tmp = DivisionTv.getText();
                 division[0] = tmp.charAt(0)=='0';
                 division[1] = tmp.charAt(1)=='0';
                 division[2] = tmp.charAt(2)=='0';
                 division[3] = tmp.charAt(3)=='0';
                 System.out.println("change port"+division.toString());
             }
         });
    }
}
