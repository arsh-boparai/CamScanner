package com.charger.driverapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;

import com.intsig.csopen.sdk.CSOpenAPI;
import com.intsig.csopen.sdk.CSOpenAPIParam;
import com.intsig.csopen.sdk.CSOpenApiFactory;
import com.intsig.csopen.sdk.CSOpenApiHandler;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

class DataHolder {
    private String data;
    public String getData() {return data;}
    public void setData(String data) {this.data = data;}

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}
public class CamscannerActivity extends Activity {
    CSOpenAPI api;
    String _scannedFileUri;
    private final int REQ_CODE_CALL_CAMSCANNER = 2;

    private static final String DIR_IMAGE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Tafs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.checkDir(DIR_IMAGE);
        Intent intent = this.getIntent();
        String srcUri =  intent.getStringExtra("SRC_URI");
        String appKey = "QT4CX6fQP6UP8E39Ne1g3DB1";
        api = CSOpenApiFactory.createCSOpenApi(this.getApplicationContext(), appKey, null);
        if (api.isCamScannerInstalled()) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            _scannedFileUri = DIR_IMAGE + "/" + timeStamp;
            CSOpenAPIParam openApiParam = new CSOpenAPIParam(
                    srcUri,
                    _scannedFileUri + ".jpg",
                    _scannedFileUri + ".pdf",
                    _scannedFileUri + "_org.jpg",
                    1.0f);
            boolean res = api.scanImage(this, 2, openApiParam);
        } else {
            Intent databackIntent = new Intent();
            databackIntent.putExtra("RESULT", "error");
            databackIntent.putExtra("ERROR", "You should install CamScanner First");
            setResult(Activity.RESULT_OK, databackIntent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if(resultCode != RESULT_CANCELED) {
          if(requestCode == REQ_CODE_CALL_CAMSCANNER) {
            api.handleResult(requestCode, resultCode, data, new CSOpenApiHandler() {
                @Override
                public void onSuccess() {
                    Intent databackIntent = new Intent();
                    databackIntent.putExtra("RESULT", "success");
                    Bitmap mBitmap = Util.loadBitmap(_scannedFileUri + ".jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    byte[] byteArrayImage = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                    DataHolder.getInstance().setData(encodedImage);
                    setResult(Activity.RESULT_OK, databackIntent);
                    finish();
                }

                @Override
                public void onError(int errorCode) {
                    Intent databackIntent = new Intent();
                    databackIntent.putExtra("RESULT", "error");
                    databackIntent.putExtra("ERROR", "There was an error creating the image. Error Code: " + errorCode);
                    setResult(Activity.RESULT_OK, databackIntent);
                    finish();
                }

                @Override
                public void onCancel() {
                    Intent databackIntent = new Intent();
                    databackIntent.putExtra("RESULT", "cancel");
                    setResult(Activity.RESULT_OK, databackIntent);
                    finish();
                }
            });
          }
      }
      else {
        Intent databackIntent = new Intent();
        databackIntent.putExtra("RESULT", "cancel");
        setResult(Activity.RESULT_OK, databackIntent);
        finish();
      }
    }
}