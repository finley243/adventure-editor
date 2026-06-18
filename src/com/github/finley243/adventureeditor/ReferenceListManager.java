package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.ui.browser.BrowserFrame;
import com.github.finley243.adventureeditor.ui.frame.ReferenceListFrame;

import java.util.*;

public class ReferenceListManager {

    private final BrowserFrame browserFrame;

    private ReferenceListFrame referenceListFrame;

    public ReferenceListManager(BrowserFrame browserFrame) {
        this.browserFrame = browserFrame;
    }

    public void openReferenceList(Set<Reference> references) {
        if (references.isEmpty()) {
            return;
        }
        if (referenceListFrame != null) {
            referenceListFrame.toFront();
            referenceListFrame.requestFocus();
        } else {
            referenceListFrame = new ReferenceListFrame(browserFrame);
        }
        referenceListFrame.loadReferences(references);
    }

    public boolean onCloseReferenceList() {
        referenceListFrame = null;
        return true;
    }

}
