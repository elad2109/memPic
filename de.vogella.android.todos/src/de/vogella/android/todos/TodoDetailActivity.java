package de.vogella.android.todos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import de.vogella.android.todos.contentprovider.MyTodoContentProvider;
import de.vogella.android.todos.database.TodoTable;

/*
 * TodoDetailActivity allows to enter a new todo item 
 * or to change an existing
 */
public class TodoDetailActivity extends Activity {

//	private EditText mTitleText;
//	private EditText mBodyText;
//	private DatePicker mDatePicker;
	private String mTitleText;
	private String mBodyText;
	private String mDatePicker;
	private ImageView mImageView;
	private Button mExportToGCalendarButton;
	
	private MyMediaScanner mMyMediaScanner;
	
	private long mEventId;
	
	private final static int REQUEST_ID = 188;

	private final static int HALF = 2;
	
	private Uri mTodoUri;
	private Uri mCurrentImageUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.todo_edit);

		initMemebers();
		
		// Check from the saved Instance
 	   mTodoUri = (bundle == null) ? null : (Uri) bundle
				.getParcelable(MyTodoContentProvider.CONTENT_ITEM_TYPE);
		
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String action = intent.getAction();
		
		handleEditMode(extras);
       	
		fillImageUriFromShareViaScreen(extras, action);
        
		setOnClickListeners();
	}



private void handleEditMode(Bundle extras) {
	// Or passed from the other activity
	if (extras != null) {
		mTodoUri = Uri.parse(extras.getString(MyTodoContentProvider.CONTENT_ITEM_TYPE));
				//.getParcelable(MyTodoContentProvider.CONTENT_ITEM_TYPE);

		//mCurrentImageUri.
		
//Uri uri = Uri.parse(strUri);
		
		loadImageBitmapFromUri(mTodoUri, mImageView);
		
//		if (uri != null) {
//
//			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
//					mContext.getContentResolver(), uri);
//
//			//mImageView.setImageBitmap(bitmap);
//			
	}
}
	





