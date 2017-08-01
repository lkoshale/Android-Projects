package com.machadalo.audit.sqllite;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.machadalo.audit.ImageLoader;
import com.machadalo.audit.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QueueAdapter extends ArrayAdapter<AuditGS>{

	Context context;
	DataBaseHandler db;
	private ImageView img;
	private TextView ad_ID;
	ArrayList<AuditGS> image = new ArrayList<AuditGS>();
	private String url = "http://android.infiniteloopsinc.com/audit/media/assigned.php"; // our Url to server
	private ImageLoader imgLoader;
	    int layoutResourceId;
	   // BcardImage data[] = null;
	    ArrayList<AuditGS> data=new ArrayList<AuditGS>();
	File file;
	private String[] FilePathStrings;
	private String[] FileNameStrings;
	private File[] listFile;
	    public QueueAdapter(Context context, int layoutResourceId, ArrayList<AuditGS> data) {
	        super(context, layoutResourceId, data);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	        this.data = data;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;

			db = DataBaseHandler.getInstance(getContext());
			List<AuditGS> audit = db.getImageUrl();
			for (AuditGS url : audit) {
				// add contacts data in arrayList
				image.add(url);

			}
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			AuditGS picture = data.get(position);

			ad_ID =(TextView)row.findViewById(R.id.txtInventoryID);
			ad_ID.setText(picture._ad_inventory_ID);

			/*img = (ImageView) row.findViewById(R.id.img);
			url = "http://android.infiniteloopsinc.com/audit/media/inventory/"+picture._image;
			imgLoader = new ImageLoader(getContext());
			imgLoader.DisplayImage(url, img);*/


			// Check for SD Card
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
			Toast.makeText(getContext(),"Error SD card not found",Toast.LENGTH_LONG).show();

			} else {
				// Locate the image folder in your SD Card
				file = new File(Environment.getExternalStorageDirectory()
					+ File.separator +"machadalo");
				// Create a new folder if no folder named SDImageTutorial exist
				file.mkdirs();
			}

			if (file.isDirectory()) {
				listFile = file.listFiles();
				// Create a String array for FilePathStrings
				FilePathStrings = new String[listFile.length];
				// Create a String array for FileNameStrings
				FileNameStrings = new String[listFile.length];

				for (int i = 0; i < listFile.length; i++) {
					// Get the path of the image file
					FilePathStrings[i] = listFile[i].getAbsolutePath();
					// Get the name image file
					FileNameStrings[i] = listFile[i].getName();
				}
			}

			img = (ImageView) row.findViewById(R.id.img);
			Bitmap bmp = BitmapFactory.decodeFile(FilePathStrings[position]);
			img.setImageBitmap(bmp);

	        //convert byte to bitmap take from contact class
	        
	      /*  byte[] outImage=picture._image;
	        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
	        Bitmap theImage = BitmapFactory.decodeStream(imageStream);
	        holder.imgIcon.setImageBitmap(theImage);*/

	       return row;
	       
	    }
	   

	}
