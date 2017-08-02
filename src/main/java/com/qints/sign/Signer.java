package com.qints.sign;

import com.qints.util.StringResource;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * 主界面，程序入口
 * 
 * @author qints
 */
public class Signer extends JFrame {
	private static final long serialVersionUID = -248267026757314816L;
	private int width = 600;
	private int height = 500;

	public Signer() {
		setTitle(StringResource.getStringByLabel("APPLICATION_NAME"));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - this.width) / 2, (screenSize.height - this.height) / 2);
		setLayout(new BorderLayout());
		add(new SignerPanel());
		setSize(this.width, this.height);
		setVisible(true);
		setDefaultCloseOperation(3);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// try {
				// UIManager.setLookAndFeel(
				// "org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel");
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
				try {
					//加载BeautyEye组件
					org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
					UIManager.put("RootPane.setupButtonVisible", false);
				} catch (Exception e) {
					// TODO exception
				}
				new Signer();
			}
		});
	}
}