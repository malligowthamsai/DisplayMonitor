import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.WinUser.*;
import javax.swing.*;
import java.awt.*;

public class DisplayMonitor {

static final int WM_DISPLAYCHANGE = 0x007E;

    static JTextArea area;

    public static void main(String[] args) {

        JFrame frame = new JFrame("HDMI / Display Monitor (JNA)");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        area = new JTextArea();
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setEditable(false);

        frame.add(new JScrollPane(area));
        frame.setVisible(true);
	detectScreens();   // 🔥 THIS LINE
        startWindowsListener();
    }

    public static void startWindowsListener() {

        new Thread(() -> {

            WinUser.HWND hwnd = User32.INSTANCE.CreateWindowEx(
                    0,
                    "STATIC",
                    "Listener",
                    0,
                    0, 0, 0, 0,
                    null, null, null, null
            );

            WinUser.MSG msg = new WinUser.MSG();

            while (User32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {

              if (msg.message == WM_DISPLAYCHANGE) {

                SwingUtilities.invokeLater(() -> {
                area.append("Display Changed (HDMI Plug/Unplug)\n");
               detectScreens();
            });
            }

            User32.INSTANCE.TranslateMessage(msg);
            User32.INSTANCE.DispatchMessage(msg);
            }

            }).start();
            }

    public static void detectScreens() {

        GraphicsDevice[] screens = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getScreenDevices();

        area.append("Total Screens: " + screens.length + "\n\n");

        for (int i = 0; i < screens.length; i++) {

            Rectangle r = screens[i]
                    .getDefaultConfiguration()
                    .getBounds();

            area.append("Screen " + (i+1) + ": "
                    + r.width + "x" + r.height + "\n");
        }

        area.append("-------------------------\n");

    }

}
