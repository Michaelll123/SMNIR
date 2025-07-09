package com.michael.Action;

import com.michael.Service.RenameInconsistentMethods;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.*;
import com.intellij.psi.search.ProjectScopeBuilder;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

public class SmellListToolWindow implements ToolWindowFactory {

    public List<String[]> methodList = MCCAction.inconsistentMethodList;
    public List<PsiMethod> psiMethodList = MCCAction.inconsistentPsiMethodList;
    public Editor editor = MCCAction.editor;
    public Project project = MCCAction.project;
    JTable table = null;
    public JPanel panel = new JPanel(new BorderLayout());

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // Create table
        String[] columnNames = {"Inconsistent Methods", "Inconsistency Type", "Reason", "Better Name Candidates"};
        Object[][] dataArray = new Object[methodList.size()][];
        for (int i = 0; i < methodList.size(); i++) {
            dataArray[i] = methodList.get(i);
        }
        table = getTable(columnNames, dataArray);

        if(methodList.isEmpty()){
            JLabel emptyLabel = new JLabel("No Inconsistent Methods Found");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            if(panel.getComponentCount() == 0){
                panel.add(emptyLabel, BorderLayout.CENTER);
            }
        }
        else{
            JBScrollPane scrollPane = new JBScrollPane(table);
            if(panel.getComponentCount() == 0){
                panel.add(scrollPane, BorderLayout.CENTER);
            }
        }
        Content content = toolWindow.getContentManager().getFactory().createContent(panel, "", false);
        toolWindow.getContentManager().removeAllContents(true);
        toolWindow.getContentManager().addContent(content);
    }

    private JTable getTable(String[] columnNames, Object[][] dataArray) {
        JTable table = new JTable(dataArray, columnNames);
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        table.getTableHeader().setDefaultRenderer(renderer);
        table.getTableHeader().setFont(new Font(table.getTableHeader().getFont().getName(), Font.BOLD, 18));
        table.setRowHeight(30);
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(100 * i);
        }
        table.setDefaultRenderer(Object.class, new CustomCellRenderer());
        final int[] lastClickedRow = {0};
        final int[] lastClickedCol = {0};
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                String methodName =  dataArray[row][4].toString();
                String recommendedName = dataArray[row][3].toString();
                PsiMethod targetMethod = psiMethodList.get(row);
                if(col == 0){
                    // Handle mouse click event
                    System.out.println(methodName);
                    getIncnsistentMethodLocation(targetMethod,editor);
                    lastClickedRow[0] = row;
                    lastClickedCol[0] = 0;
                }
                else if(col == 3 && lastClickedRow[0] == row && lastClickedCol[0] == 0){
                    System.out.println("Clicked on row: " + row + ", column: " + col);
                    RenameInconsistentMethods.invokeRenameWithSuggestion(targetMethod, recommendedName);
                    lastClickedRow[0] = row;
                    lastClickedCol[0] = 3;
                }
                else{
                    System.out.println("Clicked on row: " + row + ", column: " + col);
                }
            }
        });
        return table;
    }

    private void getIncnsistentMethodLocation(PsiMethod method,Editor editor) {
        PsiFile file = method.getContainingFile();
        TextRange methodRange = Objects.requireNonNull(method.getNameIdentifier()).getTextRange();
        if (file != null && methodRange != null) {
            editor.getSelectionModel().setSelection(methodRange.getStartOffset(), methodRange.getEndOffset());
            int offset = methodRange.getStartOffset();
            editor.getCaretModel().moveToOffset(offset);
            editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
            editor.getCaretModel().moveToOffset(methodRange.getStartOffset());
        }
    }


    public String getDupName(String input) {
        int index = input.indexOf(' ');
        if (index != -1) {
            return input.substring(0, index);
        } else {
            return input;
        }
    }

    public String getHisName(String input) {

        int index = input.indexOf(" is");
        if (index == -1) {
            return "";
        } else {
            String substring = input.substring(0, index);
            return substring.replaceAll("\\s", "");
        }
    }

    public String getCMName(String input) {
        int index = input.indexOf("in");
        if (index == -1) {
            return "";
        } else {
            String substring = input.substring(0, index);
            return substring.replaceAll("\\s", "");
        }
    }

    public void getLargeClasslocation(String classname, Project project, Editor editor) {
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);
        PsiClass[] classes = shortNamesCache.getClassesByName(classname, ProjectScopeBuilder.getInstance(project).buildProjectScope());

        if (classes.length > 0) {
            PsiElement classElement = classes[0];
            PsiFile file = classElement.getContainingFile();

            TextRange classRange = classElement.getTextRange();

            if (file != null && classRange != null) {
                int offset = classRange.getStartOffset();
                editor.getCaretModel().moveToOffset(offset);
                editor.getSelectionModel().setSelection(classRange.getStartOffset(), classRange.getEndOffset());

                editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                editor.getCaretModel().moveToOffset(classRange.getStartOffset());
            }
        }
    }

    public void getBadClassNamelocation(String classname, Project project, Editor editor) {
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);
        PsiClass[] classes = shortNamesCache.getClassesByName(classname, ProjectScopeBuilder.getInstance(project).buildProjectScope());
        if (classes.length > 0) {
            PsiClass psiClass = classes[0];
            PsiElement classNameIdentifier = psiClass.getNameIdentifier();
            if (classNameIdentifier != null) {
                TextRange classRange = classNameIdentifier.getTextRange();
                editor.getSelectionModel().setSelection(classRange.getStartOffset(), classRange.getEndOffset());
                int offset = classRange.getStartOffset();
                editor.getCaretModel().moveToOffset(offset);
                editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                editor.getCaretModel().moveToOffset(classRange.getStartOffset());

            }
        }
    }

    public void getLongMethodlocation(String methodname, Project project, Editor editor) {
        PsiManager psiManager = PsiManager.getInstance(project);

        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);
        PsiMethod[] methods = shortNamesCache.getMethodsByName(methodname, ProjectScopeBuilder.getInstance(project).buildProjectScope());
        if (methods.length > 0) {
            PsiMethod method = methods[0];
            PsiFile file = method.getContainingFile();
            TextRange methodRange = method.getTextRange();
            if (file != null && methodRange != null) {
                editor.getSelectionModel().setSelection(methodRange.getStartOffset(), methodRange.getEndOffset());
                int offset = methodRange.getStartOffset();
                editor.getCaretModel().moveToOffset(offset);
                editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                //
                editor.getCaretModel().moveToOffset(methodRange.getStartOffset());
            }
        }
