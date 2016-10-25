package com.example.android.musicmash;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


/*put a synopsis here
my first app so please don't judge too hard
*/

public class MainActivity extends Activity implements OnDragListener, View.OnLongClickListener, OnClickListener {

    private static final String TAG = "MainActivityJunk";
    MediaPlayer singleSoundByte;
    int singleSoundByteID;
    static Button drum1,drum2, drum3;
    RelativeLayout rel1, rel2;
    LinearLayout bottom_lin_container;
    HorizontalScrollView bottom_hor_container;
    int snappedXCoord;
    int initialXCoord;
    int drumPlacement1[] = new int[30];
    int drumPlacement2[] = new int[30];
    int ribbonLengthInDP = 1200;
    int minTileLengthInDP = 40;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //link each button to its respective xml id
        drum1 = (Button) findViewById(R.id.drum1);
        drum2 = (Button) findViewById(R.id.drum2);
        drum3 = (Button) findViewById(R.id.drum3);

        //link each layout to its respective xml id
        rel1 = (RelativeLayout) findViewById(R.id.rel1);
        rel2 = (RelativeLayout) findViewById(R.id.rel2);
        bottom_lin_container = (LinearLayout) findViewById(R.id.bottom_lin_container);
        bottom_hor_container = (HorizontalScrollView) findViewById(R.id.bottom_hor_container);

        //register a long click listener for the buttons
        drum1.setOnLongClickListener(this);
        drum2.setOnLongClickListener(this);
        drum3.setOnLongClickListener(this);

