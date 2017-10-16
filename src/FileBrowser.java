import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.JFileChooser;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.filechooser.FileSystemView;

import javax.imageio.ImageIO;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import java.io.*;
import java.nio.channels.FileChannel;

import java.net.URL;


class FileBrowser {


    public static final String APP_TITLE = "My_File_explorer";

    private Desktop desktop;

    private FileSystemView fileSystemView;


    private File currentFile;


    private JPanel gui;


    private JTree tree;
    private DefaultTreeModel treeModel;


    private JTable table;
    private JProgressBar progressBar;

    private FileTableModel fileTableModel;
    private ListSelectionListener listSelectionListener;
    private boolean cellSizesSet = false;
    private int rowIconPadding = 6;


    private JButton openFile;
    private JButton printFile;
    private JButton editFile;


    private JLabel fileName;
    private JTextField path;
    private JLabel date;
    private JLabel size;
    private JCheckBox readable;
    private JCheckBox writable;
    private JCheckBox executable;
    private JRadioButton isDirectory;
    private JRadioButton isFile;
    public static JFrame f;
    public JPanel panel;
    public  Component c1;
    int row;
    DefaultMutableTreeNode node;
   public File[] files;
    static FileBrowser FileBrowser;
    public  int fl=0;
    public int f2=0;

    /* GUI options/containers for new File/Directory creation.  Created lazily. */
    private JPanel newFilePanel;
    private JRadioButton newTypeFile;
    private JTextField name;
    class FileList {

        public Component getGui(File[] all, boolean vertical) {
            // put File objects in the list..
            JScrollPane scrollPane = new JScrollPane();

            JList fileList = new JList(all);
            scrollPane.setViewportView(fileList);
            MouseListener m=new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JList list = (JList) e.getSource();
                    int index = list.locationToIndex(e.getPoint());

                    if (e.getClickCount() == 2) {
                        System.out.println(row);
                        File file = ((FileTableModel) table.getModel()).getFile(index);
                        if (file.isDirectory()) {
                            files = fileSystemView.getFiles(file, true); //!!
                        }


                            if (c1 != null) {
                                panel.remove(c1);
                            }

                            FileList fl = new FileList();
                            c1 = fl.getGui(files, false);
                            panel.add(c1, BorderLayout.CENTER);

                            f.setContentPane(FileBrowser.getGui());


                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            };
            fileList.addMouseListener(m);


            // ..then use a renderer
            fileList.setCellRenderer(new FileRenderer(!vertical));


            if (!vertical) {
                fileList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                fileList.setVisibleRowCount(-1);
            } else {
                fileList.setVisibleRowCount(9);
            }
            return fileList;
        }

    }

    public Container getGui() {
        if (gui==null) {
             panel = new JPanel(new BorderLayout(4,2));
            panel.setBorder(new EmptyBorder(0,6,0,6));
            gui = new JPanel(new BorderLayout(3,3));
            gui.setBorder(new EmptyBorder(5,5,5,5));

            fileSystemView = FileSystemView.getFileSystemView();
            desktop = Desktop.getDesktop();

            JPanel detailView = new JPanel(new BorderLayout(3,3));

            table = new JTable();
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setAutoCreateRowSorter(true);
            table.setShowVerticalLines(false);

            listSelectionListener = new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent lse) {
                     row = table.getSelectionModel().getLeadSelectionIndex();
                    //setFileDetails( ((FileTableModel)table.getModel()).getFile(row) );


                }


            };
            MouseListener m= new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JTable table = (JTable) e.getSource();
                    Point p = new Point();
                    p = e.getPoint();
                    int row = table.rowAtPoint(p);
                    if (e.getClickCount() == 2) {
                        System.out.println(row);
                        File file = ((FileTableModel) table.getModel()).getFile(row);
                        if (file.isDirectory()) {
                            files = fileSystemView.getFiles(file, true); //!!
                        }

                       if(fl==1){ if(c1!=null){panel.remove(c1);}

                            FileList fl = new FileList();
                            c1 = fl.getGui(files,false);
                            panel.add(c1,BorderLayout.CENTER);

                            f.setContentPane(FileBrowser.getGui());}
                            else{
                           setTableData(files);

                       }



                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            };
            table.addMouseListener(m);
            table.getSelectionModel().addListSelectionListener(listSelectionListener);
            JScrollPane tableScroll = new JScrollPane(table);

