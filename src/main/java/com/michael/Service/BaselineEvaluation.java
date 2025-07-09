package com.michael.Service;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.TokenCountEstimator;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import edu.lu.uni.serval.utils.FileHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class BaselineEvaluation {

    public static void main(String [] args) throws FileNotFoundException {
//        BaselineEvaluation.MODEL_NAME = "qwen-max-latest";
//        evaluateLLM(MODEL_NAME);
//        BaselineEvaluation.MODEL_NAME = "deepseek-v3";
//        evaluateLLM(MODEL_NAME);
        evaluateLLM(MODEL_NAME);
//        getIDEAPerformance();
//        getDeepSeekPerformance();
//        getQwenCoder14BPerformance();
//        getQwenMaxPerformance();
//        evaluateExampleStack();
    }

    private static void evaluateExampleStack() throws FileNotFoundException {

        int recommendedNamesCount = 0;
        int correctCount = 0;
        int totalCount = 223;
        StringBuilder resultRecords = new StringBuilder();
        String datasetFilePath = "E:\\CodeAdaptation\\Baselines\\ExampleStack\\100soUrls.txt";
        String filePath = "E:\\CodeAdaptation\\Baselines\\ExampleStack\\code\\Chrome-extension\\variation-dataset\\";
        ArrayList<String> soUrlsAndCount = FileHelper.readFileByLines(datasetFilePath);
        List<File> allSubDirectories = FileHelper.getAllSubDirectories(filePath);
        List<String> soIndexes = new ArrayList<>();
        for(File subDirectory : allSubDirectories){
            String subDirectoryPath = subDirectory.getAbsolutePath();
            List<File> allFiles = FileHelper.getAllFiles(subDirectoryPath,".java");
            List<String> referenceJavaFileList = new ArrayList<>();
            for(File file : allFiles){
                String fileName = file.getName();
//                System.out.println(fileName);
                if(fileName.startsWith("so")){
                    String [] split = fileName.split("-");
                    String soIndex = split[1];
//                    System.out.println(soIndex);
                    soIndexes.add(subDirectory.getName() + File.separator + soIndex);
                }
//                else{
//                    referenceJavaFileList.add(file.getAbsolutePath());
//                }
            }
        }

        for(String line: soUrlsAndCount){
            String [] split = line.split("\t");
            String soUrl = split[0];
            String count = split[1];
            for(String soIndex : soIndexes){
                if(soIndex.contains(soUrl)){
                    recommendedNamesCount += Integer.parseInt(count);
                    System.out.println(soIndex);
                    break;
                }
            }
//
        }
//        System.out.println(recommendedNamesCount);
    }



    interface Assistant {
//        @UserMessage("Extract all the values of <infill>s into a String List object.")
        Results chat(String userMessage);
    }
    public static String API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1";
//    private static final String API_URL = "https://www.DMXapi.com/v1/";
    public static String ALIYUN_API_KEY = System.getenv("ALIYUN_API_KEY");
//    private static final String ALIYUN_API_KEY = System.getenv("OPENAI_API_KEY");
    //        private static final String MODEL_NAME = "deepseek-r1:70b";
//    private static final String MODEL_NAME = "llama3.1:8b";
//    private static final String MODEL_NAME = "qwen2.5-coder:7b";
//    private static final String MODEL_NAME = "deepseek-v3";
//    private static final String MODEL_NAME = "qwen-coder-turbo";
//    private static final String MODEL_NAME = "qwen2.5-coder-32b-instruct";
//    private static final String MODEL_NAME = "qwen-turbo";
//    public static String MODEL_NAME = "qwen-max";
    private static final String MODEL_NAME = "deepseek-v3";
//    private static final String MODEL_NAME = "qwen2.5-coder-14b-instruct";
//    private static final String MODEL_NAME = "qwen2.5-coder-32b-instruct";
//    private static final String MODEL_NAME = "gpt-4o-2024-08-06";
    private ChatLanguageModel model = null;
    public TokenCountEstimator tokenEstimator = null;
    private static final double TEMPERATURE = 0.0f;
    private static Assistant assistant = null;
    private int totalTokenCount;
    private int totalInputTokenCount;
    private int totalOutputTokenCount;

    public BaselineEvaluation() {
        totalTokenCount = 0;
        totalInputTokenCount = 0;
        totalOutputTokenCount = 0;
        model = OpenAiChatModel.builder()
                .apiKey(ALIYUN_API_KEY)
                .baseUrl(API_URL)
                .modelName(MODEL_NAME)
                .strictJsonSchema(false)
                .temperature(TEMPERATURE)
                /*
                    make sure them settings are configured if you need structured outputs
                 */
//                .responseFormat("json_schema")
//                .strictJsonSchema(true)
//                .logRequests(true)
//                .logResponses(true)
                /*
                    make sure these settings are configured if you need use tool calls and both structured outputs
                 */
//                .strictTools(true)

                .build();
        if (model != null) {
            tokenEstimator = (TokenCountEstimator) model;
        } else {
            System.out.println("The model does not support direct token estimation.");
        }
        assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .systemMessageProvider((message) -> "You are a helpful code assistant.")
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }



    public Results getResponse(String prompt) throws UnsupportedEncodingException {

//        System.out.println(prompt);
        // 2. 调用生成
        Results results = null;
        try{
            results = assistant.chat(prompt);
        }
        catch (Exception e){
            PrintStream ps = new PrintStream(System.out,true,"UTF-8");
            e.printStackTrace(ps);
//            results = assistant.chat(prompt.substring(0, prompt.length()*2/3));
            System.out.println("Error in DecisionModule.getResponse");
        }
        return results;

//        return assistant.chat(prompt);
    }

    private static void evaluateLLM(String modelName) {
        BaselineEvaluation baselineEvaluation = new BaselineEvaluation();
//        String basePath = "E:\\CodeAdaptation\\Baselines\\LLM-AboveContext\\";
//        String basePath = "E:\\BadMethodName\\MCCAgentData\\JavaFiles\\";
        String basePath = "E:\\BadMethodName\\MCCAgentData\\JavaFiles1\\";
//        String outputPath = "E:\\CodeAdaptation\\Baselines\\Qwen\\";
//        String outputPath = "E:\\CodeAdaptation\\Baselines\\DeepSeek\\";
//        String outputPath = "E:\\CodeAdaptation\\Baselines\\QwenCoder14B\\";
//        String outputPath = "E:\\CodeAdaptation\\Baselines\\QwenCoder32B\\";
//        String outputPath = "E:\\CodeAdaptation\\Baselines\\GPT-4omini\\";
        String outputPath = "E:\\BadMethodName\\MCCAgentEvaluation\\"+modelName+"\\";
        List<File> allFiles = FileHelper.getAllFiles(basePath,".java");
        StringBuilder recordResults = new StringBuilder();
        double startTime= System.currentTimeMillis();
        HashMap<Double,String> allFilePathsMap = new HashMap<>();
        for(File file : allFiles){
            String filePath = file.getAbsolutePath();
            String fileName = file.getName();
            double index = Double.parseDouble(fileName.replace("-",".").replace(".java",""));
            allFilePathsMap.put(index,filePath);
        }
        Set<Double> indexSett = allFilePathsMap.keySet();
        Object[] indexSettArray = indexSett.toArray();
        Arrays.sort(indexSettArray);
        for(int i =0;i< indexSettArray.length;i++){
            double index = (double) indexSettArray[i];
            double eachStartTime= System.currentTimeMillis();
            String filePath = allFilePathsMap.get(index);
            String fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
            System.out.println(fileName);
            String content = FileHelper.readFile(filePath);
            String question = "Please identify the inconsistent method names in the given Java file. " +
                    "In this context, inconsistency means that the method name does not accurately convey the functionality implemented within the method body. " +
                    "You should provide the following JSON structure:\n" +
                    "{\n" +
                    "  \"inconsistentMethodName\": \"methodName1, methodName2, ...\",\n" +
                    "  \"inconsistencyReasonExplanation\": \"reason1, reason2, ...\",\n" +
                    "  \"consistentMethodName\": \"correctedMethodName1, correctedMethodName2, ...\",\n" +
                    "}" +
                    "\n The Java file is as follows:" + content;
//            System.out.println("Question: " + question);
            try{
                Results result = baselineEvaluation.getResponse(question);
                if(result==null) continue;
                double eachEndTime= System.currentTimeMillis();
                int inputTokenCount = baselineEvaluation.tokenEstimator.estimateTokenCount(question);
                baselineEvaluation.totalInputTokenCount += inputTokenCount;
                int outputTokenCount =0;
                String inconsistentMethodName = result.getInconsistentMethodName();
                String reasonExplanation = result.getInconsistencyReasonExplanation();
                String betterName = result.getConsistentMethodName();
                if(inconsistentMethodName !=null && reasonExplanation !=null&& betterName !=null){
                    outputTokenCount = baselineEvaluation.tokenEstimator.estimateTokenCount(inconsistentMethodName)
                            + baselineEvaluation.tokenEstimator.estimateTokenCount(reasonExplanation)
                            + baselineEvaluation.tokenEstimator.estimateTokenCount(betterName);
                }
                baselineEvaluation.totalOutputTokenCount += outputTokenCount;
                System.out.println("Inconsistent Method Name: " + inconsistentMethodName);
                System.out.println("Better Name: " + betterName);
                System.out.println("Inconsistency Reason Explanation: " + reasonExplanation);
                double timeCost= eachEndTime-eachStartTime;
                recordResults.append(fileName.replace(".java","")).append("###").append(inconsistentMethodName).append("###").append(betterName).append("###").append(reasonExplanation).append("###").append(timeCost/1000).append("###").append(inputTokenCount).append("###").append(outputTokenCount).append("\n");
                FileHelper.outputToFile( outputPath + "results_"+modelName+".txt",recordResults,true);
                recordResults.setLength(0);
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println("Error in " + ":" + fileName);
                recordResults.append(fileName.replace(".java","")).append("###").append("Error").append("\n");
            }

        }
        baselineEvaluation.totalTokenCount = baselineEvaluation.totalInputTokenCount + baselineEvaluation.totalOutputTokenCount;
        double endTime= System.currentTimeMillis();
        System.out.println("Total Time Cost: " + (endTime-startTime) + "ms");
        System.out.println("Total Time Cost: " +(endTime-startTime)/1000 + "s");
        System.out.println("Total Input Token Count: " + baselineEvaluation.totalInputTokenCount);
        System.out.println("Total Output Token Count: " +baselineEvaluation.totalOutputTokenCount);
        System.out.println("Total Token Count: " +baselineEvaluation.totalTokenCount);

        recordResults.append("Total Time Cost: " + (endTime-startTime) + "ms").append("\n");
        recordResults.append("Total Time Cost: " +(endTime-startTime)/1000 + "s").append("\n");
        recordResults.append("Total Input Token Count: " + baselineEvaluation.totalInputTokenCount).append("\n");
        recordResults.append("Total Output Token Count: " +baselineEvaluation.totalOutputTokenCount).append("\n");
        recordResults.append("Total Token Count: " +baselineEvaluation.totalTokenCount).append("\n");
        FileHelper.outputToFile( outputPath + "results_"+modelName+".txt",recordResults,true);
    }



}
