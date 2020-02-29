package com.example.atrialfibrilationdetection.Feature.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.atrialfibrilationdetection.MainActivity;
import com.example.atrialfibrilationdetection.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tv_masuk;
    private StorageReference dummyDispPict;
    private FirebaseAuth mAuth;
    private EditText et_nama, et_email, et_pass, et_retype, et_tipe_account;
    private Spinner tipeAccount;
    private Button btnDaftar;
    private DatabaseReference userRefs;
    private ProgressDialog mDialog;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        dummyDispPict = FirebaseStorage.getInstance().getReference("DisplayPictures/dummy").child("default.svg");
        initialize();

    }

    private void initialize() {

        et_email = findViewById(R.id.et_email_daftar);
        et_nama = findViewById(R.id.et_nama_daftar);
        et_pass = findViewById(R.id.et_pass_daftar);
        et_retype = findViewById(R.id.et_repass_daftar);
        et_tipe_account = findViewById(R.id.et_tipe_account);
        btnDaftar = findViewById(R.id.btn_daftar);
        tv_masuk = findViewById(R.id.tv_masuk);
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar_register);
        tipeAccount = findViewById(R.id.tipeAccount);

        String[] entries = new String[]{
                "Pilih tipe account", "Dokter", "Pasien"
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, entries);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        tipeAccount.setAdapter(spinnerAdapter);
        tipeAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipe = (String) parent.getItemAtPosition(position);
                et_tipe_account.setText(tipe);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setToolbar();

        tv_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActivity(LoginActivity.class);
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerWithEmailandPassword();
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

        et_pass.setCompoundDrawables(drawableLock, null, drawableVisibility, null);
        et_retype.setCompoundDrawables(drawableLock, null, drawableVisibility, null);

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

        et_pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (et_pass.getRight() - et_pass.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

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

                        if (et_pass.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                            et_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            DrawableCompat.setTint(drawableVisibility.mutate(), tintColorDark);
                            et_pass.setCompoundDrawables(drawableLock, null, drawableVisibility, null);
                        } else {
                            et_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            et_pass.setCompoundDrawables(drawableLock, null, drawableVisibilityOff, null);

                        }
                        return true;
                    }
                }
                return false;
            }
        });

        et_retype.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (et_retype.getRight() - et_retype.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

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

                        if (et_retype.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                            et_retype.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            DrawableCompat.setTint(drawableVisibility.mutate(), tintColorDark);
                            et_retype.setCompoundDrawables(drawableLock, null, drawableVisibility, null);
                        } else {
                            et_retype.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            et_retype.setCompoundDrawables(drawableLock, null, drawableVisibilityOff, null);

                        }
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void registerWithEmailandPassword() {

        final String email = et_email.getText().toString();
        final String nama = et_nama.getText().toString();
        String pass = et_pass.getText().toString();
        String repass = et_retype.getText().toString();
        final String tipe = et_tipe_account.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(nama) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(repass)) {
            Toast.makeText(this, "Data harus diisi", Toast.LENGTH_SHORT).show();
        } else {
            if (tipe.equals("Pilih tipe account")) {
                Toast.makeText(this, "Pilih tipe account terlebih dahulu", Toast.LENGTH_SHORT).show();
            } else {
                if (pass.equals(repass)) {
                    mDialog.setTitle("Daftar");
                    mDialog.setCancelable(true);
                    mDialog.setMessage("Tunggu sebentar .. ");
                    mDialog.show();

                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String currentUserID = mAuth.getCurrentUser().getUid();
                                userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);
                                HashMap userMap = new HashMap();
                                userMap.put("nama", nama);
                                userMap.put("phonenumber", "-");
                                userMap.put("email", email);
                                userMap.put("tipe", tipe);
                                userMap.put("bod", "-");
                                userMap.put("subscription", "-");
                                dummyDispPict.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        userRefs.child("displaypicture").setValue(uri.toString());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.d("DISPLAY PICTURE FAILED", "OMG");
                                    }
                                });
                                userRefs.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Daftar Berhasil", Toast.LENGTH_SHORT).show();
                                            mDialog.dismiss();
                                            setActivity(MainActivity.class);
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            mDialog.dismiss();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Password harus sama!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void setActivity(Class activity) {
        Intent mainIntent = new Intent(RegisterActivity.this, activity);
        startActivity(mainIntent);
        finish();
    }

}
