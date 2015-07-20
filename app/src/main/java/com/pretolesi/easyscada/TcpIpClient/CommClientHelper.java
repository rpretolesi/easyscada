package com.pretolesi.easyscada.TcpIpClient;

import android.content.Context;
import android.os.AsyncTask;

import com.pretolesi.easyscada.BluetoothClient.BluetoothClient;
import com.pretolesi.easyscada.CommClientData.BaseCommClient;
import com.pretolesi.easyscada.CommClientData.TranspProtocolData;

import java.util.List;
import java.util.Vector;

/**
 *
 */
public class CommClientHelper {

    private static Context m_context;
    private static CommClientHelper m_Instance;
    private static List<BaseCommClient> m_ltic;

    private CommClientHelper(List<TranspProtocolData> lticd)
    {
        if ((m_ltic == null))
        {
            if(lticd != null && !lticd.isEmpty()) {
                m_ltic = new Vector<>();
                for(TranspProtocolData ticd : lticd){
                    if(ticd.getTypeID() == TranspProtocolData.TranspProtocolType.TCP_IP.getID()){
                        TCPIPClient tic = new TCPIPClient(m_context);
    //                    tic.execute(ticd);
                        tic.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ticd);
                                m_ltic.add(tic);
                    }
                    if(ticd.getTypeID() == TranspProtocolData.TranspProtocolType.BLUETOOTH.getID()){
                        BluetoothClient btc = new BluetoothClient(m_context);
                        btc.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ticd);
                                m_ltic.add(btc);
                    }
                }
            }
        }

    }

    /**
     * Get default instance of the class to keep it a singleton
     *
     * @param context
     *            the application context
     */
    public synchronized static CommClientHelper startInstance(Context context, List<TranspProtocolData> lticd)
    {
        if (context != null && lticd != null && m_Instance == null)
        {
            m_context = context;
            m_Instance = new CommClientHelper(lticd);
        }
        return m_Instance;
    }

    public synchronized static void stopInstance()
    {
        if (m_Instance != null)
        {
            // Initialize if not already done
            if(m_ltic != null && !m_ltic.isEmpty()) {
                 for(BaseCommClient bcc : m_ltic){
                     bcc.cancel(true);
                }
                m_ltic.clear();
            }
            m_ltic = null;

            m_Instance = null;
        }
    }

    public synchronized static CommClientHelper getInstance()
    {
        return m_Instance;
    }

    public synchronized static List<BaseCommClient> getBaseCommClient()
    {
        // Initialize if not already done
        return m_ltic;
    }

    public synchronized static BaseCommClient getBaseCommClient(long lID)
    {
        if (m_Instance != null)
        {
            // Initialize if not already done
            if(m_ltic != null && !m_ltic.isEmpty()) {
                for(BaseCommClient bcc : m_ltic){
                    if(bcc != null && bcc.getID() == lID){
                        return bcc;
                    }
                }
            }
        }

        return null;
    }

}
