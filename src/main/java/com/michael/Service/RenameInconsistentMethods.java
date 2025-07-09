package com.michael.Service;

import com.intellij.refactoring.rename.RenameProcessor;

public class RenameInconsistentMethods {

    public static void invokeRenameWithSuggestion(com.intellij.psi.PsiMethod method, String suggestedName) {
        if (method == null || suggestedName == null || suggestedName.isEmpty()) return;

        com.intellij.openapi.project.Project project = method.getProject();
        com.intellij.openapi.editor.Editor editor = com.intellij.openapi.fileEditor.FileEditorManager.getInstance(project)
                .getSelectedTextEditor();
        if (editor == null) return;
        new RenameProcessor(project, method, suggestedName, false, false).run();
    }
}
