package com.wandsworkstation.chat;

import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Fingerprint extends AppCompatActivity {

    private KeyGenerator keyGenerator;
    private KeyStore keyStore;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private String KEY_NAME = "key1";

    /*Finger Print*/
    private boolean checkFinger() {
        // Keyguard Manager
        keyguardManager = (KeyguardManager)
                getSystemService(KEYGUARD_SERVICE);
        // Fingerprint Manager
        fingerprintManager = (FingerprintManager)
                getSystemService(FINGERPRINT_SERVICE);

        try {
            // Check if the fingerprint sensor is present
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!fingerprintManager.isHardwareDetected()) {
                    // Update the UI with a message
//                    message.setText("Fingerprint authentication not supported");
                    Toast.makeText(getBaseContext(), "Fingerprint authentication not supported", Toast.LENGTH_LONG).show();

                    return false;
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!fingerprintManager.hasEnrolledFingerprints()) {
//                    message.setText("No fingerprint configured.");
                    Toast.makeText(getBaseContext(), "No fingerprint configured.", Toast.LENGTH_LONG).show();

                    return false;
                }
            }

            if (!keyguardManager.isKeyguardSecure()) {
//                message.setText("Secure lock screen not enabled.");
                Toast.makeText(getBaseContext(), "Secure lock screen not enabled.", Toast.LENGTH_LONG).show();

                return false;
            }
        }
        catch(SecurityException se) {
            se.printStackTrace();
        }
        return true;
    }

    private void generateKey() throws FingerprintException {
        try {
            // Get the reference to the key store
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            // Key generator to generate the key
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
            keyStore.load(null);
            keyGenerator.init( new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        }

        catch(KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    private Cipher generateCipher() throws FingerprintException {
        try {
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        }
        catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | UnrecoverableKeyException
                | KeyStoreException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

        TextView message = (TextView) findViewById(R.id.finger_status);
        Button _fingerprintButton = (Button) findViewById(R.id.btn_fingerprint_auth);
        final FingerprintHandler fph = new FingerprintHandler(_fingerprintButton);

        if (checkFinger() == false) {
            _fingerprintButton.setEnabled(false);
            checkFinger();
        }
        else {
            // We are ready to set up the cipher and the key
            try {
                generateKey();
                Cipher cipher = generateCipher();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                }
            }
            catch(FingerprintException fpe) {
                // Handle exception
                _fingerprintButton.setEnabled(false);
            }
        }
        _fingerprintButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                fph.doAuth(fingerprintManager, cryptoObject);
            }

        });
    }

    public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
        private TextView tv;

        public FingerprintHandler(TextView tv) {
            this.tv = tv;
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            tv.setText("Auth error");
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            tv.setText("auth ok");
            tv.setTextColor(tv.getContext().getResources().
                    getColor(android.R.color.holo_green_light));
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
        }

        public void doAuth(FingerprintManager manager,
                           FingerprintManager.CryptoObject obj) {
            CancellationSignal signal = new CancellationSignal();
            try {
                manager.authenticate(obj, signal, 0, this, null);
            }
            catch(SecurityException sce) {}
        }
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception exc) {

        }
    }
}
