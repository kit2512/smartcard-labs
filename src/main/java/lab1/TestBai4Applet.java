package lab1;

import com.licel.jcardsim.base.Simulator;
import javacard.framework.AID;
import utils.AIDUtil;

import static utils.ByteUtil.bytesToHex;

public class TestBai4Applet {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        AID appletAID = AIDUtil.create("F000000001");
        simulator.installApplet(appletAID, Bai4Applet.class);
        simulator.selectApplet(appletAID);
        byte[] response = simulator.transmitCommand(new byte[]{(byte) 0xA0, 0x01, 0x04, 0x02});
        System.out.println(bytesToHex(response));
        response = simulator.transmitCommand(new byte[]{(byte) 0xA0, 0x02, 0x04, 0x02});
        System.out.println(bytesToHex(response));
        response = simulator.transmitCommand(new byte[]{(byte) 0xA0, 0x03, 0x04, 0x02});
        System.out.println(bytesToHex(response));
        response = simulator.transmitCommand(new byte[]{(byte) 0xA0, 0x04, 0x04, 0x02});
        System.out.println(bytesToHex(response));
    }
}
