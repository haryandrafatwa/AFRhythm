package com.example.atrialfibrilationdetection.Feature.Auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.atrialfibrilationdetection.MainActivity;
import com.example.atrialfibrilationdetection.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog mDialog;
    Button btnMasuk;
    EditText et_email, et_password;
    TextView tv_daftar;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        initialize();
    }

    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        btnMasuk = findViewById(R.id.btn_masuk);
        et_email = findViewById(R.id.et_email_login);
        et_password = findViewById(R.id.et_password_login);
        tv_daftar = findViewById(R.id.tv_daftar);

        tv_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActivity(RegisterActivity.class);
            }
        });


        int tintColorDark = ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray);
        int tintColorDrawable = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);

        Drawable drawableVisibility = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_visibility);
        drawableVisibility = DrawableCompat.wrap(drawableVisibility);
        DrawableCompat.setTint(drawableVisibility.mutate(), tintColorDark);

        Drawable drawableLock = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_password);
        drawableLock = DrawableCompat.wrap(drawableLock);
        DrawableCompat.setTint(drawableLock.mutate(), tintColorDrawable);

        drawableVisibility.setBounds(0, 0, drawableVisibility.getIntrinsicWidth(), drawableVisibility.getIntrinsicHeight());
        drawableLock.setBounds(0, 0, drawableLock.getIntrinsicWidth(), drawableLock.getIntrinsicHeight());

        et_password.setCompoundDrawables(drawableLock, null, drawableVisibility, null);


        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!et_email.getText().toString().matches(emailPattern)) {
                    et_email.setTextColor(Color.RED);
                } else {
                    et_email.setTextColor(getResources().getColor(R.color.colorInputText));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (et_password.getRight() - et_password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        int tintColorDark = ContextCompat.getColor(getApplicationContext(), android.R.color.darker_gray);
                        int tintColorDrawable = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);

                        Drawable drawableVisibility = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_visibility);
                        drawableVisibility = DrawableCompat.wrap(drawableVisibility);
                        DrawableCompat.setTint(drawableVisibility.mutate(), tintColorDrawable);

                        Drawable drawableVisibilityOff = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_visibility_off);
                        drawableVisibilityOff = DrawableCompat.wrap(drawableVisibilityOff);
                        DrawableCompat.setTint(drawableVisibilityOff.mutate(), tintColorDrawable);

                        Drawable drawableLock = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_password);
                        drawableLock = DrawableCompat.wrap(drawableLock);
                        DrawableCompat.setTint(drawableLock.mutate(), tintColorDrawable);

                        drawableVisibility.setBounds(0, 0, drawableVisibility.getIntrinsicWidth(), drawableVisibility.getIntrinsicHeight());
                        drawableVisibilityOff.setBounds(0, 0, drawableVisibilityOff.getIntrinsicWidth(), drawableVisibilityOff.getIntrinsicHeight());
                        drawableLock.setBounds(0, 0, drawableLock.getIntrinsicWidth(), drawableLock.getIntrinsicHeight());

                        if (et_password.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                            et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            DrawableCompat.setTint(drawableVisibility.mutate(), tintColorDark);
                            et_password.setCompoundDrawables(drawableLock, null, drawableVisibility, null);
                        } else {
                            et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            et_password.setCompoundDrawables(drawableLock, null, drawableVisibilityOff, null);

                        }
                        return true;
                    }
                }
                return false;
            }
        });

        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithEmailandPassword();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            setActivity(MainActivity.class);
        }
    }

    private void loginWithEmailandPassword() {

        String email = et_email.getText().toString();
        String pass = et_password.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else {
            mDialog.setMessage("Tunggu sebentar...");
            mDialog.setCancelable(false);
            mDialog.setTitle("Login");
            mDialog.show();

            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        setActivity(MainActivity.class);
                        Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }
            });
        }
    }

    public void setActivity(Class activity) {
        Intent mainIntent = new Intent(LoginActivity.this, activity);
        startActivity(mainIntent);
        finish();
    }
}
