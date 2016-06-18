package com.example.android.musicmash;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;


/*put a synopsis here
my first app so please don't judge too hard
*/

public class MainActivity extends Activity implements OnDragListener, View.OnLongClickListener, OnClickListener {

    private static final String TAG = "MainActivityJunk";
    MediaPlayer singleSoundByte;                                        //used in playSingleSoundByte method
    int singleSoundByteID;                                              //used in onClick and playSingleSoundByte methods
    static Button drum1, drum2, drum3;                                  //used in onDrag and onClick methods
    LinearLayout cell_1_1, cell_1_2, cell_1_3;                          //used in onDrag and onClick methods


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //link each button to its respective xml id
        drum1 = (Button) findViewById(R.id.drum1);
        drum2 = (Button) findViewById(R.id.drum2);
        drum3 = (Button) findViewById(R.id.drum3);

        //link each layout to its respective xml id
        cell_1_1 = (LinearLayout) findViewById(R.id.cell_1_1);
        cell_1_2 = (LinearLayout) findViewById(R.id.cell_1_2);
        cell_1_3 = (LinearLayout) findViewById(R.id.cell_1_3);

        //register a long click listener for the buttons
        drum1.setOnLongClickListener(this);
        drum2.setOnLongClickListener(this);
        drum3.setOnLongClickListener(this);

