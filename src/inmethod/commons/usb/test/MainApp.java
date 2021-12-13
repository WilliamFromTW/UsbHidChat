package inmethod.commons.usb.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class MainApp {

	protected Shell shlDesignedByWilliam;
	private UsbHidTools aUsbHidTools;
	private List<HidDevice> aDeviceList;
	private HidDevice aHidDevice;
	private Text txtCmd;
	private Text FeedBackMessages;

	private Button btnConnect;
	private CCombo comboDevice;
	private Button btnScan;
	private CCombo comboEncode;
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
					aHidDevice = aDeviceList.get(comboDevice.getSelectionIndex());

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

		comboDevice = new CCombo(group, SWT.BORDER);
		comboDevice.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		comboDevice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comboDevice.getSelectionIndex();
				btnConnect.setEnabled(true);
				System.out.println("selected index = " + comboDevice.getSelectionIndex());
				if (aHidDevice != null && aHidDevice.isOpen()) {
					aHidDevice.close();
					btnConnect.setText("Connect");
					CommandGroup.setEnabled(false);
				}
			}
		});
		comboDevice.setEnabled(false);
		comboDevice.setEditable(false);
		comboDevice.setBounds(275, 22, 405, 21);

		btnScan = new Button(group, SWT.NONE);
		btnScan.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		btnScan.setBounds(686, 22, 78, 25);
		btnScan.addMouseListener(new MouseAdapter() {
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
					comboDevice.setEnabled(true);
					comboDevice.removeAll();
					comboDevice.setItems(sList);
					JOptionPane.showMessageDialog(null, aDeviceList.size() + " USB HID devices were found",
							"USB HID scanner", JOptionPane.INFORMATION_MESSAGE);
					comboDevice.select(0);
					btnConnect.setEnabled(true);

				} else {
					JOptionPane.showMessageDialog(null,"No USB HID device was found",
							"USB HID scanner", JOptionPane.WARNING_MESSAGE);
					comboDevice.setEnabled(false);
				}

			}
		});
		btnScan.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnScan.setText("Scan");

		CommandGroup = new Group(shlDesignedByWilliam, SWT.NONE);
		CommandGroup.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		CommandGroup.setEnabled(false);
		CommandGroup.setBounds(10, 75, 899, 466);

		txtCmd = new Text(CommandGroup, SWT.BORDER | SWT.RIGHT);
		txtCmd.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		txtCmd.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		txtCmd.setText("ff");
		txtCmd.setEnabled(true);
		txtCmd.setBounds(226, 50, 202, 21);

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
				if (txtCmd.getText() == null || txtCmd.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, "No Command", "Command", JOptionPane.WARNING_MESSAGE);
				} else {

					JOptionPane pane = new JOptionPane("Please wait, dialog will auto close ");
					pane.setOptions(new Object[] {});
					JDialog dialog = pane.createDialog("Processing command");
					dialog.setModal(false);
					dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					dialog.setVisible(true);
					
					byte[] aResultSet;
					byte[] aCmd = null;

					if (comboEncode.getSelectionIndex() == 0) { // hex
						aCmd = hexStringToByteArray(txtCmd.getText());

					} else if (comboEncode.getSelectionIndex() == 1) {
						aCmd = txtCmd.getText().getBytes();
					}

					try {
						aResultSet = aUsbHidTools.getResponsedDataBySendCommand(aHidDevice, aCmd,
								Integer.parseInt(PackageLength.getText()));
						SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date current = new Date();
						
						if (comboEncode.getSelectionIndex() == 0) // hex
							FeedBackMessages.setText(sdFormat.format(current)+"\r\n--\r\nCommand : (HEX) 0x" + txtCmd.getText() + "\r\n"
									+ bytesToHexString(aResultSet).toUpperCase() + "\r\n" + FeedBackMessages.getText());
						else if (comboEncode.getSelectionIndex() == 1)
							FeedBackMessages.setText(sdFormat.format(current)+"\r\n--\r\nommand : (Text) " + txtCmd.getText() + "\r\n"
									+ bytesToHexString(aResultSet).toUpperCase() + "\r\n" + FeedBackMessages.getText());

					} catch (NumberFormatException e1) {

						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "No Command", "Command", JOptionPane.WARNING_MESSAGE);
					} catch (Exception e1) {

						e1.printStackTrace();
					}
					dialog.dispose();
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

		comboEncode = new CCombo(CommandGroup, SWT.BORDER | SWT.RIGHT);
		comboEncode.setFont(SWTResourceManager.getFont("Ubuntu", 12, SWT.NORMAL));
		comboEncode.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		comboEncode.setEnabled(true);
		comboEncode.setListVisible(true);
		comboEncode.setItems(new String[] { "HEX ", "TEXT" });
		comboEncode.setBounds(99, 50, 119, 21);
		comboEncode.select(0);

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
		lblV.setText("InMethodUsbHIDTest \r\nv1.4\r\n2021/12/10");

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
		lblMessagesFromDevice
				.setText("Messages received from USB HID device\r\nOutput 8 Bytes data (Hex String) per line ");
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
		int j = -1;
		for (byte b : hash) {
			i++;
			j++;
			if (i >= 8) {
				i = 0;
				formatter.format("%02x\r\n", b);

			} else if (i == 1) {
				formatter.format("%04d	-	", j);
				formatter.format("%02x	", b);
			} else
				formatter.format("%02x	", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}
}
