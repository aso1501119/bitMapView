package jp.ac.asojuku.jousenb.bitmapview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    private DrawSurfaceView mCamvasView;
    private Button undo;
    private Button redo;
    private Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        reset = (Button)findViewById(R.id.resetBtn);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCamvasView.colorchange();
            }
        });


    }
}
