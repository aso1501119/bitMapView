package jp.ac.asojuku.jousenb.bitmapview.entity;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by Syu on 2017/06/01.
 */

public class OekakiPath {

    private Path path;
    private Paint paint;

    public OekakiPath(){
    }
    //コンストラクタ
    public OekakiPath(Path path , Paint paint){
        this.path = path;
        this.paint = paint;
    }


    //アクセッサ

    public Path getPath(){
        return path;
    }

    public Paint getPaint(){
        return paint;
    }

    public void setPath(Path path ){
        this.path = path;
    }

    public void setPaint(Paint paint){
        this.paint = paint;
    }

    public void setAll(Path path ,Paint paint){
        setPaint(paint);
        setPath(path);

    }



}
