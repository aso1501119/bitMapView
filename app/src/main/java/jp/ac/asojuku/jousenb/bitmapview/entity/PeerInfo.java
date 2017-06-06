package jp.ac.asojuku.jousenb.bitmapview.entity;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;

import io.skyway.Peer.DataConnection;
import io.skyway.Peer.OnCallback;
import io.skyway.Peer.Peer;
import io.skyway.Peer.PeerOption;
import jp.ac.asojuku.jousenb.bitmapview.R;

/**
 * Source https://github.com/nttcom/SkyWay-Android-Sample
 * Edited by murofushi on 2017/06/02.
 */

public class PeerInfo {

    private Peer mPeer;
    private String mId;
    private String mPartnerId;
    private boolean mConnectFlag;
    private DataConnection mData;


    public PeerInfo(Context context) {
        String key = context.getString(R.string.skyway_apiKey);
        String domain = context.getString(R.string.skyway_domain);
        //初期設定
        PeerOption option = new PeerOption();
        option.key = key;
        option.domain = domain;
        mPeer = new Peer(context, option);
        setPeerCallback(context);
    }
    /**他のPeerを検索し、配列で出力する*/
    public void listingPeers() {
        if ((null == mPeer) || (null == mId) || (0 == mId.length())) {
            return;
        }
        mPeer.listAllPeers(new OnCallback() {
            @Override
            public void onCallback(Object object) {
                if (!(object instanceof JSONArray)) {
                    return;
                }

                JSONArray peers = (JSONArray) object;

                StringBuilder sbItems = new StringBuilder();
                for (int i = 0; peers.length() > i; i++) {
                    String strValue = "";
                    try {
                        strValue = peers.getString(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (0 == mId.compareToIgnoreCase(strValue)) {
                        continue;
                    }

                    if (0 < sbItems.length()) {
                        sbItems.append(",");
                    }

                    sbItems.append(strValue);
                }

                String strItems = sbItems.toString();
                String[] mListIds = strItems.split(",");

                if ((null != mListIds) && (0 < mListIds.length)) {
                    //TODO:改変すべき箇所
                    dataConnect(mListIds[0]);
                }
            }
        });
    }
    /**************************************************************
     * Peer部分の処理
     **************************************************************/
    /**Peer通信のコールバック一覧*/
    private void setPeerCallback(Context context) {
        //PeerServerへの接続が確立すると発生
        mPeer.on(Peer.PeerEventEnum.OPEN,new OnCallback(){
            public void onCallback(Object object){
                if (object instanceof String) {
                    mId = (String) object;
                }
                Log.d("OPEN","PeerServer接続されました。");
            }
        });
        //リモートピアと新しいdata connectionが確立すると発生
        mPeer.on(Peer.PeerEventEnum.CONNECTION,new OnCallback(){
            public void onCallback(Object object){
                if (!(object instanceof DataConnection)) {
                    return;
                }
                mData = (DataConnection) object;
                setDataCallback();
                mConnectFlag = true;
                Log.d("CONNECT","Peerと接続されました。");
            }
        });
        //リモートのpeerがあなたに発信してきたときに発生
        mPeer.on(Peer.PeerEventEnum.CALL,new OnCallback(){
            public void onCallback(Object object){

            }
        });
        //ピアとの接続がdestroyedとなった場合に発生
        mPeer.on(Peer.PeerEventEnum.CLOSE,new OnCallback(){
            public void onCallback(Object object){

            }
        });
    }
    /**Peerコールバック初期化*/
    void unsetPeerCallback()
    {
        mPeer.on(Peer.PeerEventEnum.OPEN, null);
        mPeer.on(Peer.PeerEventEnum.CONNECTION, null);
        mPeer.on(Peer.PeerEventEnum.CALL, null);
        mPeer.on(Peer.PeerEventEnum.CLOSE, null);
        mPeer.on(Peer.PeerEventEnum.DISCONNECTED, null);
        mPeer.on(Peer.PeerEventEnum.ERROR, null);
    }

    /**************************************************************
     * DataConnection部分の処理
     **************************************************************/
    /**Data通信のコールバック一覧*/
    private void setDataCallback(){
        //コネクションが利用可能となった場合に発生
        mData.on(DataConnection.DataEventEnum.OPEN,new OnCallback(){
            public void onCallback(Object object){
                mConnectFlag =true;
            }
        });
        //リモートpeerからデータを受信した場合に発生
        mData.on(DataConnection.DataEventEnum.DATA,new OnCallback(){
            public void onCallback(Object object){

            }
        });
        //が、またはリモートのpeerがdata connectionをクローズした場合に発生
        mData.on(DataConnection.DataEventEnum.CLOSE,new OnCallback(){
            public void onCallback(Object object){

            }
        });
        //エラーが発生時に呼び出される
        mData.on(DataConnection.DataEventEnum.ERROR,new OnCallback(){
            public void onCallback(Object object){

            }
        });
    }
    /**DataConnectionコールバック初期化*/
    private void unsetDataCallback(){
        mData.on(DataConnection.DataEventEnum.OPEN,null);
        mData.on(DataConnection.DataEventEnum.DATA,null);
        mData.on(DataConnection.DataEventEnum.CLOSE,null);
        mData.on(DataConnection.DataEventEnum.ERROR,null);
    }
    /**接続する*/
    public void dataConnect(String s) {
       mPartnerId = s;
        mData = mPeer.connect(mPartnerId);
        if(mData != null){
            setDataCallback();
            Log.d("CONNECT","Peerとの接続が確立されました。");
        }
    }
    public void dataDisconnect(){
        if(mData != null){
            unsetPeerCallback();
            mData = null;
        }
        if (null != mPeer) {
            unsetPeerCallback();

            if (false == mPeer.isDisconnected) {
                mPeer.disconnect();
            }

            if (false == mPeer.isDestroyed) {
                mPeer.destroy();
            }

            mPeer = null;
        }
    }
    /**************************************************************
     * 共通処理
     **************************************************************/
    /**接続を切断する*/
    public void closing(){
        if (false == mConnectFlag) {
            return;
        }

        mConnectFlag = false;

        if (null != mData) {
            mData.close();
        }
    }
    /**Peerを削除する*/
    public void destroyPeer() {
        if (null != mData) {
            unsetDataCallback();

            mData = null;
        }

        if (null != mPeer) {
            unsetPeerCallback();

            if (!mPeer.isDisconnected) {
                mPeer.disconnect();
            }

            if (!mPeer.isDestroyed) {
                mPeer.destroy();
            }

            mPeer = null;
        }
    }

    public Peer getPeer() {
        return mPeer;
    }

    public void setPeer(Peer mPeer) {
        this.mPeer = mPeer;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getPartnerId() {
        return mPartnerId;
    }

    public void setPartnerId(String mPartnerId) {
        this.mPartnerId = mPartnerId;
    }

    public boolean isConnectFlag() {
        return mConnectFlag;
    }

    public void setConnectFlag(boolean mConnectFlag) {
        this.mConnectFlag = mConnectFlag;
    }

    public DataConnection getData() {
        return mData;
    }

    public void setData(DataConnection mData) {
        this.mData = mData;
    }
}

