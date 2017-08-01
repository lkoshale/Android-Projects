package com.machadalo.audit.sqllite;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.machadalo.audit.R;

import java.util.ArrayList;
import java.util.List;

public class Queued extends Activity {


    DataBaseHandler db;
    ListView dataList;
    ArrayList<AuditGS> imageArry = new ArrayList<AuditGS>();

    QueueAdapter queueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queued);
        dataList = (ListView) findViewById(R.id.list);

        db = DataBaseHandler.getInstance(this);



        List<AuditGS> audits = db.getImageUrl();


        for (AuditGS au : audits) {
            String log = "Ad_Inventory_ID:" + au.get_ad_inventory_ID() + " Address: " + au.get_society_address()
                    + " ,Image: " + au.get_image();

            // Writing Contacts to log
            Log.d("Result: ", log);
            // add contacts data in arrayList
            imageArry.add(au);

        }




       /*  Set Data base Item into listview*/

        queueAdapter = new QueueAdapter(this, R.layout.single_list_item,imageArry);
        dataList.setAdapter(queueAdapter);

        /**
         * go to next activity for detail image
         */
       /* dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                imageName = imageArry.get(position).getImage();
                imageId = imageArry.get(position).getID();

                Log.d("Before Send:****", imageName + "-" + imageId);
                // convert byte to bitmap
                ByteArrayInputStream imageStream = new ByteArrayInputStream(
                        imageName);
                theImage = BitmapFactory.decodeStream(imageStream);
                Intent intent = new Intent(SQLiteDemoActivity.this,
                        DisplayImageActivity.class);
                intent.putExtra("imagename", theImage);
                intent.putExtra("imageid", imageId);
                startActivity(intent);

            }
        });
*/

    }
}
