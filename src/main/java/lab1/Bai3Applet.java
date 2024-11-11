package lab1;

import javacard.framework.*;

import javax.print.attribute.standard.MediaSize;

public class Bai3Applet extends Applet {
    public static final byte BAI3_CLA = (byte) 0xA0;
    public static final byte ADD_INS = (byte) 0x01;
    public static final byte SUBTRACT_INS = (byte) 0x02;
    public static final byte MULTIPLY_INS = (byte) 0x03;
    public static final byte DIVIDE_INS = (byte) 0x04;


    private static final byte[] fullNameMessage = new byte[]{
            0x4E, 0x67, 0x75, 0x79, 0x65, 0x6E, 0x20, // Nguyen
            0x48, 0x75, 0x75, 0x20, // Huu
            0x48, 0x75, 0x6E, 0x67, // Hung
    };

    private static final byte[] dobMessage = new byte[]{
            0x30, 0x31, 0x2F, 0x30, 0x31, 0x2F, 0x32, 0x30, 0x30, 0x32, // 01/01/2002
    };


    protected Bai3Applet(byte[] bArray, short bOffset, byte bLength) {
        if (bLength > 0) {
            byte iLen = bArray[bOffset]; // aid length
            bOffset = (short) (bOffset + iLen + 1);
            byte cLen = bArray[bOffset]; // info length
            bOffset = (short) (bOffset + 3);
            byte aLen = bArray[bOffset]; // applet data length
        }
        register();
    }

    public static void install(byte[] bArray, short bOffset, byte bLength)
            throws ISOException {
        new Bai3Applet(bArray, bOffset, bLength);
    }

    @Override
    public void process(APDU apdu) throws ISOException {
        if (selectingApplet()) return;
        byte[] buffer = apdu.getBuffer();
        short res;


        apdu.setIncomingAndReceive();
        if (buffer[ISO7816.OFFSET_CLA] != BAI3_CLA) {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        short result = 0;
        short p1 = buffer[ISO7816.OFFSET_P1];
        short p2 = buffer[ISO7816.OFFSET_P2];

        switch (buffer[ISO7816.OFFSET_INS]) {
            case ADD_INS:
                result = (short) (p1 + p2);
                break;
            case SUBTRACT_INS:
                result = (short) (p1 - p2);
                break;
            case MULTIPLY_INS:
                result = (short) (p1 * p2);
                break;
            case DIVIDE_INS:
                result = (short) (p1 / p2);
                break;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
        Util.setShort(buffer, (short) 0, result);
        short le = apdu.setOutgoing();
        if (le < (short)2) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        apdu.setOutgoingLength((short) 2);
        apdu.sendBytes((short) 0, (short) 2);
    }
}
