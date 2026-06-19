package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureeditor.ui.frame.MainFrame;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    public static DeleteObjectConfirmationResult deleteObjectConfirmation(String objectID, int referenceCount, Window parentWindow) {
        int confirmResult;
        if (referenceCount > 0) {
            Object[] confirmOptions = {"Delete", "View References", "Cancel"};
            confirmResult = JOptionPane.showOptionDialog(parentWindow, "Are you sure you want to delete " + objectID + "?\nReferences: " + referenceCount, "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        } else {
            Object[] confirmOptions = {"Delete", "Cancel"};
            confirmResult = JOptionPane.showOptionDialog(parentWindow, "Are you sure you want to delete " + objectID + "?\nReferences: " + 0, "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        }
        if (confirmResult == 0) {
            return DeleteObjectConfirmationResult.DELETE;
        } else if (confirmResult == 1 && referenceCount > 0) {
            return DeleteObjectConfirmationResult.VIEW_REFERENCES;
        } else {
            return DeleteObjectConfirmationResult.CANCEL;
        }
    }

}
