package edu.uwb.braingrid.workbench.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
/**
 * Gathers login credentials for file uploading/downloading and execution.
 *
 * @author Del Davis
 */
public class LoginCredentialsDialog extends javax.swing.JDialog {
	
  private String workingDir() {
    String dir = System.getProperty("user.dir");
    String target = "\\target";
    if (dir.endsWith(target)) {
      dir = dir.substring(0, dir.length() - target.length());
    }
    return dir;
  }

    // <editor-fold defaultstate="collapsed" desc="Auto-Generated">
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        hostnameLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        usernameTextField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        connectButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        passwordField = new javax.swing.JPasswordField();
        hostnameTextField = new javax.swing.JTextField();
        msgLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Enter Login Credentials");

        hostnameLabel.setText("Hostname: ");

        usernameLabel.setText("Username: ");
    File inputFile = new File(workingDir() + "\\Cache\\username.encrypted");
    File key = new File(workingDir() + "\\Key");
    try {
      FileInputStream keyInput = new FileInputStream(key);
      ObjectInputStream keyObj;
      try {
        keyObj = new ObjectInputStream(keyInput);
        String keyString;
        try {
          keyString = (String) keyObj.readObject();
          SimulationSpecificationDialog tempDialog
              =
              new SimulationSpecificationDialog();
          String textName
              =
              tempDialog.decrypt(keyString, inputFile,
              inputFile, "username");
          usernameTextField.setText(textName);
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        } 
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      System.err.println("Cache does not exist");
    }

    usernameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                usernameTextFieldKeyReleased(evt);
            }
        });

        passwordLabel.setText("Password: ");

        connectButton.setText("OK");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        passwordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                passwordFieldKeyReleased(evt);
            }
        });

        hostnameTextField.setEnabled(false);

        msgLabel.setText("<html>Message: <span style=\"color:green\">None</span></html>");

    javax.swing.GroupLayout layout
        =
        new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
                .createSequentialGroup().addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
                        .createSequentialGroup().addComponent(msgLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(connectButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(cancelButton))
                        .addGroup(layout.createSequentialGroup().addComponent(hostnameLabel).addGap(18, 18, 18)
                                .addComponent(hostnameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 208,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup().addComponent(usernameLabel).addGap(18, 18, 18)
                                .addComponent(usernameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 208,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup().addComponent(passwordLabel).addGap(18, 18, 18)
                                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 208,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap()));

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                new java.awt.Component[] { hostnameLabel, passwordLabel, usernameLabel });

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                new java.awt.Component[] { hostnameTextField, passwordField, usernameTextField });

        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax
                        		.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(hostnameLabel)
                                .addComponent(hostnameTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(
                               javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(usernameLabel)
                                .addComponent(usernameTextField,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(
                                javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(passwordLabel)
                                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(
                                javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(connectButton).addComponent(
                                        cancelButton).addComponent(msgLabel))
                        .addContainerGap()));

        pack();
  } // </editor-fold>//GEN-END:initComponents

  private void usernameTextFieldKeyReleased(
      java.awt.event.KeyEvent evt) { // GEN-FIRST:event_usernameTextFieldKeyReleased
    validateUsername();
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
      specifyCredentials(true);
    }
  } // GEN-LAST:event_usernameTextFieldKeyReleased

  private void connectButtonActionPerformed(
      java.awt.event.ActionEvent evt) { // GEN-FIRST:event_connectButtonActionPerformed
    specifyCredentials(true);
  } // GEN-LAST:event_connectButtonActionPerformed

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
    // GEN-FIRST:event_cancelButtonActionPerformed
    specifyCredentials(false);
  } // GEN-LAST:event_cancelButtonActionPerformed

  private void passwordFieldKeyReleased(java.awt.event.KeyEvent evt) {
    // GEN-FIRST:event_passwordFieldKeyReleased
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
      specifyCredentials(true);
    }
  } // GEN-LAST:event_passwordFieldKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton connectButton;
    private javax.swing.JLabel hostnameLabel;
    private javax.swing.JTextField hostnameTextField;
    private javax.swing.JLabel msgLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JTextField usernameTextField;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Custom Members">
    private boolean okClicked = false;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     * Creates new form LoginCredentialsDialog.
     *
     * @param hostname  The name of the host that will be logged in to
     * @param modal  True if the dialog should have exclusive focus while visible, otherwise false
     */
    public LoginCredentialsDialog(String hostname, boolean modal) {
        setModal(modal);
        initComponents();
        center();
        hostnameTextField.setText(hostname);
        setVisible(true);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI Manipulation">
    private void center() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    /**
     * Provides the username entered.
     *
     * @return The username entered
     */
    public String getUsername() {
        return usernameTextField.getText();
    }

    /**
     * Provides a character array containing the users password. A copy of the character array
     * returned by PasswordField.getPassword is returned. After the copy is retrieved, the caller
     * should call clearPassword to set the value maintained within the dialog password to null.
     * For security reasons, the array referenced by the returned value should be zeroed out using
     * java.util.Arrays.fill(password, '0') after it is no longer needed and it should not be stored
     * anywhere else.
     *
     * @return The users password
     * @see javax.swing.JPasswordField
     * @see java.util.Arrays
     */
    public char[] getPassword() {
        return Arrays.copyOf(passwordField.getPassword(), passwordField.getPassword().length);
    }

    /**
     * Sets the data held by the password field to null. For security reasons, this method should
     * be called directly after a copy of the password is retrieved.
     */
    public void clearPassword() {
        passwordField.setText(null);
    }

    /**
     * Indicates whether or the user selected the OK button in order to close the dialog.
     *
     * @return True if the user clicked okay to close the dialog, otherwise false. A true value
     *         indicates that the remote operation should proceed.
     */
    public boolean okClicked() {
        return okClicked;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Validation">
    private void validateUsername() {
        connectButton.setEnabled(isValidUsername(usernameTextField.getText()));
        if (connectButton.isEnabled()) {
            setMsg("None", "green");
        } else {
            setMsg("Invalid Username", "red");
        }
    }

    private boolean isValidUsername(String text) {
        return text.matches("^[a-z][a-z0-9\\-]*$");
    }

    private void setMsg(String msg, String color) {
        msgLabel.setText("<html>Message: <span style=\"color:" + color + "\">"
                + msg + "</span></html>");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Action Helpers">
    private void specifyCredentials(boolean proceed) {
        this.okClicked = proceed;
        setVisible(false);
    }
    // </editor-fold>
}
