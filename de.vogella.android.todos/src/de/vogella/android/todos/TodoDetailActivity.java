package de.vogella.android.todos;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;

import android.app.Activity;
import android.app.ProgressDialog;
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
import de.vogella.android.todos.contentprovider.GoogleDriveProxeyActivity;
import de.vogella.android.todos.contentprovider.MyTodoContentProvider;
import de.vogella.android.todos.database.TodoTable;

/*
 * TodoDetailActivity allows to enter a new todo item 
 * or to change an existing
 */
public class TodoDetailActivity extends Activity {

	private ImageView mImageView;
	private Button mExportToGCalendarButton;
	private Button mExportToDrive;
	private Button mEdit;
	private Button mDelete;

	private final static int REQUEST_ID = 188;
	private final static int SAVE_TO_DRIVE = 4;
	private final static int HALF = 2;

	private Uri mTodoUri;
	private Uri mCurrentImageUri;
	private Uri mCurrentDriveUri;

	private ProgressDialog progressDialog;

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

		if (extras !=null && extras.containsKey("driveUri")) {
			mCurrentDriveUri = (Uri) Uri.parse((String) extras.get("driveUri"));
		} else {
			// benda- make extra - switch:case with default
			handleEditMode(extras);
		}
		fillImageUriFromShareViaScreen(extras, action);
		setOnClickListeners();
	}

	// go here when returns from tasks stack
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void handleEditMode(Bundle extras) {
		// Or passed from the other activity
		if (extras != null) {
			mTodoUri = Uri.parse(extras
					.getString(MyTodoContentProvider.CONTENT_ITEM_TYPE));
			loadImageBitmapFromUri(mTodoUri, mImageView);
		}
	}

	private void loadImageBitmapFromUri(Uri imageUri, ImageView imageView) {
		// benda: asyncTask
		InputStream stream = null;

		try {
			stream = getContentResolver().openInputStream(imageUri);

			Bitmap original = BitmapFactory.decodeStream(stream);
			// ((ImageView)findViewById(R.id.image_holder))

			imageView.setImageBitmap(Bitmap.createScaledBitmap(original,
					original.getWidth() / 2, original.getHeight() / 2, true));

			mCurrentImageUri = imageUri;

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

	// filter by type
	private void GetAnyImage() {
		File dir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Pictures/Screenshots");
		Log.d("File path ", dir.getPath());
		String dirPath = dir.getAbsolutePath();
		if (dir.exists() && dir.isDirectory()) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			// intent.setData(Uri.fromFile(dir));

			Log.d("b4performSpecificCrop_startActivityForResult::",
					Integer.toString(3));
			startActivityForResult(intent, REQUEST_ID);
			Log.d("afterperformSpecificCrop_startActivityForResult::",
					Integer.toString(3));
		} else {
			// Toast.makeText(this, "No file exist to show",
			// Toast.LENGTH_LONG).show();
		}
	}


	// specific file
	private void OpenSpecificFile(Uri uri) {
		Log.d("File path ", uri.getPath());
			Intent intent = new Intent(Intent.ACTION_VIEW, null);
			intent.setType("image/*");
			intent.setData(uri);

			Log.d("b4performSpecificCrop_startActivityForResult::",
					Integer.toString(3));
			startActivityForResult(intent, REQUEST_ID);
			Log.d("afterperformSpecificCrop_startActivityForResult::",
					Integer.toString(3));
		}
	
	// specific file
	private void OpenSpecificFile(File file) {
		Log.d("File path ", file.getPath());
		if (file.exists()) {
			Intent intent = new Intent(Intent.ACTION_VIEW, null);
			intent.setType("image/*");
			intent.setData(Uri.fromFile(file));

			Log.d("b4performSpecificCrop_startActivityForResult::",
					Integer.toString(3));
			startActivityForResult(intent, REQUEST_ID);
			Log.d("afterperformSpecificCrop_startActivityForResult::",
					Integer.toString(3));
		} else {
			// Toast.makeText(this, "No file exist to show",
			// Toast.LENGTH_LONG).show();
		}
	}
	
	
	// from screenshots dir
	private void GetPhotoFromScreenShotsDir() {
		File dir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Pictures/Screenshots");
		Log.d("File path ", dir.getPath());
		String dirPath = dir.getAbsolutePath();
		if (dir.exists() && dir.isDirectory()) {
			Intent intent = new Intent(Intent.ACTION_VIEW, null);
			intent.setType("image/*");
			intent.setData(Uri.fromFile(dir));

			Log.d("b4performSpecificCrop_startActivityForResult::",
					Integer.toString(3));
			startActivityForResult(intent, REQUEST_ID);
			Log.d("afterperformSpecificCrop_startActivityForResult::",
					Integer.toString(3));
		} else {
			// Toast.makeText(this, "No file exist to show",
			// Toast.LENGTH_LONG).show();
		}
	}

	// open as file explorer
	private void GetWithFileExplorer() {
		File dir = new File(
		// Environment.getExternalStoragePublicDirectory(STORAGE_SERVICE).getAbsolutePath()
		// + "/Pictures/Screenshots");
				Environment.getExternalStorageDirectory().getAbsolutePath()
						+ "/Pictures/Screenshots");
		Log.d("File path ", dir.getPath());
		String dirPath = dir.getAbsolutePath();
		if (dir.exists() && dir.isDirectory()) {
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setType("image/*");
			intent.setData(Uri.fromFile(dir));

			Log.d("b4performSpecificCrop_startActivityForResult::",
					Integer.toString(3));
			startActivityForResult(intent, REQUEST_ID);
			Log.d("afterperformSpecificCrop_startActivityForResult::",
					Integer.toString(3));
		} else {
			// Toast.makeText(this, "No file exist to show",
			// Toast.LENGTH_LONG).show();
		}
	}

	private void setOnClickListeners() {

		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				GetAnyImage();
				
				mImageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						OpenSpecificFile(mCurrentImageUri);
					}
			});
			}
		});

		mExportToDrive.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				if (validate()) {
					saveFileToDrive();
//					setResult(RESULT_OK);
//					finish();
				}
			}
		});

		mExportToGCalendarButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				if (validate()) {
					exportToGCalendar();

					setResult(RESULT_OK);
					finish();
				}
			}
		});
		

		mEdit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {

				GetAnyImage();
			}
		});
		
		mDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				//mImageView.setImageResource(android.R.color.transparent);
				//mImageView.setImageBitmap(null);
				
				mImageView.setImageResource(android.R.drawable.ic_menu_gallery);
			}
		});
	}

	// benda: move outside
	private void saveFileToDrive() {
		Intent saveToDriveInten = new Intent(this,
				GoogleDriveProxeyActivity.class);
		saveToDriveInten.putExtra("fileUri", mCurrentImageUri.toString());
		startActivityForResult(saveToDriveInten, SAVE_TO_DRIVE);
	}

	private boolean validate() {

		boolean ans = true;

		// benda: validate all
		// if (TextUtils.isEmpty(mTitleText.getText()))
		// {
		// makeToast("title");
		// ans = false;
		// }

		if (mImageView.getDrawable() == null) {
			makeToast("image");
			ans = false;
		}

		return ans;
	}

	private void exportToGCalendar() {

		// option A
		// addCalendarEvent_contentResolver()

		// option B
		addCalendarEvent_intent();
	}

	private void addCalendarEvent_intent() {
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra(Events.TITLE, "Learn Android1");
		intent.putExtra(Events.EVENT_LOCATION, "Home suit home");
		intent.putExtra(Events.DESCRIPTION, "localUri: "+ mCurrentImageUri.toString() + "\n driveUri: "
				+ mCurrentDriveUri.toString());

		// Setting dates
		GregorianCalendar calDate = new GregorianCalendar(2013, 10, 8);
		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
				calDate.getTimeInMillis());
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
				calDate.getTimeInMillis());

		// Make it a full day event
		// intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

		// Make it a recurring Event
		// intent.putExtra(Events.RRULE,
		// "FREQ=WEEKLY;COUNT=11;WKST=SU;BYDAY=TU,TH");

		// Making it private and shown as busy
		intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
		intent.putExtra(Events.AVAILABILITY,
				CalendarContract.Events.AVAILABILITY_BUSY);

		// intent.setData(CalendarContract.Events.CONTENT_URI);
		startActivity(intent);
	}

	private void fillImageUriFromShareViaScreen(Bundle extras, String action) {
		// if this is from the share menu
		if (Intent.ACTION_SEND.equals(action)) {
			if (extras.containsKey(Intent.EXTRA_STREAM)) {
				try {
					// Get resource path from intent callee
					// return null if there is no key
					mCurrentImageUri = (Uri) extras
							.getParcelable(Intent.EXTRA_STREAM);

					// // Query gallery for camera picture via
					// // Android ContentResolver interface
					// ContentResolver cr = getContentResolver();
					// InputStream is = cr.openInputStream(uri);
					// // Get binary bytes for encode
					// byte[] data = getBytesFromFile(is);
					//
					// // base 64 encode for text transmission (HTTP)
					// byte[] encoded_data = Base64.encodeBase64(data);
					// String data_string = new String(encoded_data); // convert
					// to string
					//
					// SendRequest(data_string);

					// return;
				} catch (Exception e) {
					Log.e(this.getClass().getName(), e.toString());
				}

			} else if (extras.containsKey(Intent.EXTRA_TEXT)) {
				return;
			}
		}

		try {
			if (mCurrentImageUri != null) {

				Bitmap bitmap = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), mCurrentImageUri);

				mImageView.setImageBitmap(bitmap);

			}

		} catch (IOException e) {
			e.printStackTrace();

			Log.e(this.getClass().getName(), e.toString());
		}
	}

	private void initMemebers() {
		mImageView = (ImageView) findViewById(R.id.todo_edit_image);
		mExportToGCalendarButton = (Button) findViewById(R.id.todo_edit_upload_to_calendar);
		mExportToDrive = (Button) findViewById(R.id.todo_edit_uplaod_to_drive);
		
		mEdit = (Button) findViewById(R.id.edit_image);
		mDelete = (Button) findViewById(R.id.delete_image);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SAVE_TO_DRIVE && resultCode == RESULT_OK) {
		handleSuccessfulSavingToGoogleDrive(requestCode, resultCode, data);
		}
		else if (requestCode == REQUEST_ID && resultCode == Activity.RESULT_OK) {
		handleReturnedImageFromIntent(requestCode, resultCode, data);
		}
	}

	private void handleSuccessfulSavingToGoogleDrive(int requestCode,
			int resultCode, Intent data) {
			//mCurrentDriveUri = (Uri) data.getExtras().get("driveUri");
			mCurrentDriveUri = (Uri) Uri.parse((String) data.getExtras().get("driveUri"));
//			setResult(RESULT_OK);
//			finish();
	}

	private void handleReturnedImageFromIntent(int requestCode, int resultCode,
			Intent data) {
			mCurrentImageUri = data.getData();
			loadImageBitmapFromUri(mCurrentImageUri);
	}

	private void loadImageBitmapFromUri(Uri imageUri) {
		InputStream stream = null;
		try {
			stream = getContentResolver().openInputStream(imageUri);
			Bitmap original = BitmapFactory.decodeStream(stream);
			// ((ImageView)findViewById(R.id.image_holder))
			mImageView.setImageBitmap(Bitmap.createScaledBitmap(original,
					original.getWidth() / HALF, original.getHeight() / HALF,
					true));
			mCurrentImageUri = imageUri;
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
		// saveState();
		outState.putParcelable(MyTodoContentProvider.CONTENT_ITEM_TYPE,
				mTodoUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// saveState();
	}

	private void makeToast(String missingValue) {
		Toast.makeText(TodoDetailActivity.this,
				"Please maintain a " + missingValue, Toast.LENGTH_LONG).show();
	}
}