package com.example.d_fir_login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 2;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private Uri uri;                            // URI's are actually urls meant for local storage
    private TextView filename;
    private String name;
    private String sha256Hash;
    private EditText txtEnterCaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        Button browse = findViewById(R.id.browse);
        Button upload = findViewById(R.id.upload);
        filename = findViewById(R.id.filename);
        filename.setVisibility(View.VISIBLE);

        txtEnterCaseId = findViewById(R.id.txtEnterCaseId);
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    showFileChooser();
                else
                    ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 45);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null)
                    UploadFile();
                else
                    Toast.makeText(UploadActivity.this, "Select a file", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 45 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            showFileChooser();
        else
            Toast.makeText(UploadActivity.this, "Please provide permission", Toast.LENGTH_LONG).show();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select a file"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            uri = data.getData();               //return uri of selected file
            String path = null;
            if (uri != null) {
                path = uri.getPath();
            }
            if (path != null) {
                name = path.substring(path.lastIndexOf("/") + 1);
            }
            name = name.substring((name.indexOf(":")) + 1);
            filename.setText(name);

            try {
                HashGenerator();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else
            Toast.makeText(UploadActivity.this, "Please select a file!", Toast.LENGTH_LONG).show();

    }

    private void HashGenerator() throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("sha-256");
        InputStream inputStream = getContentResolver().openInputStream(uri);


        byte[] databytes = new byte[1024];
        int nread;
        if (inputStream != null) {
            while ((nread = inputStream.read(databytes)) != -1)
                md.update(databytes, 0, nread);
        }

        byte[] messagedigest = md.digest();
        StringBuilder hexString = new StringBuilder();

        for (byte b : messagedigest) {
            String h = Integer.toHexString(0xFF & b);
            String StrComplete = PrependValue(h);
            hexString.append(StrComplete);
        }
        sha256Hash = hexString.toString();
    }

    private String PrependValue(String iStr) {
        StringBuilder sReturnedStr = new StringBuilder(iStr);
        while (sReturnedStr.length() < 2)
            sReturnedStr.insert(0, "0");
        return sReturnedStr.toString();
    }

    private void UploadFile() {
        StorageReference storageReference = firebaseStorage.getReference();          //returns root path


        storageReference.child("Cases").child(txtEnterCaseId.getText().toString()).child(name).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UploadActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this, "Uploading Failed!", Toast.LENGTH_SHORT).show();
            }
        });
       name = name.substring(0, name.indexOf("."));
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("Hash Values").child(txtEnterCaseId.getText().toString()).child("Hrithik").setValue(sha256Hash);
    }
}