            Dimension d = tableScroll.getPreferredSize();
            tableScroll.setPreferredSize(new Dimension((int)d.getWidth(), (int)d.getHeight()/2));
            panel.add(tableScroll,BorderLayout.CENTER);
            detailView.add(panel, BorderLayout.CENTER);

            // the File tree
            File ff1 = new File(System.getProperty("user.dir"));
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(ff1);
            treeModel = new DefaultTreeModel(root);

            TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent tse){
                    node = (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
                    //node = (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
                    showChildren(node);
                   // setFileDetails((File)node.getUserObject());
                    if(fl==1){
                        if(c1!=null){panel.remove(c1);}
                    FileList fl = new FileList();
                        File file = (File) node.getUserObject();
                        if (file.isDirectory()) {
                            files = fileSystemView.getFiles(file, true);}
                    c1 = fl.getGui(files,false);
                    panel.add(c1,BorderLayout.CENTER);

                    f.setContentPane(FileBrowser.getGui());}

                }
            };

            // show the file system roots.
            File[] roots = fileSystemView.getRoots();
            for (File fileSystemRoot : roots) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
                root.add( node );
                File[] files = fileSystemView.getFiles(fileSystemRoot, true);
                for (File file : files) {
                    if (file.isDirectory()) {
                        node.add(new DefaultMutableTreeNode(file));
                    }
                }

            }

            tree = new JTree(treeModel);
            tree.setRootVisible(true);
            tree.addTreeSelectionListener(treeSelectionListener);
            tree.setCellRenderer(new FileTreeCellRenderer());
            tree.expandRow(0);
            JScrollPane treeScroll = new JScrollPane(tree);


            tree.setVisibleRowCount(15);

            Dimension preferredSize = treeScroll.getPreferredSize();
            Dimension widePreferred = new Dimension(
                    200,
                    (int)preferredSize.getHeight());
            treeScroll.setPreferredSize( widePreferred );

            // details for a File
            JPanel fileMainDetails = new JPanel(new BorderLayout(4,2));
            fileMainDetails.setBorder(new EmptyBorder(0,6,0,6));

            JPanel fileDetailsLabels = new JPanel(new GridLayout(0,1,2,2));
            fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

            JPanel fileDetailsValues = new JPanel(new GridLayout(0,1,2,2));
           // fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

           /* fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
            fileName = new JLabel();
            fileDetailsValues.add(fileName);
            fileDetailsLabels.add(new JLabel("Path/name", JLabel.TRAILING));
            path = new JTextField(5);
            path.setEditable(false);
            fileDetailsValues.add(path);
            fileDetailsLabels.add(new JLabel("Last Modified", JLabel.TRAILING));
            date = new JLabel();
            fileDetailsValues.add(date);
            fileDetailsLabels.add(new JLabel("File size", JLabel.TRAILING));
            size = new JLabel();
            fileDetailsValues.add(size);*/


            JToolBar toolBar = new JToolBar();

            toolBar.setFloatable(false);

           JButton locateFile = new JButton("Back");
            locateFile.setMnemonic('l');

            locateFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {

                        if (files[0].getParent() == System.getProperty("user.home")) {

                        } else {
                            File ff = files[0].getParentFile().getParentFile();
                            if (ff == null) {
                                ff = new File(System.getProperty("user.home"));
                            }
                            files = fileSystemView.getFiles(ff, true);


                            if (fl == 1) {
                                if (c1 != null) {
                                    panel.remove(c1);
                                }
                                FileList fl = new FileList();
                                c1 = fl.getGui(files, false);
                                panel.add(c1, BorderLayout.CENTER);

                                f.setContentPane(FileBrowser.getGui());
                            } else {
                                setTableData(files);
                               // setFileDetails((File) node.getUserObject());
                            }
                        }

                        } catch(Throwable t){
                            showThrowable(t);
                        }
                        gui.repaint();

                }
            });
            toolBar.add(locateFile);

            openFile = new JButton("Open");
            openFile.setMnemonic('o');

            openFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    try {
                        System.out.println("Open: " + currentFile);
                        desktop.open(currentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                    gui.repaint();
                }
            });
            toolBar.add(openFile);

            editFile = new JButton("Table View");
            editFile.setMnemonic('e');
            editFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {
                    fl=0;
                    f.setContentPane(FileBrowser.getGui());
                    if(c1!=null){panel.remove(c1);}
                    setTableData(files);
                    panel.add(tableScroll);
                }
            });
            toolBar.add(editFile);

            printFile = new JButton("Grid View");
            printFile.setMnemonic('p');
            printFile.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae) {

                   // File f = new File(System.getProperty("user.home"));
                    fl=1;
                    if(c1!=null){panel.remove(c1);}
                    FileList fl = new FileList();
                     c1 = fl.getGui(files,false);

                    //f = new File(System.getProperty("user.home"));
                    Component c2 = fl.getGui(files,false);
                    panel.remove(tableScroll);
                    panel.add(c1,BorderLayout.CENTER);

                    f.setContentPane(FileBrowser.getGui());
                }
            });
            toolBar.add(printFile);




            JPanel fileView = new JPanel(new BorderLayout(3,3));

            fileView.add(toolBar,BorderLayout.NORTH);
            fileView.add(fileMainDetails,BorderLayout.CENTER);

            detailView.add(fileView, BorderLayout.SOUTH);

            JSplitPane splitPane = new JSplitPane(
                    JSplitPane.HORIZONTAL_SPLIT,
                    treeScroll,
                    detailView);
            gui.add(splitPane, BorderLayout.CENTER);

            JPanel simpleOutput = new JPanel(new BorderLayout(3,3));
            progressBar = new JProgressBar();
            simpleOutput.add(progressBar, BorderLayout.EAST);
            progressBar.setVisible(false);

            gui.add(simpleOutput, BorderLayout.SOUTH);

        }
        return gui;
    }

    public void showRootFile() {
        // ensure the main files are displayed
        tree.setSelectionInterval(0,0);
    }

    private TreePath findTreePath(File find) {
        for (int ii=0; ii<tree.getRowCount(); ii++) {
            TreePath treePath = tree.getPathForRow(ii);
            Object object = treePath.getLastPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)object;
            File nodeFile = (File)node.getUserObject();

            if (nodeFile==find) {
                return treePath;
            }
        }
        // not found!
        return null;
    }

    private void showErrorMessage(String errorMessage, String errorTitle) {
        JOptionPane.showMessageDialog(
                gui,
                errorMessage,
                errorTitle,
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showThrowable(Throwable t) {
        t.printStackTrace();
        JOptionPane.showMessageDialog(
                gui,
                t.toString(),
                t.getMessage(),
                JOptionPane.ERROR_MESSAGE
        );
        gui.repaint();
    }

    /** Update the table on the EDT */
    private void setTableData(final File[] files) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (fileTableModel==null) {
                    fileTableModel = new FileTableModel();
                    table.setModel(fileTableModel);
                }
                table.getSelectionModel().removeListSelectionListener(listSelectionListener);
                fileTableModel.setFiles(files);
                table.getSelectionModel().addListSelectionListener(listSelectionListener);
                if (!cellSizesSet) {
                    Icon icon = fileSystemView.getSystemIcon(files[0]);

                    // size adjustment to better account for icons
                    table.setRowHeight( icon.getIconHeight()+rowIconPadding );

                    setColumnWidth(0,-1);
                    setColumnWidth(3,60);
                    table.getColumnModel().getColumn(3).setMaxWidth(120);
                    setColumnWidth(4,-1);
                    //setColumnWidth(5,-1);


                    cellSizesSet = true;
                }
            }
        });
    }

    private void setColumnWidth(int column, int width) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width<0) {
            // use the preferred width of the header..
            JLabel label = new JLabel( (String)tableColumn.getHeaderValue() );
            Dimension preferred = label.getPreferredSize();
            // altered 10->14 as per camickr comment.
            width = (int)preferred.getWidth()+14;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }


    private void showChildren(final DefaultMutableTreeNode node) {
        tree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) {
                     files = fileSystemView.getFiles(file, true); //!!
                    if (node.isLeaf()) {
                        for (File child : files) {
                            if (child.isDirectory()) {
                                publish(child);
                            }
                        }
                    }
                    setTableData(files);
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for (File child : chunks) {
                    node.add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }


    private void setFileDetails(File file) {
        currentFile = file;
        Icon icon = fileSystemView.getSystemIcon(file);
        fileName.setIcon(icon);
        fileName.setText(fileSystemView.getSystemDisplayName(file));
        path.setText(file.getPath());
        date.setText(new Date(file.lastModified()).toString());
        size.setText(file.length() + " bytes");
  

        JFrame f = (JFrame)gui.getTopLevelAncestor();
        if (f!=null) {
            f.setTitle(
                    APP_TITLE +
                            " :: " +
                            fileSystemView.getSystemDisplayName(file) );
        }

        gui.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                /*try {

                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch(Exception weTried) {
                }*/
                f = new JFrame(APP_TITLE);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

               FileBrowser = new FileBrowser();
                f.setContentPane(FileBrowser.getGui());

              /*  try {
                    URL urlBig = FileBrowser.getClass().getResource("fb-icon-32x32.png");
                    URL urlSmall = FileBrowser.getClass().getResource("fb-icon-16x16.png");
                    ArrayList<Image> images = new ArrayList<Image>();
                    images.add( ImageIO.read(urlBig) );
                    images.add( ImageIO.read(urlSmall) );
                    f.setIconImages(images);
                } catch(Exception weTried) {}*/

                f.pack();
                f.setLocationByPlatform(true);
                f.setMinimumSize(f.getSize());
                f.setVisible(true);

                FileBrowser.showRootFile();
            }
        });
    }
}

