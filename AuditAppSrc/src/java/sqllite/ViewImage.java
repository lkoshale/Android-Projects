package com.machadalo.audit.sqllite;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.machadalo.audit.OnSwipeTouchListener;
import com.machadalo.audit.R;

public class ViewImage extends Activity {
	// Declare Variable
	TextView text;
	ImageView imageview;
	int position;
	String[] filepath;
	String[] filename;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from view_image.xml
		setContentView(R.layout.view_image);

		// Retrieve data from MainActivity on GridView item click
		Intent i = getIntent();

		// Get the position
		position = i.getExtras().getInt("position");

		// Get String arrays FilePathStrings
		filepath = i.getStringArrayExtra("filepath");

		// Get String arrays FileNameStrings
		filename = i.getStringArrayExtra("filename");

		// Locate the TextView in view_image.xml
		text = (TextView) findViewById(R.id.imagetext);

		// Load the text into the TextView followed by the position
		text.setText(filename[position]);

		// Locate the ImageView in view_image.xml
		imageview = (ImageView) findViewById(R.id.full_image_view);

		// Decode the filepath with BitmapFactory followed by the position
		Bitmap bmp = BitmapFactory.decodeFile(filepath[position]);

		// Set the decoded bitmap into ImageView
		imageview.setImageBitmap(bmp);

		imageview.setOnTouchListener(new OnSwipeTouchListener(this) {
			@Override

			public void onSwipeLeft() {
				try {

					//  Toast.makeText(getApplicationContext(), "Left Swipe ", Toast.LENGTH_SHORT).show();
					position = position + 1;

					setImage(position);  //Call method to set text and image
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Last Image ", Toast.LENGTH_SHORT).show();
					position = position - 1;
				}
			}

			@Override
			public void onSwipeRight() {
				try {
					///   Toast.makeText(getApplicationContext(), "Right Swipe ", Toast.LENGTH_SHORT).show();
					position = position - 1;

					setImage(position); //Call method to set text and image
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "First Image ", Toast.LENGTH_SHORT).show();
					position = 0;
				}
			}

		});

	}
	public void setImage(int pos)
	{

		Bitmap bmp = BitmapFactory.decodeFile(filepath[pos]);

		// Set the decoded bitmap into ImageView
		imageview.setImageBitmap(bmp);
	}
}