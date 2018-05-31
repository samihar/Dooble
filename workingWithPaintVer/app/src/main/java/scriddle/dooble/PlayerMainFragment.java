package scriddle.dooble;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.UUID;


public class PlayerMainFragment extends Fragment {

    PaintScreenListener mCallback;
    private DrawingView drawView;
    private ImageButton currPaint;
    private Button drawBtn;
    Button eraseBtn, newBtn, saveBtn;
    private float smallBrush, mediumBrush, largeBrush;

    private ImageButton btnColor1, btnColor2, btnColor3, btnColor4, btnColor5, btnColor6;
    private ImageButton btnColor7, btnColor8, btnColor9, btnColor10, btnColor11, btnColor12;



    public interface PaintScreenListener {
        void onSendSelected();
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

        drawView = (DrawingView)mRelativeLayout.findViewById(R.id.drawing);


        drawView.setBrushSize(mediumBrush);

        LinearLayout paintLayout = (LinearLayout)mRelativeLayout.findViewById(R.id.paint_colors);

        initializeColorButtons();


        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));


        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        topMenuButtons();

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
        drawBtn = (Button) mRelativeLayout.findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sizeChooser("Brush Size");
            }
        });

        eraseBtn = (Button)mRelativeLayout.findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sizeChooser("Eraser Size");
            }
        });

        newBtn = (Button)mRelativeLayout.findViewById(R.id.new_btn);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder newDialog = new AlertDialog.Builder(getContext());
                newDialog.setTitle("New drawing");
                newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        drawView.startNew();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                newDialog.show();
            }
        });

        saveBtn = (Button)mRelativeLayout.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(getContext());
                saveDialog.setTitle("Save drawing");
                saveDialog.setMessage("Save drawing to device Gallery?");
                saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        //save drawing
                        drawView.setDrawingCacheEnabled(true);
                        String imgSaved = MediaStore.Images.Media.insertImage(
                                getContext().getContentResolver(), drawView.getDrawingCache(),
                                UUID.randomUUID().toString()+".png", "drawing");

                        if(imgSaved!=null){
                            Toast savedToast = Toast.makeText(getContext().getApplicationContext(),
                                    "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                            savedToast.show();
                        }
                        else{
                            Toast unsavedToast = Toast.makeText(getContext().getApplicationContext(),
                                    "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                            unsavedToast.show();
                        }

                        drawView.destroyDrawingCache();
                    }
                });
                saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                saveDialog.show();



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
