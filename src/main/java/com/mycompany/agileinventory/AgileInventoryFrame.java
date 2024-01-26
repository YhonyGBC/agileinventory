package com.mycompany.agileinventory;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

public class AgileInventoryFrame extends JFrame {

    private DBConnection conn;
    private ArrayList<IProduct> products;
    private CustomObservable observable;
    private QuantityObserver quantityObserver;
    private TotalPriceObserver totalPriceObserver;

    public AgileInventoryFrame() {
        this.conn = DBConnection.getInstance();
        this.products = new ArrayList<>();
        this.observable = new CustomObservable(products);

        initComponents();
        customInitComponents();

        this.setVisible(true);
    }

    private void customInitComponents() {
        this.loadProducts();
        this.clearForm();
    }

    private void loadProducts() {
        this.products.clear();

        try {
            this.products.addAll(this.conn.selectProducts());
        } catch (SQLException e) {
            this.callDialog("Error at Load Products", "Could not connect to the database",
                    JOptionPane.ERROR_MESSAGE);
        }

        DefaultTableModel model = (DefaultTableModel) this.inventoryTable.getModel();
        model.setRowCount(0);

        for (IProduct product : products) {
            char prefix = product.getDBMS().charAt(0);

            Object[] row = {
                    "P" + prefix + product.getId(),
                    product.getName(),
                    product.getQuantity(),
                    product.getPricePerUnit(),
                    product.getQuantity() * product.getPricePerUnit(),
                    product.getDBMS()
            };
            model.addRow(row);
        }

        this.observable.notifyAllObservers();
    }

