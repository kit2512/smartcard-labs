package lab1;

import javacard.framework.*;

public class Bai4Applet extends Applet {
    public static final byte BAI3_CLA = (byte) 0xA0;
    public static final byte ENTER_FULL_NAME_INS = (byte) 0x01;
    public static final byte ENTER_DOB_INS = (byte) 0x02;
    public static final byte ECHO_FULL_NAME_INS = (byte) 0x03;
    public static final byte ECHO_DOB_INS = (byte) 0x04;
    public static final byte ECHO_FULL_NAME_DOB_INS = (byte) 0x05;

    public static final short LENGTH_ECHO_BYTES = 256;

    private byte[] fullNameBytes;
    private byte[] dobBytes;

    protected Bai4Applet(byte[] bArray, short bOffset, byte bLength) {
        fullNameBytes = new byte[LENGTH_ECHO_BYTES];
        dobBytes = new byte[LENGTH_ECHO_BYTES];
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
        new Bai4Applet(bArray, bOffset, bLength);
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


        switch (buffer[ISO7816.OFFSET_INS]) {
            case ENTER_FULL_NAME_INS:
                inputName(apdu);
                return;
            case ENTER_DOB_INS:
                inputDOB(apdu);
                return;
            case ECHO_FULL_NAME_INS:
                echoFullName(apdu);
                return;
            case ECHO_DOB_INS:
                echoDOB(apdu);
                return;
            case ECHO_FULL_NAME_DOB_INS:
                echoFullNameAndDob(apdu);
                return;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }

    private void inputName(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short bytesRead = apdu.setIncomingAndReceive();
        short echoOffset = (short) 0;
        while (bytesRead > 0) {
            Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, fullNameBytes, echoOffset, bytesRead);
            echoOffset += bytesRead;
            bytesRead = apdu.receiveBytes(ISO7816.OFFSET_CDATA);
        }
    }

    private void inputDOB(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short bytesRead = apdu.setIncomingAndReceive();
        short echoOffset = (short) 0;
        while (bytesRead > 0) {
            Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, dobBytes, echoOffset, bytesRead);
            echoOffset += bytesRead;
            bytesRead = apdu.receiveBytes(ISO7816.OFFSET_CDATA);
        }
    }

    private void echoFullName(APDU apdu) {
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) fullNameBytes.length);
        apdu.sendBytesLong(fullNameBytes, (short) 0, (short) fullNameBytes.length);
    }

    private void echoDOB(APDU apdu) {
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) dobBytes.length);
        apdu.sendBytesLong(dobBytes, (short) 0, (short) dobBytes.length);
    }

    private void echoFullNameAndDob(APDU apdu) {
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) (fullNameBytes.length + dobBytes.length));
        apdu.sendBytesLong(fullNameBytes, (short) 0, (short) fullNameBytes.length);
        apdu.sendBytesLong(dobBytes, (short) 0, (short) dobBytes.length);
    }
}
