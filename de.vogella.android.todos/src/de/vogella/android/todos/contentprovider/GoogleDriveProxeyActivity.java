package de.vogella.android.todos.contentprovider;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import de.vogella.android.todos.TodoDetailActivity;



public class GoogleDriveProxeyActivity extends Activity {
  static final int REQUEST_ACCOUNT_PICKER = 1;
  static final int REQUEST_AUTHORIZATION = 2;
  static final int CAPTURE_IMAGE = 3;
protected static final int SAVE_DRIVE_URI = 4;

  private static Uri fileUri;
  private static String filePath;
  private static Drive service;
  private GoogleAccountCredential credential;
  
  private ProgressDialog progressDialog;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE));
    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    
    Intent intent = getIntent();
    
    if (intent != null)
    {
    
    	String fileUriStr = intent.getStringExtra("fileUri");
    	fileUri = Uri.parse(fileUriStr);
    	filePath = getPathFromUri_CursorLoader(fileUri);
    }
   }
  
  
//using deprecated managedQuery() method 
 private String getPathFromUri_managedQuery(Uri uri){
  String [] projection = {MediaStore.Images.Media.DATA};
  
        Cursor cursor = managedQuery( 
          uri, 
          projection,
          null,   //selection
          null,   //selectionArgs
          null   //sortOrder
        );
        
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
 }


 //using CursorLoader() method for API level 11 or higher
 private String getPathFromUri_CursorLoader(Uri uri){
  
  String [] projection = {MediaStore.Images.Media.DATA};
  
  CursorLoader cursorLoader = new CursorLoader(
    getApplicationContext(),
    uri, 
          projection,
          null,   //selection
          null,   //selectionArgs
          null   //sortOrder
        );
  
  Cursor cursor = cursorLoader.loadInBackground();
  
  int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);    
 }

  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    switch (requestCode) {
    case REQUEST_ACCOUNT_PICKER:
      if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        if (accountName != null) {
          credential.setSelectedAccountName(accountName);
          service = getDriveService(credential);
          saveFileToDrive();
        }
      }
      break;
      
      //second try to upload, if credentials were wrong
    case REQUEST_AUTHORIZATION:
      if (resultCode == Activity.RESULT_OK) {
        saveFileToDrive();
      } else {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
      }
      break;
    case CAPTURE_IMAGE:
      if (resultCode == Activity.RESULT_OK) {
        saveFileToDrive();
      }
    }
  }

//  private void startCameraIntent() {
//    String mediaStorageDir = Environment.getExternalStoragePublicDirectory(
//        Environment.DIRECTORY_PICTURES).getPath();
//    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
//    fileUri = Uri.fromFile(new java.io.File(mediaStorageDir + java.io.File.separator + "IMG_"
//        + timeStamp + ".jpg"));
//
//    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//    startActivityForResult(cameraIntent, CAPTURE_IMAGE);
//  }

  private void saveFileToDrive() {
	  
	//  progressDialog = ProgressDialog.show(this, "", "Loading...");
	  
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          // File's binary content
          java.io.File fileContent = new java.io.File(filePath);
          FileContent mediaContent = new FileContent("image/jpeg", fileContent);

          // File's metadata.
          File body = new File();
          body.setTitle(fileContent.getName());
          body.setMimeType("image/jpeg");

          File file = service.files().insert(body, mediaContent).execute();
          if (file != null) {
            showToast("Photo uploaded: " + file.getTitle());
        
         
            //Intent resultIntent = new Intent(getBaseContext(), TodoDetailActivity.class);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("driveUri", file.getAlternateLink());
    		setResult(Activity.RESULT_OK, resultIntent);
    		finish();
          }
        } catch (UserRecoverableAuthIOException e) {
          startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
    t.start();
  }

 
  
  private Drive getDriveService(GoogleAccountCredential credential) {
    return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
        .build();
  }

  public void showToast(final String toast) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
      }
    });
  }
}