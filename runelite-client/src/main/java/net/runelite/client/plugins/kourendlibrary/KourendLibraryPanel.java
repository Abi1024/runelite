/*
 * Copyright (c) 2018 Abex
 * Copyright (c) 2018 Psikoi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.kourendlibrary;

import com.google.inject.Inject;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

@Singleton
class KourendLibraryPanel extends PluginPanel
{
	private static final ImageIcon RESET_ICON;
	private static final ImageIcon RESET_HOVER_ICON;

	private final KourendLibraryConfig config;
	private final Library library;
	private final KourendLibraryPlugin plugin;

	private final HashMap<Book, BookPanel> bookPanels = new HashMap<>();

	static
	{
		final BufferedImage resetIcon = ImageUtil.getResourceStreamFromClass(KourendLibraryPanel.class, "/util/reset.png");
		RESET_ICON = new ImageIcon(resetIcon);
		RESET_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(resetIcon, -100));
	}

	@Inject
	KourendLibraryPanel(KourendLibraryConfig config, Library library, KourendLibraryPlugin plugin)
	{
		super();

		this.config = config;
		this.library = library;
		this.plugin = plugin;
	}

	void init()
	{
		setLayout(new BorderLayout(0, 5));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		JPanel books = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		Stream.of(Book.values())
			/*.filter(b ->
			{
				if (!b.isDarkManuscript()){
					return true;
				}
				for (Bookcase bookcase : library.getBookcases()){
					if ( bookcase.getBook() != null && bookcase.getBook().isDarkManuscript()){
						boolean flag = false;
						if ( bookcase.getLocationString().toLowerCase().contains("Northwest") &&
							library.getDark_manuscript_location()[1].contains("north-west"))
						{
							flag = true;
						}else if ( bookcase.getLocationString().toLowerCase().contains("Southwest") &&
							library.getDark_manuscript_location()[1].contains("south-west"))
						{
							flag = true;
						}else if ( bookcase.getLocationString().toLowerCase().contains("Central") &&
							library.getDark_manuscript_location()[1].contains("central"))
						{
							flag = true;
						}else if ( bookcase.getLocationString().toLowerCase().contains("Northeast") &&
							library.getDark_manuscript_location()[1].contains("north-east"))
						{
							flag = true;
						}else if ( bookcase.getLocationString().toLowerCase().contains("Southeast") &&
							library.getDark_manuscript_location()[1].contains("south-east"))
						{
							flag = true;
						}
						if (!flag){
							return false;
						}
						if ( bookcase.getLocationString().toLowerCase().contains("ground") &&
							library.getDark_manuscript_location()[0].equals("bottom"))
						{
							return true;
						}else if ( bookcase.getLocationString().toLowerCase().contains("middle") &&
							library.getDark_manuscript_location()[0].equals("middle"))
						{
							return true;
						}else if ( bookcase.getLocationString().toLowerCase().contains("top") &&
							library.getDark_manuscript_location()[0].equals("top"))
						{
							return true;
						}
					}
				}
				return false;
			}
			)*/
			.filter(b -> !config.hideVarlamoreEnvoy() || b != Book.VARLAMORE_ENVOY)
			.sorted(Comparator.comparing(Book::getShortName))
			.forEach(b ->
			{
				BookPanel p = new BookPanel(b);
				bookPanels.put(b, p);
				if (b.isDarkManuscript()){
					p.setVisible(false);
				}
				books.add(p, c);
				c.gridy++;
			});

		JButton reset = new JButton("Reset", RESET_ICON);
		reset.setRolloverIcon(RESET_HOVER_ICON);
		reset.addActionListener(ev ->
		{
			library.reset();
			update();
		});

		add(reset, BorderLayout.NORTH);
		add(books, BorderLayout.CENTER);
		update();
	}

	void update()
	{
		System.out.println("Updating library");
		SwingUtilities.invokeLater(() ->
		{
			Book customerBook = library.getCustomerBook();
			HashMap<Book, HashSet<String>> bookLocations = new HashMap<>();

			for (Bookcase bookcase : library.getBookcases())
			{
				if (bookcase.getBook() != null)
				{
					System.out.println("Bookcase: " + bookcase.getIndex() + " book: " + bookcase.getBook());
					bookLocations.computeIfAbsent(bookcase.getBook(), a -> new HashSet<>()).add(bookcase.getLocationString());
				}
				else
				{
					for (Book book : bookcase.getPossibleBooks())
					{
						System.out.println("Bookcase possible: " + bookcase.getIndex() + " book: " + book);
						if (book != null)
						{
							bookLocations.computeIfAbsent(book, a -> new HashSet<>()).add(bookcase.getLocationString());
						}
					}
				}
			}

			for (Map.Entry<Book, BookPanel> e : bookPanels.entrySet())
			{
				if (!e.getKey().isDarkManuscript()){
					e.getValue().setColor(plugin.doesPlayerContainBook(e.getKey()),library.getCustomerBook() == e.getKey());
				}
				HashSet<String> locs = bookLocations.get(e.getKey());
				if (locs != null && locs.size() < 4){
					if (e.getKey().isDarkManuscript()){
						boolean flag = false;
						for (String location : locs){
							if (matching_manuscript(location)){
								flag = true;
								break;
							}
						}
						if (flag){
							e.getValue().setVisible(true);
							e.getValue().setIsManuscript();
						}else{
							e.getValue().setVisible(false);
						}
					}
				}
				if (locs == null || locs.size() > 3)
				{
					e.getValue().setLocation("Unknown");
				}
				else
				{
					e.getValue().setLocation("<html>" + locs.stream().collect(Collectors.joining("<br>")) + "</html>");
				}

			}
		});
	}

	void reload()
	{
		bookPanels.clear();
		removeAll();
		init();
	}

	private boolean matching_manuscript(String location){
		System.out.println("Bookcase location: " + location);
		System.out.println("Target manuscript: " + library.getDark_manuscript_location()[0] + " " + library.getDark_manuscript_location()[1]); ;
		boolean flag = false;
		if ( location.toLowerCase().contains("northwest") &&
			library.getDark_manuscript_location()[1].contains("north-west"))
		{
			flag = true;
		}else if ( location.toLowerCase().contains("southwest") &&
			library.getDark_manuscript_location()[1].contains("south-west"))
		{
			flag = true;
		}else if ( location.toLowerCase().contains("center") &&
			library.getDark_manuscript_location()[1].contains("central"))
		{
			flag = true;
		}else if ( location.toLowerCase().contains("northeast") &&
			library.getDark_manuscript_location()[1].contains("north-east"))
		{
			flag = true;
		}else if ( location.toLowerCase().contains("southeast") &&
			library.getDark_manuscript_location()[1].contains("south-east"))
		{
			flag = true;
		}
		System.out.println("First Flag " + flag);
		if ( location.toLowerCase().contains("ground") &&
			library.getDark_manuscript_location()[0].equals("bottom"))
		{
			flag &= true;
		}else if ( location.toLowerCase().contains("middle") &&
			library.getDark_manuscript_location()[0].equals("middle"))
		{
			flag &= true;
		}else if ( location.toLowerCase().contains("top") &&
			library.getDark_manuscript_location()[0].equals("top"))
		{
			flag &= true;
		}else{
			flag = false;
		}
		System.out.println("Flag " + flag);
		return flag;
	}
}