private void loadImageBitmapFromUri(Uri imageUri, ImageView imageView) {

InputStream stream = null;

try {
	  stream = getContentResolver().openInputStream(imageUri);


      Bitmap original = BitmapFactory.decodeStream(stream);
      //((ImageView)findViewById(R.id.image_holder))

      imageView.setImageBitmap(Bitmap.createScaledBitmap(original,
              original.getWidth() / 2, original.getHeight() / 2, true));
  } catch (Exception e) {
      e.printStackTrace();
  } finally {
      if (stream != null) {
          try {
              stream.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  }
}



	//filter by type
    private void GetAnyImage()
    {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/Screenshots");
        Log.d("File path ", dir.getPath());
        String dirPath=dir.getAbsolutePath();
        if(dir.exists() && dir.isDirectory()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            //intent.setData(Uri.fromFile(dir));

            Log.d("b4performSpecificCrop_startActivityForResult::", Integer.toString(3));
            startActivityForResult(intent,REQUEST_ID);
            Log.d("afterperformSpecificCrop_startActivityForResult::", Integer.toString(3));
        }
        else
        {
           // Toast.makeText(this, "No file exist to show", Toast.LENGTH_LONG).show();
        }  
    }
    
    //specific file
    private void GetSpecificFile()
    {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/Screenshots");
        Log.d("File path ", dir.getPath());
        String dirPath=dir.getAbsolutePath();
        if(dir.exists() && dir.isDirectory()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, null);
            intent.setType("image/*");
            intent.setData(Uri.fromFile(dir));

            Log.d("b4performSpecificCrop_startActivityForResult::", Integer.toString(3));
            startActivityForResult(intent, REQUEST_ID);
            Log.d("afterperformSpecificCrop_startActivityForResult::", Integer.toString(3));
        }
        else
        {
           // Toast.makeText(this, "No file exist to show", Toast.LENGTH_LONG).show();
        }  
    }
    
    //open as file explorer
    private void GetWithFileExplorer()
    {
        File dir = new File(
        	//	Environment.getExternalStoragePublicDirectory(STORAGE_SERVICE).getAbsolutePath() + "/Pictures/Screenshots");
        		Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/Screenshots");
        Log.d("File path ", dir.getPath());
        String dirPath=dir.getAbsolutePath();
        if(dir.exists() && dir.isDirectory()) {
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setType("image/*");
            intent.setData(Uri.fromFile(dir));

            Log.d("b4performSpecificCrop_startActivityForResult::", Integer.toString(3));
            startActivityForResult(intent, REQUEST_ID);
            Log.d("afterperformSpecificCrop_startActivityForResult::", Integer.toString(3));
        }
        else
        {
           // Toast.makeText(this, "No file exist to show", Toast.LENGTH_LONG).show();
        }  
    }
	
	private void setOnClickListeners() {
		
		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				GetAnyImage();
            }
	    });

	
		mExportToGCalendarButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				
				if (validate())
				{
					exportToGCalendar();
					
					setResult(RESULT_OK);
					finish();
				}
			}

			private boolean validate() {
				
				boolean ans = true;
				
				
				//benda: validate all
//				if (TextUtils.isEmpty(mTitleText.getText()))
//				{
//					makeToast("title");
//					ans = false;
//				}
				
				if (mImageView.getDrawable() == null)
				{
					makeToast("image");
					ans = false;
				}
				
				return ans;
			}

			private void exportToGCalendar() {
				
				//option A
				//addCalendarEvent_contentResolver()
				
				//option B
				addCalendarEvent_intent();
			}

			private void addCalendarEvent_contentResolver() {
				long calID = 3;
				long startMillis = 0; 
				long endMillis = 0;     
				Calendar beginTime = Calendar.getInstance();
				beginTime.set(2012, 9, 14, 7, 30);
				startMillis = beginTime.getTimeInMillis();
				Calendar endTime = Calendar.getInstance();
				endTime.set(2012, 9, 14, 8, 45);
				endMillis = endTime.getTimeInMillis();


				ContentResolver cr = getContentResolver();
				ContentValues values = new ContentValues();
				values.put(Events.DTSTART, startMillis);
				values.put(Events.DTEND, endMillis);
				values.put(Events.TITLE, "Jazzercise");
				values.put(Events.DESCRIPTION, "Group workout");
				values.put(Events.CALENDAR_ID, calID);
				values.put(Events.EVENT_TIMEZONE, "America/Los_Angeles");
				Uri uri = cr.insert(Events.CONTENT_URI, values);

				// get the event ID that is the last element in the Uri
				mEventId = Long.parseLong(uri.getLastPathSegment());
				// 
				// ... do something with event ID
				//
				//
			}

			private void addCalendarEvent_intent() {
				Intent intent = new Intent(Intent.ACTION_INSERT);
	                intent.setType("vnd.android.cursor.item/event");
	                intent.putExtra(Events.TITLE, "Learn Android");
	                intent.putExtra(Events.EVENT_LOCATION, "Home suit home");
	                intent.putExtra(Events.DESCRIPTION, mCurrentImageUri.toString());
	                
	// Setting dates
	                GregorianCalendar calDate = new GregorianCalendar(2013, 10, 8);
	                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
	                        calDate.getTimeInMillis());
	                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
	                        calDate.getTimeInMillis());

	// Make it a full day event
	                //intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

	// Make it a recurring Event
	               // intent.putExtra(Events.RRULE, "FREQ=WEEKLY;COUNT=11;WKST=SU;BYDAY=TU,TH");

	// Making it private and shown as busy
	                intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
	                intent.putExtra(Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

	           //     intent.setData(CalendarContract.Events.CONTENT_URI);
	                startActivity(intent);
			}

		});
	}



 	private void fillImageUriFromShareViaScreen(Bundle extras,
			String action) {
		// if this is from the share menu
        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_STREAM))
            {
                try
                {
                    // Get resource path from intent callee
                	// return null if there is no key
                	mCurrentImageUri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);

//                    // Query gallery for camera picture via
//                    // Android ContentResolver interface
//                    ContentResolver cr = getContentResolver();
//                    InputStream is = cr.openInputStream(uri);
//                    // Get binary bytes for encode
//                    byte[] data = getBytesFromFile(is);
//
//                    // base 64 encode for text transmission (HTTP)
//                    byte[] encoded_data = Base64.encodeBase64(data);
//                    String data_string = new String(encoded_data); // convert to string
//
//                    SendRequest(data_string);

//                    return;
                } catch (Exception e)
                {
                    Log.e(this.getClass().getName(), e.toString());
                }

            } else if (extras.containsKey(Intent.EXTRA_TEXT))
            {
                return;
            }
        }

        try {
            if (mCurrentImageUri !=null)
            {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCurrentImageUri);

            mImageView.setImageBitmap(bitmap);
            }

         } catch (IOException e) {
            e.printStackTrace();

            Log.e(this.getClass().getName(), e.toString());
        }
	}


	private void initMemebers() {
		//mTitleText = (EditText) findViewById(R.id.todo_edit_title);
		//mBodyText = (EditText) findViewById(R.id.todo_edit_description);
		mImageView = (ImageView) findViewById(R.id.todo_edit_image);
		//mDatePicker = (DatePicker) findViewById(R.id.todo_edit_date);
		mExportToGCalendarButton = (Button) findViewById(R.id.todo_edit_button);
		
		//mMyMediaScanner = new MyMediaScanner();
	}

	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//      if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
//          Bitmap photo = (Bitmap) data.getExtras().get("data");
//          imageView.setImageBitmap(photo);
//      }


      handleReturnedImageFromIntent(requestCode, resultCode, data);
  }


	private void handleReturnedImageFromIntent(int requestCode, int resultCode,
			Intent data) {
		
		  if (requestCode == REQUEST_ID && resultCode == Activity.RESULT_OK) {
		     
			  mCurrentImageUri = data.getData();
		    	
			  loadImageBitmapFromUri(mCurrentImageUri);

		  }
	}


	private void loadImageBitmapFromUri(Uri imageUri) {
		
		InputStream stream = null;
		
		try {
			  stream = getContentResolver().openInputStream(imageUri);


		      Bitmap original = BitmapFactory.decodeStream(stream);
		      //((ImageView)findViewById(R.id.image_holder))

		      mImageView.setImageBitmap(Bitmap.createScaledBitmap(original,
		              original.getWidth() / HALF, original.getHeight() / HALF, true));
		  } catch (Exception e) {
		      e.printStackTrace();
		  } finally {
		      if (stream != null) {
		          try {
		              stream.close();
		          } catch (Exception e) {
		              e.printStackTrace();
		          }
		      }
		  }
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//saveState();
		outState.putParcelable(MyTodoContentProvider.CONTENT_ITEM_TYPE, mTodoUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//saveState();
	}



	private void makeToast(String missingValue) {
		Toast.makeText(TodoDetailActivity.this, "Please maintain a "+ missingValue,
				Toast.LENGTH_LONG).show();
	}
}