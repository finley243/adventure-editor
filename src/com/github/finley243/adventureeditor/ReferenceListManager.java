package com.github.finley243.adventureeditor;

import com.github.finley243.adventureeditor.ui.frame.ReferenceListFrame;

import java.util.*;

public class ReferenceListManager {

    private final Main main;

    private ReferenceListFrame referenceListFrame;

    public ReferenceListManager(Main main) {
        this.main = main;
    }

    public void openReferenceList(Set<Reference> references) {
        if (references.isEmpty()) {
            return;
        }
        if (referenceListFrame != null) {
            referenceListFrame.toFront();
            referenceListFrame.requestFocus();
        } else {
            referenceListFrame = new ReferenceListFrame(main);
        }
        referenceListFrame.loadReferences(references);
    }

    public boolean onCloseReferenceList() {
        referenceListFrame = null;
        return true;
    }

}
