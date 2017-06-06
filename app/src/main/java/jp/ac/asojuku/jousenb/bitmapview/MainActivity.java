package jp.ac.asojuku.jousenb.bitmapview;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {


    private DrawSurfaceView mCamvasView;
    private Button undo;
    private Button redo;
    private Button reset;
    private Button color;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mCamvasView = (DrawSurfaceView)findViewById(R.id.canvasView);

        undo = (Button)findViewById(R.id.undoBtn);

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamvasView.undo();
            }
        });

        redo = (Button)findViewById(R.id.redoBtn);
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamvasView.redo();
            }
        });


        /**
         * リセットボタンの実装
         */
        reset = (Button)findViewById(R.id.resetBtn);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCamvasView.reset();
            }
        });

        color = (Button)findViewById(R.id.colorBtn);
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamvasView.colorChange();


            }
        });




    }
}
