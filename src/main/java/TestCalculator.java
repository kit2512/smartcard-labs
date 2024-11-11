import com.licel.jcardsim.base.Simulator;
import javacard.framework.AID;
import org.jetbrains.annotations.NotNull;
import utils.AIDUtil;

public class TestCalculator {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        AID appletAID = AIDUtil.create("F000000001");
        simulator.installApplet(appletAID, Calculator.class);
        simulator.selectApplet(appletAID);
        byte[] response = simulator.transmitCommand(new byte[]{(byte) 0xA0, 0x02, 0x02, 0x03});
        System.out.println(bytesToHex(response));
    }

    @org.jetbrains.annotations.NotNull
    private static String bytesToHex(byte @NotNull [] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
