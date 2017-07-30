package com.qints.sign;

import com.qints.util.Message;
import com.qints.util.StringResource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.miginfocom.swing.MigLayout;

public class SignerJarPanel extends JPanel {
	private static final long serialVersionUID = 2629317848411658540L;
	private JLabel jksFilePathLabel = new JLabel(StringResource.getStringByLabel("JKS_FILE_PATH"));
	private JTextField jksFilePathField = new JTextField(30);
	private JButton selectJksFileBtn = new JButton("...");

	private JLabel jksAliasLabel = new JLabel(StringResource.getStringByLabel("JKS_ALIAS_LABEL"));
	private JTextField jksAliasField = new JTextField(30);

	private JLabel jarFilePathLabel = new JLabel(StringResource.getStringByLabel("JAR_FILE_PATH"));
	private JTextField jarFilePathField = new JTextField(30);
	private JButton selectJarFileBtn = new JButton("...");

	private JTextArea reslutArea = new JTextArea(20, 30);

	private JButton signeBtn = new JButton(StringResource.getStringByLabel("SIGN"));
	private JButton helpBtn = new JButton(StringResource.getStringByLabel("HELP"));

	PrintWriter pw = null;
	private StringBuffer userInputString = new StringBuffer();

	private ActionListener signBtnListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String jksFilePath = SignerJarPanel.this.jksFilePathField.getText();
			String jksAlias = SignerJarPanel.this.jksAliasField.getText();
			String jarFilePath = SignerJarPanel.this.jarFilePathField.getText();

			if (jksFilePath.isEmpty()) {
				SignerJarPanel.this.showMessage("请选择jks签名文件！");
				return;
			}
			if (jksAlias.isEmpty()) {
				SignerJarPanel.this.showMessage("请输入签名文件别名！");
				return;
			}
			if (jarFilePath.isEmpty()) {
				SignerJarPanel.this.showMessage("请选择jar签名文件！");
				return;
			}

			final StringBuffer cdExc = new StringBuffer();
			final StringBuffer signExc = new StringBuffer();
			cdExc.append("").append(SignerJarPanel.this.findSignToolLab()).append("");

