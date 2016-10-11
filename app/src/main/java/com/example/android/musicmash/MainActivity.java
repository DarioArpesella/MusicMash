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
import android.widget.RelativeLayout;
import android.widget.Toast;


/*put a synopsis here
my first app so please don't judge too hard
*/

public class MainActivity extends Activity implements OnDragListener, View.OnLongClickListener, OnClickListener {

    private static final String TAG = "MainActivityJunk";
    MediaPlayer singleSoundByte;                                        //used in onClick and playSingleSoundByte method
    int singleSoundByteID;                                              //used in onClick and playSingleSoundByte methods
    static Button drum1,drum2, drum3;                                   //used in onClick and buttonSelection methods
    RelativeLayout rel1, rel2;                                          //used in onDrag, isSpaceOpen, occupySpace, openSpace methods
    int snappedXCoord;                                                  //used in onDrag, buttonSelection, isSpaceOpen, occupySpace methods
    int initialXCoord;                                                  //used in onLongClick and openSpace methods
    int drumPlacement1[] = new int[19];                                 //used in isSpaceOpen, occupySpace and openSpace methods
    int drumPlacement2[] = new int[19];                                 //used in isSpaceOpen, occupySpace and openSpace methods


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

        //register a long click listener for the buttons
        drum1.setOnLongClickListener(this);
        drum2.setOnLongClickListener(this);
        drum3.setOnLongClickListener(this);

        //register drag event listeners for the target layout containers
        rel1.setOnDragListener(this);
        rel2.setOnDragListener(this);
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
    //used by top and bottom layout containers
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

                //used to hold the parent layout of the button being dragged so that it can be sent to openSpace method
                //and can to also remove button which was dragged in one of the ribbons
                ViewGroup draggedImageViewParentLayout = (ViewGroup) draggedButtonView.getParent();

                //call openSpace method to ensure the dragged button in the ribbons' space has been freed as to allow the next button to be placed
                openSpace(draggedButtonView.getWidth(),draggedImageViewParentLayout);

