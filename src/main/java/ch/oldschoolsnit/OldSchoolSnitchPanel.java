package ch.oldschoolsnit;

import ch.oldschoolsnit.models.ModelSnapshot;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

@Slf4j
public class OldSchoolSnitchPanel extends PluginPanel
{
	private ScheduledExecutorService executor;

	private final EventBus eventBus;

	private final String websiteUrl = "https://oldschoolsnit.ch/";

	@Inject
	public OldSchoolSnitchPanel(ScheduledExecutorService executor, EventBus eventBus)
	{
		super();

		this.executor = executor;
		this.eventBus = eventBus;


		var basePanel = new JPanel();
		basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.Y_AXIS));


		var headerPanel = new JPanel();
		headerPanel.setLayout(new BorderLayout());
		headerPanel.setBackground(ColorScheme.BRAND_ORANGE);

		JLabel headerLabel = new JLabel("Old School Snitch");
		headerLabel.setFont(FontManager.getRunescapeBoldFont());
		headerLabel.setForeground(Color.BLACK);
		headerLabel.setHorizontalAlignment(JLabel.CENTER);
		headerPanel.add(headerLabel, BorderLayout.NORTH);

		basePanel.add(headerPanel);
		basePanel.add(Box.createRigidArea(new Dimension(0, 15)));

		var modelPanel = new JPanel();
		modelPanel.setLayout(new BorderLayout());
		JLabel modelPanelHeader = new JLabel("Player Model Integration");
		modelPanelHeader.setFont(FontManager.getRunescapeBoldFont());
		modelPanelHeader.setHorizontalAlignment(JLabel.CENTER);
		modelPanel.add(modelPanelHeader, BorderLayout.NORTH);
		var getPlayerModel = new JButton("Send Player Model");
		getPlayerModel.addActionListener(a ->
		{
			eventBus.post(new ModelSnapshot());
		});
		//TODO: Disable the button if the api key is null or whitespace.
		modelPanel.add(getPlayerModel, BorderLayout.CENTER);

		basePanel.add(modelPanel);
		basePanel.add(Box.createRigidArea(new Dimension(0, 15)));

		var websitePanel = new JPanel();
		websitePanel.setLayout(new BorderLayout());
		JLabel websitePanelHeader = new JLabel("About");
		websitePanelHeader.setFont(FontManager.getRunescapeBoldFont());
		websitePanelHeader.setHorizontalAlignment(JLabel.CENTER);
		websitePanel.add(websitePanelHeader, BorderLayout.NORTH);

		var openWebsite = new JButton("Open Website");
		openWebsite.addActionListener(a -> {
			try
			{
				Desktop desktop = java.awt.Desktop.getDesktop();
				URI homepage = new URI(websiteUrl);
				desktop.browse(homepage);
			}
			catch (Exception ex)
			{
				log.error("Error attempting to open the Old School Snitch Website", ex);
			}
		});
		websitePanel.add(openWebsite, BorderLayout.CENTER);

		basePanel.add(websitePanel);
		basePanel.add(Box.createRigidArea(new Dimension(0, 15)));

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		add(basePanel, BorderLayout.CENTER);

		//Grabs the config and checks for length
		//Add a button to test connection or something.
		//Show XP Drops sent this session
		//Show NPC Kills sent this session
		//Show Item Drops sent this session
		//Show Locations sent this session

	}

}
