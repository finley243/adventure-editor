package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    public static DeleteObjectConfirmationResult deleteObjectConfirmation(String objectID, int referenceCount, Window parentWindow) {
        Object[] confirmOptions;
        if (referenceCount > 0) {
            confirmOptions = new Object[]{"Delete", "View References", "Cancel"};
        } else {
            confirmOptions = new Object[]{"Delete", "Cancel"};
        }
        int confirmResult = JOptionPane.showOptionDialog(parentWindow, "Are you sure you want to delete " + objectID + "?\nReferences: " + referenceCount, "Confirm Delete", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, confirmOptions, confirmOptions[0]);
        if (confirmResult == 0) {
            return DeleteObjectConfirmationResult.DELETE;
        } else if (confirmResult == 1 && referenceCount > 0) {
            return DeleteObjectConfirmationResult.VIEW_REFERENCES;
        } else {
            return DeleteObjectConfirmationResult.CANCEL;
        }
    }

}
