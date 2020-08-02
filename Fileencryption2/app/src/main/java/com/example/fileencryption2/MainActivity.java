package com.example.fileencryption2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 2;
    private static final String ALGO = "AES";
    private Cipher cipher;
    private Button browse, encrypt, decrypt;
    private TextView textView;
    private Uri input_uri, output_uri;
    private String filename, encFileName;
    private SecretKey secretKey;
    private TextView outputFileText;
    private File outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        browse = findViewById(R.id.browse);
        encrypt = findViewById(R.id.encrypt);
        decrypt = findViewById(R.id.decrypt);
        textView = findViewById(R.id.textView);
        outputFileText = findViewById(R.id.outputFileText);

        textView.setText("No file selected");

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser();
                }
                else
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 65);
            }
        });
        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    encryptFile();
                } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }
        });
        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    decryptFile();
                } catch (InvalidAlgorithmParameterException | InvalidKeyException | IOException | BadPaddingException | IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void encryptFile() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        if (input_uri != null) {
            filename = findFileName(input_uri);

            if (filename == null) {
                Toast.makeText(MainActivity.this, "Error: File name could not be found", Toast.LENGTH_SHORT).show();
                filename = UUID.randomUUID().toString();

                String extension = getFileExtension(input_uri);

                if (extension == null) {
                    Toast.makeText(MainActivity.this, "Error: File extension could not be found", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                    filename = filename + "." + extension;
            }
        }

        textView.setText(filename);

        encFileName = "Encrypted - " + filename;

        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGO);
        keyGenerator.init(256);

        secretKey = keyGenerator.generateKey();

        byte[] iv = new byte[256/8];
        SecureRandom secureRandom = new SecureRandom();

        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        outputFile = new File(getExternalFilesDir(null), encFileName);

        InputStream iStream = getContentResolver().openInputStream(input_uri);
        byte[] inputData = getBytes(iStream);

        byte[] encryptedData = cipher.doFinal(inputData);
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(encryptedData);
        fos.close();

        output_uri = Uri.fromFile(outputFile);

        outputFileText.setText(output_uri.getPath());
    }


    private void decryptFile() throws InvalidAlgorithmParameterException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
        String decFileName = "Decrypted " + filename;

        byte[] iv = new byte[256/8];
        SecureRandom secureRandom = new SecureRandom();

        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        File decryptedFile = new File(getExternalFilesDir(null), decFileName);

        File file = new File(output_uri.getPath());
        byte[] fileData = new byte[(int)file.length()];

        FileInputStream fis = new FileInputStream(file);
        fis.read(fileData);

        byte[] decryptedData = cipher.doFinal(fileData);
        FileOutputStream fos = new FileOutputStream(decryptedFile);
        fos.write(decryptedData);
        fos.close();

        Toast.makeText(MainActivity.this, "Decryption Successful", Toast.LENGTH_LONG).show();

    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;

        while ((len = inputStream.read(buffer)) != -1)
            byteArrayOutputStream.write(buffer, 0, len);

        return byteArrayOutputStream.toByteArray();
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();

        if (uri.getScheme() == ContentResolver.SCHEME_CONTENT)
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(contentResolver.getType(uri));
        else
            return MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

    }

    private String findFileName(Uri uri) {
        String displayName = null;

        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);

        try {
            if (cursor != null && cursor.moveToFirst())
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return displayName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 65 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            showFileChooser();
        else Toast.makeText(MainActivity.this, "Please Provide Permission", Toast.LENGTH_SHORT).show();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select a File"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            input_uri = data.getData();
        }
    }
}