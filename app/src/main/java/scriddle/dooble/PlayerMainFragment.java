package scriddle.dooble;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PlayerMainFragment extends Fragment {

    PaintScreenListener mCallback;
    private DrawingView drawView;
    private ImageButton currPaint;
    ImageButton drawBtn;
    ImageButton eraseBtn;
    //Button newBtn, saveBtn; DELETED
    private float smallBrush, mediumBrush, largeBrush;
    private Button btnDone;

    private ImageButton btnColor1, btnColor2, btnColor3, btnColor4, btnColor5, btnColor6;
    private ImageButton btnColor7, btnColor8, btnColor9, btnColor10, btnColor11, btnColor12;

    TextView tvTopic;
    TextView tvScore;

    public interface PaintScreenListener {
        void onSendSelected(byte[] imgSend);
    }

    RelativeLayout mRelativeLayout;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_player_main, parent, false);
        return mRelativeLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        tvTopic = (TextView)mRelativeLayout.findViewById(R.id.tvTopic);

        drawView = (DrawingView)mRelativeLayout.findViewById(R.id.drawing);


        drawView.setBrushSize(mediumBrush);

        LinearLayout paintLayout = (LinearLayout)mRelativeLayout.findViewById(R.id.paint_colors);

        initializeColorButtons();

        tvScore = (TextView) mRelativeLayout.findViewById(R.id.tvScore);


        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

        btnDone = (Button) mRelativeLayout.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDoneExecute();
            }
        });

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        topMenuButtons();
    }

    public void btnDoneExecute()
    {

        Bitmap original = getBitmapFromView(drawView);
        Bitmap drawing = resize(original, 200,200);

        ByteArrayOutputStream imbytes = new ByteArrayOutputStream();
        drawing.compress(Bitmap.CompressFormat.JPEG, 100, imbytes);
        byte[] imgSend = imbytes.toByteArray();
        mCallback.onSendSelected(imgSend);
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }


    public void settvScore(String setMe) {
        tvScore.setText(setMe);
    }

    public void settvTopc(String setMe) {
        tvTopic.setText(setMe);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (PlayerMainFragment.PaintScreenListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void paintClicked(View view) {
        // CHECK THIS maybe it should be inside if statement
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());

        if (view != currPaint) {
            //update color

            Log.d("TAG", "reached colors");
            ImageButton newColor = (ImageButton) view;
            Log.d("TAG", "reached colors 2");

            String color = newColor.getTag().toString();

            Log.d("TAG", "reached colors 3");

            drawView.setColor(color);

            Log.d("TAG", color);

            newColor.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint = (ImageButton) newColor;
        }
    }

    void topMenuButtons()
    {
        drawBtn = (ImageButton) mRelativeLayout.findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sizeChooser("Brush Size");
            }
        });

        eraseBtn = (ImageButton)mRelativeLayout.findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sizeChooser("Eraser Size");
            }
        });

    }

    void initializeColorButtons()
    {

        btnColor1 = (ImageButton)mRelativeLayout.findViewById(R.id.btnColor1);
        btnColor2 = (ImageButton)mRelativeLayout.findViewById(R.id.btnColor2);
        btnColor3 = (ImageButton)mRelativeLayout.findViewById(R.id.btnColor3);
        btnColor4 = (ImageButton)mRelativeLayout.findViewById(R.id.btnColor4);
        btnColor5 = (ImageButton)mRelativeLayout.findViewById(R.id.btnColor5);
        btnColor6 = (ImageButton)mRelativeLayout.findViewById(R.id.btnColor6);
        btnColor7 = (ImageButton)mRelativeLayout.findViewById(R.id.btnColor7);
        btnColor8 = (ImageButton)mRelativeLayout.findViewById(R.id.btnColor8);
        btnColor9 = (ImageButton)mRelativeLayout.findViewById(R.id.btnColor9);
        btnColor10 = (ImageButton)mRelativeLayout.findViewById(R.id.btnColor10);
        btnColor11 = (ImageButton)mRelativeLayout.findViewById(R.id.btnColor11);
        btnColor12 = (ImageButton)mRelativeLayout.findViewById(R.id.btnColor12);


        btnColor1.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { paintClicked(v); }
        });

        btnColor2.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { paintClicked(v); }
        });

        btnColor3.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { paintClicked(v); }
        });

        btnColor4.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { paintClicked(v); }
        });

        btnColor5.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { paintClicked(v); }
        });

        btnColor6.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { paintClicked(v); }
        });

        btnColor7.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { paintClicked(v); }
        });

        btnColor8.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { paintClicked(v); }
        });

        btnColor9.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { paintClicked(v); }
        });

        btnColor10.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { paintClicked(v); }
        });

        btnColor11.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { paintClicked(v); }
        });

        btnColor12.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { paintClicked(v); }
        });

    }

    void sizeChooser(final String title)
    {
        final Dialog brushDialog = new Dialog(getContext());
        brushDialog.setTitle(title);
        brushDialog.setContentView(R.layout.brush_chooser);

        //small brush size button clicked
        ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (title == "Eraser Size")
                    drawView.setErase(true);
                else
                    drawView.setErase(false);
                drawView.setBrushSize(smallBrush);
                drawView.setLastBrushSize(smallBrush);
                brushDialog.dismiss();
            }
        });

        ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (title == "Eraser Size")
                    drawView.setErase(true);
                else
                    drawView.setErase(false);
                drawView.setBrushSize(mediumBrush);
                drawView.setLastBrushSize(mediumBrush);
                brushDialog.dismiss();
            }
        });

        ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (title == "Eraser Size")
                    drawView.setErase(true);
                else
                    drawView.setErase(false);
                drawView.setBrushSize(largeBrush);
                drawView.setLastBrushSize(largeBrush);
                brushDialog.dismiss();
            }
        });

        brushDialog.show();
    }



}
