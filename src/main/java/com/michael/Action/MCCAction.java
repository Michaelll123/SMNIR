package com.michael.Action;

import com.michael.Service.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import groovy.lang.Tuple2;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MCCAction extends AnAction {
    public static Editor editor;
    public static Project project;
    public static List<String[]> inconsistentMethodList = new ArrayList<>();
    public static List<PsiMethod> inconsistentPsiMethodList = new ArrayList<>();
    public static PsiFile psiFile = null;
    public static boolean isChanged = false;


    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("entered");
        StringBuilder resultRecord = new StringBuilder();
        project = e.getProject();
        editor = e.getData(CommonDataKeys.EDITOR);
        assert project != null;
        assert editor != null;
        ContextProvider contextProvider = new ContextProvider();

        contextProvider.project = project;
        contextProvider.currentFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if(contextProvider.currentFile == null){
            System.out.println("No current file found.");
            return;
        }
        /*
            set context
         */
        if(psiFile == null){
            inconsistentMethodList.clear();
            inconsistentPsiMethodList.clear();
            psiFile = contextProvider.currentFile;
            execute(contextProvider);
            PsiManager.getInstance(project).addPsiTreeChangeListener(new MyPsiTreeChangeListener());
        }
        else if(psiFile != contextProvider.currentFile) {
            inconsistentPsiMethodList.clear();
            inconsistentMethodList.clear();
            psiFile = contextProvider.currentFile;
            execute(contextProvider);
        }
        else{
            if(isChanged){
                inconsistentPsiMethodList.clear();
                inconsistentMethodList.clear();
                isChanged = false;
                execute(contextProvider);
            }
            else{
                execute(contextProvider);
                System.out.println("No changes detected.");
            }
        }

    }

    private void execute(ContextProvider contextProvider) {
        ArrayList<PsiMethod> methods = obtainAllMethodsExceptGetterSetterConstructor(contextProvider.currentFile);
        if(methods==null){
            return;
        }
        List<Tuple2<PsiMethod, String>> inconsistentMethods = identifyAndRecommend(contextProvider, methods);
        if(inconsistentMethods == null){
            return;
        }
        for(Tuple2<PsiMethod, String> tuple : inconsistentMethods){
            PsiMethod method = tuple.getV1();
            String content = tuple.getV2();
            String [] parts = content.split("#");
            String type = parts[0];
            String reason = parts[1];
            String suggestedName = parts[2];
            String methodName = method.getName();
            String signature = getSignature(method);
            inconsistentMethodList.add(new String[]{signature,type,reason,suggestedName,methodName});
            inconsistentPsiMethodList.add(method);
        }
        ToolWindowManager instance = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = instance.getToolWindow("Inconsistent Method List");
        if (toolWindow != null) {
            SmellListToolWindow toolWindowFactory = new SmellListToolWindow();
            toolWindowFactory.createToolWindowContent(project, toolWindow);
            toolWindow.activate(null);
        }
    }

    private @NotNull String getSignature(PsiMethod method) {
        PsiType returnType = method.getReturnType();
        String signature ="";
        if(returnType!=null){
            String returnTypeCanonicalText = returnType.getCanonicalText();
            signature = getMethodSignature(method, returnTypeCanonicalText);
        }
        else{
            signature = getMethodSignature(method);
        }
        return signature;
    }

    private String getMethodSignature(PsiMethod method) {
        return method.getModifierList().getText() +" "
                + method.getSignature(PsiSubstitutor.EMPTY).toString().replace("MethodSignatureBackedByPsiMethod:","");
    }

    private String getMethodSignature(PsiMethod method, String returnTypeCanonicalText) {
        return method.getModifierList().getText() +" " + returnTypeCanonicalText.substring(returnTypeCanonicalText.lastIndexOf(".")+1)
                + method.getSignature(PsiSubstitutor.EMPTY).toString().replace("MethodSignatureBackedByPsiMethod:","");
    }

    private List<Tuple2<PsiMethod,String>> identifyAndRecommend(ContextProvider contextProvider, ArrayList<PsiMethod> methods) {
        List<Tuple2<PsiMethod,String>> InconsistentNamesAndRecommendedNames = new ArrayList<>();
        ClassLoader classLoader = MCCAction.class.getClassLoader();
        InputStream stream = classLoader.getResourceAsStream("Prompt.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String command;
        Stream<String> lines = reader.lines();
        command = lines.collect(Collectors.toSet()).stream().collect(Collectors.joining());
        IdentifyByLLM identifyByLLM = new IdentifyByLLM(project);
        if(identifyByLLM.model == null){
//            System.out.println("Model is not initialized. Please set your API key first in File | Settings | Tools | SMNIR AI Settings.");
            return null;
        }
        String promptWithContext = constructPrompt(contextProvider.currentFile);
        Results response = identifyByLLM.getResponse(command + "\n" + promptWithContext);
        System.out.println(command + "\n" + promptWithContext);
        if(response == null) {
            System.out.println("No response from LLM.");
            return InconsistentNamesAndRecommendedNames;
        }
        String inconsistentMethodName = response.getInconsistentMethodName();
        String type = response.getInconsistencyType();
        String betterName = response.getConsistentMethodName();
        String reasonExplanation = response.getInconsistencyReasonExplanation();
//        String orignallyConsistentMethodName = response.getOriginallyConsistentMethodName();
//        String consistencyReasonExplanation = response.getConsistencyReasonExplanation();
        System.out.println("Inconsistent Method Name: " + inconsistentMethodName);
        System.out.println("Inconsistency Type: " + type);
        System.out.println("Better Name: " + betterName);
        System.out.println("Inconsistency Reason Explanation: " + reasonExplanation);
//        System.out.println("Consistent Method Name: " + orignallyConsistentMethodName);
//        System.out.println("consistency Reason Explanation: " + consistencyReasonExplanation);
        List<String> methodNames = methods.stream()
                .map(PsiMethod::getName)
                .collect(Collectors.toList());
        String [] inconsistentMethodNames = inconsistentMethodName.split("#");
        String [] betterNames = betterName.split("#");
        String [] types = type.split("#");
        String [] reasons = reasonExplanation.split("#");
        for(int i=0;i<inconsistentMethodNames.length;i++){
            String methodName = inconsistentMethodNames[i].trim();
            String inconsistencyType = types[i].trim();
            String suggestedName = betterNames[i].trim();
            String reason = reasons[i].trim();
            if(methodNames.contains(methodName)){
                int index = methodNames.indexOf(methodName);
                InconsistentNamesAndRecommendedNames.add(new Tuple2<>(methods.get(index), inconsistencyType+"#"+reason+"#"+suggestedName));
            }
        }
        return InconsistentNamesAndRecommendedNames;

    }

    private String constructPrompt(PsiFile currentFile) {
        ArrayList<PsiMethod> allMethods = obtainAllMethodsExceptGetterSetterConstructor(currentFile);
        ArrayList<PsiClass> psiClasses = obtainClasses(currentFile);
        StringBuilder prompt = new StringBuilder();
        List<String> validMethodNames = new ArrayList<>();
        for(PsiClass psiClass : psiClasses){
            PsiField[] fields = psiClass.getFields();
            ArrayList<PsiMethod> psiMethods = obtainMethodsOfClass(psiClass);
            prompt.append("The following is a class named ").append(psiClass.getName()).append(":\n");
            for(PsiMethod psiMethod : psiMethods){
                if(isGetter(psiMethod,fields) || isSetter(psiMethod,fields) || isConstructor(psiMethod) || isOverriden(psiMethod)){
                    continue; // 过滤掉getter和setter方法以及constructor和override方法
                }
                prompt.append(psiMethod.getText()).append("\n");
                validMethodNames.add(psiMethod.getName());
//                prompt.append("[Caller Methods]").append("\n");
//                int callerMark = 0;
//                List<PsiMethod> callers = CallerCalleeFinder.findCallers(psiMethod);
//                if(callers.isEmpty()){
//                    prompt.append("No callers found.\n");
//                }
//                else{
//                    for(PsiMethod caller : callers){
//                        String signature = getSignature(caller);
//                        if(caller.getName().startsWith("test")) continue;
//                        prompt.append(signature).append("\n");
//                        callerMark = 1;
//                    }
//                }
//                if(callerMark == 0){
//                    prompt.append("No callers found.\n");
//                }
//
//                prompt.append("[Callee Methods]").append("\n");
//                List<PsiMethod> callees = CallerCalleeFinder.findCallees(psiMethod,allMethods);
//                if(callees.isEmpty()){
//                    prompt.append("No callees found.\n");
//                }
//                else{
//                    for(PsiMethod callee : callees){
//                        String signature = getSignature(callee);
//                        prompt.append(signature).append("\n");
//                    }
//                }

            }
        }
        System.out.println(validMethodNames.size());
        System.out.println(validMethodNames);
        return prompt.toString();
    }

    private ArrayList<PsiClass> obtainClasses(PsiFile currentFile) {
        ArrayList<PsiClass> classes = new ArrayList<>();
        if (currentFile instanceof PsiJavaFile javaFile) {
            PsiClass[] psiClasses = javaFile.getClasses();
            classes.addAll(Arrays.asList(psiClasses));
            return classes;
        }
        return null;
    }

    private ArrayList<PsiMethod> obtainMethodsOfClass(PsiClass psiClass) {
        ArrayList<PsiMethod> methods = new ArrayList<>();
        PsiMethod[] psiMethods = psiClass.getMethods();
        methods.addAll(Arrays.asList(psiMethods));
        return methods;
    }

    private ArrayList<PsiMethod> obtainAllMethodsExceptGetterSetterConstructor(PsiFile currentFile) {
        ArrayList<PsiMethod> methods = new ArrayList<>();
        if (currentFile instanceof PsiJavaFile javaFile) {
            PsiClass[] classes = javaFile.getClasses();
            for (PsiClass psiClass : classes) {
                PsiField[] fields = psiClass.getFields();
                PsiMethod[] psiMethods = psiClass.getMethods();
                for(PsiMethod psiMethod : psiMethods) {
                    // 过滤掉getter和setter方法以及constructor
                    if (!isGetter(psiMethod, fields) && !isSetter(psiMethod, fields) && !isConstructor(psiMethod) && !isOverriden(psiMethod)) {
                        methods.add(psiMethod);
                    }
                }
            }
            return methods;
        }
        return null;
    }
    private boolean isOverriden(PsiMethod psiMethod){
        PsiMethod[] superMethods = psiMethod.findSuperMethods();
        return superMethods.length > 0;
    }
    private boolean isConstructor(PsiMethod psiMethod) {
        if(psiMethod.getName().equals(psiMethod.getContainingClass().getName())
        )
            return true;
        else
            return false;
    }
    private boolean isGetter(PsiMethod psiMethod, PsiField[] fields) {
        String methodName = psiMethod.getName();
        if (methodName.startsWith("get") && psiMethod.getParameterList().getParametersCount() == 0) {
            PsiType returnType = psiMethod.getReturnType();
            if( returnType == null || returnType.equals(PsiType.VOID)){
                return false;
            }
            else{
                String fieldName = methodName.substring(3);
                for (PsiField field : fields) {
                    if (field.getName().equalsIgnoreCase(fieldName)) {
                        if (field.getType().equals(returnType)) {
                            return true;
                        }
                    }
                }

                PsiCodeBlock body = psiMethod.getBody();
                if(body == null) return false;
                int statementCount = body.getStatementCount();
                if(statementCount ==1)
                {
                    PsiStatement statement = body.getStatements()[0];
                    if (statement instanceof PsiReturnStatement psiReturnStatement) {
                        PsiExpression returnValue = psiReturnStatement.getReturnValue();
                        if (returnValue != null && returnValue.getType() != null && returnValue.getType().equals(returnType)) {
                            for(PsiField field : fields) {
                                if (field.getName().equalsIgnoreCase(returnValue.getText())) {
                                    return true;
                                }
                            }
                        }
                    }
                }

            }
        }
        else if (methodName.startsWith("is") && psiMethod.getParameterList().getParametersCount() == 0) {
            PsiType returnType = psiMethod.getReturnType();
            if (returnType != null && returnType.equals(PsiType.BOOLEAN)) {
                String fieldName = methodName.substring(2);
                for (PsiField field : fields) {
                    if (field.getName().equalsIgnoreCase(fieldName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isSetter(PsiMethod psiMethod, PsiField[] fields) {
        String methodName = psiMethod.getName();
        String fieldName = "";
        if (methodName.startsWith("set") && psiMethod.getParameterList().getParametersCount() == 1) {
            fieldName = methodName.substring(3);
            for (PsiField field : fields) {
                if (field.getName().equalsIgnoreCase(fieldName)) {
                    if (field.getType().equals(psiMethod.getParameterList().getParameters()[0].getType())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static class MyPsiTreeChangeListener implements PsiTreeChangeListener {
        @Override
        public void beforeChildAddition(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {

        }

        @Override
        public void beforeChildRemoval(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {

        }

        @Override
        public void beforeChildReplacement(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {

        }

        @Override
        public void beforeChildMovement(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {

        }

        @Override
        public void beforeChildrenChange(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {

        }

        @Override
        public void beforePropertyChange(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {

        }

        @Override
        public void childAdded(@NotNull PsiTreeChangeEvent event) {
            isChanged = true;
        }

        @Override
        public void childRemoved(@NotNull PsiTreeChangeEvent event) {
            isChanged = true;
        }

        @Override
        public void childReplaced(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
            isChanged = true;
        }

        @Override
        public void childrenChanged(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
            isChanged = true;
        }

        @Override
        public void childMoved(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
            isChanged = true;
        }

        @Override
        public void propertyChanged(@NotNull PsiTreeChangeEvent psiTreeChangeEvent) {
            isChanged = true;
        }
    }
}
