

import com.licel.jcardsim.samples.BaseApplet;
import javacard.framework.*;

public class Calculator extends BaseApplet {
    private static final byte CALCULATOR_CLA = (byte) 0xA0;
    private static final byte INS_CONG = (byte) 0x00;
    private static final byte INS_TRU = (byte) 0x01;
    private static final byte INS_NHAN = (byte) 0x02;
    private static final byte INS_CHIA = (byte) 0x03;

    private byte[] initParamsBytes;

    protected Calculator(byte[] bArray, short bOffset, byte bLength) {
        if (bLength > 0) {
            byte iLen = bArray[bOffset];
            bOffset = (short) (bOffset + iLen + 1);
            byte var10000 = bArray[bOffset];
            bOffset = (short) (bOffset + 3);
            byte aLen = bArray[bOffset];
            this.initParamsBytes = new byte[aLen];
            Util.arrayCopyNonAtomic(bArray, (short) (bOffset + 1), this.initParamsBytes, (short) 0, (short) aLen);
        }

        this.register();
    }

    public static void install(byte[] bArray, short bOffset, byte bLength) throws ISOException {
        new Calculator(bArray, bOffset, bLength);
    }

    public void process(APDU apdu) {
        if (!this.selectingApplet()) {
            byte[] buffer = apdu.getBuffer();
            apdu.setIncomingAndReceive();

            if (buffer[ISO7816.OFFSET_CLA] != CALCULATOR_CLA) {
                ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
            }
            short res = 0;
            short tmp1 = buffer[ISO7816.OFFSET_P1];
            short tmp2 = buffer[ISO7816.OFFSET_P2];
            switch (buffer[1]) {
                case INS_CONG:
                    res = (short) (tmp1 + tmp2);
                    break;
                case INS_TRU:
                    res = (short) (tmp1 - tmp2);
                    break;
                case INS_NHAN:
                    res = (short) (tmp1 * tmp2);
                    break;
                case INS_CHIA:
                    res = (short) (tmp1 / tmp2);
                    break;
                default:
                    ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
            }
            Util.setShort(buffer, (short) 0, res);
            short le = apdu.setOutgoing();
            if (le < (short) 2) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
            apdu.setOutgoingLength((short) 2);
            apdu.sendBytes((short) 0, (short) 2);
        }
    }
}
