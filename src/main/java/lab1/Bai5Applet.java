package lab1;

import javacard.framework.*;

public class Bai5Applet extends Applet {
    public static final byte BAI3_CLA = (byte) 0xA0;
    public static final byte INPUT_DATA_INS = (byte) 0x01;
    public static final byte OUTPUT_DATA_INS = (byte) 0x02;

    static final byte PRINT_FULL_NAME_INS = (byte) 0x01;
    static final byte PRINT_STU_CODE_INS = (byte) 0x02;
    static final byte PRINT_DOB_INS = (byte) 0x03;
    static final byte PRINT_ADDRESS_INS = (byte) 0x04;

    // output map
    // 0x00 0x00 -> NAME
    // 0x00 0x01 -> STUDENT_CODE
    // 0x01 0x00 -> DOB
    // 0x01 0x01 -> ADDRESS
    // input format: Full Name|Student Code|DOB|Address

    public short getInstruction(short sw1, short sw2) {
        switch (sw1) {
            case 0x00:
                if (sw2 == 0x00) return PRINT_FULL_NAME_INS;
                if (sw2 == 0x01) return PRINT_STU_CODE_INS;
            case 0x01:
                if (sw2 == 0x00) return PRINT_DOB_INS;
                if (sw2 == 0x01) return PRINT_ADDRESS_INS;
            default:
                return -1;
        }
    }


    public static final short LENGTH_ECHO_BYTES = 256;

    private byte[] fullNameBytes;
    private byte[] dobBytes;
    private byte[] addressBytes;
    private byte[] studentCodeBytes;


    protected Bai5Applet(byte[] bArray, short bOffset, byte bLength) {
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
        apdu.setIncomingAndReceive();
        if (buffer[ISO7816.OFFSET_CLA] != BAI3_CLA) {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }
        switch (buffer[ISO7816.OFFSET_INS]) {
            case INPUT_DATA_INS:
                inputData(apdu);
                return;
            case OUTPUT_DATA_INS:
                short sw1 = buffer[ISO7816.OFFSET_P1];
                short sw2 = buffer[ISO7816.OFFSET_P2];
                short ins = getInstruction(sw1, sw2);
                switch (ins) {
                    case PRINT_FULL_NAME_INS:
                        printFullName(apdu);
                        return;
                    case PRINT_STU_CODE_INS:
                        printStudentCode(apdu);
                        return;
                    case PRINT_DOB_INS:
                        printDob(apdu);
                        return;
                    case PRINT_ADDRESS_INS:
                        printAddress(apdu);
                        return;
                    default:
                        ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                }
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }

    private void printFullName(APDU apdu) {
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) fullNameBytes.length);
        apdu.sendBytesLong(fullNameBytes, (short) 0, (short) fullNameBytes.length);
    }

    private void printStudentCode(APDU apdu) {
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) studentCodeBytes.length);
        apdu.sendBytesLong(studentCodeBytes, (short) 0, (short) studentCodeBytes.length);
    }

    private void printDob(APDU apdu) {
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) dobBytes.length);
        apdu.sendBytesLong(dobBytes, (short) 0, (short) dobBytes.length);
    }

    private void printAddress(APDU apdu) {
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) addressBytes.length);
        apdu.sendBytesLong(addressBytes, (short) 0, (short) addressBytes.length);
    }

    private void inputData(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short bytesRead = apdu.setIncomingAndReceive();
        byte[] inputBytes = new byte[LENGTH_ECHO_BYTES];
        short echoOffset = (short) 0;
        while (bytesRead > 0) {
            Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, inputBytes, echoOffset, bytesRead);
            echoOffset += bytesRead;
            bytesRead = apdu.receiveBytes(ISO7816.OFFSET_CDATA);
        }
        parseInput(inputBytes);
    }

    private void parseInput(byte[] inputBytes) {
        short fullNameLength = 0;
        short studentCodeLength = 0;
        short dobLength = 0;
        short addressLength = 0;
        short i = 0;
        while (inputBytes[i] != '|') {
            fullNameLength++;
            i++;
        }
        i++;
        while (inputBytes[i] != '|') {
            studentCodeLength++;
            i++;
        }
        i++;
        while (inputBytes[i] != '|') {
            dobLength++;
            i++;
        }
        i++;
        while (i < inputBytes.length) {
            addressLength++;
            i++;
        }
        fullNameBytes = new byte[fullNameLength];
        studentCodeBytes = new byte[studentCodeLength];
        dobBytes = new byte[dobLength];
        addressBytes = new byte[addressLength];
        Util.arrayCopyNonAtomic(inputBytes, (short) 0, fullNameBytes, (short) 0, fullNameLength);
        Util.arrayCopyNonAtomic(inputBytes, (short) (fullNameLength + 1), studentCodeBytes, (short) 0, studentCodeLength);
        Util.arrayCopyNonAtomic(inputBytes, (short) (fullNameLength + studentCodeLength + 2), dobBytes, (short) 0, dobLength);
        Util.arrayCopyNonAtomic(inputBytes, (short) (fullNameLength + studentCodeLength + dobLength + 3), addressBytes, (short) 0, addressLength);
    }

}