//        if (methods.length > 0) {
//            PsiMethod method = methods[0];
//            PsiFile file = method.getContainingFile();
//
//            TextRange methodBodyRange = method.getBody().getTextRange();
//
//            if (file != null && methodBodyRange != null) {
//                editor.getSelectionModel().setSelection(methodBodyRange.getStartOffset(), methodBodyRange.getEndOffset());
//                int offset = methodBodyRange.getStartOffset();
//                editor.getCaretModel().moveToOffset(offset);
//                editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
//                //
//                editor.getCaretModel().moveToOffset(methodBodyRange.getStartOffset());
//            }
//        }

    }

    public void getCommonMethodlocation(String methodname2, Project project, Editor editor) {

        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }

        PsiMethod[] methods = PsiShortNamesCache.getInstance(project).getMethodsByName(methodname2, file.getResolveScope());

        for (PsiMethod method : methods) {
            PsiFile containingFile = method.getContainingFile();
            if (containingFile.equals(file)) {
                TextRange methodRange = method.getBody().getTextRange();
                if (methodRange != null) {
                    int offset = methodRange.getStartOffset();
                    editor.getCaretModel().moveToOffset(offset);

                    editor.getSelectionModel().setSelection(methodRange.getStartOffset() + 1, methodRange.getEndOffset() -1);

                    editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                    editor.getCaretModel().moveToOffset(methodRange.getStartOffset());
                    return;
                }
            }
        }
    }

    public void getLongParameterlocation(String methodName, Project project, Editor editor) {
        PsiManager psiManager = PsiManager.getInstance(project);

        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);
        PsiMethod[] methods = shortNamesCache.getMethodsByName(methodName, ProjectScopeBuilder.getInstance(project).buildProjectScope());

        if (methods.length > 0) {
            PsiMethod method = methods[0];
            PsiFile file = method.getContainingFile();

            if (file != null) {
                PsiParameterList parameterList = method.getParameterList();
                TextRange parameterRange = parameterList.getTextRange();
                if (parameterRange != null) {

                    int offset = parameterRange.getStartOffset();
                    editor.getCaretModel().moveToOffset(offset);
                    editor.getSelectionModel().setSelection(parameterRange.getStartOffset(), parameterRange.getEndOffset());
                    editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                    editor.getCaretModel().moveToOffset(parameterRange.getStartOffset());
                }
            }
        }

    }
    public void getBadMethodNamelocation(String methodname, Project project, Editor editor) {

        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);
        PsiMethod[] methods = shortNamesCache.getMethodsByName(methodname, ProjectScopeBuilder.getInstance(project).buildProjectScope());

        if (methods.length > 0) {
            PsiMethod method = methods[0];
            PsiElement methodNameIdentifier = method.getNameIdentifier();

            if (methodNameIdentifier != null) {
                TextRange methodRange = methodNameIdentifier.getTextRange();
                editor.getSelectionModel().setSelection(methodRange.getStartOffset(), methodRange.getEndOffset());


                int offset = methodRange.getStartOffset();
                editor.getCaretModel().moveToOffset(offset);
                editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                editor.getCaretModel().moveToOffset(methodRange.getStartOffset());
            }
        }
    }
    public void getFEMethodNamelocation(String methodname, Project project, Editor editor) {

        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
        if (file == null) {
            return;
        }
        PsiMethod[] methods = PsiShortNamesCache.getInstance(project).getMethodsByName(methodname, file.getResolveScope());

        for (PsiMethod method : methods) {
            PsiFile containingFile = method.getContainingFile();
            if (containingFile.equals(file)) {

                PsiElement methodNameIdentifier = method.getNameIdentifier();
                if (methodNameIdentifier != null) {
                    TextRange methodRange = methodNameIdentifier.getTextRange();
                    if (methodRange != null) {
                        int offset = methodRange.getStartOffset();
                        editor.getSelectionModel().setSelection(methodRange.getStartOffset(), methodRange.getEndOffset());
                        editor.getCaretModel().moveToOffset(offset);
                        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                        editor.getCaretModel().moveToOffset(methodRange.getStartOffset());
                        return;
                    }
                }
            }
        }
    }


    public void getBadFieldNamelocation(String fieldName, Project project, Editor editor) {
        PsiManager psiManager = PsiManager.getInstance(project);

        PsiShortNamesCache shortNamesCache = PsiShortNamesCache.getInstance(project);
        PsiField[] fields = shortNamesCache.getFieldsByName(fieldName, ProjectScopeBuilder.getInstance(project).buildProjectScope());

        if (fields.length > 0) {
            PsiField field = fields[0];
            PsiElement fieldNameIdentifier = field.getNameIdentifier();

            if (fieldNameIdentifier != null) {
                TextRange fieldRange = fieldNameIdentifier.getTextRange();
                editor.getSelectionModel().setSelection(fieldRange.getStartOffset(), fieldRange.getEndOffset());
                int offset = fieldRange.getStartOffset();
                editor.getCaretModel().moveToOffset(offset);
                editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
                editor.getCaretModel().moveToOffset(fieldRange.getStartOffset());
            }
        }


    }



}