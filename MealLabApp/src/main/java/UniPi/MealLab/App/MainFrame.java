package UniPi.MealLab.App;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import UniPi.MealLab.API.MealService;
import UniPi.MealLab.Model.Recipe;

/**
 * Main GUI class for Meal Lab App.
 * Manages search, details view, and user lists.
 * [Criteria B1, B2, B3]
 */
public class MainFrame extends JFrame {

    private MealService service = new MealService();
    private DataManager dataManager = new DataManager();
    private DefaultListModel<Recipe> searchModel = new DefaultListModel<>();
    private DefaultListModel<Recipe> favoritesModel = new DefaultListModel<>();
    private DefaultListModel<Recipe> cookedModel = new DefaultListModel<>();

    private final Color ACCENT = new Color(37, 99, 235);
    private final Color BG_MAIN = new Color(248, 250, 252);
    private final Color TEXT_DARK = new Color(30, 41, 59);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 13);

    public MainFrame() {
        setTitle("Meal Lab Manager");
        setSize(1280, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadSavedData();

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG_MAIN);
        setContentPane(content);

        // Header Section
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        JLabel title = new JLabel("  Meal Lab");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ACCENT);
        title.setBorder(new EmptyBorder(12, 20, 12, 20));
        header.add(title, BorderLayout.WEST);
        content.add(header, BorderLayout.NORTH);

        // Tabs Section
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        tabs.setBorder(new EmptyBorder(10, 10, 0, 10));
        tabs.addTab("  Search  ", createSplitView(searchModel, true));
        tabs.addTab("  Favorites  ", createSplitView(favoritesModel, false));
        tabs.addTab("  Cooked  ", createSplitView(cookedModel, false));
        content.add(tabs, BorderLayout.CENTER);
    }

    private JPanel createSplitView(DefaultListModel<Recipe> model, boolean isSearchTab) {
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        left.setBorder(new EmptyBorder(0, 0, 0, 15));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(20, 0, 20, 0));

        if (isSearchTab) {
            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.setOpaque(false);

            JTextField field = new RoundedTextField(20);
            field.setPreferredSize(new Dimension(400, 45));

            ModernButton sBtn = new ModernButton("Search", ACCENT, Color.WHITE);
            ModernButton rBtn = new ModernButton("Random", new Color(16, 185, 129), Color.WHITE);
            Dimension btnSize = new Dimension(110, 45);
            sBtn.setPreferredSize(btnSize);
            rBtn.setPreferredSize(btnSize);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 5, 0, 5);
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            centerPanel.add(field, gbc);

            gbc.weightx = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.gridx = 1;
            centerPanel.add(sBtn, gbc);

            gbc.gridx = 2;
            centerPanel.add(rBtn, gbc);

            top.add(centerPanel, BorderLayout.CENTER);

            JProgressBar bar = new JProgressBar();
            loadingStyle(bar);

            JPanel botPanel = new JPanel(new BorderLayout());
            botPanel.setOpaque(false);
            botPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
            botPanel.add(bar, BorderLayout.CENTER);
            top.add(botPanel, BorderLayout.SOUTH);

            // Corrected Search Logic [Criteria B1]
            ActionListener searchAction = e -> {
                String q = field.getText().trim();
                if(!q.isEmpty()) {
                    // Use searchByIngredient as requested
                    runWorker(() -> service.searchByIngredient(q), searchModel, sBtn, bar);
                }
            };
            sBtn.addActionListener(searchAction);
            field.addActionListener(searchAction);

            rBtn.addActionListener(e -> runWorker(() -> {
                Recipe r = service.getRandomRecipe();
                return r != null ? List.of(r) : new ArrayList<>();
            }, searchModel, rBtn, bar));
        } else {
            JLabel l = new JLabel("Saved Recipes", SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 16)); l.setForeground(TEXT_DARK);
            top.add(l, BorderLayout.CENTER);
        }

        left.add(top, BorderLayout.NORTH);

        JList<Recipe> list = new JList<>(model);
        list.setCellRenderer(new ModernListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(55);
        left.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        JLabel placeholder = new JLabel("Select a recipe to view details", SwingConstants.CENTER);
        placeholder.setForeground(Color.GRAY);
        right.add(placeholder, BorderLayout.CENTER);

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Recipe selected = list.getSelectedValue();
                if (selected != null) {
                    // Clear previous content
                    right.removeAll();
                    right.setLayout(new BorderLayout());
                    JLabel loading = new JLabel("Fetching full details...", SwingConstants.CENTER);
                    right.add(loading, BorderLayout.CENTER);
                    right.revalidate();
                    right.repaint();

                    // Fetch FULL details (Fix for "No Instructions")
                    new SwingWorker<Recipe, Void>() {
                        @Override
                        protected Recipe doInBackground() throws Exception {
                            // If we already have instructions, use the object.
                            // Otherwise, fetch by ID.
                            if (selected.getStrInstructions() != null && !selected.getStrInstructions().isEmpty()) {
                                return selected;
                            }
                            return service.getRecipeById(selected.getIdMeal());
                        }

                        @Override
                        protected void done() {
                            try {
                                Recipe fullRecipe = get();
                                if (fullRecipe != null) {
                                    updateDetailPanel(fullRecipe, right);
                                }
                                else {
                                    loading.setText("Error loading details.");
                                }
                            } catch (Exception ex) {
                                loading.setText("Error: " + ex.getMessage());
                            }
                        }
                    }.execute();
                }
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setDividerLocation(450); split.setDividerSize(0);

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_MAIN); p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private void updateDetailPanel(Recipe r, JPanel panel) {
        panel.removeAll(); panel.setLayout(new BorderLayout());
        JLabel imgL = new JLabel("Loading image...", SwingConstants.CENTER);
        imgL.setPreferredSize(new Dimension(0, 350));
        imgL.setOpaque(true);
        imgL.setBackground(new Color(241, 245, 249));
        // Center the image within the label
        imgL.setHorizontalAlignment(SwingConstants.CENTER);

        // Load image in background [Criteria B2] - Fixed Scaling
        new SwingWorker<ImageIcon, Void>() {
            protected ImageIcon doInBackground() throws Exception {
                if(r.getStrMealThumb() == null) return null;
                BufferedImage o = javax.imageio.ImageIO.read(new URL(r.getStrMealThumb()));
                // Fix: Use -1 for width to preserve aspect ratio based on height (350)
                Image scaled = o.getScaledInstance(-1, 350, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
            }
            protected void done() { try { ImageIcon i = get(); if(i!=null) { imgL.setText(""); imgL.setIcon(i); } } catch(Exception e){} }
        }.execute();
        panel.add(imgL, BorderLayout.NORTH);

        JEditorPane text = new JEditorPane(); text.setEditable(false); text.setContentType("text/html");
        text.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Careful String construction here
        String instructions = (r.getStrInstructions() != null) ? r.getStrInstructions().replace("\n", "<br>") : "No instructions available.";
        String fullIngredients = (r.getFullIngredients() != null) ? r.getFullIngredients().replace("\n", "<br>") : "";

        String htmlContent = "<html><body style='font-family: Segoe UI; color: #334155;'>" +
                   "<h2 style='color: #1e293b;'>" + r.getStrMeal() + "</h2>" +
                   "<p><b>Category:</b> " + r.getStrCategory() + " | <b>Area:</b> " + r.getStrArea() + "</p>" +
                   "<h4 style='color:#2563eb;'>Ingredients</h4>" +
                   "<div style='background-color: #f1f5f9; padding: 10px;'>" + fullIngredients + "</div>" +
                   "<h4 style='color:#2563eb;'>Instructions</h4>" +
                   "<div style='text-align: justify;'>" + instructions + "</div>" +
                   "</body></html>";

        text.setText(htmlContent); text.setCaretPosition(0);
        panel.add(new JScrollPane(text), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        btns.setBackground(Color.WHITE);
        btns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton fav = new ModernButton("Favorite", new Color(236, 72, 153), Color.WHITE);
        JButton cook = new ModernButton("Cooked", new Color(16, 185, 129), Color.WHITE);
        JButton del = new ModernButton("Delete", Color.WHITE, Color.GRAY);

        fav.addActionListener(e -> {
            if(addUnique(favoritesModel, r))
                JOptionPane.showMessageDialog(this, "Added to Favorites!");
        });

        cook.addActionListener(e -> {
            if(addUnique(cookedModel, r))
                JOptionPane.showMessageDialog(this, "Marked as Cooked!");
        });

        del.addActionListener(e -> {
            boolean removed = false;
            if(favoritesModel.contains(r)) { favoritesModel.removeElement(r); removed=true; }
            if(cookedModel.contains(r)) { cookedModel.removeElement(r); removed=true; }
            if(removed) {
                saveCurrentData();
                JOptionPane.showMessageDialog(this, "Recipe Removed.");
                panel.removeAll(); panel.revalidate(); panel.repaint();
            }
        });

        btns.add(fav); btns.add(cook); btns.add(del);
        panel.add(btns, BorderLayout.SOUTH); panel.revalidate(); panel.repaint();
    }

    private boolean addUnique(DefaultListModel<Recipe> m, Recipe r) {
        for(int i=0; i<m.size(); i++)
            if(m.get(i).getIdMeal().equals(r.getIdMeal())) return false;
        m.addElement(r);
        saveCurrentData();
        return true;
    }

    private void runWorker(TaskSupplier t, DefaultListModel<Recipe> m, JButton b, JProgressBar bar) {
        new SwingWorker<List<Recipe>, Void>() {
            protected void done() {
                bar.setVisible(false);
                b.setEnabled(true);
                try {
                    List<Recipe> l = get();
                    m.clear();
                    if(l!=null && !l.isEmpty()) {
                        for(Recipe r:l) m.addElement(r);
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, "No results found.");
                    }
                } catch(Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Network Error: " + e.getMessage());
                }
            }
            protected List<Recipe> doInBackground() throws Exception {
                SwingUtilities.invokeLater(() -> { bar.setVisible(true); b.setEnabled(false); });
                return t.get();
            }
        }.execute();
    }

    interface TaskSupplier { List<Recipe> get() throws Exception; }

    private void loadingStyle(JProgressBar b) { b.setIndeterminate(true); b.setVisible(false); b.setPreferredSize(new Dimension(100, 3)); b.setForeground(ACCENT); b.setBorderPainted(false); }

    private void loadSavedData() {
        DataManager.UserData d = dataManager.load();
        if(d.favorites!=null) for(Recipe r:d.favorites) favoritesModel.addElement(r);
        if(d.cooked!=null) for(Recipe r:d.cooked) cookedModel.addElement(r);

    }

    private void saveCurrentData() {
        List<Recipe> f = new ArrayList<>(); for(int i=0; i<favoritesModel.size(); i++) f.add(favoritesModel.get(i));
        List<Recipe> c = new ArrayList<>(); for(int i=0; i<cookedModel.size(); i++) c.add(cookedModel.get(i));
        dataManager.save(f, c);
    }

    class ModernButton extends JButton {
        private Color bg, fg;
        public ModernButton(String text, Color bg, Color fg) {
            super(text); this.bg = bg; this.fg = fg;
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setForeground(fg); setFont(FONT_BOLD); setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 20, 8, 20));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g); g2.dispose();
        }
    }

    class RoundedTextField extends JTextField {
        public RoundedTextField(int cols) { super(cols); setOpaque(false); setBorder(new EmptyBorder(8, 15, 8, 15)); setFont(FONT_PLAIN); }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
            g2.setColor(new Color(203, 213, 225));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 30, 30);
            super.paintComponent(g); g2.dispose();
        }
    }

    class ModernListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Recipe r = (Recipe) value;
            l.setText("<html><div style='padding:5px;'><b>" + r.getStrMeal() + "</b></div></html>");
            l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)));
            l.setBackground(isSelected ? new Color(219, 234, 254) : Color.WHITE);
            return l;
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception e){}
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}