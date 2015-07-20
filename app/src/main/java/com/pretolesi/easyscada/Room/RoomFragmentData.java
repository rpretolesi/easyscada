package com.pretolesi.easyscada.Room;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ricca_000 on 20/03/2015.
 */
public class RoomFragmentData implements Parcelable {

    private boolean m_bSaved;
    private boolean m_bSelected;
    private long m_ID;
    private String m_strHouseTAG;
    private String m_strTAG;
    private float m_fPosX;
    private float m_fPosY;
    private float m_fPosZ;
    private boolean m_bLandscape;


    public RoomFragmentData(boolean bSaved, boolean bSelected, long id, String strHouseTAG, String strTAG, float fPosX, float fPosY, float fPosZ, boolean bLandscape) {
        this.m_bSaved = bSaved;
        this.m_bSelected = bSelected;
        this.m_ID = id;
        this.m_strHouseTAG = strHouseTAG;
        this.m_strTAG = strTAG;
        this.m_fPosX = fPosX;
        this.m_fPosY = fPosY;
        this.m_fPosZ = fPosZ;
        this.m_bLandscape = bLandscape;
    }

    public RoomFragmentData() {
        this.m_bSaved = false;
        this.m_bSelected = false;
        this.m_ID = -1;
        this.m_strHouseTAG = "";
        this.m_strTAG = "";
        this.m_fPosX = 0.0f;
        this.m_fPosY = 0.0f;
        this.m_fPosZ = 0.0f;
        this.m_bLandscape = false;
    }

    public void setSaved(boolean bSaved) { this.m_bSaved = bSaved; }

    public void setSelected(boolean bSaved) { this.m_bSelected = bSaved; }

    public void setID(long id) { this.m_ID = id; }

    public void setHouseTAG(String strHouseTAG) {
        this.m_strHouseTAG = strHouseTAG;
    }

    public void setTag(String strTAG) {
        this.m_strTAG = strTAG;
    }

    public void setPosX(float fPosX) {
        this.m_fPosX = fPosX;
    }

    public void setPosY(float fPosY) {
        this.m_fPosY = fPosY;
    }

    public void setPosZ(float fPosZ) {
        this.m_fPosZ = fPosZ;
    }

    public void setLandscape(boolean bLandscape) {
        this.m_bLandscape = bLandscape;
    }

    public boolean getSaved() { return m_bSaved; }

    public boolean getSelected() { return m_bSelected; }

    public long getID() { return m_ID; }

    public String getHouseTAG() { return m_strHouseTAG;  }

    public String getTAG() {
        return m_strTAG;
    }

    public float getPosX() {
        return m_fPosX;
    }

    public float getPosY() {
        return m_fPosY;
    }

    public float getPosZ() {
        return m_fPosZ;
    }

    public boolean getLandscape() {
        return m_bLandscape;
    }

    protected RoomFragmentData(Parcel in) {
        m_bSaved = in.readByte() != 0;
        m_bSelected = in.readByte() != 0;
        m_ID = in.readLong();
        m_strHouseTAG = in.readString();
        m_strTAG = in.readString();
        m_fPosX = in.readFloat();
        m_fPosY = in.readFloat();
        m_fPosZ = in.readFloat();
        m_bLandscape = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (m_bSaved ? 1 : 0));
        dest.writeByte((byte) (m_bSelected ? 1 : 0));
        dest.writeLong(m_ID);
        dest.writeString(m_strHouseTAG);
        dest.writeString(m_strTAG);
        dest.writeFloat(m_fPosX);
        dest.writeFloat(m_fPosY);
        dest.writeFloat(m_fPosZ);
        dest.writeByte((byte) (m_bLandscape ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RoomFragmentData> CREATOR = new Parcelable.Creator<RoomFragmentData>() {
        @Override
        public RoomFragmentData createFromParcel(Parcel in) {
            return new RoomFragmentData(in);
        }

        @Override
        public RoomFragmentData[] newArray(int size) {
            return new RoomFragmentData[size];
        }
    };
}