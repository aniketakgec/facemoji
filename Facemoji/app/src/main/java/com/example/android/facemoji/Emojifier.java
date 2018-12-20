package com.example.android.facemoji;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class Emojifier {
    private  static final String LOG_TAG= Emojifier.class.getSimpleName();

    private static final float EMOJI_SCALE_FACTOR = .9f;
private static final double SMILING_PROB_THRESHOLD=0.5;
private static final double EYE_OPENED_PROB_THRESHOLD=0.5;


    static Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap picture){

        // Create the face detector, disable tracking and enable classifications

        FaceDetector detector=new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();


        //Build the frame

        Frame frame=new Frame.Builder().setBitmap(picture).build();

        // Detect the faces

        SparseArray<Face>faces=detector.detect(frame);


        Log.d(LOG_TAG,"Dectected face:number of faces="+faces.size());

        Bitmap resultBitmap=picture;

        if (faces.size()==0) {

            Toast.makeText(context,"No faces right now",Toast.LENGTH_SHORT).show();

        }
        else
            {


           for(int i=0;i<faces.size();i++){

               Face face=faces.valueAt(i);
              Bitmap emojiBitmap;

               switch (whichEmoji(face)) {
                   case SMILE:
                       emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                               R.drawable.smile);
                       break;
                   case FROWN:
                       emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                               R.drawable.frown);
                       break;
                   case LEFT_WINK:
                       emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                               R.drawable.leftwink);
                       break;
                   case RIGHT_WINK:
                       emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                               R.drawable.rightwink);
                       break;
                   case LEFT_WINK_FROWN:
                       emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                               R.drawable.leftwinkfrown);
                       break;
                   case RIGHT_WINK_FROWN:
                       emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                               R.drawable.rightwinkfrown);
                       break;
                   case CLOSED_EYE_SMILE:
                       emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                               R.drawable.closed_smile);
                       break;
                   case CLOSED_EYE_FROWN:
                       emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                               R.drawable.closed_frown);
                       break;
                   default:
                       emojiBitmap = null;
                       Toast.makeText(context, R.string.no_emoji, Toast.LENGTH_SHORT).show();
               }


               // Add the emojiBitmap to the proper position in the original image
               resultBitmap = addBitmapToFace(resultBitmap, emojiBitmap, face);

           }


        }


        // Release the detector
        detector.release();
        return resultBitmap;

    }


    private static Emoji whichEmoji(Face face) {

        Log.d(LOG_TAG, "whichEmoji: smilingProb = " + face.getIsSmilingProbability());
        Log.d(LOG_TAG,"whichEmoji: leftEyeOpenProb="+face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG,"whichEmoji: rightEyeOpenProb="+face.getIsRightEyeOpenProbability());


        boolean smiling=face.getIsSmilingProbability()>SMILING_PROB_THRESHOLD;
        boolean leftEyeclosed=face.getIsLeftEyeOpenProbability()<EYE_OPENED_PROB_THRESHOLD;
        boolean rightEyeclosed=face.getIsRightEyeOpenProbability()<EYE_OPENED_PROB_THRESHOLD;


        Emoji emoji;
        if(smiling) {
            if(leftEyeclosed&&!rightEyeclosed)
                emoji=Emoji.LEFT_WINK;
            else if(rightEyeclosed&&!leftEyeclosed)
                emoji=Emoji.RIGHT_WINK;
            else if(leftEyeclosed)
                emoji=Emoji.CLOSED_EYE_SMILE;
            else
                emoji=Emoji.SMILE;

            }

            else {

            if(leftEyeclosed&&!rightEyeclosed)
                emoji=Emoji.LEFT_WINK_FROWN;
            else if(rightEyeclosed&&!leftEyeclosed)
                emoji=Emoji.RIGHT_WINK_FROWN;
            else if (leftEyeclosed)
                emoji=Emoji.CLOSED_EYE_FROWN;
            else
                emoji=Emoji.FROWN;


            }




Log.d(LOG_TAG,"whichEmoji:"+emoji.name());
return emoji;


        }





    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }































        private enum Emoji{

            SMILE,
            FROWN,
            LEFT_WINK,
            RIGHT_WINK,
            LEFT_WINK_FROWN,
            RIGHT_WINK_FROWN,
            CLOSED_EYE_SMILE,
            CLOSED_EYE_FROWN,



        }












    }

