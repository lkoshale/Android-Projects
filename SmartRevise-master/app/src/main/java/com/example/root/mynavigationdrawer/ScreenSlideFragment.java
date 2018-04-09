package com.example.root.mynavigationdrawer;

import android.app.Dialog;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.util.Size;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by root on 31/12/15.
 */
public class ScreenSlideFragment  extends Fragment {

    private int position;
    private ImageDataStructure  obj;
    private Vector<String> tag_container;
    private String image_id = "";
    private static Boolean is_description_updated = false;
    private float scale = 1f;
    private ScaleGestureDetector SGD;
    private Matrix matrix = new Matrix();
    private GestureDetectorCompat gestureDetector;

    PhotoViewAttacher mAttacher;
    private ImageView iv;


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private ImageView iv;
        public ScaleListener ( ImageView iv) {

            this.iv = iv;

        }
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(0.1f, Math.min(scale, 5.0f));
            matrix.setScale(scale, scale);
            iv.setImageMatrix(matrix);
            System.out.println ( "ScreenSlideFragment(): onScale() called ") ;
            return true;
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_displaytext, container, false);
        final ImageView v = (ImageView)rootView.findViewById(R.id.ivSetImage);
        //TextView tv = (TextView)rootView.findViewById(R.id.tvDisplay);
        final String path = getArguments().getString("image");
        image_id = getArguments().getString("image_id");

        //SGD = new ScaleGestureDetector( getActivity() ,new ScaleListener( v ));

        gestureDetector = new GestureDetectorCompat( getActivity(), new SingleTapConfirm() ) ;



        gestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {

            boolean isVisible = false;


            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {


                final LinearLayout mLayout = (LinearLayout) rootView.findViewById(R.id.button_holder);
                final LinearLayout upper_layout = (LinearLayout) rootView.findViewById(R.id.upper_view);
                System.out.println("ScreenSlideFragment() : Single tap on the ImageView");


                if (!isVisible) {
                    isVisible = true;
                    mLayout.setVisibility(View.VISIBLE);
                    upper_layout.setVisibility(View.VISIBLE);

                } else {
                    isVisible = false;
                    mLayout.setVisibility(View.GONE);
                    upper_layout.setVisibility(View.GONE);

                }

                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                System.out.println("ScreenSlideFragment(): Double tap on the image view");
                mAttacher = new PhotoViewAttacher((ImageView)v);
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });





        ViewTreeObserver vto = v.getViewTreeObserver();

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { // this zoom feature is not working

                //SGD.onTouchEvent(event);
                gestureDetector.onTouchEvent( event);
                //mAttacher = new PhotoViewAttacher((ImageView)v);
                System.out.println ( "ScreenSlideFragment() :   Touch Event called");
                return true;

            }
        });


        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {

                // Remove after the first run so it doesn't fire forever
                v.getViewTreeObserver().removeOnPreDrawListener(this);
                final int finalHeight;
                final int finalWidth;
                finalHeight = v.getMeasuredHeight();
                finalWidth = v.getMeasuredWidth();
                Bitmap bitmap_img = BitMapUtility.decodeSampledBitmapFromResource(path, finalWidth, finalHeight);
                if (bitmap_img != null)
                    System.out.println("ScreenSlideFragment(): final BitMap width = " + bitmap_img.getWidth() + " height = " + bitmap_img.getHeight());
                else {

                    System.out.println("bitmap_img is null");
                }


                /*Bitmap bp = BitmapFactory.decodeFile(path);

                if ( bp == null) {

                    CommonMethods cm = new CommonMethods(  getActivity());
                    cm.show_message("bitmap object is null");


                }
                else {

                    Bitmap resized = Bitmap.createScaledBitmap(bp, (int) (bp.getWidth() * 0.7), (int) (bp.getHeight() * 0.7), true);
                    v.setImageBitmap(resized);

                }*/

                v.setImageBitmap(bitmap_img);
                // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.

                // tv.setText("Height: " + finalHeight + " Width: " + finalWidth);
                return false;
            }
        });