        //register drag event listeners for the target layout containers
        cell_1_1.setOnDragListener(this);
        cell_1_2.setOnDragListener(this);
        cell_1_3.setOnDragListener(this);
    }

    //called when button has been touched and held
    @Override
    public boolean onLongClick(View imageView) {
        //the button has been touched
        //create clip data holding data of the type MIMETYPE_TEXT_PLAIN
        ClipData clipData = ClipData.newPlainText("", "");

        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(imageView);
        //start the drag - contains the data to be dragged,
        //metadata for this data and callback for drawing shadow
        imageView.startDrag(clipData, shadowBuilder, imageView, 0);
        return true;
    }

    //called when the button starts to be dragged
    //used by top and bottom layout containers
    @Override
    public boolean onDrag(View receivingLayoutView, DragEvent dragEvent) {
        View draggedButtonView = (View) dragEvent.getLocalState();

        // Handles each of the expected events
        switch (dragEvent.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:
                Log.i(TAG, "drag action started");

                // Determines if this View can accept the dragged data
                if (dragEvent.getClipDescription()
                        .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    Log.i(TAG, "Can accept this data");

                    // returns true to indicate that the View can accept the dragged data.
                    return true;

                } else {
                    Log.i(TAG, "Can not accept this data");

                }

                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                Log.i(TAG, "drag action entered");
//                the drag point has entered the bounding box
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                Log.i(TAG, "drag action location");
                /*triggered after ACTION_DRAG_ENTERED
                stops after ACTION_DRAG_EXITED*/
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                Log.i(TAG, "drag action exited");
//                the drag shadow has left the bounding box
                return true;

            case DragEvent.ACTION_DROP:
                /* the listener receives this action type when
                drag shadow released over the target view
                the action only sent here if ACTION_DRAG_STARTED returned true
                return true if successfully handled the drop else false*/

                //Looks at id of view button was dropped in and adds that button clone in said view (cell)
                switch (receivingLayoutView.getId()) {
                    case R.id.cell_1_1:

                        Log.i(TAG, "cell_1_1");

                        buttonSelection(draggedButtonView, receivingLayoutView);    //sends button and cell into buttonSelection method
                        return true;

                    case R.id.cell_1_2:

                        Log.i(TAG, "cell_1_2");

                        buttonSelection(draggedButtonView, receivingLayoutView);
                        return true;

                    case R.id.cell_1_3:

                        Log.i(TAG, "cell_1_3");

                        buttonSelection(draggedButtonView, receivingLayoutView);
                        return true;

                }

            case DragEvent.ACTION_DRAG_ENDED:

                Log.i(TAG, "drag action ended");
                Log.i(TAG, "getResult: " + dragEvent.getResult());

                return true;
            // An unknown action type was received.
            default:
                Log.i(TAG, "Unknown action type received by OnDragListener.");
                break;
        }
        return false;
    }

    /*this method is called when a button is tapped. It looks for an id of the button, assigns a
    sound byte id with the variable singleSoundByteID which sends to playSingleSoundByte method which will play the desired
    sound byte*/

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drum1:
                singleSoundByteID = R.raw.drum1;    //give button sound byte id to be used in playSingleSoundByte method
                playSingleSoundByte();
                break;
            case R.id.drum1Cloned:                  //cloned button must play the same sound byte
                singleSoundByteID = R.raw.drum1;
                playSingleSoundByte();
                break;
            case R.id.drum2:
                singleSoundByteID = R.raw.drum2;
                playSingleSoundByte();
                break;
            case R.id.drum2Cloned:
                singleSoundByteID = R.raw.drum2;
                playSingleSoundByte();
                break;
            case R.id.drum3:
                singleSoundByteID = R.raw.drum3;
                playSingleSoundByte();
                break;
            case R.id.drum3Cloned:
                singleSoundByteID = R.raw.drum3;
                playSingleSoundByte();
                break;
            default:
                break;
        }
    }

    //plays the sound byte which was tapped. If it is already playing and is tapped again then will
    //stop playing and play the next button which was tapped
    public void playSingleSoundByte() {

        if (singleSoundByte == null) {   //will only play the sound byte if it isn't playing already
            singleSoundByte = MediaPlayer.create(this, singleSoundByteID);
            singleSoundByte.start();
        } else {
            singleSoundByte.release();
            singleSoundByte = null;
            singleSoundByte = MediaPlayer.create(this, singleSoundByteID);
            singleSoundByte.start();
        }
    }

    public boolean buttonSelection(View button, View cell) {

        LinearLayout cellHolder = (LinearLayout) findViewById(cell.getId());    //gets the id of cell and places it in cellHolder

        switch (button.getId()) {
            case R.id.drum1:

                Log.i(TAG, "button drum1");
                // create new button so that we don't have to use removeView on the original button

                Button drum1Cloned = new Button(this);
                drum1Cloned.setId(R.id.drum1Cloned);          //create id in ids.xml so that new button can be referred to in onClick method
                drum1Cloned.setText(drum1.getText());         //getText so that buttons look identical
                drum1Cloned.setOnClickListener(this);         //setOnClickListener so that new button id can be sent to onClick method when tapped
                drum1Cloned.setOnLongClickListener(this);     //setOnLongClickListener so that new button can be dragged
                drum1Cloned.setWidth(drum1.getWidth());       //setWidth so that buttons look identical
                drum1Cloned.setHeight(drum1.getHeight());     //setWidth so that buttons look identical
                cellHolder.addView(drum1Cloned);              //add the new drum2Cloned button to top linear layout
                return true;

            case R.id.drum2:

                Log.i(TAG, "button drum2");

                Button drum2Cloned = new Button(this);
                drum2Cloned.setId(R.id.drum2Cloned);
                drum2Cloned.setText(drum2.getText());
                drum2Cloned.setOnClickListener(this);
                drum2Cloned.setOnLongClickListener(this);
                drum2Cloned.setWidth(drum2.getWidth());
                drum2Cloned.setHeight(drum2.getHeight());
                cellHolder.addView(drum2Cloned);

                return true;

            case R.id.drum3:

                Log.i(TAG, "button drum3");

                Button drum3Cloned = new Button(this);
                drum3Cloned.setId(R.id.drum3Cloned);
                drum3Cloned.setText(drum3.getText());
                drum3Cloned.setOnClickListener(this);
                drum3Cloned.setOnLongClickListener(this);
                drum3Cloned.setWidth(drum3.getWidth());
                drum3Cloned.setHeight(drum3.getHeight());
                cellHolder.addView(drum3Cloned);

                return true;

            default:
                Log.i(TAG, "in default");
                return false;
        }
    }
}




