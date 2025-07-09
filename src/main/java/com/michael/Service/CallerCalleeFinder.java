package com.michael.Service;

import com.intellij.psi.PsiMethod;

import java.util.ArrayList;

public class CallerCalleeFinder {

    public static java.util.List<com.intellij.psi.PsiMethod> findCallers(com.intellij.psi.PsiMethod method) {
        java.util.List<com.intellij.psi.PsiMethod> callers = new java.util.ArrayList<>();
        if (method == null || !method.isValid()) return callers;

        com.intellij.openapi.project.Project project = method.getProject();
        com.intellij.psi.search.GlobalSearchScope scope = com.intellij.psi.search.GlobalSearchScope.projectScope(project);

        for (com.intellij.psi.PsiReference ref : com.intellij.psi.search.searches.ReferencesSearch.search(method, scope)) {
            com.intellij.psi.PsiElement element = ref.getElement();
            com.intellij.psi.PsiMethod caller = com.intellij.psi.util.PsiTreeUtil.getParentOfType(element, com.intellij.psi.PsiMethod.class);
            if (caller != null && !callers.contains(caller)) {
                callers.add(caller);
            }
        }
        return callers;
    }

    public static java.util.List<com.intellij.psi.PsiMethod> findCallees(com.intellij.psi.PsiMethod method, ArrayList<PsiMethod> allMethods) {
        java.util.List<com.intellij.psi.PsiMethod> callees = new java.util.ArrayList<>();
        if (method == null || !method.isValid() || method.getBody() == null) return callees;

        method.getBody().accept(new com.intellij.psi.JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(com.intellij.psi.PsiMethodCallExpression expression) {
                super.visitMethodCallExpression(expression);
                com.intellij.psi.PsiMethod callee = expression.resolveMethod();
                if (callee != null && !callees.contains(callee) && allMethods.contains(callee)) {
                    callees.add(callee);
                }
            }
        });
        return callees;
    }

}
