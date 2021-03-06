package android.telephony;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import com.android.internal.telephony.ITelephony;
import com.android.internal.telephony.ITelephony.Stub;
import com.android.internal.telephony.PhoneConstants;

public abstract class CellLocation {
    private static final String TAG = "CellLocation";
    protected int mType;

    public abstract void fillInNotifierBundle(Bundle bundle);

    public abstract boolean isEmpty();

    public abstract void setStateInvalid();

    public static void requestLocationUpdate() {
        try {
            ITelephony phone = Stub.asInterface(ServiceManager.getService(PhoneConstants.PHONE_KEY));
            if (phone != null) {
                phone.updateServiceLocation();
            }
        } catch (RemoteException e) {
        }
    }

    public static CellLocation newFromBundle(Bundle bundle) {
        int phoneType;
        try {
            phoneType = TelephonyManager.getDefault().getCurrentPhoneType(SubscriptionManager.getDefaultDataSubscriptionId());
            if (bundle != null && bundle.containsKey("type")) {
                int phoneTypeFromBundle = bundle.getInt("type", 0);
                if (phoneTypeFromBundle != 0) {
                    phoneType = phoneTypeFromBundle;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            phoneType = TelephonyManager.getDefault().getCurrentPhoneType();
        }
        Rlog.d(TAG, "phoneType=" + phoneType);
        switch (phoneType) {
            case 1:
                Log.e(TAG, "create GsmCellLocation");
                return new GsmCellLocation(bundle);
            case 2:
                Log.e(TAG, "create CdmaCellLocation");
                return new CdmaCellLocation(bundle);
            default:
                Log.e(TAG, "create null");
                return null;
        }
    }

    public static CellLocation newFromBundle(Bundle bundle, String vCardType) {
        int vPhoneType = bundle.getInt("type", 0);
        if ((vCardType == null || (!vCardType.equals("CSIM") && !vCardType.equals("RUIM"))) && vPhoneType != 2) {
            return new GsmCellLocation(bundle);
        }
        return new CdmaCellLocation(bundle);
    }

    public static CellLocation getEmpty() {
        switch (TelephonyManager.getDefault().getCurrentPhoneType()) {
            case 1:
                return new GsmCellLocation();
            case 2:
                return new CdmaCellLocation();
            default:
                return null;
        }
    }
}