			signExc.append("jarsigner -keystore \"").append(jksFilePath).append("\" ");
			signExc.append("\"").append(jarFilePath).append("\" ");
			signExc.append(jksAlias);
			System.out.println(cdExc.toString());
			System.out.println(signExc.toString());
			new Thread() {
				public void run() {
					Process pro = null;
					try {
						pro = Runtime.getRuntime().exec(signExc.toString(), null, new File(cdExc.toString()));
						InputStream is = pro.getInputStream();
						InputStream isE = pro.getErrorStream();
						SignerJarPanel.this.pw = new PrintWriter(pro.getOutputStream());
						SignerJarPanel.StreamGobbler outputGobbler = new StreamGobbler(is);
						SignerJarPanel.StreamGobbler errorGobbler = new StreamGobbler(isE);
						outputGobbler.start();
						errorGobbler.start();
						pro.waitFor();
					} catch (Exception e1) {
						e1.printStackTrace();
					} finally {
						pro.destroy();
					}
				}
			}.start();
		}
	};

	private ActionListener signFileListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Preferences pre = SignerJarPanel.this.getPref();
			JFileChooser fileChooser = new JFileChooser();
			if (e.getSource().equals(SignerJarPanel.this.selectJksFileBtn)) {
				String rememberPath = pre.get("jks.file.path", "");
				File rememberFile = new File("\\");
				if (!rememberPath.equals("")) {
					rememberFile = new File(rememberPath).getParentFile();
				}
				fileChooser.setCurrentDirectory(rememberFile);
				fileChooser.setFileFilter(new SignFileFilter(".jks"));
			} else if (e.getSource().equals(SignerJarPanel.this.selectJarFileBtn)) {
				String rememberPath = pre.get("jar.file.path", "");
				File rememberFile = new File("\\");
				if (!rememberPath.equals("")) {
					rememberFile = new File(rememberPath).getParentFile();
				}
				fileChooser.setCurrentDirectory(rememberFile);
				fileChooser.setFileFilter(new SignerJarPanel.SignFileFilter(".jar"));
			}
			int rel = fileChooser.showOpenDialog(SignerJarPanel.this);
			if (rel == 0)
				if (e.getSource().equals(SignerJarPanel.this.selectJksFileBtn)) {
					String tempFilePath = fileChooser.getSelectedFile().getAbsolutePath();
					String tempFileName = fileChooser.getSelectedFile().getName();
					try {
						tempFileName = tempFileName.substring(0, tempFileName.indexOf("."));
					} catch (Exception e2) {
						tempFileName = fileChooser.getSelectedFile().getName();
					}
					SignerJarPanel.this.jksFilePathField.setText(tempFilePath);
					SignerJarPanel.this.jksFilePathField.setToolTipText(tempFilePath);
					SignerJarPanel.this.jksAliasField.setText(tempFileName);
					pre.put("jks.file.path", tempFilePath);
					pre.put("jks.alias", tempFileName);
				} else if (e.getSource().equals(SignerJarPanel.this.selectJarFileBtn)) {
					String tempFilePath = fileChooser.getSelectedFile().getAbsolutePath();
					SignerJarPanel.this.jarFilePathField.setText(tempFilePath);
					SignerJarPanel.this.jarFilePathField.setToolTipText(tempFilePath);
					pre.put("jar.file.path", tempFilePath);
				}
		}
	};

	public SignerJarPanel() {
		initCompo();
		initListener();
	}

	private void initCompo() {
		this.reslutArea.setToolTipText("此处显示输出结果");

		setLayout(new MigLayout("insets 10", "[right,grow][center,150!][left,grow]",
				"20[]15[]15[]20[grow]20[30!,bottom]"));

		add(this.jksFilePathLabel, "");
		add(this.jksFilePathField, "");
		add(this.selectJksFileBtn, "w 25!,wrap");

		add(this.jksAliasLabel, "");
		add(this.jksAliasField, "wrap");

		add(this.jarFilePathLabel, "");
		add(this.jarFilePathField, "");
		add(this.selectJarFileBtn, "w 25!,wrap");

		add(new JScrollPane(this.reslutArea), "span 3,growx,growy,wrap");

		add(this.signeBtn, "span 3,split 2,center");
		add(this.helpBtn, "center");

		Preferences pref = getPref();
		this.jksFilePathField.setText(pref.get("jks.file.path", ""));
		this.jksFilePathField.setToolTipText(pref.get("jks.file.path", ""));
		this.jksAliasField.setText(pref.get("jks.alias", ""));
		this.jksAliasField.setToolTipText(pref.get("jks.alias", ""));
		this.jarFilePathField.setText(pref.get("jar.file.path", ""));
		this.jarFilePathField.setToolTipText(pref.get("jar.file.path", ""));
	}

	private void initListener() {
		this.selectJksFileBtn.addActionListener(this.signFileListener);
		this.selectJarFileBtn.addActionListener(this.signFileListener);
		this.signeBtn.addActionListener(this.signBtnListener);
		this.reslutArea.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				char c = e.getKeyChar();
				if (c == '\n') {
					if (SignerJarPanel.this.pw != null)
						try {
							System.out.println(SignerJarPanel.this.userInputString.toString());
							SignerJarPanel.this.pw.println(SignerJarPanel.this.userInputString.toString());
							SignerJarPanel.this.pw.flush();
							SignerJarPanel.this.userInputString.setLength(0);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
				} else
					SignerJarPanel.this.userInputString.append(c);
			}
		});
	}

	private Preferences getPref() {
		return Preferences.userNodeForPackage(SignerJarPanel.class);
	}

	protected String findSignToolLab() {
		System.getProperties();

		String[] lab = System.getProperty("sun.boot.class.path").split(";");
		if ((lab != null) && (lab.length > 0)) {
			String jreHome = lab[0];
			File tmp = new File(jreHome);
			File jdkHome = null;
			while (tmp != null) {
				if (tmp.getName().toLowerCase().indexOf("java") > -1) {
					jdkHome = tmp;
					break;
				}
				tmp = tmp.getParentFile();
			}
			File[] listFiles = jdkHome.listFiles();
			jdkHome = null;
			for (File file : listFiles) {
				if (file.getName().toLowerCase().indexOf("jdk") > -1) {
					jdkHome = file;
					break;
				}
			}
			if (jdkHome != null) {
				return jdkHome.getAbsolutePath() + "\\bin";
			}
		}

		String jdkHomePath = JOptionPane.showInputDialog(null, "未找到JDK路径，请手动输入：");
		if ((jdkHomePath != null) && (jdkHomePath.trim().length() > 0)) {
			if (jdkHomePath.endsWith(File.separator)) {
				jdkHomePath = jdkHomePath.substring(0, jdkHomePath.length() - 1);
			}
			return jdkHomePath + "\\bin";
		}
		JOptionPane.showMessageDialog(null, "未找到JDK的路径，签名无法进行。");
		return "";
	}

	private void showMessage(String context) {
		Message.showMessage(this, context);
	}

	private void insertRelArea(JTextArea jta, String str) {
		Document doc = jta.getDocument();
		if (doc != null)
			try {
				doc.insertString(doc.getLength(), str, null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
	}

	private static class SignFileFilter extends FileFilter {
		String filtPattern = "";

		public SignFileFilter(String pattern) {
			this.filtPattern = pattern;
		}

		public String getDescription() {
			return null;
		}

		public boolean accept(File f) {
			if (f.isFile()) {
				if (f.getName().toLowerCase().endsWith(this.filtPattern)) {
					return true;
				}
				return false;
			}
			return true;
		}
	}

	private class StreamGobbler extends Thread {
		private InputStream is;

		public StreamGobbler(InputStream is) {
			this.is = is;
		}

		public void run() {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] data = new byte[4096];
			int count = -1;
			try {
				while ((count = this.is.read(data, 0, 4096)) != -1) {
					outStream.write(data, 0, count);
					String temp = new String(outStream.toByteArray(), "gbk");
					SignerJarPanel.this.insertRelArea(SignerJarPanel.this.reslutArea, temp);
					SignerJarPanel.this.reslutArea.setCaretPosition(SignerJarPanel.this.reslutArea.getText().length());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}