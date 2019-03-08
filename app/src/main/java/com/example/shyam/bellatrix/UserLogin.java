package com.example.shyam.bellatrix;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class UserLogin extends AppCompatActivity {

    EditText editTextphone, editTextcode;
    FirebaseAuth mAuth,firebaseAuth;

    DatabaseReference ref,demoref;

    DataSnapshot dataSnapshot;
    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        ActionBar actionBar=getSupportActionBar();
        actionBar.hide();
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.splash));
        mAuth = FirebaseAuth.getInstance();
        editTextcode = findViewById(R.id.editTextcode);
        editTextphone = findViewById(R.id.editTextphone);

        //     progressBarload.findViewById(R.id.progressBarload);

        findViewById(R.id.buttongetotp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //          progressBarload.setVisibility(v.VISIBLE);
                sendVerificationCode();
            }
        });

        findViewById(R.id.buttonsignin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String cde = editTextcode.getText().toString();
                if(cde.isEmpty()){
                    editTextcode.setError("Enter code");
                    editTextcode.requestFocus();
                    return;
                }
                else if(cde.length()<6 || cde.length() >= 7){
                    editTextcode.setError("Enter valid code");
                    editTextcode.requestFocus();
                    return;
                }
                else{
                    verifySigninCode();
                }




            }
        });

    }


    private void verifySigninCode() {

        String code = editTextcode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //here we can call next activity after login page
                            Toast.makeText(getApplicationContext(),
                                    "Login successful", Toast.LENGTH_SHORT).show();


                            firebaseAuth = FirebaseAuth.getInstance();
                            ref = FirebaseDatabase.getInstance().getReference();
                            FirebaseUser user = firebaseAuth.getCurrentUser();



                            //demoref = ref.child("users");
                            ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    FirebaseUser useer = firebaseAuth.getCurrentUser();


                                    if (dataSnapshot.hasChild(useer.getUid())) {


                                        Intent intent = new Intent(UserLogin.this, Userui.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);


                                    } else {

                                        ref.child("users").child(useer.getUid()).child("points").setValue(0);
                                        ref.child("users").child(useer.getUid()).child("weight").setValue(0);
                                        Toast.makeText(UserLogin.this, "WELCOME TO TRASH MASTER", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(UserLogin.this, Userui.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);


                                    }
                                }

                                //}

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(),
                                    "Incorrect code", Toast.LENGTH_LONG).show();


                        }
                    }

                });
    }

    private void sendVerificationCode() {
        String phone = editTextphone.getText().toString();
        if (phone.isEmpty()) {
            editTextphone.setError("phone number is required");
            editTextphone.requestFocus();
            return;
        }

        if (phone.length() <10 || phone.length()>=11) {
            editTextphone.setError("enter valid phone number");
            editTextphone.requestFocus();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

    }


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                editTextcode.setText(code);
                verifySigninCode();
            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(UserLogin.this, e.getMessage(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };

    //the below override method is used when the user is already logged in the account
   /* @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, ChoiceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }
    } */
}