    private void callDialog(String title, String message, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    private void clearForm() {
        this.idField.setText("");
        this.nameField.setText("");
        this.quantityField.setText("");
        this.pricePerUnitField.setText("");
        this.DBMSField.setText("");

        this.operationLabel.setText("Add new Product");
        this.cloneButton.setVisible(false);
        this.removeButton.setVisible(false);
        this.clearCancelButton.setText("Clear");
    }

    private IProduct searchProduct(int id, String DBMS) {
        for (IProduct product : this.products)
            if (product.getId() == id && product.getDBMS().equals(DBMS))
                return product;
        return null;
    }

    private void inventoryTableMouseClicked(MouseEvent evt) {
        int selectedRow = this.inventoryTable.getSelectedRow();

        if (selectedRow != -1) {
            String idText = (String) this.inventoryTable.getValueAt(selectedRow, 0);
            int productId = Integer.parseInt(idText.substring(2));
            String DBMS = (String) this.inventoryTable.getValueAt(selectedRow, 5);

            IProduct selectedProduct = this.searchProduct(productId, DBMS);

            if (selectedProduct != null) {
                this.idField.setText(idText);
                this.nameField.setText(selectedProduct.getName());
                this.quantityField.setText(selectedProduct.getQuantity() + "");
                this.pricePerUnitField.setText(selectedProduct.getPricePerUnit() + "");
                this.DBMSField.setText(DBMS);
            } else {
                System.out.println("No se encontró el producto con ID: " + productId + " y DBMS: " + DBMS);
            }

            this.operationLabel.setText("Edit Product");
            this.cloneButton.setVisible(true);
            this.removeButton.setVisible(true);
            this.clearCancelButton.setText("Cancel");
        }
    }

    private boolean considerUpdateDatabase(IProduct product) {
        String correctDatabase;

        if (product.getPricePerUnit() > 100000)
            correctDatabase = "MySQL";
        else
            correctDatabase = "PostgreSQL";

        if (!product.getDBMS().equals(correctDatabase)) {
            try {
                IProduct newProduct = product.clone();
                newProduct.setDBMS(correctDatabase);

                this.conn.deleteProduct(product);
                this.conn.insertProduct(newProduct);

                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    private void saveButtonActionPerformed(ActionEvent evt) {
        IProduct product;
        String idText = this.idField.getText();
        int id;
        String name = this.nameField.getText();
        String quantityText = this.quantityField.getText();
        String priceText = this.pricePerUnitField.getText();
        String DBMS = this.DBMSField.getText();

        int quantity;
        float pricePerUnit;

        try {
            quantity = Integer.parseInt(quantityText);
            pricePerUnit = Float.parseFloat(priceText);
        } catch (Exception e) {
            this.callDialog("Error at Save Product", "One field is empty or contains invalid value",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (idText.equals("")) {
                product = ProductFactory.factory(name, quantity, pricePerUnit);
                this.conn.insertProduct(product);
                this.callDialog("Product Added", "The product was added successfully", JOptionPane.INFORMATION_MESSAGE);

            } else {
                id = Integer.parseInt(idText.substring(2));

                product = this.searchProduct(id, DBMS);
                product.setName(name);
                product.setQuantity(quantity);
                product.setPricePerUnit(pricePerUnit);

                if (!this.considerUpdateDatabase(product))
                    this.conn.updateProduct(product);

                this.callDialog("Product Updated", "The product was updated successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException | SQLException e) {
            this.callDialog("Error at Save Product", "Could not connect to the database",
                    JOptionPane.ERROR_MESSAGE);
        }

        this.loadProducts();
        this.clearForm();
    }

    private void removeButtonActionPerformed(ActionEvent evt) {
        try {
            int selectedRow = inventoryTable.getSelectedRow();

            if (selectedRow != -1) {
                String idText = this.idField.getText().substring(2);
                int productId = Integer.parseInt(idText);
                String DBMS = this.DBMSField.getText();

                int option = JOptionPane.showOptionDialog(this, "Are you sure about deleting the product?",
                        "Delete product", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

                if (option != JOptionPane.YES_OPTION)
                    return;

                IProduct productToRemove = this.searchProduct(productId, DBMS);
                this.conn.deleteProduct(productToRemove);

                this.callDialog("Product Deleted", "The product was updated successfully",
                        JOptionPane.INFORMATION_MESSAGE);

                this.loadProducts();
                this.clearForm();
            } else {
                System.out.println("Seleccione un producto para eliminar.");
            }
        } catch (SQLException e) {
            this.callDialog("Error at Remove Product", "Could not connect to the database",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cloneButtonActionPerformed(ActionEvent evt) {
        int selectedRow = this.inventoryTable.getSelectedRow();

        if (selectedRow != -1) {
            String idText = this.idField.getText().substring(2);
            int productId = Integer.parseInt(idText);
            String productDBMS = this.DBMSField.getText();

            IProduct selectedProduct = this.searchProduct(productId, productDBMS);
            try {
                IProduct clonedProduct = selectedProduct.clone();
                this.conn.insertProduct(clonedProduct);

                this.callDialog("Product Cloned", "The product was cloned successfully",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException | CloneNotSupportedException e) {
                this.callDialog("Error at Clone Product", "Could not connect to the database",
                        JOptionPane.ERROR_MESSAGE);
            }

            this.loadProducts();
            this.clearForm();
        } else {
            System.out.println("Seleccione un producto para clonar.");
        }
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        clearForm();
    }

    private void initComponents() {
        this.scrollPane1 = new JScrollPane();
        this.inventoryTable = new JTable();
        this.title = new JLabel();
        this.countLabel = new JLabel();
        this.totalField = new JTextField();
        this.totalPriceLabel = new JLabel();
        this.panel = new JPanel();
        this.operationLabel = new JLabel();
        this.idLabel = new JLabel();
        this.nameLabel = new JLabel();
        this.nameField = new JTextField();
        this.quantityLabel = new JLabel();
        this.quantityField = new JTextField();
        this.priceLabel = new JLabel();
        this.pricePerUnitField = new JTextField();
        this.DBMSLabel = new JLabel();
        this.saveButton = new JButton();
        this.removeButton = new JButton();
        this.clearCancelButton = new JButton();
        this.idField = new JTextField();
        this.DBMSField = new JTextField();
        this.cloneButton = new JButton();
        this.countLabel2 = new JLabel();

        this.quantityObserver = new QuantityObserver(countLabel2);
        this.totalPriceObserver = new TotalPriceObserver(totalField);
        this.observable.addObserver(quantityObserver);
        this.observable.addObserver(totalPriceObserver);

        Dimension dimension = new Dimension(900, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(dimension);
        this.setPreferredSize(dimension);
        this.setLocationRelativeTo(null);

        this.inventoryTable.setModel(new DefaultTableModel(
                new String[] {
                        "Id", "Name", "Quantity", "Price per Unit $", "Subtotal $", "DBMS"
                }, 0) {
        });
        this.inventoryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                inventoryTableMouseClicked(evt);
            }
        });

        this.inventoryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        this.scrollPane1.setViewportView(inventoryTable);

        this.title.setFont(new Font("Segoe UI", 1, 24)); // NOI18N
        this.title.setText("Agile Inventory");

        this.operationLabel.setFont(new Font("Segoe UI", 0, 18)); // NOI18N
        this.operationLabel.setText("Add Product");

        this.idLabel.setFont(new Font("Segoe UI", 0, 14)); // NOI18N
        this.idLabel.setText("Id:");

        this.nameLabel.setFont(new Font("Segoe UI", 0, 14)); // NOI18N
        this.nameLabel.setText("Name:");

        this.quantityLabel.setFont(new Font("Segoe UI", 0, 14)); // NOI18N
        this.quantityLabel.setText("Quantity:");

        this.priceLabel.setFont(new Font("Segoe UI", 0, 14)); // NOI18N
        this.priceLabel.setText("Price per Unit:");

        this.DBMSLabel.setFont(new Font("Segoe UI", 0, 14));
        this.DBMSLabel.setText("DBMS:");

        this.countLabel.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
        this.countLabel.setText("Products count:");

        this.countLabel2.setFont(new Font("Segoe UI", 0, 12)); // NOI18N
        this.countLabel2.setText("0");

        this.totalPriceLabel.setFont(new Font("Segoe UI", 1, 14)); // NOI18N
        this.totalPriceLabel.setText("Total:");

        // totalField.setBorder(BorderFactory.createBevelBorder(border.BevelBorder.RAISED));

        // jPanel2.setBackground(new Color(204, 204, 204));
        // jPanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        this.idField.setText("idField");
        this.idField.setEnabled(false);

        this.DBMSField.setText("dbmsField");
        this.DBMSField.setEnabled(false);

        this.nameField.setText("nameField");

        this.quantityField.setText("countField");

        this.pricePerUnitField.setText("priceField");

        this.totalField.setFont(new Font("Segoe UI", 1, 12)); // NOI18N
        this.totalField.setText("$ 0");

        // saveButton.setBackground(new Color(0, 255, 0));
        this.saveButton.setText("Save");
        // saveButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        // removeButton.setBackground(new Color(255, 255, 0));
        this.removeButton.setText("Remove");
        this.removeButton.setVisible(false);
        // removeButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        this.removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        // cancelButton.setBackground(new Color(255, 0, 0));
        this.clearCancelButton.setText("Clear");
        // cancelButton.setBorder(BorderFactory.createBevelBorder(border.BevelBorder.RAISED));
        this.clearCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        // Clone.setBackground(new java.awt.Color(0, 0, 255));
        this.cloneButton.setText("Clone");
        this.cloneButton.setVisible(false);
        // Clone.setBorder(BorderFactory.createBevelBorder(border.BevelBorder.RAISED));
        this.cloneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                cloneButtonActionPerformed(evt);
            }
        });

        GroupLayout jPanel2Layout = new GroupLayout(panel);
        panel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addGroup(jPanel2Layout
                                                        .createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                        // .addComponent(idLabel)
                                                        // .addGap(52, 52, 52)
                                                        // .addComponent(idField, GroupLayout.PREFERRED_SIZE, 190,
                                                        // GroupLayout.PREFERRED_SIZE)
                                                        )
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGroup(jPanel2Layout
                                                                        .createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING)
                                                                        .addComponent(idLabel)
                                                                        .addComponent(nameLabel)
                                                                        .addComponent(quantityLabel)
                                                                        .addComponent(DBMSLabel)
                                                                        .addComponent(priceLabel))
                                                                .addGap(26, 26, 26)
                                                                .addGroup(jPanel2Layout
                                                                        .createParallelGroup(
                                                                                GroupLayout.Alignment.LEADING)
                                                                        .addComponent(idField,
                                                                                GroupLayout.PREFERRED_SIZE, 188,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(nameField,
                                                                                GroupLayout.PREFERRED_SIZE, 188,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(quantityField,
                                                                                GroupLayout.PREFERRED_SIZE, 188,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(pricePerUnitField,
                                                                                GroupLayout.PREFERRED_SIZE, 188,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(DBMSField,
                                                                                GroupLayout.PREFERRED_SIZE, 188,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(saveButton,
                                                                                GroupLayout.PREFERRED_SIZE, 100,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(cloneButton,
                                                                                GroupLayout.PREFERRED_SIZE, 100,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(removeButton,
                                                                                GroupLayout.PREFERRED_SIZE, 100,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(clearCancelButton,
                                                                                GroupLayout.PREFERRED_SIZE, 100,
                                                                                GroupLayout.PREFERRED_SIZE)
                                                                // .addGroup(jPanel2Layout
                                                                // .createParallelGroup(
                                                                // GroupLayout.Alignment.TRAILING,
                                                                // false)
                                                                // .addComponent(cloneButton,
                                                                // GroupLayout.Alignment.LEADING,
                                                                // GroupLayout.DEFAULT_SIZE,
                                                                // GroupLayout.DEFAULT_SIZE,
                                                                // Short.MAX_VALUE)
                                                                // .addComponent(removeButton,
                                                                // GroupLayout.Alignment.LEADING,
                                                                // GroupLayout.DEFAULT_SIZE, 100,
                                                                // Short.MAX_VALUE))
                                                                ))))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(29, 29, 29)
                                                .addComponent(operationLabel)))
                                .addContainerGap(17, Short.MAX_VALUE)));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(operationLabel)
                                .addGap(26, 26, 26)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(idLabel)
                                        .addComponent(idField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(nameLabel)
                                        .addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(quantityLabel)
                                        .addComponent(quantityField, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(priceLabel)
                                        .addComponent(pricePerUnitField, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(DBMSLabel)
                                        .addComponent(DBMSField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(saveButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cloneButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(removeButton)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clearCancelButton)
                                .addContainerGap(23, Short.MAX_VALUE)));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(countLabel, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(countLabel2, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                // .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                                        GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                // .addComponent(totalPriceLabel)
                                                .addComponent(totalPriceLabel, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                // .addGap(18, 18, 18)
                                                .addComponent(totalField, GroupLayout.PREFERRED_SIZE,
                                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())
                                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 4, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                        .addGroup(GroupLayout.Alignment.TRAILING,
                                                                layout.createSequentialGroup()
                                                                        .addComponent(title)
                                                                        .addGap(162, 162, 162))
                                                        .addGroup(GroupLayout.Alignment.TRAILING, layout
                                                                .createSequentialGroup()
                                                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE,
                                                                        GroupLayout.DEFAULT_SIZE,
                                                                        GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap()))))));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(title)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 240,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                        .addComponent(countLabel)
                                                        .addComponent(countLabel2, GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(totalPriceLabel)
                                                        .addComponent(totalField, GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(0, 0, Short.MAX_VALUE))))
                                .addContainerGap()));
        pack();
    }

    public static void main(String args[]) {
        // try {
        // DBConnection.getInstance().resetDatabases();
        // } catch (Exception e) {
        // }
        EventQueue.invokeLater(() -> new AgileInventoryFrame());
    }

    private JPanel panel;

    private JLabel title;
    private JLabel operationLabel;
    private JLabel idLabel;
    private JLabel nameLabel;
    private JLabel quantityLabel;
    private JLabel priceLabel;
    private JLabel DBMSLabel;

    private JTextField idField;
    private JTextField nameField;
    private JTextField pricePerUnitField;
    private JTextField quantityField;
    private JTextField DBMSField;

    private JButton saveButton;
    private JButton cloneButton;
    private JButton removeButton;
    private JButton clearCancelButton;

    private JScrollPane scrollPane1;
    private JTable inventoryTable;

    private JLabel countLabel;
    private JLabel countLabel2;
    private JLabel totalPriceLabel;
    private JTextField totalField;
}