/** A TableModel to hold File[]. */
class FileTableModel extends AbstractTableModel {

    private File[] files;
    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private String[] columns = {
            "Icon",
            "File",
            "Path/name",
            "Size",
            "Last Modified",
            "is Directory"

    };
    public void setRowCount()
    {

    };

    FileTableModel() {
        this(new File[0]);
    }

    FileTableModel(File[] files) {
        this.files = files;
    }

    public Object getValueAt(int row, int column) {
        File file = files[row];
        switch (column) {
            case 0:
                return fileSystemView.getSystemIcon(file);
            case 1:
                return fileSystemView.getSystemDisplayName(file);
            case 2:
                return file.getPath();
            case 3:
                return file.length();
            case 4:
                return file.lastModified();

            case 5:
                return file.isDirectory();
            default:
                System.err.println("Logic Error");
        }
        return "";
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return ImageIcon.class;
            case 3:
                return Long.class;
            case 4:
                return Date.class;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return Boolean.class;
        }
        return String.class;
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getRowCount() {
        return files.length;
    }



    public File getFile(int row) {
        return files[row];
    }

    public void setFiles(File[] files) {
        this.files = files;
        fireTableDataChanged();
    }
}

/** A TreeCellRenderer for a File. */
class FileTreeCellRenderer extends DefaultTreeCellRenderer {

    private FileSystemView fileSystemView;

    private JLabel label;

    FileTreeCellRenderer() {
        label = new JLabel();
        label.setOpaque(true);
        fileSystemView = FileSystemView.getFileSystemView();
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        File file = (File) node.getUserObject();
        label.setIcon(fileSystemView.getSystemIcon(file));
        label.setText(fileSystemView.getSystemDisplayName(file));
        label.setToolTipText(file.getPath());

        if (selected) {
            label.setBackground(backgroundSelectionColor);
            label.setForeground(textSelectionColor);
        } else {
            label.setBackground(backgroundNonSelectionColor);
            label.setForeground(textNonSelectionColor);
        }

        return label;
    }
}