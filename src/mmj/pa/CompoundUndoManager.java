//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * CompoundUndoManager.java  0.01 11/08/2013
 *
 * Version 0.01:
 * Aug-11-2013: new.
 */

package mmj.pa;

import javax.swing.SwingUtilities;
import javax.swing.event.*;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.text.JTextComponent;
import javax.swing.undo.*;

/**
 * This class will merge individual edits into a single larger edit. That is,
 * characters entered sequentially will be grouped together and undone as a
 * group. Any attribute changes will be considered as part of the group and will
 * therefore be undone when the group is undone.
 */
public class CompoundUndoManager extends UndoManager implements
    UndoableEditListener, DocumentListener
{
    private EditEvent compoundEdit;
    private final JTextComponent textComponent;
    private final Runnable callback;

    // These fields are used to help determine whether the edit is an
    // incremental edit. The offset and length should increase by 1 for
    // each character added or decrease by 1 for each character removed.

    private int lastCaret;
    private int lastLength;

    /**
     * The goal caret represents the position of the caret immediately before
     * and after an edit, and it is set whenever one of the {@code EditEvent}s
     * is undone or redone. The {@code DocumentListener} part of this class is
     * called shortly afterward, and it attempts to relocate the cursor to where
     * it had been when the edit was made.
     */
    int goalCaret = 0;

    public CompoundUndoManager(final JTextComponent textComponent,
        final Runnable updateCallback)
    {
        this.textComponent = textComponent;
        callback = updateCallback;
        textComponent.getDocument().addUndoableEditListener(this);
        updateCursorPosition();
    }

    /**
     * Add a DocumentLister before the undo is done so we can position the Caret
     * correctly as each edit is undone.
     */
    @Override
    public void undo() {
        textComponent.getDocument().addDocumentListener(this);
        super.undo();
        textComponent.getDocument().removeDocumentListener(this);
    }

    /**
     * Add a DocumentLister before the redo is done so we can position the Caret
     * correctly as each edit is redone.
     */
    @Override
    public void redo() {
        textComponent.getDocument().addDocumentListener(this);
        super.redo();
        textComponent.getDocument().removeDocumentListener(this);
    }

    @Override
    public synchronized void discardAllEdits() {
        super.discardAllEdits();
        updateCursorPosition();
    }

    public void updateCursorPosition() {
        // Track Caret and Document information of this compound edit
        lastCaret = textComponent.getCaretPosition();
        lastLength = textComponent.getDocument().getLength();
    }

    /**
     * Whenever an UndoableEdit happens the edit will either be absorbed by the
     * current compound edit or a new compound edit will be started
     */
    @Override
    public void undoableEditHappened(final UndoableEditEvent e) {
        if (((DefaultDocumentEvent)e.getEdit()).getType() == EventType.CHANGE)
            return;
        if (compoundEdit == null)
            // start a new compound edit
            compoundEdit = startCompoundEdit(e.getEdit());
        else if (isIncremental((DefaultDocumentEvent)e.getEdit()))
            // append to existing edit
            compoundEdit.addEdit(e.getEdit(), textComponent.getCaretPosition());
        else {
            // close this compound edit and start a new one
            compoundEdit.end();
            compoundEdit = startCompoundEdit(e.getEdit());
        }
        if (isClosed((DefaultDocumentEvent)e.getEdit())) {
            // This edit is self-closing, so preempt any future incremental
            // edits
            compoundEdit.end();
            compoundEdit = null;
        }
        updateCursorPosition();
    }

    private boolean isIncremental(final DefaultDocumentEvent event) {
        final int newCaret = textComponent.getCaretPosition();
        final int newLength = textComponent.getDocument().getLength();

        if (lastCaret == 0 && lastLength == 0)
            // When we unify or do some other programmatic update to the
            // worksheet via JTextComponent.setText(), the effect is to create
            // *two* edit events here. One clears the screen, and the other adds
            // the new text. We want to mark the second of these as incremental,
            // so it appears as one compound edit, and also close it below in
            // isClosed() so that subsequent edits will not append to the
            // program change.
            return true;

        // single-character inserts and removes are incremental
        final int caretChange = newCaret - lastCaret;
        return caretChange == newLength - lastLength
            && Math.abs(caretChange) == 1;
    }

    private boolean isClosed(final DefaultDocumentEvent event) {
        return lastCaret == 0 && lastLength == 0;
    }

    /**
     * Each CompoundEdit will store a group of related incremental edits (ie.
     * each character typed or backspaced is an incremental edit)
     * 
     * @param anEdit the edit to start this group with
     * @return the new CompoundEdit object
     */
    private EditEvent startCompoundEdit(final UndoableEdit anEdit) {
        // The compound edit is used to store incremental edits

        compoundEdit = new EditEvent(lastCaret);
        compoundEdit.addEdit(anEdit, textComponent.getCaretPosition());

        // The compound edit is added to the UndoManager. All incremental
        // edits stored in the compound edit will be undone/redone at once

        addEdit(compoundEdit);
        callback.run();
        return compoundEdit;
    }

    /**
     * Updates to the Document as a result of Undo/Redo will cause the Caret to
     * be repositioned
     */
    public void insertUpdate(final DocumentEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                goalCaret = Math.min(goalCaret, textComponent.getDocument()
                    .getLength());
                textComponent.setCaretPosition(goalCaret);
            }
        });
    }

    public void removeUpdate(final DocumentEvent e) {
        insertUpdate(e);
    }

    public void changedUpdate(final DocumentEvent e) {}

    class EditEvent extends CompoundEdit {
        private final int beforeCaret;
        private int afterCaret;

        public EditEvent(final int before) {
            beforeCaret = before;
        }

        public void addEdit(final UndoableEdit anEdit, final int after) {
            addEdit(anEdit);
            afterCaret = after;
        }

        @Override
        public boolean isInProgress() {
            // in order for the canUndo() and canRedo() methods to work
            // assume that the compound edit is never in progress
            return false;
        }

        @Override
        public void undo() throws CannotUndoException {
            goalCaret = beforeCaret;

            // End the edit so future edits don't get absorbed by this edit
            if (compoundEdit != null)
                compoundEdit.end();

            super.undo();

            // Always start a new compound edit after an undo
            compoundEdit = null;
        }

        @Override
        public void redo() throws CannotRedoException {
            goalCaret = afterCaret;
            super.redo();
        }
    }
}
