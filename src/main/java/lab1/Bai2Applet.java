package lab1;

import javacard.framework.*;

public class Bai2Applet extends Applet {
    public static final byte BAI2_CLA = (byte) 0xA0;
    public static final byte PRINT_FULL_NAME_INS = (byte) 0x01;
    public static final byte PRINT_DOB_INS = (byte) 0x02;
    public static final byte PRINT_FULL_NAME_DOB_INS = (byte) 0x03;


    private static final byte[] fullNameMessage = new byte[]{
            0x4E, 0x67, 0x75, 0x79, 0x65, 0x6E, 0x20, // Nguyen
            0x48, 0x75, 0x75, 0x20, // Huu
            0x48, 0x75, 0x6E, 0x67, // Hung
    };

    private static final byte[] dobMessage = new byte[]{
            0x30, 0x31, 0x2F, 0x30, 0x31, 0x2F, 0x32, 0x30, 0x30, 0x32, // 01/01/2002
    };


    protected Bai2Applet(byte[] bArray, short bOffset, byte bLength) {
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
        new Bai2Applet(bArray, bOffset, bLength);
    }

    @Override
    public void process(APDU apdu) throws ISOException {
        if (selectingApplet()) return;
        byte[] buffer = apdu.getBuffer();


        apdu.setIncomingAndReceive();
        if (buffer[ISO7816.OFFSET_CLA] != BAI2_CLA) {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        switch (buffer[ISO7816.OFFSET_INS]) {
            case PRINT_FULL_NAME_INS:
                printFullName(apdu);
                return;
            case PRINT_DOB_INS:
                printDob(apdu);
                return;
            case PRINT_FULL_NAME_DOB_INS:
                printFullNameAndDob(apdu);
                return;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }

    private void printFullName(APDU apdu) {
        byte[] echo = JCSystem.makeTransientByteArray((short) fullNameMessage.length, JCSystem.CLEAR_ON_DESELECT);
        Util.arrayCopyNonAtomic(fullNameMessage, (short) 0, echo, (short) 0, (short) fullNameMessage.length);
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) echo.length);
        apdu.sendBytesLong(echo, (short) 0, (short) echo.length);
    }

    private void printDob(APDU apdu) {
        byte[] echo = JCSystem.makeTransientByteArray((short) dobMessage.length, JCSystem.CLEAR_ON_DESELECT);
        Util.arrayCopyNonAtomic(dobMessage, (short) 0, echo, (short) 0, (short) dobMessage.length);
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) echo.length);
        apdu.sendBytesLong(echo, (short) 0, (short) echo.length);
    }

    private void printFullNameAndDob(APDU apdu) {
        byte[] echo = JCSystem.makeTransientByteArray((short) (fullNameMessage.length + dobMessage.length), JCSystem.CLEAR_ON_DESELECT);
        Util.arrayCopyNonAtomic(fullNameMessage, (short) 0, echo, (short) 0, (short) fullNameMessage.length);
        Util.arrayCopyNonAtomic(dobMessage, (short) 0, echo, (short) fullNameMessage.length, (short) dobMessage.length);
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) echo.length);
        apdu.sendBytesLong(echo, (short) 0, (short) echo.length);
    }
}
