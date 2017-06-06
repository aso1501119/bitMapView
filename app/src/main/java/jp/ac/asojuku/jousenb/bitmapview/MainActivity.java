package jp.ac.asojuku.jousenb.bitmapview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import jp.ac.asojuku.jousenb.bitmapview.entity.PeerInfo;
import jp.ac.asojuku.jousenb.bitmapview.view.DrawSurfaceView;

/**
 * Created by Syu on 2017/05/25.
 * Edited by Murofushi on 2017/06/06.
 */

public class MainActivity extends AppCompatActivity {

    private PeerInfo mInfo;
    private Context mContext;
    private DrawSurfaceView mCamvasView;
    private Button mUndoButton;
    private Button mRedoButton;
    private Button mResetButton;
    private Button mColorButton;
    private Button mConnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this.getApplicationContext();
        mCamvasView = (DrawSurfaceView)findViewById(R.id.canvasView);

        mUndoButton = (Button)findViewById(R.id.undoBtn);

        mUndoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamvasView.undo();
            }
        });

        mRedoButton = (Button)findViewById(R.id.redoBtn);
        mRedoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamvasView.redo();
            }
        });

        mResetButton = (Button)findViewById(R.id.resetBtn);
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamvasView.hutosa();
            }
        });

        mColorButton = (Button)findViewById(R.id.colorBtn);
        mColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamvasView.colorChange();

            }
        });

        mConnectButton = (Button)findViewById(R.id.connectBtn);
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                if (!mInfo.isConnectFlag()) {
                    // Calling dialog
                    mInfo.listingPeers();
                } else {
                    // Close
                    mInfo.closing();
                }

                v.setEnabled(true);
            }
        });

        //初期値宣言
        mInfo = new PeerInfo(mContext);

    }
}