/*

        v.setOnClickListener(new View.OnClickListener() { // when image is tapped

            boolean isVisible = false;

            @Override
            public void onClick(View v) {
                final LinearLayout mLayout = (LinearLayout) rootView.findViewById(R.id.button_holder);
                final LinearLayout upper_layout = (LinearLayout) rootView.findViewById(R.id.upper_view);
                System.out.println("ScreenSlideFragment() : ImageView is clicked");


                if (!isVisible) {
                    isVisible = true;
                    mLayout.setVisibility(View.VISIBLE);
                    upper_layout.setVisibility(View.VISIBLE);

                } else {
                    isVisible = false;
                    mLayout.setVisibility(View.GONE);
                    upper_layout.setVisibility(View.GONE);

                }
            }
        });
*/


        Button bAbout =  (Button)rootView.findViewById(R.id.bAbout);
        bAbout.setOnClickListener(new View.OnClickListener() { // when Update Button is clicked

            @Override
            public void onClick(View v) {

                handle_when_update_is_clicked( rootView ) ;

            }

        });


        Button bViewDescription =  (Button)rootView.findViewById(R.id.bViewDescription);

        bViewDescription.setOnClickListener( // when View Description is clicked

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // show description

      //                  desc = getArguments().getString("description"); // fetch the description
                        //mLayout.setVisibility(View.GONE); // make lower layout off
                        //upper_layout.setVisibility(View.GONE); // make upper layout off
                        Dialog dialog = new Dialog( getContext() );
                        dialog.setContentView(R.layout.my_message_box);
                        TextView tv = (TextView)dialog.findViewById(R.id.tvDisplay);

                        DatabaseHelper h = new DatabaseHelper( getContext() ) ; // every time i have to hit the database for latest Descriptin. Need to be optimised  :(
                        String str = h.getImageDescription( h, image_id);
                        if ( str != null &&  (!str.contentEquals(""))  ) {

                            tv.setText(str);
                            dialog.show(); // show dialogue  message

                        }

                        else {

                            show_message("This Image Does not has any Description.Add it by clicking UPDATE button below");
                        }

                    }
                }
        );

        Button bViewTags = (Button)rootView.findViewById(R.id.bViewTags); // when ViewTags is clicked
        bViewTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initalize_middle_layout(image_id);
                System.out.println("View Tags button is clicked") ;

            }

        });

        Button bDelete = (Button)rootView.findViewById(R.id.bDelete); // when Delete  is clicked
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handle_delete();

            }
        });

        Button bBack = (Button)rootView.findViewById(R.id.bBack); // when Delete  is clicked
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish(); // terminate the activity which called this fragment

            }
        });

        //mAttacher = new PhotoViewAttacher(v);


        return rootView;

    }

    private class SingleTapConfirm implements GestureDetector.OnGestureListener {


        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }


    }


    public static Fragment newInstance( int position, ImageDataStructure object) {

        ScreenSlideFragment frag = new ScreenSlideFragment();
        Bundle args = new Bundle();

        if ( object != null ) {

            String path = object.getPath(); // get file path from object
            String description = object.getDescription(); // get description from the object
            String id = object.getId();

            args.putString("image", path);
            args.putString("description", description);
            args.putString("image_id", id);

        }
        else {

            System.out.print( "Object received is  null in newInstance() method");

        }

        frag.setArguments(args);
        return frag;
    }

    public ScreenSlideFragment(  ) {

    }

    public void initalize_middle_layout (  String image_id)  {

        System.out.print("middle_layout function is called");
        Dialog d = new Dialog( getContext() ) ;
        d.setContentView(R.layout.display_tags);
        LinearLayout middle_layout = (LinearLayout) d.findViewById(R.id.display_tags);
        DatabaseHelper h = new DatabaseHelper(  getContext() ) ;
        String [] tags = h.getTagsForImage( h, image_id ) ;
        int i;
        middle_layout.setOrientation(LinearLayout.VERTICAL);
        if ( tags != null ) {

            for (i = 0; i < tags.length; i++) {

                String tag = tags[i];
                // get the LinearLayout
                TextView new_tv = new TextView(getContext());
                new_tv.setText(tag);
                new_tv.setTextSize(30);
                new_tv.setLayoutParams(new
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                middle_layout.addView(new_tv);

            }

            d.show();

        }
        else {

            CharSequence text =  "No Tags exist for This Image. PLease add it by clicking UPDATE button" ;
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText( getContext() , text, duration);
            toast.show();

        }

    }


    public void handle_when_update_is_clicked ( ViewGroup rootView) {


        final Dialog d = new Dialog(getContext());
        d.setContentView(R.layout.update_layout);
        d.show();

        RadioButton r1 = (RadioButton)d.findViewById(R.id.rb1) ;
        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updating_tags();

            }
        });

        RadioButton r2 = (RadioButton) d.findViewById(R.id.rb2);
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updating_description();

            }
        });


        Button b = (Button) d.findViewById(R.id.bDone);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                d.dismiss(); // dimiss the box when done

            }
        });


    }


    public void load_initial_description( Dialog dialog ) {
        // initial description will always be loaded from Database first .
        TextView tv = (TextView) dialog.findViewById( R.id.tvViewDescription) ;
        //String str = getArguments().getString("description") ;

        DatabaseHelper h = new DatabaseHelper( getContext() ) ; // hitting database everytime for latest Description. :(
        String str = h.getImageDescription( h, image_id);
          if ( str != null && str != "") {
            tv.setText(str);
         }
        else {

              show_message("This Image Does not has any Description.Add it by clicking UPDATE button below");

          }

    }



    public  void updating_description() {


        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.update_description);
        dialog.show(); // show dialogue  message

        load_initial_description(dialog);

        Button cancel = (Button) dialog.findViewById( R.id.bAddDescription_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button save = (Button) dialog.findViewById( R.id.bAddDescription_save) ;
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {

                    TextView tv = (TextView) dialog.findViewById( R.id.tvViewDescription) ;
                    String str = tv.getText().toString();
                    DatabaseHelper h = new DatabaseHelper( getContext() ) ;
                    h.update_description(h, image_id, str);

                    //args.putString("description", str);
                    //is_description_updated = true;

                } catch (Exception e) {
                    System.out.println("Error updating description: " + e.getMessage().toString()) ;

                }

                show_message ( "your new Description has been saved") ;
                dialog.dismiss();

            }

        });


    }


    public void updating_tags() {

        // called when 'update tags' button is called

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.add_tags);
        dialog.show(); // show dialogue  message

        Button bAdd = (Button) dialog.findViewById(R.id.bAddTags_add);
        final LinearLayout middle_layout = (LinearLayout) dialog.findViewById(R.id.add_tags_2);

        tag_container = new Vector<String>();

        bAdd.setOnClickListener(new View.OnClickListener() {

            TextView tv = (TextView) dialog.findViewById(R.id.tvAddTags);


            @Override
            public void onClick(View v) {

                middle_layout.setOrientation(LinearLayout.VERTICAL);
                String tag = tv.getText().toString();  // got the text , now display it on the LinearLayout in a new TextView
                tag_container.addElement(tag);
                // get the LinearLayout
                TextView new_tv = new TextView(getContext());
                new_tv.setText(tag);
                new_tv.setTextSize(30);
                new_tv.setLayoutParams(new
                        LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                middle_layout.addView(new_tv);
                tv.setText(""); // make the TextView Empty

            }
        });

        Button bCancel = (Button) dialog.findViewById(R.id.bAddTags_Cancel); // make the dialogue go away
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });


        Button bSave = (Button) dialog.findViewById(R.id.bAddTags_Save); // make the dialogue go away
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    DatabaseHelper h = new DatabaseHelper(getContext());
                    h.InsertTagsForImage(h, image_id, tag_container);
                    tag_container.clear();

                } catch (Exception e) {

                    System.out.print("error while inserting tags: " + e.getMessage().toString());

                }

                dialog.dismiss(); // dismiss the box whn done

            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        //args.clear(); // clear args for this fragment
        System.out.println("calling onStop for this fragment. clearing args") ;
    }

    public void show_message( CharSequence text) {

        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText( getContext() , text, duration);
        toast.show();

    }

    public void delete_tags() {

        DatabaseHelper h = new DatabaseHelper( getContext() ) ;
        if ( h.DeleteAlltagsForImage(h, image_id) )
            show_message("All tags associated with this image  have been deleted");
        else
            show_message("Error in Deleting all tags");

    }

    public void delete_description() {

        DatabaseHelper h = new DatabaseHelper( getContext() ) ;
        if ( h.DeleteDescription(h, image_id) )
            show_message("Description has been deleted");
        else
            show_message("Error occurred in Deleting Description");


    }


    public void delete_image() {

        DatabaseHelper h = new DatabaseHelper( getContext() ) ;
        if ( h.deleteAnImage(h, image_id) )
            show_message("Image has been deleted");
        else
            show_message("Error occurred in Deleting Description");

    }

    public void handle_delete() {

        final Dialog d = new Dialog( getContext() ) ;
        d.setContentView(R.layout.delete_box_1);
        d.show();
        RadioButton r1 = (RadioButton)d.findViewById( R.id.rbDelete_box_1) ;
        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delete_tags(); // deletes all rows with image id in ImageTags table .
            }
        });


        RadioButton r2 = (RadioButton)d.findViewById( R.id.rbDelete_box_2) ;
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delete_description(); // sets description to null
            }
        });


        RadioButton r3 = (RadioButton)d.findViewById( R.id.rbDelete_box_3) ;
        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delete_image(); // deletes that image
            }
        });

        Button b =  (Button) d.findViewById(R.id.bDelete_done);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss(); // dismiss when done
            }
        });

    }



}
