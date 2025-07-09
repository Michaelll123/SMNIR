package com.michael.Service;

import dev.langchain4j.model.output.structured.Description;


public class Results {

//    public enum Decision{
//        @Description("There is no need to use the tool to obtain extra context.")
//        NO_NEED,
//        @Description("Need to use retrieveFields tool to obtain the fields in the class.")
//        RETRIEVE_ALL_FIELDS,
//        @Description("Need to use retrieveFields tool to obtain the fields in the class.")
//        RETRIEVE_VARIABLES_OF_TYPE,
//        @Description("Need to use retrieveIdenticalFunctionCall tool to obtain the variables invoking identical function calls in the class.")
//        RETRIEVE_VARIABLE_INVOKING_IDENTICAL_FUNCTION_CALL,
//        @Description("Need to use getFunctionParameter tool to obtain the parameters of the function.")
//        GET_METHOD_SIGNATURE,
//    }
    @Description("The name of the method where its name is inconsistent with its body")
    String inconsistentMethodName;
    @Description("The reason why this name is inconsistent with its body")
    String inconsistencyReasonExplanation;
    @Description("The inconsistency type, e.g., Semantic Mismatch, Semantic Omission, Semantic Redundancy, and Semantic Ambiguity")
    String inconsistencyType;
//    @Description("The reason why this name is consistent with its body")
//    String consistencyReasonExplanation;
//    @Description("The name of the method where its name is already consistent with its body")
//    String originallyConsistentMethodName;
    @Description("The name of the recommended method that is more consistent with its body")
    String consistentMethodName;
    public Results(String inconsistentMethodName, String consistentMethodName){
        this.inconsistentMethodName = inconsistentMethodName;
        this.consistentMethodName = consistentMethodName;
    }

    public String getInconsistentMethodName() {
        return inconsistentMethodName;
    }

    public String getConsistentMethodName() {
        return consistentMethodName;
    }

    public String getInconsistencyType() {
        return inconsistencyType;
    }

    public String getInconsistencyReasonExplanation() {
        return inconsistencyReasonExplanation;
    }

//    public String getConsistencyReasonExplanation() {
//        return consistencyReasonExplanation;
//    }
//
//    public String getOriginallyConsistentMethodName() {
//        return originallyConsistentMethodName;
//    }
}
