package de.vogella.android.todos;

import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
	private final Context mContext;
	private final String[] mTitles;
	private final String[] mImageUris;
	private final Date[] mDateTimes;

	//benda maybe utills help
	Format format = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
	
	public MySimpleArrayAdapter(Context context, String[] titles,
			Date[] dateTimes, String[] imageUris) {
		super(context, R.layout.todo_list, titles);
		mContext = context;
		mTitles = titles;
		mImageUris = imageUris;
		mDateTimes = dateTimes;
	}

	public MySimpleArrayAdapter(TodosOverviewActivity context,
			ArrayList<String> nameOfEvent, ArrayList<Date> startDates,
			ArrayList<String> descriptions) {
		super(context, R.layout.todo_list, nameOfEvent.toArray(new String[0]));
		
		mContext = context;
		mTitles = nameOfEvent.toArray(new String[0]);
		mImageUris = descriptions.toArray(new String[0]);
		mDateTimes = startDates.toArray(new Date[0]);;

	}

	
	public String getDesc(int i) {
		return mImageUris[i].toString();
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		//View rowView = inflater.inflate(R.layout.todo_list, parent, false);
		
		View rowView = inflater.inflate(R.layout.todo_row, parent, false);
		
		TextView titleTextView = (TextView) rowView
				.findViewById(R.id.todo_row_title);
		TextView dateTextView = (TextView) rowView
				.findViewById(R.id.todo_row_date);
		ImageView imageView = (ImageView) rowView
				.findViewById(R.id.todo_row_image);

		//Typeface font= Typeface.createFromAsset(mContext.getAssets(), "fonts/Raanana.ttf");
		//titleTextView.setTypeface(font); 
		
		titleTextView.setText(mTitles[position]);
		
		dateTextView.setText(format.format(mDateTimes[position]).toString());

		String strUri = mImageUris[position];
//		try {
			if (strUri != null && strUri.split("/n")[0].contains("images")) {
				
				Uri uri = Uri.parse(strUri);
				
				//loadImageBitmapFromUri(uri, imageView);
				
//				if (uri != null) {
//
//					Bitmap bitmap = MediaStore.Images.Media.getBitmap(
//							mContext.getContentResolver(), uri);
//
//					//imageView.setImageBitmap(bitmap);
//					
//					
//					
//				}
			}

//		} 
		
//		catch (IOException e) {
//			e.printStackTrace();
//
//			Log.e(this.getClass().getName(), e.toString());
//		}

		return rowView;
	}
	
	
	
private void loadImageBitmapFromUri(Uri imageUri, ImageView imageView) {
		
		InputStream stream = null;
		
		try {
			  stream = mContext.getContentResolver().openInputStream(imageUri);


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

}