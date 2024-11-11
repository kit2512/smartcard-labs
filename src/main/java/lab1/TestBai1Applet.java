package lab1;

import com.licel.jcardsim.base.Simulator;
import javacard.framework.AID;
import utils.AIDUtil;


public class TestBai1Applet {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        AID appletAID = AIDUtil.create("F000000001");
        simulator.installApplet(appletAID, Bai1Applet.class);
        simulator.selectApplet(appletAID);
        byte[] response = simulator.transmitCommand(new byte[]{(byte) 0xA0, 0x01, 0x00, 0x00});
        System.out.println(bytesToHex(response));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
