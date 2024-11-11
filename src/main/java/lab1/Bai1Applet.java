package lab1;

import javacard.framework.*;

public class Bai1Applet extends Applet {
    public static final byte HELLO_WORLD_CLA = (byte) 0xA0;

    public static final byte SAY_HELLO_INS = (byte) 0x01;


    private static final byte[] helloMessage = new byte[]{
            0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, // "Hello "
            0x77, 0x6F, 0x72, 0x6C, 0x64, 0x20, 0x21 // "world !"
    };

    protected Bai1Applet(byte[] bArray, short bOffset, byte bLength) {
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
        new Bai1Applet(bArray, bOffset, bLength);
    }

    @Override
    public void process(APDU apdu) throws ISOException {
        if (selectingApplet()) return;
        byte[] buffer = apdu.getBuffer();
        apdu.setIncomingAndReceive();
        if (buffer[ISO7816.OFFSET_CLA] != HELLO_WORLD_CLA || buffer[ISO7816.OFFSET_INS] != SAY_HELLO_INS) {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
        byte[] echo = JCSystem.makeTransientByteArray((short) helloMessage.length, JCSystem.CLEAR_ON_DESELECT);
        Util.arrayCopyNonAtomic(helloMessage, (short) 0, echo, (short) 0, (short) helloMessage.length);
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) echo.length);
        apdu.sendBytesLong(echo, (short) 0, (short) echo.length);
    }
}