                //will run only if the area where button is to be placed is not occupied
                if (isSpaceOpen(draggedButtonView.getWidth(),receivingLayoutView)) {

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
                            Log.i(TAG, "default case in ACTION_DRAG_STARTED switch");
                            break;
                    }
                } else {
                    Log.i(TAG, "ACTION_DROP else of the if statement - space is not open");

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

        Log.i(TAG, "button drum width = " + button.getWidth());

        if (isSpaceOpen(button.getWidth(),layout)) {

            occupySpace(button.getWidth(),layout);

            switch (button.getId()) {
                case R.id.drum1:

                    Log.i(TAG, "button drum1");
                    // create new button so that we don't have to use removeView on the original button

                    //Button drum1Cloned = (Button)getLayoutInflater().inflate(R.layout.drumstyle1, null);
                    Button drum1Cloned = new Button(this);
                    drum1Cloned.setId(R.id.drum1);                              //create id in ids.xml so that new button can be referred to in onClick method
                    drum1Cloned.setText(drum1.getText());                       //getText so that buttons look identical
                    drum1Cloned.setOnClickListener(this);                       //setOnClickListener so that new button id can be sent to onClick method when tapped
                    drum1Cloned.setOnLongClickListener(this);                   //setOnLongClickListener so that new button can be dragged
                    drum1Cloned.setWidth(drum1.getWidth());                     //setWidth so that buttons look identical
                    drum1Cloned.setHeight(drum1.getHeight());                   //setHeight so that buttons look identical
                    drum1Cloned.setBackgroundResource(R.drawable.drums);        //setBackgroundResource so that drum buttons look identical
                    drum1Cloned.setMinimumWidth(1);                             //setMinimumWidth to override default so that buttons appear correctly
                    drum1Cloned.setMinimumHeight(1);                            //setMinimumHeight to override default so that buttons appear correctly
                    drum1Cloned.setX(snappedXCoord - (drum1.getWidth() / 2));   //positions button where dropped ( width /2 , because x-coord of drop is
                                                                                // where finger was released and x-coord of button is at the left side of button)
                    layoutHolder.addView(drum1Cloned);                          //add the new drum1Cloned button to layout

                    return true;

                case R.id.drum2:

                    Log.i(TAG, "button drum2");

                    Button drum2Cloned = new Button(this);
                    drum2Cloned.setId(R.id.drum2);
                    drum2Cloned.setText(drum2.getText());
                    drum2Cloned.setOnClickListener(this);
                    drum2Cloned.setOnLongClickListener(this);
                    drum2Cloned.setWidth(drum2.getWidth());
                    drum2Cloned.setHeight(drum2.getHeight());
                    drum2Cloned.setBackgroundResource(R.drawable.drums);
                    drum2Cloned.setMinimumWidth(1);
                    drum2Cloned.setMinimumHeight(1);
                    drum2Cloned.setX(snappedXCoord - (drum2.getWidth() / 2));
                    layoutHolder.addView(drum2Cloned);
                    return true;

                case R.id.drum3:

                    Log.i(TAG, "button drum3");

                    Button drum3Cloned = new Button(this);
                    drum3Cloned.setId(R.id.drum3);
                    drum3Cloned.setText(drum3.getText());
                    drum3Cloned.setOnClickListener(this);
                    drum3Cloned.setOnLongClickListener(this);
                    drum3Cloned.setWidth(drum3.getWidth());
                    drum3Cloned.setHeight(drum3.getHeight());
                    drum3Cloned.setBackgroundResource(R.drawable.drums);
                    drum3Cloned.setMinimumWidth(1);
                    drum3Cloned.setMinimumHeight(1);
                    drum3Cloned.setX(snappedXCoord - (drum3.getWidth() / 2));
                    layoutHolder.addView(drum3Cloned);

                    return true;

                default:
                    Log.i(TAG, "in default");
                    Toast.makeText(getApplicationContext(), "buttonSelection method default case - Can't place button", Toast.LENGTH_SHORT).show();
                    return false;
            }
        } else {
            Log.i(TAG, "buttonSelection method if statement - Can't place button");
            return false;
        }
    }

    //gives the button which was dropped a "snap" function of 40dp increments
    private int snap(int px)
    {
        float dpXCoord = pxToDp(px);  //takes the x-coordinates in pixels and converts to dp

        Log.i(TAG, "x coord in dp before rounding " + dpXCoord);

        dpXCoord = 40*(Math.round(dpXCoord/40));                                  //rounds off the dp to the closest increment of 40

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
    private boolean isSpaceOpen(int buttonwidth,View layout)                        //uses the width of the button for parameter calculations and layout to determine which ribbon the button resides in
    {
        int x;
        boolean spaceOpen = true;                                                   //return default of true when the if statement doesn't execute

        switch (layout.getId()) {
            case R.id.rel1:

                //cycles through the length of the button at this specific
                //area where button was dropped in the array
                for(int i = 0; dpToPx(40)*i/buttonwidth < 1; i++) {                 //when dpToPx(40)*i/buttonwidth reaches a value 1 then means has gone through entire width of button

                    x = ((snappedXCoord - (buttonwidth / 2)) / dpToPx(40)) + i;     //formula to determine index of the array

                    if (drumPlacement1[x] == 1) {                                   //if that specific index of the array = 1 then that space is already occupied

                        Log.i(TAG, "isSpaceOpen method case rel1 - Can't place button");
                        spaceOpen = false;
                        break;
                    }
                }
                break;
            case R.id.rel2:

                for(int i = 0; dpToPx(40)*i/buttonwidth < 1; i++) {

                    x = ((snappedXCoord - (buttonwidth / 2)) / dpToPx(40)) + i;

                    if (drumPlacement2[x] == 1) {

                        Log.i(TAG, "isSpaceOpen method case rel2 - Can't place button");
                        spaceOpen = false;
                        break;
                    }
                }
                break;
            default:

                Log.i(TAG, "isSpaceOpen method case default");
                break;
        } return spaceOpen;                                                         //return either true or false
    }

    //renders area where button was dropped unavailable/full
    private void occupySpace(int buttonWidth,View layout) {                         //uses the width of the button for parameter calculations and layout to determine which ribbon
        int x;
        float u;
        int i = 0;

        switch (layout.getId()) {
            case R.id.rel1:

                do {
                    x = ((snappedXCoord - (buttonWidth / 2)) / dpToPx(40)) + i;     //formula to determine index of the array
                    drumPlacement1[x] = 1;                                          //assigns a value of 1 to index to close the space
                    i++;
                    u = dpToPx(40) * i;
                }
                while (u / buttonWidth < 1);
                break;
            case R.id.rel2:

                do {
                    x = ((snappedXCoord - (buttonWidth / 2)) / dpToPx(40)) + i;
                    drumPlacement2[x] = 1;
                    i++;
                    u = dpToPx(40) * i;
                }
                while (u / buttonWidth < 1);
                break;
            default:
                Log.i(TAG, "occupySpace method case default");
                break;
        }
    }

    //renders area where button was dropped available/open
    private void openSpace(int buttonWidth,View layout) {                         //uses the width of the button for parameter calculations and layout to determine which ribbon
        int x;
        float u;
        int i = 0;

        switch (layout.getId()) {
            case R.id.rel1:

                do {
                    x = (initialXCoord / dpToPx(40)) + i;                           //formula to determine index of the array
                    drumPlacement1[x] = 0;                                          //assigns a value of 0 to index to open the space
                    i++;
                    u = dpToPx(40) * i;
                }
                while (u / buttonWidth < 1);
                break;
            case R.id.rel2:

                do {
                    x = (initialXCoord  / dpToPx(40)) + i;
                    drumPlacement2[x] = 0;
                    i++;
                    u = dpToPx(40) * i;
                }
                while (u / buttonWidth < 1);
                break;
            default:
                Log.i(TAG, "openSpace method case default");
                break;
        }
    }

    public void runLoop()   //used to show array index values
    {
        {
            int i;

            for (i = 0; i < drumPlacement1.length; i++) {
                Log.i(TAG, "drumPlacement1 [" + i + "] = " + drumPlacement1[i]);
            }
        }
        {
            int i;

            for (i = 0; i < drumPlacement2.length; i++) {
                Log.i(TAG, "drumPlacement2 [" + i + "] = " + drumPlacement2[i]);
            }
        }
    }

    public void test()
    {
        float a = 0;
        float b = 1;
        float c = 19;
        float d = 20;
        float e = 21;
        float f = 39;
        float g = 40;
        float h = 41;
        float i = 59;
        float j = 60;
        float k = 61;

        Log.i(TAG, "Math.round : " + a + " = " +  40*(Math.round(a/40)) );
        Log.i(TAG, "Math.round : " + b + " = " +  40*(Math.round(b/40)) );
        Log.i(TAG, "Math.round : " + c + " = " +  40*(Math.round(c/40)) );
        Log.i(TAG, "Math.round : " + d + " = " +  40*(Math.round(d/40)) );
        Log.i(TAG, "Math.round : " + e + " = " +  40*(Math.round(e/40)) );
        Log.i(TAG, "Math.round : " + f + " = " +  40*(Math.round(f/40)) );
        Log.i(TAG, "Math.round : " + g + " = " +  40*(Math.round(g/40)) );
        Log.i(TAG, "Math.round : " + h + " = " +  40*(Math.round(h/40)) );
        Log.i(TAG, "Math.round : " + i + " = " +  40*(Math.round(i/40)) );
        Log.i(TAG, "Math.round : " + j + " = " +  40*(Math.round(j/40)) );
        Log.i(TAG, "Math.round : " + k + " = " +  40*(Math.round(k/40)) );
    }


}








