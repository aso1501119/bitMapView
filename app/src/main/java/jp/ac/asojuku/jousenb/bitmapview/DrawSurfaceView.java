package jp.ac.asojuku.jousenb.bitmapview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by Syu on 2017/05/25.
 */

public class DrawSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Paint   mPaint;
    private Path    mPath;
    private Bitmap mLastDrawBitmap;
    private Canvas mLastDrawCanvas;
    private Deque<Path> mUndoStack = new ArrayDeque<Path>();
    private Deque<Path> mRedoStack = new ArrayDeque<Path>();
    private int color;


    //コンストラクタ
    public DrawSurfaceView(Context context){
        super(context);
        init();
    }


    //コンストラクタ
    public DrawSurfaceView(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }


    /**
     * 初期化処理
     */
    private void init(){

        //サーフェスビュー操作のための用意
        mHolder = getHolder();

        //透過
        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSPARENT);

        //コールバックメソッドを設定
        mHolder.addCallback(this);

        //ペイントの設定
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(6);
    }


    //SurfaceViewが作られた時
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //描画状態を保存するBitmapを作成する
        clearLastDrawBitmap();
    }

    //SurfaceViewが変更された時
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //SurfaceViewが破棄された時
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mLastDrawBitmap.recycle();
    }


    //SurfaceViewが作られた際にBitmapとCanvasがなければ作る
    private void clearLastDrawBitmap(){
        if(mLastDrawBitmap == null){        //Bitmapが作られていない場合
            mLastDrawBitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        }
        if(mLastDrawCanvas == null){        //Canvasが作られていない場合
            mLastDrawCanvas = new Canvas(mLastDrawBitmap);
        }

        mLastDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }


    /**
     * 画面がタッチされた際の操作
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:   //画面にタッチした時
                onTouchDown(event.getX(),event.getY());
                break;

            case MotionEvent.ACTION_MOVE:   //指を動かした時
                onTouchMove(event.getX(),event.getY());
                break;

            case MotionEvent.ACTION_UP:  //画面からゆびを離したとき
                onTouchUp(event.getX(),event.getY());
                break;

            default:
        }
        return true;
    }

    /**
     *  画面をタッチした際のPathの開始位置の決定
     */
    private void onTouchDown(float x, float y){
        mPath = new Path();
        mPath.moveTo(x, y);     //開始位置の座標の決定
    }

    /*
        画面をタッチして移動した時の座標を記録していく
     */
    private void onTouchMove(float x, float y){
        mPath.lineTo(x,y);  //描画する座標を決定
        drawLine(mPath);
    }

    /**
     * 画面を離した時の座標を記録を決定
     */
    private void onTouchUp(float x ,float y){
        mPath.lineTo(x, y);
        drawLine(mPath);
        mLastDrawCanvas.drawPath(mPath,mPaint);
        mUndoStack.addLast(mPath);
        mRedoStack.clear();
    }

    /**
     *  onTouchMove()の際にキャンバスをロックし
     *  線を描画していく
     */
    private void drawLine(Path path){
        //ロックしてキャンバス取得
        Canvas canvas = mHolder.lockCanvas();

        //キャンバスをクリア
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        //前回描画したビットマップをキャンバスに描画
        canvas.drawBitmap(mLastDrawBitmap,0,0,null);

        //パスを描画する
        canvas.drawPath(path,mPaint);

        //ロックを外す
        mHolder.unlockCanvasAndPost(canvas);

    }


    /**
     * 直前の操作を取り消して元の状態に戻す
     */
    public void undo(){
        if(mUndoStack.isEmpty()){   //戻せない場合
            return;
        }

        //undoスタックからパスを取り出しredoスタックに格納
        Path lastUndoPath = mUndoStack.removeLast();    //removeLast():キューの最後の要素を取得して削除
        mRedoStack.addLast(lastUndoPath);

        //ロックしてキャンバスを取得する
        Canvas canvas = mHolder.lockCanvas();

        //キャンバスをクリアします
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        //描画状態を保持するBitmapをクリア
        clearLastDrawBitmap();

        //パスを描画する
        for (Path path : mUndoStack){
            canvas.drawPath(path,mPaint);
            mLastDrawCanvas.drawPath(path,mPaint);
        }

        //ロックを外す
        mHolder.unlockCanvasAndPost(canvas);
    }

    public void  redo(){
        if(mRedoStack.isEmpty()){
            return;
        }

        // redoスタックからパスを取り出し、undoスタックに格納します
        Path lastRedoPath = mRedoStack.removeLast();
        mUndoStack.addLast(lastRedoPath);

        //パスを描画します
        drawLine(lastRedoPath);

        mLastDrawCanvas.drawPath(lastRedoPath,mPaint);
    }

    public void reset(){

        //スタックをからにする
        mUndoStack.clear();
        mRedoStack.clear();


        clearLastDrawBitmap();

        Canvas canvas = mHolder.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        mHolder.unlockCanvasAndPost(canvas);
    }

    public void colorhange(){

        color++;
        if(color%2 == 0){
            mPaint.setColor(Color.RED);
        }else{
            mPaint.setColor(Color.YELLOW);
        }

    }


}
