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
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;


/*put a synopsis here
my first app so please don't judge too hard
*/

public class MainActivity extends Activity implements OnDragListener, View.OnLongClickListener, OnClickListener {

    private static final String TAG = "junk";
    MediaPlayer singleSoundByte;                                        //used in playSingleSoundByte method
    MediaPlayer AllSoundBytes;                                          //used in playAll and playPlaylistSoundByte methods
    int singleSoundByteID;                                              //used in onClick and playSingleSoundByte methods
    int playlistSoundByteID;                                            //used in giveClonesSound
    Button drum1, drum2, drum3;                                         //used in onDrag and onClick methods
    HorizontalScrollView top_hor_container, bottom_hor_container;       //used in onDrag and onClick methods
    LinearLayout top_lin_container, bottom_lin_container;               //used in onDrag and onClick methods
    int[] playlist = new int[0];                                        //used in updatePlaylist and playAll methods


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //link each button to its respective xml id
        drum1 = (Button) findViewById(R.id.drum1);
        drum2 = (Button) findViewById(R.id.drum2);
        drum3 = (Button) findViewById(R.id.drum3);

        //link each layout to its respective xml id
        top_hor_container = (HorizontalScrollView) findViewById(R.id.top_hor_container);
        bottom_hor_container = (HorizontalScrollView) findViewById(R.id.bottom_hor_container);
        top_lin_container = (LinearLayout) findViewById(R.id.top_lin_container);
        bottom_lin_container = (LinearLayout) findViewById(R.id.bottom_lin_container);

        //register a long click listener for the buttons
        drum1.setOnLongClickListener(this);
        drum2.setOnLongClickListener(this);
        drum3.setOnLongClickListener(this);

        //register drag event listeners for the target layout containers
        top_hor_container.setOnDragListener(this);
        bottom_hor_container.setOnDragListener(this);
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
        View draggedImageView = (View) dragEvent.getLocalState();

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

                if (top_hor_container == receivingLayoutView) {     //if dropped in top linear layout run this code

                    switch (draggedImageView.getId()) {
                        case R.id.drum1:

                            Log.i(TAG, "button drum1");
                            // create new button so that we don't have to use removeView on the original button

                            Button drum1Cloned = new Button(this);
                            drum1Cloned.setId(R.id.drum1Cloned);          //create id in ids.xml so that new button can be referred to in onClick method
                            drum1Cloned.setText(drum1.getText());         //getText so that buttons looks identical
                            drum1Cloned.setOnClickListener(this);         //setOnClickListener so that new button id can be sent to onClick method when tapped
                            drum1Cloned.setOnLongClickListener(this);     //setOnLongClickListener so that new button can be dragged
                            top_lin_container.addView(drum1Cloned);       //add the new drum2Cloned button to top linear layout
                            updatePlaylist(R.id.drum1Cloned);             //pass in drum1Cloned id into updatePlaylist method so that it can be indexed into the playlist array
                            return true;

                        case R.id.drum2:

                            Log.i(TAG, "button drum2");

                            Button drum2Cloned = new Button(this);
                            drum2Cloned.setId(R.id.drum2Cloned);
                            drum2Cloned.setText(drum2.getText());
                            drum2Cloned.setOnClickListener(this);
                            drum2Cloned.setOnLongClickListener(this);
                            top_lin_container.addView(drum2Cloned);
                            updatePlaylist(R.id.drum2Cloned);

                            return true;

                        case R.id.drum3:

                            Log.i(TAG, "button drum3");

                            Button drum3Cloned = new Button(this);
                            drum3Cloned.setId(R.id.drum3Cloned);
                            drum3Cloned.setText(drum3.getText());
                            drum3Cloned.setOnClickListener(this);
                            drum3Cloned.setOnLongClickListener(this);
                            top_lin_container.addView(drum3Cloned);
                            updatePlaylist(R.id.drum3Cloned);

                            return true;

                        default:
                            Log.i(TAG, "in default");
                            return false;
                    }
                } else {
                    Log.i(TAG, "Did not drop in top_container");

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
                singleSoundByteID = R.raw.drum1;   //give button sound byte id to be used in playSingleSoundByte method
                playSingleSoundByte();
                break;
            case R.id.drum1Cloned:  //cloned button must play the same sound byte
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
//
    public void updatePlaylist(int clonedButtonID) {

        //define the new array with an extra slot to store the next button id
        int[] newPlaylist = new int[playlist.length + 1];

        //copy values into new array
        for (int i = 0; i < playlist.length; i++)
            newPlaylist[i] = playlist[i];

        //add new value to the new array
        newPlaylist[newPlaylist.length - 1] = clonedButtonID;

        //set the newPlaylist as the default playlist
        playlist = newPlaylist;
    }

    //runs when the play button is tapped
    //must cycle through the entire playlist and play each sound byte sequentially
    public void playAll(View v) {

        for (int i = 0; i < playlist.length; i++) {     //cycle through the entire playlist
            giveClonesSound(playlist[i]);

            while (AllSoundBytes.isPlaying()) {         //will continue to loop while AllSoundBytes is playing its respective sound byte in the playPlaylistSoundByte method

            }
        }
    }

    //gives the cloned buttons a sound byte id
    public void giveClonesSound(int id) {
        switch (id) {
            case R.id.drum1Cloned:
                playlistSoundByteID = R.raw.drum1;   //give button sound byte id to be used in playPlaylistSoundByte method
                playPlaylistSoundByte();
                break;
            case R.id.drum2Cloned:
                playlistSoundByteID = R.raw.drum2;
                playPlaylistSoundByte();
                break;
            case R.id.drum3Cloned:
                playlistSoundByteID = R.raw.drum3;
                playPlaylistSoundByte();
                break;
            default:
                break;
        }
    }

    //plays the individual sound byte from the playlist array
    public void playPlaylistSoundByte() {
        AllSoundBytes = MediaPlayer.create(this, playlistSoundByteID);
        AllSoundBytes.start();
        AllSoundBytes.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer AllSoundBytes) {
                AllSoundBytes.release();
            }
        });
    }

}




