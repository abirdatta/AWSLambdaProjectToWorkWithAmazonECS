package com.amazonaws.lambda;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ad.lambda.handler.LambdaFunctionHandlerForEcs;
import com.ad.lambda.model.ECSServiceRequest;
import com.amazonaws.services.lambda.runtime.Context;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
public class LambdaFunctionHandlerForEcsTest {

    private static ECSServiceRequest input;

    @BeforeClass
    public static void createInput() throws IOException {
        // TODO: set up your sample input object here.
        input = null;
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }

    @Test
    public void testLambdaFunctionHandlerForEcs() {
        LambdaFunctionHandlerForEcs handler = new LambdaFunctionHandlerForEcs();
        Context ctx = createContext();

        String output = handler.handleRequest(input, ctx);

        // TODO: validate output here if needed.
        if (output != null) {
            System.out.println(output.toString());
        }
    }
}
