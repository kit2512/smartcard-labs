package lab1;

import com.licel.jcardsim.base.Simulator;
import javacard.framework.AID;
import utils.AIDUtil;

import static utils.ByteUtil.bytesToAscii;

public class TestBai2Applet {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        AID appletAID = AIDUtil.create("F000000001");
        simulator.installApplet(appletAID, Bai2Applet.class);
        simulator.selectApplet(appletAID);
        byte[] response = simulator.transmitCommand(new byte[]{(byte) 0xA0, 0x01, 0x00, 0x00});
        System.out.println(bytesToAscii(response));
        response = simulator.transmitCommand(new byte[]{(byte) 0xA0, 0x02, 0x00, 0x00});
        System.out.println(bytesToAscii(response));
        response = simulator.transmitCommand(new byte[]{(byte) 0xA0, 0x03, 0x00, 0x00});
        System.out.println(bytesToAscii(response));
    }
}
