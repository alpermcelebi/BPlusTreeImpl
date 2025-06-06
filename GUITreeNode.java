import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class GUITreeNode extends JPanel {
	public ScholarNode node; // To check and paint while searching
	
	protected ArrayList<JLabel> labels;
	protected ArrayList<JPanel> paddings;
	
	private static final int maxYValue = 40;
	
	public GUITreeNode(ScholarNode node) {
		this.node = node;
		this.labels = new ArrayList<JLabel>();
		this.paddings = new ArrayList<JPanel>();
		
		this.setLayout(new FlowLayout());
		this.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, node.getColor()));

		for(int i = 0; i < CengGUI.orderN - 1; i++) {
			
			JLabel keyLabel = new JLabel("");
			keyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			
			labels.add(keyLabel);
			
			this.add(this.getNewPadding());
			this.add(Box.createHorizontalStrut(5));
			this.add(keyLabel);
			this.add(Box.createHorizontalStrut(5));
		}
		
		this.add(this.getNewPadding());
						
		this.revalidate();
		this.repaint();
	}

	private JPanel getNewPadding() {
		JPanel padding = new JPanel();
		padding.setAlignmentX(Component.CENTER_ALIGNMENT);
		padding.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.black));
		padding.setMaximumSize(new Dimension(5, 1000));
		padding.setPreferredSize(new Dimension(5, maxYValue));
		
		this.paddings.add(padding);
		
		return padding;
	}
}