        //register drag event listeners for the target layout containers
        rel1.setOnDragListener(this);
        rel2.setOnDragListener(this);
        bottom_lin_container.setOnDragListener(this);
        bottom_hor_container.setOnDragListener(this);
    }

    //called when button has been touched and held
    @Override
    public boolean onLongClick(View imageView) {
        //the button has been touched
        //create clip data holding data of the type MIMETYPE_TEXT_PLAIN
        ClipData clipData = ClipData.newPlainText("", "");

        initialXCoord = snap((int)imageView.getX());    //used to capture x-coords before onDrag begins

        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(imageView);
        //start the drag - contains the data to be dragged,
        //metadata for this data and callback for drawing shadow
        imageView.startDrag(clipData, shadowBuilder, imageView, 0);
        return true;
    }

    //called when the button starts to be dragged
    @Override
    public boolean onDrag(View receivingLayoutView, DragEvent dragEvent) {
        View draggedButtonView = (View) dragEvent.getLocalState();

        Log.i(TAG, "draggedButtonView = " + draggedButtonView.getWidth());

        // Handles each of the expected events
        switch (dragEvent.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:
                Log.i(TAG, "drag action started");

                // Determines if this View can accept the dragged data
                if (dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
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

                //dragEvent.getX() gets the x coordinate of where button was dropped.
                //Coordinates then get sent to the snap method
                snappedXCoord = snap((int)dragEvent.getX());

                //used to set "bounding area" of where one can drop the button
                if ((snappedXCoord - (draggedButtonView.getWidth() / 2)) >= 0 && (snappedXCoord + (draggedButtonView.getWidth() / 2)) <= dpToPx(ribbonLengthInDP)) {

                    //used to hold the parent layout of the button being dragged so that it can be sent to openSpace method
                    //and to also remove button which was dragged in one of the ribbons
                    ViewGroup draggedImageViewParentLayout = (ViewGroup) draggedButtonView.getParent();

                    //call openSpace method to ensure the dragged button in the ribbons' space has been freed as to allow the next button to be placed
                    openSpace(initialXCoord, draggedButtonView.getWidth(), draggedImageViewParentLayout);

                    //will run only if the area where button is to be placed is not occupied
                    if (isSpaceOpen((snappedXCoord - (draggedButtonView.getWidth() / 2)), draggedButtonView.getWidth(), receivingLayoutView)) { //(snappedXCoord- (button.getWidth() / 2) sends coords at beginning of button

                        switch (draggedImageViewParentLayout.getId()) {

                            case R.id.rel1:
                                Log.i(TAG, "the parent view of this button is rel1");
                                draggedImageViewParentLayout.removeView(draggedButtonView);         //remove button from parent view
                                break;

                            case R.id.rel2:
                                Log.i(TAG, "the parent view of this button is rel2");
                                draggedImageViewParentLayout.removeView(draggedButtonView);
                                break;

                            default:
                                Log.i(TAG, "default case in ACTION_DROP switch - parent view wasn't rel1 or rel2 so don't delete any buttons");
                                break;
                        }

                    } else {
                        occupySpace(initialXCoord, draggedButtonView.getWidth(), draggedImageViewParentLayout);
                        Log.i(TAG, "ACTION_DROP else of the if statement - space is not open");
                        return false;
                    }

                    //Looks at id of view button was dropped in and adds that button clone in said view (relative layout)
                    switch (receivingLayoutView.getId()) {
                        case R.id.rel1:

                            buttonSelection(draggedButtonView, receivingLayoutView);    //sends button and layout into buttonSelection method
                            break;

                        case R.id.rel2:

                            buttonSelection(draggedButtonView, receivingLayoutView);
                            break;

                        default:
                            Log.i(TAG, "default case in ACTION_DROP switch");
                            break;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "ACTION_DROP else statement - Can't drop out of bounding area", Toast.LENGTH_SHORT).show();
                    return false;
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
            case R.id.drum2:
                singleSoundByteID = R.raw.drum2;
                playSingleSoundByte();
                break;
            case R.id.drum3:
                singleSoundByteID = R.raw.drum3;
                playSingleSoundByte();
                break;
            case R.id.playlist:                     //used to show array index values
                runLoop();
                break;
            case R.id.test:                         //used to test random shit
                test();
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

    public boolean buttonSelection(View button, View layout) {

        RelativeLayout layoutHolder = (RelativeLayout) findViewById(layout.getId());    //gets the id of layout and places it in layoutHolder
        Button buttonHolder = (Button) button;                                          //parse View button to Button so that can set properties of drumCloned

        Log.i(TAG, "button drum width = " + button.getWidth());

        if (isSpaceOpen((snappedXCoord - (button.getWidth() / 2)), button.getWidth(), layout)) {    //(snappedXCoord- (button.getWidth() / 2) sends coords at beginning of button

            occupySpace(snappedXCoord - (button.getWidth() / 2), button.getWidth(), layout);         //(snappedXCoord- (button.getWidth() / 2) sends coords at beginning of button

            Button drumCloned = new Button(this);                           //create new button so that we don't have to use removeView on the original button
            drumCloned.setId(button.getId());                               //create id in ids.xml so that new button can be referred to in onClick method
            drumCloned.setText(buttonHolder.getText());                     //getText so that buttons look identical
            drumCloned.setOnClickListener(this);                            //setOnClickListener so that new button id can be sent to onClick method when tapped
            drumCloned.setOnLongClickListener(this);                        //setOnLongClickListener so that new button can be dragged
            drumCloned.setWidth(buttonHolder.getWidth());                   //setWidth so that buttons look identical
            drumCloned.setHeight(buttonHolder.getHeight());                 //setHeight so that buttons look identical
            drumCloned.setBackgroundResource(R.drawable.drums);             //setBackgroundResource so that drum buttons look identical
            drumCloned.setMinimumWidth(1);                                  //setMinimumWidth to override default so that buttons appear correctly
            drumCloned.setMinimumHeight(1);                                 //setMinimumHeight to override default so that buttons appear correctly
            drumCloned.setX(snappedXCoord - (buttonHolder.getWidth() / 2)); //positions button where dropped ( width /2 , because x-coord of drop is where finger was released and x-coord of button is at the left side of button)
            layoutHolder.addView(drumCloned);                               //add the new drum1Cloned button to layout

            return true;

        } else {
            Log.i(TAG, "buttonSelection method if statement - Can't place button");
            return false;
        }
    }

    //gives the button which was dropped a "snap" function of 40dp increments
    private int snap(int px)
    {
        float dpXCoord = pxToDp(px);                                              //takes the x-coordinates in pixels and converts to dp

        Log.i(TAG, "x coord in dp before rounding = " + dpXCoord);

        dpXCoord = minTileLengthInDP*(Math.round(dpXCoord/minTileLengthInDP));    //rounds off the dp to the closest increment of 40

        Log.i(TAG, "x coord in dp after rounding = " + dpXCoord);                 //prints x-coordinates in dp and not px

        dpXCoord = dpToPx((int) dpXCoord);                                        //converts back to px's
        return (int) dpXCoord;
    }

    //converts dp to px's
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    //converts px's to dp
    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    //evaluates if the button has enough space to fit in drop zone
    private boolean isSpaceOpen(int coordinates, int buttonWidth, View layout) {

        boolean spaceOpen = true;

        switch (layout.getId()) {
            case R.id.rel1:

                spaceOpen = arrayOccupancyCheck(coordinates, buttonWidth, drumPlacement1);

                break;
            case R.id.rel2:

                spaceOpen = arrayOccupancyCheck(coordinates, buttonWidth, drumPlacement2);

                break;
            default:

                Log.i(TAG, "isSpaceOpen method case default - parent view wasn't rel1 or rel2");

                break;
        } return spaceOpen; //return either true or false
    }

    private boolean arrayOccupancyCheck(int coordinates, int buttonWidth,int[] myArray) {

        int x;
        boolean spaceOpen = true;                                                   //return default of true when the if statement doesn't execute

        //cycles through the length of the button at this specific
        //area where button was dropped in the array
        for (int i = 0; dpToPx(minTileLengthInDP) * i / buttonWidth < 1; i++) {     //when dpToPx(minTileLengthInDP)*i/buttonWidth reaches a value 1 then means has gone through entire width of button

            x = ((coordinates / dpToPx(minTileLengthInDP)) + i);                    //formula to determine index of the array

            if (myArray[x] == 1) {                                                  //if that specific index of the array = 1 then that space is already occupied

                Log.i(TAG, "arrayOccupancyCheck method case - Can't place button");
                spaceOpen = false;
                break;
            } else {
                Log.i(TAG, "arrayOccupancyCheck method returning true");
            }
        }
        return spaceOpen; //return either true or false
    }

    //renders area where button was dropped unavailable/full
    private void occupySpace(int coordinates, int buttonWidth,View layout) {

        switch (layout.getId()) {
            case R.id.rel1:     //rel1 relates to drumPlacement1

                arrayCycler (coordinates, buttonWidth,drumPlacement1 , 1);  //pass 1 for arrayValue parameter to close space

                break;
            case R.id.rel2:     //rel2 relates to drumPlacement2

                arrayCycler (coordinates, buttonWidth,drumPlacement2 , 1);

                break;
            default:

                Log.i(TAG, "occupySpace method case default");

                break;
        }
    }

    //renders area where button was dropped available/open
    private void openSpace(int coordinates, int buttonWidth, View layout) {

        switch (layout.getId()) {
            case R.id.rel1:     //rel1 relates to drumPlacement1

                arrayCycler (coordinates, buttonWidth,drumPlacement1 , 0);  //pass 0 for arrayValue parameter to close space

                break;
            case R.id.rel2:     //rel2 relates to drumPlacement2

                arrayCycler (coordinates, buttonWidth,drumPlacement2 , 0);

                break;
            default:

                Log.i(TAG, "openSpace method case default");

                break;
        }
    }

    private void arrayCycler (int coordinates, int buttonWidth,int[] myArray , int arrayValue) {

        int x;
        float u;
        int i = 0;

        do {
            x = (coordinates / dpToPx(minTileLengthInDP)) + i;      //formula to determine index of the array
            myArray[x] = arrayValue;                                //assigns a value of 0 or 1 to the array index to open or close the space
            i++;
            u = dpToPx(minTileLengthInDP) * i;                      //this calculates increments of minTileLengthInDP as to cycle through each increment of buttonWidth
        }
        while (u / buttonWidth < 1);                                //when u / buttonWidth = 1 it would mean that the loop has cycled through the full button length

    }

    public void runLoop()   //used to show array index values
    {
        {
            int i;
            int u;

            for (i = 0; i < drumPlacement1.length; i++) {
                Log.i(TAG, "drumPlacement1 [" + i + "] = " + drumPlacement1[i]);
            }

            for (u = 0; u < drumPlacement2.length; u++) {
                Log.i(TAG, "drumPlacement2 [" + u + "] = " + drumPlacement2[u]);
            }
        }
    }

    public void test() {}
}








