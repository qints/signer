package com.qints.sign;

import com.qints.util.StringResource;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;

public class SignerPanel extends JPanel {
	private static final long serialVersionUID = 8644436722267192675L;

	private final String titleContext = StringResource.getStringByLabel("SIGNER_LABEL");

	private JTabbedPane tabPane = new JTabbedPane();
	private JLabel titleLab = new JLabel(this.titleContext);

	public SignerPanel() {
		initCompo();
		initListener();
		setting();
	}

	private void setting() {
		setVisible(true);
	}

	private void initCompo() {
		this.tabPane.add(new SignerJarPanel(), "为jar签名");
		this.tabPane.add(new GenerateJksPanel(), "制作签名文件");

		setLayout(new MigLayout("insets 10", "[grow]", "[][grow]"));
		JPanel northPane = new JPanel(new FlowLayout(1));
		northPane.add(this.titleLab);
		add(northPane, "growx,wrap,h 30!");
		add(this.tabPane, "growx,growy,h 400");
	}

	private void initListener() {
	}
}