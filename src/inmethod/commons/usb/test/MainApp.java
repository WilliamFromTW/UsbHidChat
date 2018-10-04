package inmethod.commons.usb.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.hid4java.HidDevice;

import inmethod.commons.usb.UsbHidTools;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class MainApp {

	protected Shell shlDesignedByWilliam;
	private UsbHidTools aUsbHidTools;
	private List<HidDevice> aDeviceList;
	private HidDevice aHidDevice;
	private Text txtFf;
	private Text FeedBackMessages;

	private Button btnConnect;
	private CCombo combo;
	private Button btnNewButton;
	private CCombo combo_1;
	private Button btnSend;
	private Group CommandGroup;
	private Text PackageLength;
	private CLabel lblSpackagelength;
	private CLabel lblHintOr;
	private CLabel lblV;
	private CLabel lblMessageSentTo;
	private Button btnSave;
	private CLabel lblMessagesFromDevice;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainApp window = new MainApp();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlDesignedByWilliam.open();
		shlDesignedByWilliam.layout();
		while (!shlDesignedByWilliam.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlDesignedByWilliam = new Shell();
		shlDesignedByWilliam.setSize(921, 592);
		shlDesignedByWilliam.setText("USB HID test");

		Group group = new Group(shlDesignedByWilliam, SWT.NONE);
		group.setBounds(10, 10, 899, 59);

		CLabel lblNewLabel = new CLabel(group, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		lblNewLabel.setBounds(10, 22, 258, 21);
		lblNewLabel.setText("Usb Device (VendorID,ProductID) : ");

		btnConnect = new Button(group, SWT.NONE);
		btnConnect.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		btnConnect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (btnConnect.getText().equalsIgnoreCase("Connect")) {
					aHidDevice = aDeviceList.get(combo.getSelectionIndex());

					if (aHidDevice.open()) {
						System.out.println("success");
						btnConnect.setText("Disconnect");
						CommandGroup.setEnabled(true);
						JOptionPane.showMessageDialog(null, aHidDevice.getProduct() + " is connected!",
								"USB HID connection", JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null, aHidDevice.getProduct() + " can not be connected!",
								"USB HID connection", JOptionPane.WARNING_MESSAGE);
						btnConnect.setText("Connect");
					}
				} else if (btnConnect.getText().equalsIgnoreCase("Disconnect")) {
					if (aHidDevice != null && aHidDevice.isOpen()) {
						aHidDevice.close();
						btnConnect.setText("Connect");
						CommandGroup.setEnabled(false);
					}
				}
			}
		});
		btnConnect.setEnabled(false);
		btnConnect.setBounds(770, 22, 117, 25);
		btnConnect.setText("Connect");

		combo = new CCombo(group, SWT.BORDER);
		combo.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				combo.getSelectionIndex();
				btnConnect.setEnabled(true);
				System.out.println("selected index = " + combo.getSelectionIndex());
				if (aHidDevice != null && aHidDevice.isOpen()) {
					aHidDevice.close();
					btnConnect.setText("Connect");
					CommandGroup.setEnabled(false);
				}
			}
		});
		combo.setEnabled(false);
		combo.setEditable(false);
		combo.setBounds(275, 22, 405, 21);

		btnNewButton = new Button(group, SWT.NONE);
		btnNewButton.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		btnNewButton.setBounds(686, 22, 78, 25);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				aUsbHidTools = new UsbHidTools();
				aDeviceList = aUsbHidTools.getHidDevices();

				if (aDeviceList.size() > 0) {
					String[] sList = new String[aDeviceList.size()];
					String sProductId = null;
					for (int i = 0; i < aDeviceList.size(); i++) {
						HidDevice aTmpHidDevice = aDeviceList.get(i);
						sProductId = aTmpHidDevice.getProduct();
						if (sProductId == null)
							sList[i] = "NULL(No write permission)" + " (0x"
									+ Integer.toHexString(aTmpHidDevice.getVendorId() & 0xffff) + " , 0x"
									+ Integer.toHexString(aTmpHidDevice.getProductId() & 0xffff) + ")";
						else
							sList[i] = sProductId + " (0x" + Integer.toHexString(aTmpHidDevice.getVendorId() & 0xffff)
									+ " , 0x" + Integer.toHexString(aTmpHidDevice.getProductId() & 0xffff) + ")";

						System.out.println(aTmpHidDevice);
					}
					combo.setEnabled(true);
					combo.removeAll();
					combo.setItems(sList);
					JOptionPane.showMessageDialog(null, aDeviceList.size() + " USB HID devices were found",
							"USB HID scanner", JOptionPane.INFORMATION_MESSAGE);

				} else
					combo.setEnabled(false);

			}
		});
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnNewButton.setText("Scan");

		CommandGroup = new Group(shlDesignedByWilliam, SWT.NONE);
		CommandGroup.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		CommandGroup.setEnabled(false);
		CommandGroup.setBounds(10, 75, 899, 466);

		txtFf = new Text(CommandGroup, SWT.BORDER | SWT.RIGHT);
		txtFf.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		txtFf.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		txtFf.setText("ff");
		txtFf.setEnabled(true);
		txtFf.setBounds(226, 50, 202, 21);

		CLabel lblNewLabel_1 = new CLabel(CommandGroup, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		lblNewLabel_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblNewLabel_1.setBounds(10, 50, 83, 21);
		lblNewLabel_1.setText("Command");

		btnSend = new Button(CommandGroup, SWT.NONE);
		btnSend.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		btnSend.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		btnSend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (txtFf.getText() == null || txtFf.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, "No Command", "Command", JOptionPane.WARNING_MESSAGE);
				} else {
					byte[] aResultSet;
					byte[] aCmd = null;
					if (combo_1.getSelectionIndex() == 0) { // hex
						aCmd = hexStringToByteArray(txtFf.getText());

					} else if (combo_1.getSelectionIndex() == 1) {
						aCmd = txtFf.getText().getBytes();
					}

					try {
						aResultSet = aUsbHidTools.getResponsedDataBySendCommand(aHidDevice, aCmd,
								Integer.parseInt(PackageLength.getText()));
						if (combo_1.getSelectionIndex() == 0) // hex
							FeedBackMessages.setText("command : (HEX) 0x" + txtFf.getText() + "\r\n"
									+ bytesToHexString(aResultSet).toUpperCase() + "\r\n" + FeedBackMessages.getText());
						else if (combo_1.getSelectionIndex() == 1)
							FeedBackMessages.setText("command : (Text) " + txtFf.getText() + "\r\n"
									+ bytesToHexString(aResultSet).toUpperCase() + "\r\n" + FeedBackMessages.getText());

						// for (byte b : aResultSet) {
						// System.out.printf(" %02x", b);
						// }
					} catch (NumberFormatException e1) {

						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "No Command", "Command", JOptionPane.WARNING_MESSAGE);
					} catch (Exception e1) {

						e1.printStackTrace();
					}
				}
			}
		});
		btnSend.setBounds(350, 77, 78, 25);
		btnSend.setText("Send");

		FeedBackMessages = new Text(CommandGroup, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		FeedBackMessages.setFont(SWTResourceManager.getFont("Verdana", 10, SWT.NORMAL));
		FeedBackMessages.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		FeedBackMessages.setEnabled(true);
		FeedBackMessages.setBounds(434, 47, 453, 362);

		combo_1 = new CCombo(CommandGroup, SWT.BORDER | SWT.RIGHT);
		combo_1.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		combo_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		combo_1.setEnabled(true);
		combo_1.setListVisible(true);
		combo_1.setItems(new String[] { "HEX ", "TEXT" });
		combo_1.setBounds(99, 50, 119, 21);
		combo_1.select(0);

		PackageLength = new Text(CommandGroup, SWT.BORDER);
		PackageLength.setFont(SWTResourceManager.getFont("Ubuntu", 10, SWT.NORMAL));

		PackageLength.setText("8");
		PackageLength.setBounds(684, 415, 32, 21);

		lblSpackagelength = new CLabel(CommandGroup, SWT.NONE);
		lblSpackagelength.setFont(SWTResourceManager.getFont("Ubuntu", 10, SWT.NORMAL));
		lblSpackagelength.setText("Packet Length");
		lblSpackagelength.setBounds(588, 419, 90, 21);

		lblHintOr = new CLabel(CommandGroup, SWT.NONE);
		lblHintOr.setFont(SWTResourceManager.getFont("Microsoft JhengHei UI", 8, SWT.ITALIC));
		lblHintOr.setBounds(584, 435, 192, 21);
		lblHintOr.setText(" byte size  every reading loop");

		lblV = new CLabel(CommandGroup, SWT.RIGHT);
		lblV.setFont(SWTResourceManager.getFont("Verdana", 7, SWT.NORMAL));
		lblV.setBounds(754, 415, 133, 41);
		lblV.setText("InMethodUsbHIDTest \r\nv1.1\r\n2018/10/04");

		lblMessageSentTo = new CLabel(CommandGroup, SWT.NONE);
		lblMessageSentTo.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lblMessageSentTo.setFont(SWTResourceManager.getFont("Microsoft JhengHei UI", 10, SWT.BOLD));
		lblMessageSentTo.setText("Message sent to USB HID device");
		lblMessageSentTo.setBounds(10, 10, 279, 21);

		Button btnClear = new Button(CommandGroup, SWT.NONE);
		btnClear.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		btnClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FeedBackMessages.setText("");
			}
		});
		btnClear.setBounds(434, 415, 53, 25);
		btnClear.setText("Clear");

		btnSave = new Button(CommandGroup, SWT.NONE);
		btnSave.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				FileDialog dialog = new FileDialog(shlDesignedByWilliam, SWT.SAVE);
				dialog.setFilterNames(new String[] { "Text Files", "All Files (*.*)" });
				dialog.setFilterExtensions(new String[] { "*.txt", "*.*" });

				dialog.setFilterPath(File.separator);
				SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date current = new Date();
				dialog.setFileName(aHidDevice.getProduct() + "_UsbHidMessages_" + sdFormat.format(current) + ".txt");
				if (dialog.open() != null) {
					String sFileName = dialog.getFilterPath() + File.separator + dialog.getFileName();
					System.out.println("File Name = " + sFileName);
					try {
						BufferedWriter writer = new BufferedWriter(new FileWriter(sFileName, true));
						writer.append(FeedBackMessages.getText());
						writer.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
			}
		});
		btnSave.setBounds(493, 415, 78, 25);
		btnSave.setText("Save");

		lblMessagesFromDevice = new CLabel(CommandGroup, SWT.NONE);
		lblMessagesFromDevice.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lblMessagesFromDevice.setText("Messages received from USB HID device\nOutput :  8 bytes Hex String each line ");
		lblMessagesFromDevice.setFont(SWTResourceManager.getFont("Microsoft JhengHei UI", 10, SWT.BOLD));
		lblMessagesFromDevice.setBounds(434, 10, 453, 34);

	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static String bytesToHexString(final byte[] hash) {
		Formatter formatter = new Formatter();
		int i = 0;
		int j = 1;
		for (byte b : hash) {
			i++;
			if (i >= 8) {
				i = 0;
				j++;
				formatter.format("%02x\r\n", b);

			} else if (i == 1) {
				formatter.format("%03d	-	", j);
				formatter.format("%02x	", b);
			} else
				formatter.format("%02x	", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}
}
