package de.vogella.android.todos;

import java.util.ArrayList;
import java.util.Date;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import de.vogella.android.todos.contentprovider.MyTodoContentProvider;
import de.vogella.android.todos.database.TodoTable;

/*
 * TodosOverviewActivity displays the existing todo items
 * in a list
 * 
 * You can create new ones via the ActionBar entry "Insert"
 * You can delete existing ones via a long press on the item
 */

public class TodosOverviewActivity extends ListActivity {
//implements	LoaderManager.LoaderCallbacks<Cursor> {
	
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	
	
	// private Cursor;
	private MySimpleArrayAdapter adapter2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_list);
		this.getListView().setDividerHeight(2);
		registerForContextMenu(getListView());
		fillData();
	}



	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fillData();
	}


	// Create the menu based on the XML defintion
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu, menu);
		return true;
	}

	// Reaction to the menu selection
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createTodo();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Uri uri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/"
					+ info.id);
			getContentResolver().delete(uri, null, null);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void createTodo() {
		Intent i = new Intent(this, TodoDetailActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	// Opens the second activity if an entry is clicked
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, TodoDetailActivity.class);

		String todoUri = adapter2.getDesc(position);
		i.putExtra(MyTodoContentProvider.CONTENT_ITEM_TYPE, todoUri);

		// Activity returns an result if called with startActivityForResult
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	// Called with the result of the other activity
	// requestCode was the origin request code send to the activity
	// resultCode is the return code, 0 is everything is ok
	// intend can be used to get data
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

	
	 public static ArrayList<String> nameOfEvent = new ArrayList<String>();
     public static ArrayList<Date> startDates = new ArrayList<Date>();
     public static ArrayList<String> endDates = new ArrayList<String>();
     public static ArrayList<String> descriptions = new ArrayList<String>();

	
	private void fillData() {

	//sort by date
	fetchEvents();

	adapter2 = new MySimpleArrayAdapter
	    		(this, nameOfEvent, startDates, descriptions);
	        setListAdapter(adapter2);
    }

	private void fetchEvents() {
		String[] selection = new String[] { "calendar_id", "title", "description",
                "dtstart", "dtend", "eventLocation" };
		
		String projection = "description LIKE ?";
		String[] selecionArgs = new String[]{"%/images/%"};
		String orderby = "dtstart ASC";
		
		Cursor cursor = getContentResolver()
		        .query(
		                Uri.parse("content://com.android.calendar/events"),
		                selection, projection,
		                selecionArgs, orderby);
		cursor.moveToFirst();
		// fetching calendars name
		String CNames[] = new String[cursor.getCount()];

		// fetching calendars id
		nameOfEvent.clear();
		startDates.clear();
		endDates.clear();
		descriptions.clear();
		for (int i = 0; i < CNames.length; i++) {
		    nameOfEvent.add(cursor.getString(1));
		    startDates.add(new Date(Long.parseLong(cursor.getString(3))));
		    //endDates.add(new Date(Long.parseLong(cursor.getString(4))));
		    
		    String fullDescription = cursor.getString(2);
		    
		    int firstIndex = fullDescription.indexOf("localUri: ");
		    firstIndex = (firstIndex < 0)? 0 : 10;
		    
		    
		    int lastIndex = fullDescription.indexOf("\n");
		    lastIndex = (lastIndex < 0)? fullDescription.length() : lastIndex;
		    
		    descriptions.add(fullDescription.substring(firstIndex, lastIndex));
		  
		    CNames[i] = cursor.getString(1);
		    cursor.moveToNext();

		}
	}
}
